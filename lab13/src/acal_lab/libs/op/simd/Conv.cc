#include "acal_lab/includes/op/simd/Conv.h"
#include <stdio.h>
#include <stdlib.h>

namespace acal_lab {
namespace simd {

inline void sPMULI8I16S_vv(int16_t c[4], int8_t a[4], int8_t b[4]) {
	sPMULI8I16S_vv_L(c, a, b);
	sPMULI8I16S_vv_H(c + 2, a, b);
}

void Conv::execPerLayerNaiveQuant() {
	/********************************************************************
	 * TODO:                                                            *
	 * For Homework 13.4, your task is to implement CONV with per Layer *
	 * `Naive Quantization`. This involves using the instruction        *
	 * `sPMULI8I16S(.vv/.vx)` to generate int16 output. However, the    *
	 * int16 output needs to be converted to int8 and then forwarded    *
	 * to the next Operator. To achieve this, utilize `sQNTI16I8S.vv.NQ`*
	 * for the conversion.                                              *
	 *******************************************************************/
}

void Conv::execPerLayerAdvanceQuant() {
	/***********************************************************
	 * TODO:                                                   *
	 * For Homework 13.4, implement CONV with per Operation    *
	 * `Advance Quantization`. Update instruction in the       *
	 * `void acal_lab::Conv::execPerLayerNaiveQuant()`         *
	 * function from `sQNTI16I8S.vv.NQ` to `sQNTI16I8S.vv.AQ`. *
	 **********************************************************/
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

void Conv::execPerOperationNaiveQuant() {
	/********************************************************
	 * TODO:                                                *
	 * For Homework 13.3, implement CONV with per Operation *
	 * Naive Quantization. Utilize `sAMULI8I8S(.vv/.vx).NQ` *
	 * to generate int8 output.                             *
	 *******************************************************/

}

void Conv::execPerOperationAdvanceQuant() {
	/********************************************************
	 * TODO:                                                *
	 * For Homework 8.4, implement CONV with per Operation  *
	 * Advance Quantization. Update instruction in the      *
	 * `void acal_lab::Conv::execPerOperationNaiveQuant()`  *
	 * function from `sAMULI8I8S(.vv/.vx).NQ` to            *
	 * `sAMULI8I8S(.vv/.vx).AQ`.                            *
	 *******************************************************/
}

}  // namespace simd
}  // namespace acal_lab
