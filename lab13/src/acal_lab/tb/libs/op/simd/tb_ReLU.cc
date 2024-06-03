#include "acal_lab/tb/includes/op/simd/tb_ReLU.h"
#include "perf.h"

#define TB_SIZE 100
namespace acal_lab {
namespace tb {

bool tb_ReLU() {
	int    correct_cnt                                = 0;
	int8_t ipt[RELU_dimC * RELU_dimH * RELU_dimW]     = {0};
	int8_t opt[RELU_dimC * RELU_dimH * RELU_dimW]     = {0};
	int8_t optTest[RELU_dimC * RELU_dimH * RELU_dimW] = {0};
	unsigned int start, end;

	tensorInfo data_0      = {.N = 1, .C = RELU_dimC, .H = RELU_dimH, .W = RELU_dimW, .data = ipt};
	tensorInfo data_1      = {.N = 1, .C = RELU_dimC, .H = RELU_dimH, .W = RELU_dimW, .data = opt};
	tensorInfo data_1_Test = {.N = 1, .C = RELU_dimC, .H = RELU_dimH, .W = RELU_dimW, .data = optTest};

	int tb_idx = TB_SIZE;
	while (tb_idx--) {
		randomInit8(&data_0);

		start = perf_get_mcycle();
		simd::ReLU(&data_1, &data_0, GENERAL).execute();
		end = perf_get_mcycle();
		if (tb_idx == 0) printf("SIMD Cycles   =  %u\n", end - start);

		start = perf_get_mcycle();
		scalar::ReLU(&data_1_Test, &data_0, GENERAL).execute();
		end = perf_get_mcycle();
		if (tb_idx == 0) printf("SCALAR Cycles =  %u\n", end - start);

		correct_cnt += compare8(&data_1, &data_1_Test);
	}

	printf("[ TEST ] `ReLU`  :                                    %3d/%3d\n", correct_cnt, TB_SIZE);

	return correct_cnt == TB_SIZE ? true : false;
}

}  // namespace tb
}  // namespace acal_lab
