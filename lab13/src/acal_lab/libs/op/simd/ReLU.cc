#include "acal_lab/includes/op/simd/ReLU.h"
namespace acal_lab {
namespace simd {

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
}  // namespace simd
}  // namespace acal_lab