#include "acal_lab/includes/op/simd/MxPl.h"
#define max(a, b) a > b ? a : b
namespace acal_lab {
namespace simd {

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

}  // namespace simd
}  // namespace acal_lab
