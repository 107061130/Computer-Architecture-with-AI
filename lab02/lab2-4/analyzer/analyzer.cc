#include <torch/script.h>
#include <torch/torch.h>
#include <iostream>
#include <memory>
#include <torch/csrc/jit/api/module.h>
#include <torch/csrc/jit/jit_log.h>
#include <torch/csrc/jit/ir/ir.h>
#include <c10/util/ArrayRef.h>
using namespace std;

bool Contain_Substr(const string& str, const string& substr) {
    return str.find(substr) != string::npos;
}

int Count_Substr(const string& str, const string& substr) {
    int count = 0;
    size_t pos = str.find(substr, 0);

    while (pos != string::npos) {
        count++;
        pos = str.find(substr, pos + substr.length());
    }
    return count;
}

int main(int argc, const char* argv[]) {
    if (argc != 2) {
        std::cerr << "usage: example-app <path-to-exported-script-module>\n";
        return -1;
    } 
    
    torch::jit::script::Module module;
    try {
        // Deserialize the ScriptModule from a file using torch::jit::load().
        module = torch::jit::load(argv[1]);
    }
    catch (const c10::Error& e) {
        std::cerr << "error loading the model\n";
        return -1;
    }
    std::cout << "Loaded the torchscript model, " + std::string(argv[1]) + ", successfully\n";

    cout << "Part1" << endl;
    // part1
    size_t param_size = 0;
    for (const auto& p : module.parameters()) {
        param_size += p.numel() * p.element_size();
    }
    cout << "Total memory for parameters: " << param_size << endl << endl;
    
    cout << "Part2" << endl;
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
    
    cout << "Part3" << endl;  
    
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
            conv_shapes.push_back(temp);
        }
    }

    // Caculate Conv Layer Macs
    // Equal to (kernel size) x (Output Shape)
    int j = 0;
    for (int i = 0; i < layer_names.size(); i++) {
        if(Contain_Substr(layer_names[i], "conv")) {
            MAC += layer_output_shapes[i][1] * layer_output_shapes[i][2] * layer_output_shapes[i][3] * conv_shapes[j][1]
                    * conv_shapes[j][1] * conv_shapes[j][2] * conv_shapes[j][3];
            j++;
        } 
    }
    cout << "Total MACs = " << MAC << endl;
    
    return 0;
}
