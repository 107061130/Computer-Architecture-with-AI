NTHU_111064529_張瀚 ACAL 2024 Spring Lab 13 HW 
===

## Gitlab code link
- Gitlab link - https://course.playlab.tw/git/111064528/lab13

# Homework 13
## Hw 13-1 : SIMD Extra Instruction Design
### Hardware Architecture
![](https://course.playlab.tw/md/uploads/a46c8bea-f5be-4cb7-a325-c218d0285925.png)



### Chisel Implementation

- Controller: [Link](https://course.playlab.tw/git/111064528/lab13/-/blob/main/chisel/src/main/scala/simd/Controller.scala?ref_type=heads)

- AddSub Activation Unit: [Link](https://course.playlab.tw/git/111064528/lab13/-/blob/main/chisel/src/main/scala/simd/AddSubActivationUnit.scala?ref_type=heads)

- Multiplication Unit: [Link](https://course.playlab.tw/git/111064528/lab13/-/blob/main/chisel/src/main/scala/simd/MulUnit.scala?ref_type=heads)

- Quantization Unit: [Link](https://course.playlab.tw/git/111064528/lab13/-/blob/main/chisel/src/main/scala/simd/QuanUnit.scala?ref_type=heads)

- Others Unit(Relu & MaxPool): [Link](https://course.playlab.tw/git/111064528/lab13/-/blob/main/chisel/src/main/scala/simd/OthersUnit.scala?ref_type=heads)

    
### Result
- Chisel Testbench
![](https://course.playlab.tw/md/uploads/859a2b24-40cf-47ae-b67a-505fad2e95a8.png)

- Software Testbench
![](https://course.playlab.tw/md/uploads/15cd21e4-b231-411d-9a83-5df939c1ddd5.png)

## Hw 13-2 : CONV Acceleration
### C++ code
PerLayerAdvanceQuant

- `src/acal_lab/libs/op/scalar/Conv.cc`
```cpp
void Conv::execPerLayerAdvanceQuant() {
    int8_t  temp_A[4], temp_B[4];
    int16_t temp_C[4], temp_sum[4]; 
    int output_index, sum_index;
    sQNT_INFO(qInfo->scaling_factor, qInfo->zero_point);

    // Perform convolution with bias
    for (int n = 0; n < info->kernel.N; n++) {
        for (int oh = 0; oh < output->H; oh++) {
            for (int ow = 0; ow < output->W; ow++) {

                output_index = n * output->H * output->W + oh * output->W + ow;
                sum_index = output_index % 4;
                temp_sum[sum_index] = info->bias.data[n];

                for (int c = 0; c < info->kernel.C; c++) {
                    for (int kh = 0; kh < info->kernel.H; kh++) {

                        int input_index = c * input->H * input->W + (oh + kh) * input->W + ow;
                        int kernel_index = ((n * info->kernel.C + c) * info->kernel.H + kh) * info->kernel.W;
                        int kernel_W_round = (info->kernel.W >> 2) << 2; 
                        // Divide Loop by 4
                        for (int kw = 0; kw < kernel_W_round; kw += 4) {
                            // store input and kernel to temp
                            temp_A[0] = input->data[input_index + kw];
                            temp_A[1] = input->data[input_index + kw + 1];
                            temp_A[2] = input->data[input_index + kw + 2];
                            temp_A[3] = input->data[input_index + kw + 3];

                            temp_B[0] = info->kernel.data[kernel_index + kw];
                            temp_B[1] = info->kernel.data[kernel_index + kw + 1];
                            temp_B[2] = info->kernel.data[kernel_index + kw + 2];
                            temp_B[3] = info->kernel.data[kernel_index + kw + 3];

                            // SIMD MUL
                            sPMULI8I16S_vv(temp_C, temp_A, temp_B);

                            // Sum Result
                            temp_sum[sum_index] += temp_C[0];
                            temp_sum[sum_index] += temp_C[1];
                            temp_sum[sum_index] += temp_C[2];
                            temp_sum[sum_index] += temp_C[3];
                        }
                        for (int kw = kernel_W_round; kw < info->kernel.W; kw++) {
                            temp_sum[sum_index] += (int16_t)input->data[input_index + kw] * (int16_t)info->kernel.data[kernel_index + kw];
                        }
                    }
                }
                // PER LAYER QUANTIZATION
                // if accumulate 4 output, do Quantization together
                if (sum_index == 3) {
                    sQNTI16I8S_vv_AQ(temp_A, temp_sum, temp_sum + 2);
                    output->data[output_index - 3] = temp_A[0];
                    output->data[output_index - 2] = temp_A[1];
                    output->data[output_index - 1] = temp_A[2];
                    output->data[output_index]     = temp_A[3];
                }
            }
        }
    }
    // PER LAYER QUANTIZATION
    // Remain Part
    if (sum_index != 3) {
        for (int i = 0; i <= sum_index; i++) {
            output->data[output_index - (sum_index - i)] = (int8_t)((temp_sum[i] >> qInfo->scaling_factor) + qInfo->zero_point);
        }
    }
}
```

### Result
![](https://course.playlab.tw/md/uploads/5fa58d3f-9060-46b2-aa3b-6f13f9a5cbc6.png)


## Hw 13-3 : AlexNet Model Acceleration
### Gemm
- `src/acal_lab/libs/op/simd/Gemm.cc`
```cpp=
void Gemm::execPerLayerAdvanceQuant() {
    int16_t tempINT16_Buffer[10000] = {0};
    int8_t  temp_A[4];
    int16_t temp_C[4]; 
    sQNT_INFO(qInfo->scaling_factor, qInfo->zero_point);

    int index_A, index_B, index_C;
    for (int m = 0; m < input->H; m++) {
        index_A = m * input->W;   // M * K
        index_C = m * output->W;  // M * N
        for (int k = 0; k < input->W; k++) {

            index_B = k * info->weight.W;  // K * N
            int8_t input_data = input->data[index_A + k];
            int n_round = (info->weight.W >> 2) << 2;
            // Divide Loop by 4
            for (int n = 0; n < n_round; n += 4) {
                // store weight to temp
                temp_A[0] = info->weight.data[index_B + n];
                temp_A[1] = info->weight.data[index_B + n + 1];
                temp_A[2] = info->weight.data[index_B + n + 2];
                temp_A[3] = info->weight.data[index_B + n + 3];
                // SIMD MUL
                sPMULI8I16S_vx(temp_C, temp_A, input_data);
                // Sum Result
                tempINT16_Buffer[index_C + n]     += temp_C[0];
                tempINT16_Buffer[index_C + n + 1] += temp_C[1];
                tempINT16_Buffer[index_C + n + 2] += temp_C[2];
                tempINT16_Buffer[index_C + n + 3] += temp_C[3];
            }
            // Remain Part
            for (int n = n_round; n < info->weight.W; n++) {
                tempINT16_Buffer[index_C + n] += (int16_t)input_data * (int16_t)info->weight.data[index_B + n];
            }
        }
        for (int n = 0; n < info->weight.W; n++) tempINT16_Buffer[index_C + n] += info->bias.data[index_C + n];
    }

    // PER LAYER QUANTIZATION
    int tempH = 0, tempW;
    for (int h = 0; h < output->H; h++) {
        tempH = h * output->W;  // M * N
        int output_W_round = (output->W >> 2) << 2;
        // Divide Loop by 4
        for (int w = 0; w < output->W; w += 4) {
            tempW = tempH + w;
            sQNTI16I8S_vv_AQ(temp_A, tempINT16_Buffer + tempW, tempINT16_Buffer + tempW + 2);
            output->data[tempW]     = temp_A[0];
            output->data[tempW + 1] = temp_A[1];
            output->data[tempW + 2] = temp_A[2];
            output->data[tempW + 3] = temp_A[3];
        }
        // Remain Part
        for (int w = output_W_round; w < output->W; w++) {
            tempW               = tempH + w;
            output->data[tempW] = (int8_t)((tempINT16_Buffer[tempW] >> qInfo->scaling_factor) + qInfo->zero_point);
        }
    }
}
```
### Conv
same as 13-2

### 2. RELU
- `src/acal_lab/libs/op/simd/ReLU.cc`
```cpp=
void ReLU::exec() {
    int size = input->C * input->H * input->W;
    int size_round = (size >> 2) << 2;
    // Divide by 4
    for (int i = 0; i < size_round; i += 4) {
        sRELUI8S(output->data + i, input->data + i, 0);
    }
    // Remain Part
    for (int i = size_round; i < size; i++) output->data[i] = input->data[i] > 0 ? input->data[i] : 0;
}
```
### 3. MaxPool
- `src/acal_lab/libs/op/simd/MxPl.cc`
```cpp=
void MxPl::exec() {
    for (int c = 0; c < output->C; c++) {
        for (int oh = 0; oh < output->H; oh++) {
            for (int ow = 0; ow < output->W; ow++) {
                int    input_start_h = oh * info->stride - info->padding;
                int    input_start_w = ow * info->stride - info->padding;
                int8_t max_val = INT8_MIN;
                int8_t temp_in[4], temp_out[4];
                int count = 0;
                for (int kh = 0; kh < info->kernelSize; kh++) {
                    for (int kw = 0; kw < info->kernelSize; kw++) {
                        int ih = input_start_h + kh;
                        int iw = input_start_w + kw;

                        if (ih >= 0 && ih < input->H && iw >= 0 && iw < input->W) {
                            temp_in[count++] = input->data[c * input->H * input->W + ih * input->W + iw];
                            // if accumulate to 4 element, do maxpool and return maximum to temp_out[0]
                            if(count == 4) {
                                sMAXPOOLI8S(temp_out, temp_in, 0);
                                max_val = max(temp_out[0],  max_val);
                                count = 0;
                            }
                        }
                    }
                }
                // Remain Part
                if (count != 0) {
                    for (int i = 0; i < count; i++) max_val = max(temp_in[i],  max_val);
                }

                output->data[c * output->H * output->W + oh * output->W + ow] = max_val;
            }
        }
    }
}
```
### Result

- Software Testbench
![](https://course.playlab.tw/md/uploads/77fb56ca-2062-42e4-9cc3-bf5adbf42d3f.png)
![](https://course.playlab.tw/md/uploads/73abf595-9f8e-4f76-957a-3943a188319a.png)


## Hw 13-4 : Model and Operator Profile

### Theoretical Improvement (upper bound)
Combine four arithmetic operations into one, saving 3/4 of arithmetic operations time.
### Actual Improvement
* GEMM
![](https://course.playlab.tw/md/uploads/e0fc3097-dd01-4b14-abca-358a4fd0dfec.png)

* CONV
![](https://course.playlab.tw/md/uploads/58b1d7ec-43ce-4b4d-92ec-1ffe49a5cfd8.png)

* MAXPOOL
![](https://course.playlab.tw/md/uploads/7329faaf-9c6f-4e1f-9861-1ea9002694f4.png)

* RELU
![](https://course.playlab.tw/md/uploads/6d486840-ddac-40a0-b40f-3ffaabf18a32.png)

* AlexNet
![](https://course.playlab.tw/md/uploads/ca225d82-37b1-48e2-a17a-c1b92e3ba1a7.png)


### Reasons Led to not Achievethe Desired Outcome
* GEMM
SIMD Gemm saves 25 cycles.
Although we save the multiplication operations, we need to load ```weight.data``` to ```temp_A[]``` array in advance, which takes more instructions in mips machine code than placing it to the register because of the address caculation of ```temp_A```.
    ```
    // store weight to temp
    temp_A[0] = info->weight.data[index_B + n];
    temp_A[1] = info->weight.data[index_B + n + 1];
    temp_A[2] = info->weight.data[index_B + n + 2];
    temp_A[3] = info->weight.data[index_B + n + 3];
    // SIMD MUL
    sPMULI8I16S_vx(temp_C, temp_A, input_data);
    ```
    Furthermore, the SIMD instructions need addition communication between VexRiscvCPU and my funnction custom unit, which cost more cycles than simple multiplication instruction. 
    ```
    sPMULI8I16S_vx(temp_C, temp_A, input_data);
    ```
    So as the store operations
    ```
    // Sum Result
    tempINT16_Buffer[index_C + n]     += temp_C[0];
    tempINT16_Buffer[index_C + n + 1] += temp_C[1];
    tempINT16_Buffer[index_C + n + 2] += temp_C[2];
    tempINT16_Buffer[index_C + n + 3] += temp_C[3];
    
    -------------------------------
    
    sQNTI16I8S_vv_AQ(temp_A, tempINT16_Buffer + tempW, tempINT16_Buffer + tempW + 2);
    output->data[tempW]     = temp_A[0];
    output->data[tempW + 1] = temp_A[1];
    output->data[tempW + 2] = temp_A[2];
    output->data[tempW + 3] = temp_A[3];
    ```
    We use ```input_data``` to store ```input->data``` to reduce access to heap memory.

    ```
    int8_t input_data = input->data[index_A + k];
    ```
* CONV
SIMD CONV cost 7000000 more cycles. 
Same problem as GEMM, loading to ```temp_A``` and ```temp_B```  and storig from ```temp_C``` are time consuming.
    ```
    // store input and kernel to temp
    temp_A[0] = input->data[input_index + kw];
    temp_A[1] = input->data[input_index + kw + 1];
    temp_A[2] = input->data[input_index + kw + 2];
    temp_A[3] = input->data[input_index + kw + 3];
    temp_B[0] = info->kernel.data[kernel_index + kw];
    temp_B[1] = info->kernel.data[kernel_index + kw + 1];
    temp_B[2] = info->kernel.data[kernel_index + kw + 2];
    temp_B[3] = info->kernel.data[kernel_index + kw + 3];
    // SIMD MUL
    sPMULI8I16S_vv(temp_C, temp_A, temp_B);
    // Sum Result
    temp_sum[sum_index] += temp_C[0];
    temp_sum[sum_index] += temp_C[1];
    temp_sum[sum_index] += temp_C[2];
    temp_sum[sum_index] += temp_C[3];
    
    -------------------------------
    
    // PER LAYER QUANTIZATION
    // if accumulate 4 output, do Quantization together
    if (sum_index == 3) {
        sQNTI16I8S_vv_AQ(temp_A, temp_sum, temp_sum + 2);
        output->data[output_index - 3] = temp_A[0];
        output->data[output_index - 2] = temp_A[1];
        output->data[output_index - 1] = temp_A[2];
        output->data[output_index]     = temp_A[3];
    }
    ```

* MAXPOOL
SIMD MAXPOOL cost 27000 more cycles.
Same problem of load to ```temp``` issue. The comparsion only take one instruction so store it to ```temp``` then compare four element in once is redundant. Requiring more cycles than SCALAR is reasonable. 
    ```
    if (ih >= 0 && ih < input->H && iw >= 0 && iw < input->W) {
        temp_in[count++] = input->data[c * input->H * input->W + ih * input->W + iw];
        // if accumulate to 4 element, do maxpool and return maximum to temp_out[0]
        if(count == 4) {
            sMAXPOOLI8S(temp_out, temp_in, 0);
            max_val = max(temp_out[0],  max_val);
            count = 0;
        }
    }
    ```

* RELU
SIMD RELU saves 8000 cycles, which is 84.5% of the origin time.
Transform to one dimesion loop to reduce index caculation.
Directly put ```output->data``` and ```input->data``` in function to avoid ```temp[]``` issue.
    ```
    for (int i = 0; i < size_round; i += 4) {
        sRELUI8S(output->data + i, input->data + i, 0);
    }
    ```
* AlexNet

### Further Improvement

* Reduce heap memory accessment(reduce the time that load/store of ```input->data```, ```output_data``` or ```kernel->info```).
* Reduce repeated index caculation.
* Find a way to put ```input->data``` or ```output_data``` in SIMD functions directly to avoid store to ```temp[]``` and load from  ```temp[]```. 
