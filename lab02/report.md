# (NTHU 111064528 張瀚) AIAS 2024 Lab 2 HW 


## Gitlab code link
- Gitlab link - https://course.playlab.tw/git/111064528/lab02


## HW 2-1 Model Analysis Using Pytorch

### 2-1-1. Calculate the number of model parameters：

#### Code
```python=
total_params = sum(p.numel() for p in model.parameters())
print("Total number of parameters: ", total_params)
```

#### Execution Result
Total number of parameters:  6624904


### 2-1-2. Calculate memory requirements for storing the model weights.
#### Code
```python=
param_size = sum(p.numel() * p.element_size() for p in model.parameters())
print("Total memory for parameters: ", param_size)
```

#### Execution Result
Total memory for parameters:  26499616


### 2-1-3. Use Torchinfo to print model architecture including the number of parameters and the output activation size of each layer 
#### Code
```python=
torchinfo.summary(model, (3, 224, 224), batch_dim=0, col_names=("input_size", "output_size", "num_params", "kernel_size", "mult_adds"), verbose=0)
```

#### Execution Result
![](https://course.playlab.tw/md/uploads/47c5dd40-6f2b-4727-9723-93a46861032a.png)



### 2-1-4. Calculate computation requirements
#### Code

```python=
def calculate_output_shape(input_shape, layer):
    # Calculate the output shape for Conv2d, MaxPool2d, and Linear layers
    if isinstance(layer, (nn.Conv2d, nn.MaxPool2d)):
        kernel_size = (
            layer.kernel_size
            if isinstance(layer.kernel_size, tuple)
            else (layer.kernel_size, layer.kernel_size)
        )
        stride = (
            layer.stride
            if isinstance(layer.stride, tuple)
            else (layer.stride, layer.stride)
        )
        padding = (
            layer.padding
            if isinstance(layer.padding, tuple)
            else (layer.padding, layer.padding)
        )
        dilation = (
            layer.dilation
            if isinstance(layer.dilation, tuple)
            else (layer.dilation, layer.dilation)
        )

        output_height = (
            input_shape[1] + 2 * padding[0] - dilation[0] * (kernel_size[0] - 1) - 1
        ) // stride[0] + 1
        output_width = (
            input_shape[2] + 2 * padding[1] - dilation[1] * (kernel_size[1] - 1) - 1
        ) // stride[1] + 1
        return (
            layer.out_channels if hasattr(layer, "out_channels") else input_shape[0],
            output_height,
            output_width,
        )
    elif isinstance(layer, nn.Linear):
        # For Linear layers, the output shape is simply the layer's output features
        return (layer.out_features,)
    else:
        return input_shape


def calculate_macs(layer, input_shape, output_shape):
    # Calculate MACs for Conv2d and Linear layers
    if isinstance(layer, nn.Conv2d):
        kernel_ops = (
            layer.kernel_size[0]
            * layer.kernel_size[1]
            * (layer.in_channels / layer.groups)
        )
        output_elements = output_shape[1] * output_shape[2]
        macs = int(kernel_ops * output_elements * layer.out_channels)
        return macs
    elif isinstance(layer, nn.Linear):
        # For Linear layers, MACs are the product of input features and output features
        macs = int(layer.in_features * layer.out_features)
        return macs
    else:
        return 0
# Initial input shape
input_shape = (3, 224, 224)
total_macs = 0

# Iterate through the layers of the model
for name, layer in model.named_modules():
    if isinstance(layer, (nn.Conv2d, nn.Linear)):
        output_shape = calculate_output_shape(input_shape, layer)
        macs = calculate_macs(layer, input_shape, output_shape)
        total_macs += macs
        if isinstance(layer, (nn.Conv2d, nn.Linear)):
            print(
                f"Layer: {name}, Type: {type(layer).__name__}, Input Shape: {input_shape}, Output Shape: {output_shape}, MACs: {macs}"
            )
        input_shape = output_shape  # Update the input shape for the next layer

print(f"Total MACs: {total_macs}")
```

#### Execution Result
![](https://course.playlab.tw/md/uploads/901b7411-84b3-428f-9a74-a3b379b14df9.png)


### 2-1-5. Use forward hooks to extract the output activations of  the Conv2d layers.
#### Code
```python=
def get_activation(name):
    def hook(model, input, output):
        activation[name] = output.detach()
    return hook


# Dictionary to store activations from each layer
activation = {}

# Register hook to each linear layer
for layer_name, layer in model.named_modules():
    if isinstance(layer, torch.nn.Conv2d):
        # Register forward hook
        layer.register_forward_hook(get_activation(layer_name))

# Run model inference
data = torch.randn(1, 3, 224, 224)
output = model(data)

# Access the saved activations
for layer in activation:
    print(f"Activation from layer {layer}: {activation[layer].shape}")
```

#### Execution Result
![](https://course.playlab.tw/md/uploads/594c40f8-4497-463a-90c2-9f6e190ce4d9.png)


## HW 2-2 Add more statistics to analyze the an ONNX model Using sclblonnx

### 2-2-1. model characteristics
#### Code
```python=
# Extract unique operator names
# Print the number of ONNX operators
num_operators = len(model.graph.node)
print(f"Number of ONNX operators: {num_operators}")


unique_operator_names = set(node.op_type for node in model.graph.node)
print("Unique ONNX operator names:")
for op_name in unique_operator_names:
    print(f"- {op_name}")

# Print attributes for each operator
print("\nAttributes for each operator:")
for node in model.graph.node:
    op_name = node.op_type
    print(f"\nOperator: {op_name}")

    for attr in node.attribute:
        print(f"{attr.name}: {attr.ints}")

```
#### Execution Result
![](https://course.playlab.tw/md/uploads/cde6221b-4364-4920-8f7a-024023f51c9d.png)

### 2-2-2. Data bandwidth requirement 
#### Code
```python=
def _parse_element(elem: xpb2.ValueInfoProto):
    name = getattr(elem, 'name', "None")
    data_type = "NA"
    shape_str = "NA"
    etype = getattr(elem, 'type', False)
    if etype:
        ttype = getattr(etype, 'tensor_type', False)
        if ttype:
            data_type = getattr(ttype, 'elem_type', 0)
            shape = getattr(elem.type.tensor_type, "shape", False)
            if shape:
                shape_str = "["
                dims = getattr(shape, 'dim', [])
                for dim in dims:
                    vals = getattr(dim, 'dim_value', "?")
                    shape_str += (str(vals) + ",")
                shape_str = shape_str.rstrip(",")
                shape_str += "]"
    return name, data_type, shape_str

def get_valueproto_or_tensorproto_by_name(name: str, graph: xpb2.GraphProto):
    for i, node in enumerate(inferred_model.graph.node):
            if node.name == "":
                inferred_model.graph.node[i].name = str(i)
    input_nlist = [k.name for k in graph.input]
    initializer_nlist = [k.name for k in graph.initializer]
    value_info_nlist = [k.name for k in graph.value_info]
    output_nlist = [k.name for k in graph.output]

    # get tensor data
    if name in input_nlist:
        idx = input_nlist.index(name)
        return graph.input[idx], int(1)
    elif name in value_info_nlist:
        idx = value_info_nlist.index(name)
        return graph.value_info[idx], int(2)
    elif name in initializer_nlist:
        idx = initializer_nlist.index(name)
        return graph.initializer[idx], int(3)
    elif name in output_nlist:
        idx = output_nlist.index(name)
        return graph.output[idx], int(4)
    else:
        print("[ERROR MASSAGE] Can't find the tensor: ", name)
        print('input_nlist:\n', input_nlist)
        print('===================')
        print('value_info_nlist:\n', value_info_nlist)
        print('===================')
        print('initializer_nlist:\n', initializer_nlist)
        print('===================')
        print('output_nlist:\n', output_nlist)
        print('===================')
        return False, 0

def cal_tensor_mem_size(elem_type: str, shape: [int]):
    """ given the element type of the tensor and its shape, and return its memory size.

    Utility.

    Args:
        ttype: the type of the element of the given tensor. format: 'int', ...
        shape: the shape of the given tensor. format: [] of int

    Returns:
        mem_size: int
    """
    # init
    mem_size = int(1)
    # traverse the list to get the number of the elements
    for num in shape:
        mem_size *= num
    # multiple the size of variable with the number of the elements
    # "FLOAT": 1,
    # "UINT8": 2,
    # "INT8": 3,
    # "UINT16": 4,
    # "INT16": 5,
    # "INT32": 6,
    # "INT64": 7,
    # # "STRING" : 8,
    # "BOOL": 9,
    # "FLOAT16": 10,
    # "DOUBLE": 11,
    # "UINT32": 12,
    # "UINT64": 13,
    # "COMPLEX64": 14,
    # "COMPLEX128": 15
    if elem_type == 1:
        mem_size *= 4
    elif elem_type == 2:
        mem_size *= 1
    elif elem_type == 3:
        mem_size *= 1
    elif elem_type == 4:
        mem_size *= 2
    elif elem_type == 5:
        mem_size *= 2
    elif elem_type == 6:
        mem_size *= 4
    elif elem_type == 7:
        mem_size *= 8
    elif elem_type == 9:
        mem_size *= 1
    elif elem_type == 10:
        mem_size *= 2
    elif elem_type == 11:
        mem_size *= 8
    elif elem_type == 12:
        mem_size *= 4
    elif elem_type == 13:
        mem_size *= 8
    elif elem_type == 14:
        mem_size *= 8
    elif elem_type == 15:
        mem_size *= 16
    else:
        print("Undefined data type")

    return mem_size

def get_bandwidth(graph: xpb2.GraphProto):
    try:
        mem_BW_list = []
        total_mem_BW = 0
        unknown_tensor_list = []
        # traverse all the nodes
        for nodeProto in graph.node:
            # init variables
            read_mem_BW_each_layer = 0
            write_mem_BW_each_layer = 0
            total_each_layer = 0
            # traverse all input tensor
            for input_name in nodeProto.input:
                # get the TensorProto/ValueInfoProto by searching its name
                proto, type_Num = get_valueproto_or_tensorproto_by_name(
                    input_name, graph)
                # parse the ValueInfoProto/TensorProto
                if proto:
                    if type_Num == 3:
                        dtype = getattr(proto, 'data_type', False)
                        # get the shape of the tensor
                        shape = getattr(proto, 'dims', [])
                    elif type_Num == 1 or type_Num == 2:
                        name, dtype, shape_str = _parse_element(proto)
                        shape_str = shape_str.strip('[]')
                        shape_str = shape_str.split(',')
                        shape = []
                        for dim in shape_str:
                            shape.append(int(dim))
                    else:
                        print(
                            '[ERROR MASSAGE] [get_info/mem_BW_without_buf] The Tensor: ',
                            input_name, ' is from a wrong list !')
                else:
                    print(
                        '[ERROR MASSAGE] [get_info/mem_BW_without_buf] The Tensor: ',
                        input_name, ' is no found !')
                    unknown_tensor_list.append(
                        (nodeProto.name, input_name, nodeProto.op_type))
                # calculate the tensor size in btye
                
                read_mem_BW_each_layer += cal_tensor_mem_size(dtype, shape)

            # traverse all output tensor
            for output_name in nodeProto.output:
                # get the TensorProto/ValueInfoProto by searching its name
                proto, type_Num = get_valueproto_or_tensorproto_by_name(
                    output_name, graph)
                # parse the ValueInfoProto
                if proto:
                    if type_Num == 2 or type_Num == 4:
                        # name, dtype, shape = utils._parse_ValueInfoProto(proto)
                        name, dtype, shape_str = _parse_element(proto)
                        shape_str = shape_str.strip('[]')
                        shape_str = shape_str.split(',')
                        shape = []
                        for dim in shape_str:
                            shape.append(int(dim))
                    else:
                        print(
                            '[ERROR MASSAGE] [get_info/mem_BW_without_buf] The Tensor: ',
                            output_name, ' is from a wrong list !')
                else:
                    print(
                        '[ERROR MASSAGE] [get_info/mem_BW_without_buf] The Tensor: ',
                        input_name, ' is no found !')
                    unknown_tensor_list.append(
                        (nodeProto.name, output_name, nodeProto.op_type))
                # calculate the tensor size in btye
                write_mem_BW_each_layer += cal_tensor_mem_size(dtype, shape)

            # cal total bw
            total_each_layer = read_mem_BW_each_layer + write_mem_BW_each_layer

            # store into tuple
            temp_tuple = (nodeProto.name, read_mem_BW_each_layer,
                        write_mem_BW_each_layer, total_each_layer)
            #append it
            mem_BW_list.append(temp_tuple)
            # accmulate the value
            total_mem_BW += total_each_layer

        # display the mem_bw of eahc layer
        columns = ['layer', 'read_bw', 'write_bw', 'total_bw']
        # resort the list
        mem_BW_list = sorted(mem_BW_list,
                             key=lambda Layer: Layer[1],
                             reverse=True)
        print(tabulate(mem_BW_list, headers=columns))
        print(
            '====================================================================================\n'
        )
        # display it
        print(
            "The memory bandwidth for processor to execute a whole model without on-chip-buffer is: \n",
            total_mem_BW, '(bytes)\n',
            float(total_mem_BW) / float(1000000), '(MB)\n')
        # display the unknown tensor
        columns = ['op_name', 'unfound_tensor', 'op_type']
        print(tabulate(unknown_tensor_list, headers=columns))
        print(
            '====================================================================================\n'
        )
    except Exception as e:
        print("[ERROR MASSAGE] Unable to display: " + str(e))
        return False

    return True

#從這裡開始
print("start")
get_bandwidth(inferred_model.graph)
```

#### Execution Result
![](https://course.playlab.tw/md/uploads/f353c8d0-81e3-4fb3-9060-7394c0ca92d6.png)


### 2-2-3. activation memory storage requirement
#### Code
```python=
for node in inferred_model.graph.input:
    names = node.name.split('/')
    node_name = node.name
    dim_values = [dim.dim_value for dim in node.type.tensor_type.shape.dim if dim.dim_value is not None]
    # Print the size of each node
    activations = 1
    for dim in dim_values:
        activations *= dim
    print(f"'{names[-1]}': {dim_values}, activations = {activations}")
    
for node in inferred_model.graph.value_info:
    names = node.name.split('/')
    node_name = node.name
    dim_values = [dim.dim_value for dim in node.type.tensor_type.shape.dim if dim.dim_value is not None]
    # Print the size of each node
    activations = 1
    for dim in dim_values:
        activations *= dim
    print(f"'{names[-1]}': {dim_values}, activations = {activations}")

for node in inferred_model.graph.output:
    names = node.name.split('/')
    node_name = node.name
    dim_values = [dim.dim_value for dim in node.type.tensor_type.shape.dim if dim.dim_value is not None]
    # Print the size of each node
    activations = 1
    for dim in dim_values:
        activations *= dim
    print(f"'{names[-1]}': {dim_values}, activations = {activations}")
```

#### Execution Result

![](https://course.playlab.tw/md/uploads/b06da97c-f5d7-4637-b2cf-0d9bc12adccb.png)


## HW 2-3 Build tool scripts to manipulate an ONNX model graph

### 2-3-1. create a subgraph (1) that consist of a single Linear layer of size MxKxN

#### Code
```python=
M, N, K = 128, 128, 128
graph_def = helper.make_graph(
    nodes=[],
    name='GEMM',
    inputs=[
        helper.make_tensor_value_info('A', onnx.TensorProto.FLOAT, [M, N]),
        helper.make_tensor_value_info('B', onnx.TensorProto.FLOAT, [N, K]),
        helper.make_tensor_value_info('C', onnx.TensorProto.FLOAT, [K])
    ],
    outputs=[
        helper.make_tensor_value_info('OUT', onnx.TensorProto.FLOAT, [M, K])
    ],
)

gemm = helper.make_node(
    op_type='GEMM',
    inputs=['A', 'B', 'C'],
    outputs=['OUT'],
)
graph_def.node.extend([gemm])

# Create the ONNX model
model_def = helper.make_model(graph_def, producer_name='simple_gemm')
```

#### Visualize the subgraph (1)
![](https://course.playlab.tw/md/uploads/5c7d2f38-4ab6-42ae-b838-10d063c966b8.png)


### 2-3-2. create a subgraph (2) as shown in the above diagram for the subgraph (1)  

#### Code
```python=
def Build_GEMM(M, N, K, Block_Size, model_name, input_A_name, input_B_name, input_C_name, output_name):
    M_blocks = (M + Block_Size - 1) // Block_Size
    N_blocks = (N + Block_Size - 1) // Block_Size
    K_blocks = (K + Block_Size - 1) // Block_Size
    
    graph_def = helper.make_graph(
        nodes=[],
        name='my_model',
        inputs=[
            helper.make_tensor_value_info(input_A_name, onnx.TensorProto.FLOAT, [M, N]),
            helper.make_tensor_value_info(input_B_name, onnx.TensorProto.FLOAT, [N, K]),
            helper.make_tensor_value_info(input_C_name, onnx.TensorProto.FLOAT, [K])
        ],
        outputs=[
            helper.make_tensor_value_info(output_name, onnx.TensorProto.FLOAT, [M, K])
        ],
    )

    # Split along axis 1
    split_A_names1 = [f'A{i}' for i in range(M_blocks)]
    split_A_row = helper.make_node(
        op_type='Split',
        inputs=[input_A_name],
        outputs=split_A_names1,
        axis=0,
        num_outputs=M_blocks
    )
    graph_def.node.extend([split_A_row])
    
    # Split along axis 2
    for i in range(M_blocks):
        split_A_names2 = [f'A{i}/{j}' for j in range(N_blocks)]
        split_A_col = helper.make_node(
            op_type='Split',
            inputs=[split_A_names1[i]],
            outputs=split_A_names2,
            axis=1,
            num_outputs=N_blocks
        )
        graph_def.node.extend([split_A_col])
    
    # Split along axis 1
    split_B_names1 = [f'B{i}' for i in range(N_blocks)]
    split_B_row = helper.make_node(
        op_type='Split',
        inputs=[input_B_name],
        outputs=split_B_names1,
        axis=0,
        num_outputs=N_blocks
    )
    graph_def.node.extend([split_B_row])
    
    # Split along axis 2
    for i in range(N_blocks):
        split_B_names2 = [f'B{i}/{j}' for j in range(K_blocks)]
        split_B_col = helper.make_node(
            op_type='Split',
            inputs=[split_B_names1[i]],
            outputs=split_B_names2,
            axis=1,
            num_outputs=K_blocks
        )
        graph_def.node.extend([split_B_col])
    
    # Loop through matrix multiplication
    for i in range(M_blocks):
        for j in range(K_blocks):
            for k in range(N_blocks):
                mul_node = helper.make_node('MatMul', [f'A{i}/{k}', f'B{k}/{j}'], [f'C{i}/{j}/{k}'])
                graph_def.node.extend([mul_node])
    
    # Add matrix multiplication result
    for i in range(M_blocks):
        for j in range(K_blocks):
            if (N_blocks == 1):
                copy_node = helper.make_node('Identity', inputs=[f'C{i}/{j}/{0}'], outputs=[f'C{i}/{j}'])
                graph_def.node.extend([copy_node])
            else:
                name_list = [f'C{i}/{j}/{k}' for k in range(N_blocks)]
                count = 0
                while (len(name_list) > 2):
                    add_node = helper.make_node('Add', inputs=[name_list[0], name_list[1]], outputs=[f'temp{i}/{j}/{count}'])
                    graph_def.node.extend([add_node])
                    name_list.pop(0)
                    name_list.pop(0)
                    name_list.append(f'temp{i}/{j}/{count}')
                    count += 1
                add_node = helper.make_node('Add', inputs=[name_list[0], name_list[1]], outputs=[f'C{i}/{j}'])
                graph_def.node.extend([add_node])

    for i in range(M_blocks):
        concat_node = helper.make_node('Concat', inputs=[f'C{i}/{j}' for j in range(K_blocks)], outputs=[f'C{i}'], axis=1)
        graph_def.node.extend([concat_node])
    
    concat_node = helper.make_node('Concat', inputs=[f'C{i}' for i in range(M_blocks)], outputs=['output_wt_bias'], axis=0)
    graph_def.node.extend([concat_node])

    add_node = helper.make_node('Add', inputs=['output_wt_bias', input_C_name], outputs=[output_name])
    graph_def.node.extend([add_node])
    # Create the ONNX model
    model_def = helper.make_model(graph_def, producer_name=model_name)
    
    # Save the ONNX model to a file
    onnx.save(model_def, './models/' + model_name + '.onnx')
    pass

M, N, K, BLOCK_SIZE = 128, 128, 128, 64
Build_GEMM(M, N, K, BLOCK_SIZE, 'my_model', 'A', 'B', 'C', 'OUT')
```

#### Visualize the subgraph (2)
![](https://course.playlab.tw/md/uploads/9845afd8-2a32-4ff3-b077-d383753633c2.png)



### 2-3-3. replace the `Linear` layers in the AlexNet with the equivalent subgraphs (2)
#### Code
```python=
def Build_GEMM2(M, N, K, Block_Size, index, input_A_name, input_B_name, input_C_name, output_name, graph_def):
    M_blocks = (M + Block_Size - 1) // Block_Size
    N_blocks = (N + Block_Size - 1) // Block_Size
    K_blocks = (K + Block_Size - 1) // Block_Size
    
    # Split along axis 1
    split_A_names1 = [f'{index} A{i}' for i in range(M_blocks)]
    split_A_row = helper.make_node(
        op_type='Split',
        inputs=[input_A_name],
        outputs=split_A_names1,
        axis=0,
        num_outputs=M_blocks
    )
    graph_def.node.extend([split_A_row])

    # Split along axis 2
    for i in range(M_blocks):
        split_A_names2 = [f'{index} A{i}/{j}' for j in range(N_blocks)]
        split_A_col = helper.make_node(
            op_type='Split',
            inputs=[split_A_names1[i]],
            outputs=split_A_names2,
            axis=1,
            num_outputs=N_blocks
        )
        graph_def.node.extend([split_A_col])
    
    # Create the Split nodes
    Trans = helper.make_node(
        'Transpose',
        inputs=[input_B_name],
        outputs=[f'{index} BT'],
    )
    graph_def.node.extend([Trans])
    
    # Split along axis 1
    split_B_names1 = [f'{index} B{i}' for i in range(N_blocks)]
    split_B_row = helper.make_node(
        op_type='Split',
        inputs=[f'{index} BT'],
        outputs=split_B_names1,
        axis=0,
        num_outputs=N_blocks
    )
    graph_def.node.extend([split_B_row])
    
    # Split along axis 2
    for i in range(N_blocks):
        split_B_names2 = [f'{index} B{i}/{j}' for j in range(K_blocks)]
        split_B_col = helper.make_node(
            op_type='Split',
            inputs=[split_B_names1[i]],
            outputs=split_B_names2,
            axis=1,
            num_outputs=K_blocks
        )
        graph_def.node.extend([split_B_col])
    
    # Loop through matrix multiplication
    for i in range(M_blocks):
        for j in range(K_blocks):
            for k in range(N_blocks):
                mul_node = helper.make_node('MatMul', [f'{index} A{i}/{k}', f'{index} B{k}/{j}'], [f'{index} C{i}/{j}/{k}'])
                graph_def.node.extend([mul_node])
    
    # Add matrix multiplication result
    for i in range(M_blocks):
        for j in range(K_blocks):
            if (N_blocks == 1):
                copy_node = helper.make_node('Identity', inputs=[f'{index} C{i}/{j}/{0}'], outputs=[f'{index} C{i}/{j}'])
                graph_def.node.extend([copy_node])
            else:
                name_list = [f'{index} C{i}/{j}/{k}' for k in range(N_blocks)]
                count = 0
                while (len(name_list) > 2):
                    add_node = helper.make_node('Add', inputs=[name_list[0], name_list[1]], outputs=[f'{index} temp{i}/{j}/{count}'])
                    graph_def.node.extend([add_node])
                    name_list.pop(0)
                    name_list.pop(0)
                    name_list.append(f'{index} temp{i}/{j}/{count}')
                    count += 1
                add_node = helper.make_node('Add', inputs=[name_list[0], name_list[1]], outputs=[f'{index} C{i}/{j}'])
                graph_def.node.extend([add_node])
                
    for i in range(M_blocks):
        concat_node = helper.make_node('Concat', inputs=[f'{index} C{i}/{j}' for j in range(K_blocks)], outputs=[f'{index} C{i}'], axis=1)
        graph_def.node.extend([concat_node])
    
    concat_node = helper.make_node('Concat', inputs=[f'{index} C{i}' for i in range(M_blocks)], outputs=[f'{index} output_wt_bias'], axis=0)
    graph_def.node.extend([concat_node])

    add_node = helper.make_node('Add', inputs=[f'{index} output_wt_bias', input_C_name], outputs=[output_name])
    graph_def.node.extend([add_node])
    pass
    
my_alexnet = onnx.helper.make_graph(
    nodes=[],
    name="my_alexnet",
    inputs=alexnet_model.graph.input,
    outputs=alexnet_model.graph.output,
    initializer=alexnet_model.graph.initializer,
)

Block_Size = 64

for node in alexnet_model.graph.node:
    if node.name == GEMM_Layer_names[0]:
        M, N, K, BLOCK_SIZE = 1, 9216, 4096, Block_Size
        input1, input2, input3 = GEMM_Layer_input_names[0]
        output = GEMM_Layer_output_names[0][0]
        Build_GEMM2(M, N, K, BLOCK_SIZE, 0, input1, input2, input3, output, my_alexnet)
    
    elif node.name == GEMM_Layer_names[1]:
        M, N, K, BLOCK_SIZE = 1, 4096, 4096, Block_Size
        input1, input2, input3 = GEMM_Layer_input_names[1]
        output = GEMM_Layer_output_names[1][0]
        Build_GEMM2(M, N, K, BLOCK_SIZE, 1, input1, input2, input3, output, my_alexnet)
       
    elif node.name == GEMM_Layer_names[2]:
        M, N, K, BLOCK_SIZE = 1, 4096, 1000, Block_Size
        input1, input2, input3 = GEMM_Layer_input_names[2]
        output = GEMM_Layer_output_names[2][0]
        Build_GEMM2(M, N, K, BLOCK_SIZE, 2, input1, input2, input3, output, my_alexnet)
   
    else:
        my_alexnet.node.extend([node])

# Create a new model with the modified graph
modified_model = onnx.helper.make_model(my_alexnet)
modified_model_path = "./models/modified_alexnet.onnx"
onnx.save(modified_model, modified_model_path)

```

#### Visualize the transformed model graph
Set Block_Size = 2048 for visulization, or the graph will be to large
![](https://course.playlab.tw/md/uploads/ca61eac3-4e48-4781-8945-212e26ba1d97.png)
![](https://course.playlab.tw/md/uploads/ca84615a-48db-4a8c-9797-ea90b6de9e9a.png)



### 2-3-4. Correctness Verification
#### Code
```python=
modified_alexnet = onnx.load("./models/modified_alexnet.onnx", load_external_data=False)
onnx.checker.check_model(modified_alexnet)

onnx_session = ort.InferenceSession("./models/alexnet.onnx")
mod_onnx_session = ort.InferenceSession("./models/modified_alexnet.onnx")

input_name = onnx_session.get_inputs()[0].name
input = np.random.rand(1, 3, 224, 224).astype(np.float32)

onnx_output = onnx_session.run(None, {input_name: input})
mod_onnx_output = mod_onnx_session.run(None, {input_name: input})
print(np.allclose(np.array(mod_onnx_output), np.array(onnx_output), atol = 1e-6))
print(np.array(mod_onnx_output).shape)
```

#### Execution Result
True
(1, 1, 1000)


## HW 2-4 Using Pytorch C++ API to do model analysis on the transformed model graph

### 2-4-1. Calculate memory requirements for storing the model weights.

#### Code
```c++=
size_t param_size = 0;
for (const auto& p : module.parameters()) {
    param_size += p.numel() * p.element_size();
}
cout << "Total memory for parameters: " << param_size << endl << endl;
```

#### Execution Result
Total memory for parameters: 244403360


### 2-4-2. Calculate memory requirements for storing the activations

#### Code
```c++=
vector<string> layer_names;
layer_names.push_back("input");
for (const auto& s : module.named_children()) {
     layer_names.push_back(s.name);
}
vector<vector<int>> layer_output_shapes;
layer_output_shapes.push_back({1, 3, 224, 224});

vector<torch::jit::IValue> input;	
input.push_back(torch::ones({1, 3, 224, 224}));

for (const auto& subm : module.children()) {
    auto& mutable_subm = const_cast<torch::jit::Module&>(subm);
    at::Tensor output = mutable_subm.forward(input).toTensor();
    //cout << output.sizes() << endl;

    vector<int> temp(output.sizes().begin(), output.sizes().end());
    layer_output_shapes.push_back(temp);
    input[0] = output;
}

for (int i = 0; i < layer_names.size(); i++) {
    int activation = 1;
    for (const int& dim : layer_output_shapes[i]) activation *= dim;
    cout << layer_names[i] << " / shape:" << layer_output_shapes[i] << " / activations = " << activation << endl;
}
cout << endl;
```

#### Execution Result
![](https://course.playlab.tw/md/uploads/8ea4139d-aa49-4b45-a21d-e1f63fc402f1.png)


### 2-4-3. Calculate computation requirements

#### Code
```c++=
unsigned long long MAC = 0;
    
    // Caculate Modified Linear Layer Macs
    // Matmul Number x (1 x 64 x 64)
    int id = 0;
    for (const auto& subm : module.children()) {
        if(Contain_Substr(layer_names[id+1], "linear")) {
            auto& mutable_subm = const_cast<torch::jit::Module&>(subm);
            string temp = mutable_subm.dump_to_str(true,false,false);
            //cout << temp << endl;
            unsigned long long matmul_num = Count_Substr(temp, "aten::matmul");
            MAC += matmul_num * 1 * 64 * 64;
        }
        id++;
    }
    cout << "Linear MACs = " << MAC << endl;

    // Extract Conv Informations
    // conv_shapes[i] = {kernel channel, kernel depth, kernel height, kernel width}
    vector<vector<int>> conv_shapes;
    for (const auto& p : module.named_attributes()) {
        if(Contain_Substr(p.name, "conv") && Contain_Substr(p.name, "weight")) {
            vector<int> temp(p.value.toTensor().sizes().begin(), p.value.toTensor().sizes().end());
            cout << p.name << " : " << temp << endl;
            conv_shapes.push_back(temp);
        }
    }

    // Caculate Conv Layer Macs
    // Equal to (kernel size) x (Output Shape)
    int j = 0;
    for (int i = 0; i < layer_names.size(); i++) {
        if(Contain_Substr(layer_names[i], "conv")) {
            MAC += layer_output_shapes[i][1] * layer_output_shapes[i][2] * layer_output_shapes[i][3]
                    * conv_shapes[j][1] * conv_shapes[j][2] * conv_shapes[j][3];
            j++;
        } 
    }
    cout << "Total MACs = " << MAC << endl;
```

#### Execution Result
Part3
Linear MACs = 58720256
conv1.weight : 64 3 11 11
conv2.weight : 192 64 5 5
conv3.weight : 384 192 3 3
conv4.weight : 256 384 3 3
conv5.weight : 256 256 3 3
Total MACs = 714286784


### 2-4-4. Compare your results to the result in HW2-1 and HW2-2

#### Discussion
c++ pytorch api 我認為沒有很方便，在提取每一層layer資訊時，格式不如pytorch整齊，功能也沒pytorch多，也沒看到能像onnx能traverse每個node的功能(ex. torch.matmul, torch.flatten), 他好像只能掃到torch.nn的layer，要找到非torch.nn的只能靠method，但output格式也不友好。

至於修改版的linear layer，MAC數並沒有比較少，因為(MxNxK) = (M/B)x(N/B)x(K/B) x B^3，要選擇用哪一種就是看硬體限制
