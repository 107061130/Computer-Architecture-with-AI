---
title: ACAL 2024 Curriculum Lab 2 - Introduction to AI Models
robots: noindex, nofollow
---

# <center> ACAL 2024 Curriculum Lab 2 <br /><font color="blue">Introduction to AI Models </font></center>

###### tags: `AIAS Spring 2024`

[TOC]

## Overview


AI or Artificial Intelligence is a subfield within computer science associated with constructing machines that can simulate human intelligence.

An AI model is a program or algorithm that utilizes a set of data that enables it to recognize certain patterns. This allows it to reach a conclusion or make a prediction when provided with sufficient information.

This is especially useful for solving complex problems using huge amounts of data with high accuracy and minimum costs. To learn more about one of the most complex AI tasks, check out an [article about Pattern Recognition](https://viso.ai/deep-learning/pattern-recognition) from https://vision.ai.

### List of the Most Popular AI Models

In practice, there is no single AI model that can solve all problems. Although projects like [Google Pathways](https://blog.google/technology/ai/introducing-pathways-next-generation-ai-architecture/) intend to handle many tasks using a single architecture, it is still a long-term goal to achieve. Here is a list of popular AI models used in practice to solve domain-specific tasks in the contemporary computing systems. 

- AI Model #1: Linear Regression
- AI Model #2: Logistic Regression
- AI Model #3: K-nearest Neighbors
- AI Model #4: Decision Trees
- AI Model #5: Linear Discriminant Analysis
- AI Model #6: Naive Bayes
- AI Model #7: Support Vector Machines
- AI Model #8: Learning Vector Quantization
- AI Model #9: Random Forest
- AI Model #10: Deep Neural Networks


In our labs, we focus on the Deep Neural Networks in most cases. Deep Neural Networks or DNN, is an Artificial Neural Network (ANN) with multiple (hidden) layers between the input and output layers. Inspired by the neural network of the human brain, these are similarly based on interconnected units known as artificial neurons. Please refer to an article, [Deep Neural Network: The 3 Popular Types (MLP, CNN and RNN)](https://viso.ai/deep-learning/deep-neural-network-three-popular-types/) , to get a top-level idea about the DNN models. 


### AI Model Framework and Format
An artificial intelligence framework allows for easier and faster creation of AI applications. Many big companies create their own AI framework to facilitate the process of delveloping AI models and applications. The following diagram shows a list of popular AI frameworks available in the industry and academia. 

![](https://course.playlab.tw/md/uploads/308451ed-edfd-4808-bd0d-4b2fca42d814.png)


The following article talks about AI framework in more details. 
- [深度學習，從「框架」開始學起](https://makerpro.cc/2018/06/deep-learning-frameworks/)

#### Model format 

When we design an AI chip, the ultimate goal is to run AI model traing or inference on the chip. The inputs to the AI chip includes an AI model and the associated data. With so many AI frameworks available, AI models are represented in different formats and it creates complexity if a single chip wants to support AI models from different AI frameworks. We list three most popular AI model formats below and our project intends to support AI models in those three format if possible. 

- Tensorflow/Tensorflow lite 
    - [Model Garden for TensorFlow](https://github.com/tensorflow/models)
    - [Supported Select TensorFlow operators](https://www.tensorflow.org/lite/guide/op_select_allowlist)
- Pytorch
    - [Pytorch Model Zoo](https://pytorch.org/serve/model_zoo.html)
    - [Pytorch NN library](https://pytorch.org/docs/stable/nn.html)
- ONNX 
    - [ONNX model zoo](https://github.com/onnx/models)
    - [ONNX operator spec](https://github.com/onnx/onnx/blob/master/docs/Operators.md#Abs)
  
The last AI model format, ONNX,  does not belong to any AI framework. ONNX (Open Neural Network Exchange Format) is a format designed to represent any type of Machine Learning and Deep Learning model. The main goal of ONNX is to bring together all the different AI frameworks and make as easy as possible to make them communicate with each other in order to build better models which can be supported on any type of platform or hardware.

## Working Environment Setup

### Start the docker container 
- First, clone the project repository to your local machine:

```shell=
$ git clone https://course.playlab.tw/git/acal-curriculum/acal-curriculum-workspace.git
```
- Next, navigate into the project directory:

```shell=
$ cd acal-curriculum-workspace
```

- Finally, start the working environment:


```shell=
$ ./run start
```

### Conda virtual environement

Once you get into the docker container, you'll see the following welcome message:
```shell
    Welcome to use the Docker Workspace for ACAL Curriculum.
    You can type 'startup' anywhere in the container to see this message.

    All permanent projects are in the ~/projects directory.

weifen@ACAL-WORKSPACE-MAIN:~$ 
```
We use `Conda`, which is a multi-platform open-source package management system, to manage the required python package installation. You can type the following command to check which conda environment is avaialbe.
```shell=
$ conda env list
# conda environments:
#
base                     /opt/conda
python39                 /opt/conda/envs/python39
tensorflow               /opt/conda/envs/tensorflow
```
In order to do all the work in this lab, we have prepared the `tensorflow` environment for you. You may enter the `tensorflow` virtual environment using the following command:

```shell=
$ source activate tensorflow
(tensorflow) weifen@ACAL-WORKSPACE-MAIN:~$ 
```
You'll see the prompt is prefixed with the veritual environment name `(tensorflow)`

### Checkout the lab repository
- Check the upstream repository location and create your own private lab02 repository in the course GitLab sever, and make sure you have create your own `lab2` under your Gitlab account.

:::warning
- You may setup passwordless ssh login if you like. Please refer to [Use SSH keys to communicate with GitLab](https://docs.gitlab.com/ee/user/ssh.html)
- Also, if you would like to setup the SSH Key in our Container. Please refer to this [document](https://course.playlab.tw/md/CW_gy1XAR1GDPgo8KrkLgg#Set-up-the-SSH-Key) to set up the SSH Key in acal-curriculum workspace.
:::

```shell=
## setup Lab 2 Project
user@ACAL-WORKSPACE:~$ cd projects
user@ACAL-WORKSPACE:~/projects/$ git clone  ssh://git@course.playlab.tw:30022/acal-curriculum/lab02.git

## Do your lab and homework in the project folder
user@ACAL-WORKSPACE:~/projects/$ cd lab02
user@ACAL-WORKSPACE:~/projects/lab02/$ ls

## show the remote repositories 
$  git remote -v
origin	ssh://git@course.playlab.tw:30022/acal-curriculum/lab02.git (fetch)
origin	ssh://git@course.playlab.tw:30022/acal-curriculum/lab02.git (push)

## add your private upstream repositories
$  git remote add gitlab ssh://git@course.playlab.tw:30022/<your ldap name>/lab02.git

$  git remote -v
gitlab	ssh://git@course.playlab.tw:30022/<your ldap name>/lab02.git (fetch)
gitlab	ssh://git@course.playlab.tw:30022/<your ldap name>/lab02.git (push)
origin	ssh://git@course.playlab.tw:30022/acal-curriculum/lab02.git (fetch)
origin	ssh://git@course.playlab.tw:30022/acal-curriculum/lab02.git (push)
```
- When you are done with your code, you have to push your code back to your own gitlab account with the following command :
```shell=
## the first time
$  git push --set-upstream gitlab main
## after the first time
$  git fetch origin main
## remember to solve conflicts
$  git merge origin/main
## then push back to your own repo
$  git push gitlab main
```

### Start the Jupyter Notebook Server

When you are doing lab2, TA prepare an example code in a Jupyter notebook file. The filename has a subffix `*.ipynb`. You can either bring up a Jupyter notebook locally to run the file or copy and paste the code to a python file on your own. 

In order to bring up the Jupyter notebook server, you can use the following commands:

```shell=
## Make sure you are in the tensorflow cond evnironemnt
$ source activate tensorflow 

## Start the Jupyter Notebook Server at default port 8888
$ run-jupyter

## You need to open your brownser and go to the following
## URL: http://localhost:8888. 
## You might need to type the following command to get the token
$ jupyter server list

## stop jupyter notebook server
$ jupyter server stop 8888
```
The Jupyter server running log are saved in the `jupyter.stderr.log` and `jupyter.stdout.log` files. 

Bring up your Jupyter Notebook URL, http://localhost:8888,   with your browser. 

## Lab 2-1. Model Format Conversion and Analysis Using Pytorch Tools


### Lab 2-1-1. Converting a PyTorch model into an ONNX model

- Prerequisites
    ```shell=
    $ cd ~/projects/lab02/lab2-1
    $ mkdir models
    ```
    - Let's download a MobileNet model for later experiments. 
    ```shell=
    $ cd lab2-1/models
    $ wget https://storage.googleapis.com/download.tensorflow.org/models/tflite/mobilenet_v1_1.0_224_quant_and_labels.zip
    $ sudo apt-get install unzip
    $ unzip mobilenet_v1_1.0_224_quant_and_labels.zip
    ```

    - Install Pytorch and Onnx 
        - you may skip this step since they are already installed in workspace docker
    ```shell=
    $ pip install torch torchvision torchaudio
    $ pip install onnx 
    ```
    
- Tutorial
    A simple example in PyTorch is available below. This simple example shows how to take a pre-trained PyTorch model (a weights object and network class object) and convert it to ONNX format (that contains the weights and net structure). 

    ```python=
    import torch
    import torchvision.models as models

    # Use an existing model from Torchvision, note it 
    # will download this if not already on your computer (might take time)
    model = models.alexnet(pretrained=True)

    # Create some sample input in the shape this model expects
    dummy_input = torch.randn(10, 3, 224, 224)

    # It's optional to label the input and output layers
    input_names = [ "actual_input_1" ] + [ "learned_%d" % i for i in range(16) ]
    output_names = [ "output1" ]

    # Use the exporter from torch to convert to onnx 
    # model (that has the weights and net arch)
    torch.onnx.export(model, dummy_input, "alexnet.onnx", verbose=True, input_names=input_names, output_names=output_names)
    ```
- How to run the python code in this lab?
    - Option 1. Save the code to a local python script and run it
    - Option 2. Run the *.ipynb file on the Jupyter Notebook server
     
     
### Lab 2-1-2. Converting a Tensorflow model into an ONNX model

Tensorflow uses several file formats to represent a model, such as checkpoint files, graph with weight(called frozen graph next) and saved_model, and it has APIs to generate these files. TensorFlow models (including keras and TFLite models) can be converted to ONNX using the Tensorflow-onnx tool. Tensorflow-onnx can accept all the three formats to represent a Tensorflow model, the format "saved_model" is typically the preference since it doesn't require the user to specify input and output names of graph. Another format, "tflite", is very popular as well. 

- Prerequisites 
    - **Install packages from scratch** (you may skip this step since they are already installed in workspace docker)
        - Install TensorFlow
        ```shell=
        $ pip install tensorflow 
        ```
        - Install Onnx Runtime
        ```shell=
        $ pip install onnxruntime
        ```

    - **Use the `acal-curriculum-workspace` docker container** - Try the following commands to activate the tensorflow virtual environemnt in the course docker container:
        ```shell=    
        ## activate the tensorflow virtual environment
        $ source activate tensorflow
        
        ## test whether tensorflow is avialble
        $ (tensorflow) $ python3 -c 'import tensorflow as tf; print(tf.__version__)'
        ```
        If everything works, you will see the following information:
        :::info
        2024-02-12 11:45:57.844263: I tensorflow/core/platform/cpu_feature_guard.cc:182] This TensorFlow binary is optimized to use available CPU instructions in performance-critical operations.
        To enable the following instructions: SSE4.1 SSE4.2 AVX AVX2 FMA, in other operations, rebuild TensorFlow with the appropriate compiler flags.
        2.12.0
        :::
        
- Getting Started
    :::warning
    If your notebook is MAC with M-series CPU, the tf2onnx package has no support for the CPU to our best knowledge. So you won't be able to run the code successfully in this section. It's not a big deal. Tensorflow used to be popular but most people tends to use pytorch models more due to its more user-friendly design. If you are a M1-series user, you can just skip this section. 
    :::
    - Install tf2onnx
    ```shell=
    ## to to projects folder in the docker container
    $ pip install tf2onnx
    ```
    - To get started with tensorflow-onnx, run the t2onnx.convert command, providing:
        - the path to your TensorFlow model (where the model is the specified model format)
        - a name for the ONNX output file
        ```shell=
        $ python3 -m tf2onnx.convert --saved-model <tensorflow_model_name> --output <onnx_model_name>
        ```

    - The above command uses a default of `9` for the ONNX opset. If you need a newer opset, or want to limit your model to use an older opset then you can provide the --opset argument to the command. If you are unsure about which opset to use, refer to the ONNX operator documentation. The --saved-model parameter can be replaced with `--tflite` or `--checkpoint` depending on the format used in the Tensorflow model. 
        ```shell=
        $ python -m tf2onnx.convert --saved-model <tensorflow_model_name> --opset 13 --output <onnx_model_name>
        ```
    - Let's use the `MobileNet` model to try the conversion. Note that the downloaded model is in the tflite format and it requires opset 10 for quantization. 
        ```shell=
        $ cd lab2-1/models
        $ python -m tf2onnx.convert --tflite ./mobilenet_v1_1.0_224_quant.tflite --output mobilenet_v1_1.0_224_quant.onnx --opset 10
        ```
    

### Lab 2-1-3. Model Analysis in Pytorch

To begin this lab, you'll first need to install PyTorch. PyTorch is a popular open-source machine learning library for Python, known for its flexibility and dynamic computational graph. It's widely used for deep learning and artificial intelligence applications.

You can download and install PyTorch directly from the [official PyTorch website](https://pytorch.org/). Follow the instructions on the website to ensure you install the version compatible with your system and other dependencies.

To install PyTorch along with its companion libraries, torchvision and torchaudio, you can use the following pip command:


```bash=
// you may skip this step since they are already installed in workspace docker
$ pip install torch torchvision torchaudio
```
- you may skip the above step since those packages are already installed in workspace docker

In PyTorch, AlexNet is a popular deep learning architecture commonly used for image recognition and classification. This model demonstrates the typical architecture and workload of a deep learning model.

```python=
import torchvision.models as models

# Using an existing model from Torchvision, it will download the model if not already on your computer
model = models.alexnet(pretrained=True)
print(model)
```

When you print the AlexNet model in PyTorch, it displays the architecture, including the arrangement of layers and their configurations like kernel sizes and number of filters:

```
AlexNet(
  (features): Sequential(
    (0): Conv2d(3, 64, kernel_size=(11, 11), stride=(4, 4), padding=(2, 2))
    (1): ReLU(inplace=True)
    (2): MaxPool2d(kernel_size=3, stride=2, padding=0, dilation=1, ceil_mode=False)
    (3): Conv2d(64, 192, kernel_size=(5, 5), stride=(1, 1), padding=(2, 2))
    (4): ReLU(inplace=True)
    (5): MaxPool2d(kernel_size=3, stride=2, padding=0, dilation=1, ceil_mode=False)
    (6): Conv2d(192, 384, kernel_size=(3, 3), stride=(1, 1), padding=(1, 1))
    (7): ReLU(inplace=True)
    (8): Conv2d(384, 256, kernel_size=(3, 3), stride=(1, 1), padding=(1, 1))
    (9): ReLU(inplace=True)
    (10): Conv2d(256, 256, kernel_size=(3, 3), stride=(1, 1), padding=(1, 1))
    (11): ReLU(inplace=True)
    (12): MaxPool2d(kernel_size=3, stride=2, padding=0, dilation=1, ceil_mode=False)
  )
  (avgpool): AdaptiveAvgPool2d(output_size=(6, 6))
  (classifier): Sequential(
    (0): Dropout(p=0.5, inplace=False)
    (1): Linear(in_features=9216, out_features=4096, bias=True)
    (2): ReLU(inplace=True)
    (3): Dropout(p=0.5, inplace=False)
    (4): Linear(in_features=4096, out_features=4096, bias=True)
    (5): ReLU(inplace=True)
    (6): Linear(in_features=4096, out_features=1000, bias=True)
  )
)
```


#### Get Model Parameter Size
This section focuses on the parameters size of the AlexNet model.

```python=
# Calculating the total number of parameters in the model
total_params = sum(p.numel() for p in model.parameters())
print("Total number of parameters: ", total_params)
```

The printed result will show you the cumulative count of all trainable parameters in the model, giving you an insight into the model's size and complexity:

```
Total number of parameters:  61100840
```

#### Get Memory Requirement
Memory requirements depend on the size and complexity of the model. For a deep learning model like AlexNet, memory needs are primarily for storing parameters and intermediate computation results.


```python=
# Calculating the size of the model's parameters in bytes
param_size = sum(p.numel() * p.element_size() for p in model.parameters())
print("Total memory for parameters: ", param_size)
```

This computation gives you an estimate of how much memory the model's parameters consume as below:
```
Total memory for parameters:  244403360
```

#### Print Pytorch Summary
You can get a summary similar to Keras' model summary, using the `pytorch-summary` package in PyTorch

```python=
from torchvision import models
from torchsummary import summary

model = models.alexnet(pretrained=True)
summary(model, (3, 224, 224))
```

In the "Print PyTorch Summary" section, using torchsummary, you get a concise overview of the AlexNet model's layers, their shapes, and the number of parameters, aiding in understanding the model's structure and complexity:

```
----------------------------------------------------------------
        Layer (type)               Output Shape         Param #
================================================================
            Conv2d-1           [-1, 64, 55, 55]          23,296
              ReLU-2           [-1, 64, 55, 55]               0
         MaxPool2d-3           [-1, 64, 27, 27]               0
            Conv2d-4          [-1, 192, 27, 27]         307,392
              ReLU-5          [-1, 192, 27, 27]               0
         MaxPool2d-6          [-1, 192, 13, 13]               0
            Conv2d-7          [-1, 384, 13, 13]         663,936
              ReLU-8          [-1, 384, 13, 13]               0
            Conv2d-9          [-1, 256, 13, 13]         884,992
             ReLU-10          [-1, 256, 13, 13]               0
           Conv2d-11          [-1, 256, 13, 13]         590,080
             ReLU-12          [-1, 256, 13, 13]               0
        MaxPool2d-13            [-1, 256, 6, 6]               0
AdaptiveAvgPool2d-14            [-1, 256, 6, 6]               0
          Dropout-15                 [-1, 9216]               0
           Linear-16                 [-1, 4096]      37,752,832
             ReLU-17                 [-1, 4096]               0
          Dropout-18                 [-1, 4096]               0
           Linear-19                 [-1, 4096]      16,781,312
             ReLU-20                 [-1, 4096]               0
           Linear-21                 [-1, 1000]       4,097,000
================================================================
Total params: 61,100,840
Trainable params: 61,100,840
Non-trainable params: 0
----------------------------------------------------------------
Input size (MB): 0.57
Forward/backward pass size (MB): 8.38
Params size (MB): 233.08
Estimated Total Size (MB): 242.03
----------------------------------------------------------------
```
#### Using torchInfo 

Torchinfo provides information complementary to what is provided by print(your_model) in PyTorch, similar to Tensorflow's `model.summary()` API to view the visualization of the model, which is helpful while debugging your network. 

You can install it using:

```bash=
$ pip install torchinfo
```
- you may skip the above step since it is already installed in workspace docker


This library also has a function named `summary`. But it comes with many more options, and that is what makes it better. The arguments are:

- **model** (`nn.module`): The neural network model.
- **input_size** (`Sequence of Sizes`): The size of the input tensor.
- **input_data** (`Sequence of Tensors`): Actual data to pass through the model (optional).
- **batch_dim** (`int`): The dimension used as the batch size.
- **cache_forward_pass** (`bool`): Whether to cache the forward pass.
- **col_names** (`Iterable[str]`): Column names for the summary output.
- **col_width** (`int`): The width of each column in the output.
- **depth** (`int`): Depth for nested summary.
- **device** (`torch.Device`): The device to run the model on.
- **dtypes** (`List[torch.dtype]`): Data types of the input tensor.
- **mode** (`str`): Mode for the summary.
- **row_settings** (`Iterable[str]`): Additional row settings.
- **verbose** (`int`): Verbosity level of the summary output.
- **\*\*kwargs**: Additional keyword arguments.


Using torchinfo.summary, we can get a lot of information by giving currently supported options as input for the argument col_names:

```python=
import torchinfo
torchinfo.summary(model, (3, 224, 224), batch_dim=0, col_names=("input_size", "output_size", "num_params", "kernel_size", "mult_adds"), verbose=0)
```

Change the verbose to 1 if not using Jupyter Notebook or Google Colab.

The output of the above code looks like this:

```
=====================================================================================================================================================================
Layer (type:depth-idx)                   Input Shape               Output Shape              Param #                   Kernel Shape              Mult-Adds
=====================================================================================================================================================================
VGG                                      [1, 3, 224, 224]          [1, 1000]                 --                        --                        --
├─Sequential: 1-1                        [1, 3, 224, 224]          [1, 512, 7, 7]            --                        --                        --
│    └─Conv2d: 2-1                       [1, 3, 224, 224]          [1, 64, 224, 224]         1,792                     [3, 3]                    89,915,392
│    └─ReLU: 2-2                         [1, 64, 224, 224]         [1, 64, 224, 224]         --                        --                        --
│    └─Conv2d: 2-3                       [1, 64, 224, 224]         [1, 64, 224, 224]         36,928                    [3, 3]                    1,852,899,328
│    └─ReLU: 2-4                         [1, 64, 224, 224]         [1, 64, 224, 224]         --                        --                        --
│    └─MaxPool2d: 2-5                    [1, 64, 224, 224]         [1, 64, 112, 112]         --                        2                         --
│    └─Conv2d: 2-6                       [1, 64, 112, 112]         [1, 128, 112, 112]        73,856                    [3, 3]                    926,449,664
│    └─ReLU: 2-7                         [1, 128, 112, 112]        [1, 128, 112, 112]        --                        --                        --
│    └─Conv2d: 2-8                       [1, 128, 112, 112]        [1, 128, 112, 112]        147,584                   [3, 3]                    1,851,293,696
│    └─ReLU: 2-9                         [1, 128, 112, 112]        [1, 128, 112, 112]        --                        --                        --
│    └─MaxPool2d: 2-10                   [1, 128, 112, 112]        [1, 128, 56, 56]          --                        2                         --
│    └─Conv2d: 2-11                      [1, 128, 56, 56]          [1, 256, 56, 56]          295,168                   [3, 3]                    925,646,848
│    └─ReLU: 2-12                        [1, 256, 56, 56]          [1, 256, 56, 56]          --                        --                        --
│    └─Conv2d: 2-13                      [1, 256, 56, 56]          [1, 256, 56, 56]          590,080                   [3, 3]                    1,850,490,880
│    └─ReLU: 2-14                        [1, 256, 56, 56]          [1, 256, 56, 56]          --                        --                        --
│    └─Conv2d: 2-15                      [1, 256, 56, 56]          [1, 256, 56, 56]          590,080                   [3, 3]                    1,850,490,880
│    └─ReLU: 2-16                        [1, 256, 56, 56]          [1, 256, 56, 56]          --                        --                        --
│    └─MaxPool2d: 2-17                   [1, 256, 56, 56]          [1, 256, 28, 28]          --                        2                         --
│    └─Conv2d: 2-18                      [1, 256, 28, 28]          [1, 512, 28, 28]          1,180,160                 [3, 3]                    925,245,440
│    └─ReLU: 2-19                        [1, 512, 28, 28]          [1, 512, 28, 28]          --                        --                        --
│    └─Conv2d: 2-20                      [1, 512, 28, 28]          [1, 512, 28, 28]          2,359,808                 [3, 3]                    1,850,089,472
│    └─ReLU: 2-21                        [1, 512, 28, 28]          [1, 512, 28, 28]          --                        --                        --
│    └─Conv2d: 2-22                      [1, 512, 28, 28]          [1, 512, 28, 28]          2,359,808                 [3, 3]                    1,850,089,472
│    └─ReLU: 2-23                        [1, 512, 28, 28]          [1, 512, 28, 28]          --                        --                        --
│    └─MaxPool2d: 2-24                   [1, 512, 28, 28]          [1, 512, 14, 14]          --                        2                         --
│    └─Conv2d: 2-25                      [1, 512, 14, 14]          [1, 512, 14, 14]          2,359,808                 [3, 3]                    462,522,368
│    └─ReLU: 2-26                        [1, 512, 14, 14]          [1, 512, 14, 14]          --                        --                        --
│    └─Conv2d: 2-27                      [1, 512, 14, 14]          [1, 512, 14, 14]          2,359,808                 [3, 3]                    462,522,368
│    └─ReLU: 2-28                        [1, 512, 14, 14]          [1, 512, 14, 14]          --                        --                        --
│    └─Conv2d: 2-29                      [1, 512, 14, 14]          [1, 512, 14, 14]          2,359,808                 [3, 3]                    462,522,368
│    └─ReLU: 2-30                        [1, 512, 14, 14]          [1, 512, 14, 14]          --                        --                        --
│    └─MaxPool2d: 2-31                   [1, 512, 14, 14]          [1, 512, 7, 7]            --                        2                         --
├─AdaptiveAvgPool2d: 1-2                 [1, 512, 7, 7]            [1, 512, 7, 7]            --                        --                        --
├─Sequential: 1-3                        [1, 25088]                [1, 1000]                 --                        --                        --
│    └─Linear: 2-32                      [1, 25088]                [1, 4096]                 102,764,544               --                        102,764,544
│    └─ReLU: 2-33                        [1, 4096]                 [1, 4096]                 --                        --                        --
│    └─Dropout: 2-34                     [1, 4096]                 [1, 4096]                 --                        --                        --
│    └─Linear: 2-35                      [1, 4096]                 [1, 4096]                 16,781,312                --                        16,781,312
│    └─ReLU: 2-36                        [1, 4096]                 [1, 4096]                 --                        --                        --
│    └─Dropout: 2-37                     [1, 4096]                 [1, 4096]                 --                        --                        --
│    └─Linear: 2-38                      [1, 4096]                 [1, 1000]                 4,097,000                 --                        4,097,000
=====================================================================================================================================================================
Total params: 138,357,544
Trainable params: 138,357,544
Non-trainable params: 0
Total mult-adds (G): 15.48
=====================================================================================================================================================================
Input size (MB): 0.60
Forward/backward pass size (MB): 108.45
Params size (MB): 553.43
Estimated Total Size (MB): 662.49
=====================================================================================================================================================================
```

In complex PyTorch models, especially those with multiple branches or distinct pathways, understanding the architecture and its parameter distribution can be challenging. torchinfo offers a comprehensive way to visualize and analyze such models.

Consider a model structured as follows, with several branches where each branch takes a different input:


```python=
import torchvision.models as models
import torch

class Model(torch.nn.Module):
    def __init__(self):
        super().__init__()
        # Initialize three instances of pretrained AlexNet
        self.alexnet1 = models.alexnet(pretrained=True)
        self.alexnet2 = models.alexnet(pretrained=True)
        self.alexnet3 = models.alexnet(pretrained=True)
    
    def forward(self, *x):
        # Ensure that the input is a tuple of three tensors
        if len(x) != 3:
            raise ValueError("Expected three input tensors")

        # Pass each tensor through the corresponding AlexNet model
        out1 = self.alexnet1(x[0])
        out2 = self.alexnet2(x[1])
        out3 = self.alexnet3(x[2])

        # Concatenate the outputs along the 0th dimension
        out = torch.cat([out1, out2, out3], dim=0)
        return out
```

Using torchinfo.summary, we can effectively dissect this complex structure. The following code:

```python=
import torchinfo
torchinfo.summary(Model(), [(3, 64, 64)]*3, batch_dim=0, col_names=("input_size", "output_size", "num_params", "kernel_size", "mult_adds"), verbose=0)
```

## Lab 2-2. Model Visualization - Using Netron

- Prerequisites
    ```shell=
    $ cd ~/projects/lab02/lab2-2
    $ mkdir models
    ```

###  Introduction to Netron
Netron is a powerful and versatile tool for visualizing machine learning models. It supports a wide range of model formats including TensorFlow, PyTorch, ONNX, Keras, and many others. Netron helps in understanding and interpreting the structure of neural networks by providing a graphical representation of the layers and connections within the model. This visualization can be invaluable for debugging, optimizing model architecture, and sharing model designs with others.

#### Installation and Usage
Netron can be installed and used in several ways:

- Desktop Application: Download and install Netron from [GitHub releases](https://github.com/lutzroeder/netron). It's available for Windows, macOS, and Linux.

- Web Application: Access Netron directly in your web browser without any installation at [Netron web app](https://netron.app/).

- Python Package: Install Netron as a Python package using pip:
    - you may skip the above step since it is already installed in workspace docker
    ```shell=
    pip install netron
    ```
    After installation, you can start Netron and open models directly from the command line:

    ```shell=
    netron [path to model file]
    ```

### Lab 2-2-1 Visualizing the LeNet Model
LeNet is an early and influential convolutional neural network model primarily used for image classification tasks like handwriting and character recognition.

To visualize a LeNet model using Netron:

#### Download the LeNet Model:
You can download a pre-trained LeNet model in ONNX format using the following command:

```
$ wget -P models https://github.com/ONNC/onnc-tutorial/raw/master/models/lenet/lenet.onnx 
```

#### Open the Model in Netron:
After installing Netron, open the downloaded lenet.onnx file. You will see a graphical representation of the LeNet model similar to the following image:

![](https://course.playlab.tw/md/uploads/50cf2422-39b3-4e9c-847b-aeb2d0fd1d24.png)


#### LeNet Model Visualization

When you click on a convolutional layer (Conv) node, Netron displays details such as the kernel size of the convolution, and the names of the input and output blobs. This visualization is crucial for understanding the architecture, including how data flows through layers and the role of each layer in processing the input.


![](https://course.playlab.tw/md/uploads/7d01668d-62c8-4789-ab56-9faa36832ad7.png)


### Lab 2-2-2 Visualizing the Transformer Model

Transformer models, known for their role in processing sequential data and extensively used in NLP, feature complex architectures with attention mechanisms and multiple layers.

To visualize a Transformer model like BERT:

#### Obtain the Model:
Download a pre-trained Transformer model. For example, you can use Hugging Face’s Transformers library to obtain a model. Here is a Python snippet to save a BERT model in ONNX format:

```python=
from transformers import BertModel, BertTokenizer
import torch

# Load pre-trained model and tokenizer
model = BertModel.from_pretrained('bert-base-uncased')
tokenizer = BertTokenizer.from_pretrained('bert-base-uncased')

# Model must be in evaluation mode for export
model.eval()

# Create dummy input for the model. It should match the input shape that the model expects.
# For BERT, typically this is a sequence of token IDs.
dummy_input = tokenizer.encode_plus("Hello, my dog is cute", 
                                    add_special_tokens=True, 
                                    max_length=512, 
                                    return_tensors="pt")

# Convert inputs to appropriate format for model
input_ids = dummy_input['input_ids']
attention_mask = dummy_input['attention_mask']

# Export the model to an ONNX file
torch.onnx.export(model, 
                  (input_ids, attention_mask), 
                  "models/transformer.onnx", 
                  opset_version=11, 
                  do_constant_folding=True, 
                  input_names=['input_ids', 'attention_mask'], 
                  output_names=['output'], 
                  dynamic_axes={'input_ids': {0: 'batch_size'}, 
                                'attention_mask': {0: 'batch_size'}, 
                                'output': {0: 'batch_size'}})
```


#### Visualize Using Netron:
Open the transformer.onnx file in Netron. You will be able to see the detailed structure of the Transformer model, including attention layers, linear layers, and the flow of data through these components. This visualization helps in understanding the complex interactions and the architecture of the Transformer model.


![](https://course.playlab.tw/md/uploads/412079ef-a2ec-4609-af57-69bdcf405035.png)


## Lab 2-3. Parse an AI Model to Extract Model Information

- Prerequisites - Download Lenet Models
    ```shell=
    $ cd ~/projects/lab02/lab2-3
    $ mkdir models
    $ wget https://github.com/ONNC/onnc-tutorial/raw/master/models/lenet/lenet.onnx 
    ```
### Introduction

Although a visualization tool like Netron can help us to view the details of a model, in many cases, we need to parse an AI model to extract information systematically. Many framework provides APIs for users to extract model information. The following reference gives you an overview of available Python APIs for the ONNX models.

- [ONNX Python API Overview](https://github.com/onnx/onnx/blob/main/docs/PythonAPIOverview.md)

The ONNX format stores an model graph as a protobuf structure and and it can be accessed using the [standard protobuf protocol APIs](https://developers.google.com/protocol-buffers/docs/reference/overview). [Protocol buffers](https://developers.google.com/protocol-buffers) are Google's language-neutral, platform-neutral, extensible mechanism for serializing structured data. You define how you want your data to be structured once, then you can use special generated source code to easily write and read your structured data to and from a variety of data streams and using a variety of languages. In this section, we will use the Python API to process an ONNX model as an example. 

```python=
import onnx

onnx_model = onnx.load('./lenet.onnx')

# The model is represented as a protobuf structure and it can be accessed
# using the standard python-for-protobuf methods

## list all the operator types in the model
node_list = []
count = []
for i in onnx_model.graph.node:
    if (i.op_type not in node_list):
        node_list.append(i.op_type)
        count.append(1)
    else:
        idx = node_list.index(i.op_type)
        count[idx] = count[idx]+1
print(node_list)
print(count)

```
In the above example, we first load the 'lenet.onnx' model downloaded in the earlier section. Then we traverse the nodes of the model graph to find all unique operators. In addition, we count the number of nodes of the same operator type in the graph. 

The computation graph of an ONNX model is described in the protobuf format. You may refer to an automatically-generated file, [onnx.proto](https://github.com/onnx/onnx/blob/main/onnx/onnx.proto) for the protobuf semantics. 

#### How to read the protobuf semantics in onnx.proto

ModelProto is a top-level file/container format for bundling a ML model. The semantics of the model are described by the GraphProto that represents a parameterized computation graph against a set of named operators that are defined independently from the graph.

The return object format of the onnx.load() function is in the ModelProto format. You may find the semantics definition of `ModelProto` in the [onnx.proto](https://github.com/onnx/onnx/blob/main/onnx/onnx.proto) file. You may also refer to the [Language Guide (proto3)](https://developers.google.com/protocol-buffers/docs/proto3) for the proto file syntax 

```proto=
// Models
//
// ModelProto is a top-level file/container format for bundling a ML model and
// associating its computation graph with metadata.
//
// The semantics of the model are described by the associated GraphProto's.
message ModelProto {
  // The version of the IR this model targets. See Version enum above.
  // This field MUST be present.
  optional int64 ir_version = 1;

  // The OperatorSets this model relies on.
  // All ModelProtos MUST have at least one entry that
  // specifies which version of the ONNX OperatorSet is
  // being imported.
  //
  // All nodes in the ModelProto's graph will bind against the operator
  // with the same-domain/same-op_type operator with the HIGHEST version
  // in the referenced operator sets.
  repeated OperatorSetIdProto opset_import = 8;

  // The name of the framework or tool used to generate this model.
  // This field SHOULD be present to indicate which implementation/tool/framework
  // emitted the model.
  optional string producer_name = 2;

  // The version of the framework or tool used to generate this model.
  // This field SHOULD be present to indicate which implementation/tool/framework
  // emitted the model.
  optional string producer_version = 3;

  // Domain name of the model.
  // We use reverse domain names as name space indicators. For example:
  // `com.facebook.fair` or `com.microsoft.cognitiveservices`
  //
  // Together with `model_version` and GraphProto.name, this forms the unique identity of
  // the graph.
  optional string domain = 4;

  // The version of the graph encoded. See Version enum below.
  optional int64 model_version = 5;

  // A human-readable documentation for this model. Markdown is allowed.
  optional string doc_string = 6;

  // The parameterized graph that is evaluated to execute the model.
  optional GraphProto graph = 7;

  // Named metadata values; keys should be distinct.
  repeated StringStringEntryProto metadata_props = 14;

  // Training-specific information. Sequentially executing all stored
  // `TrainingInfoProto.algorithm`s and assigning their outputs following
  // the corresponding `TrainingInfoProto.update_binding`s is one training
  // iteration. Similarly, to initialize the model
  // (as if training hasn't happened), the user should sequentially execute
  // all stored `TrainingInfoProto.initialization`s and assigns their outputs
  // using `TrainingInfoProto.initialization_binding`s.
  //
  // If this field is empty, the training behavior of the model is undefined.
  repeated TrainingInfoProto training_info = 20;

  // A list of function protos local to the model.
  //
  // Name of the function "FunctionProto.name" should be unique within the domain "FunctionProto.domain".
  // In case of any conflicts the behavior (whether the model local functions are given higher priority,
  // or standard opserator sets are given higher priotity or this is treated as error) is defined by 
  // the runtimes.
  // 
  // The operator sets imported by FunctionProto should be compatible with the ones
  // imported by ModelProto and other model local FunctionProtos. 
  // Example, if same operator set say 'A' is imported by a FunctionProto and ModelProto 
  // or by 2 FunctionProtos then versions for the operator set may be different but, 
  // the operator schema returned for op_type, domain, version combination
  // for both the versions should be same for every node in the function body.
  //
  // One FunctionProto can reference other FunctionProto in the model, however, recursive reference
  // is not allowed.
  repeated FunctionProto functions = 25;
};
```
Based on the semantics definition, you may figure out how to access each field in the ModelProto structure in python as below:

```python=
## find the IR version
print(onnx_model.ir_version)

## find the computation graph
print(onnx_model.graph)
```
The most important proto structure is GraphProto. It includes a lot of information in an AI model. Here are some examples:

```python=
## find the number of inputs
print(len(onnx_model.graph.input))

## find the number of nodes in the graph
print(len(onnx_model.graph.node))

```

### Lab 2-3-1. Extract Input Information From an ONNX Model
In this lab, we will write a small python script to extract some statistics in an AI model. Although we use an ONNX model in our example, the same methodogy can be applied to models in other formats with proper python library support. 

The following python script will print out information of all `Conv` operators
```python=
import onnx

onnx_model = onnx.load('./lenet.onnx')
for i in onnx_model.graph.node:
    if (i.op_type == 'Conv'):
        print(i)
```
The `lenet.onnx` model has 4 `Conv` operators as shown below:

```shell=
input: "import/Placeholder:0"
input: "import/conv1first/Variable:0"
output: "import/conv1first/Conv2D:0"
name: "import/conv1first/Conv2D"
op_type: "Conv"
attribute {
  name: "dilations"
  ints: 1
  ints: 1
  type: INTS
}
attribute {
  name: "strides"
  ints: 1
  ints: 1
  type: INTS
}
attribute {
  name: "kernel_shape"
  ints: 5
  ints: 5
  type: INTS
}
attribute {
  name: "pads"
  ints: 2
  ints: 2
  ints: 2
  ints: 2
  type: INTS
}

input: "import/pool1/MaxPool:0"
input: "import/conv2/Variable:0"
output: "import/conv2/Conv2D:0"
name: "import/conv2/Conv2D"
op_type: "Conv"
attribute {
  name: "dilations"
  ints: 1
  ints: 1
  type: INTS
}
attribute {
  name: "strides"
  ints: 1
  ints: 1
  type: INTS
}
attribute {
  name: "kernel_shape"
  ints: 5
  ints: 5
  type: INTS
}
attribute {
  name: "pads"
  ints: 2
  ints: 2
  ints: 2
  ints: 2
  type: INTS
}

input: "import/pool2/MaxPool:0"
input: "import/conv3/Variable:0"
output: "import/conv3/Conv2D:0"
name: "import/conv3/Conv2D"
op_type: "Conv"
attribute {
  name: "dilations"
  ints: 1
  ints: 1
  type: INTS
}
attribute {
  name: "strides"
  ints: 1
  ints: 1
  type: INTS
}
attribute {
  name: "kernel_shape"
  ints: 7
  ints: 7
  type: INTS
}

input: "import/conv3/Relu:0"
input: "import/conv4last/Variable:0"
output: "import/conv4last/Conv2D:0"
name: "import/conv4last/Conv2D"
op_type: "Conv"
attribute {
  name: "dilations"
  ints: 1
  ints: 1
  type: INTS
}
attribute {
  name: "strides"
  ints: 1
  ints: 1
  type: INTS
}
attribute {
  name: "kernel_shape"
  ints: 1
  ints: 1
  type: INTS
}
```
Most DNN models spend a significant time in the `Conv` operators, likely to be around 60%~90%. To estimate the inference time, we need to figure out the total number of multiply operations of all `Conv` operators required in a model. Do you know how to calculate the number of required number of multiply operations of all `Conv` operators in an ONNX model? We will give you a hint here and ask you to implement a script to generate the statistics in the homework section. The next script demonstrate how to figure out the input tensor sizes and dimensions of a `Conv` operator. 

```python=
## parse_model.py
import onnx

onnx_model = onnx.load('./lenet.onnx')

## need to run shape inference in order to get a full value_info list
onnx_model = onnx.shape_inference.infer_shapes(onnx_model)

## List all tensor names in the graph
input_nlist = [k.name for k in onnx_model.graph.input]
initializer_nlist = [k.name for k in onnx_model.graph.initializer]
value_info_nlist = [k.name for k in onnx_model.graph.value_info]

print('\ninput list: {}'.format(input_nlist))
print('\ninitializer list: {}'.format(initializer_nlist))
print('\nvalue_info list: {}'.format(value_info_nlist))

## a simple function to calculate the tensor size and extract dimension information
def get_size(shape):
    dims = []
    ndim = len(shape.dim)
    size = 1;
    for i in range(ndim):
        size = size * shape.dim[i].dim_value
        dims.append(shape.dim[i].dim_value)
    return dims, size

## find all `Conv` operators and print its input information
for i in onnx_model.graph.node:
    if (i.op_type == 'Conv'):
        print('\n-- Conv "{}" --'.format(i.name))
        for j in i.input:
            if j in input_nlist:
                idx = input_nlist.index(j)
                (dims, size) = get_size(onnx_model.graph.input[idx].type.tensor_type.shape)
                print('input {} has {} elements dims = {}'.format(j, size, dims  ))
            elif j in initializer_nlist:
                idx = initializer_nlist.index(j)
                (dims, size) = get_size(onnx_model.graph.initializer[idx].type.tensor_type.shape)
                print('input {} has {} elements dims = {}'.format(j, size, dims))
            elif j in value_info_nlist:
                idx = value_info_nlist.index(j)
                (dims, size) = get_size(onnx_model.graph.value_info[idx].type.tensor_type.shape)
                print('input {} has {} elements dims = {}'.format(j, size, dims))

```


### Lab 2-3-2 Extracting Hidden State Tensors Using Hooks in PyTorch

One advanced method of model analysis in PyTorch involves using hooks to extract hidden state tensors. Hooks in PyTorch are a powerful feature that allow you to add custom functionality to layers during the forward and backward passes. They can be particularly useful for extracting intermediate tensors, debugging, and understanding the internal workings of complex models.

#### What are Hooks?
Hooks are functions that can be registered on a nn.Module or a Tensor. There are two types of hooks:

- Forward hooks, registered using .register_forward_hook(), are called during the forward pass.
- Backward hooks, registered using .register_backward_hook(), are called during the backward pass.

#### Extracting Hidden States Using Forward Hooks:
- [Pytorch Docs](https://pytorch.org/docs/stable/generated/torch.nn.modules.module.register_module_forward_hook.html)
Forward hooks can be utilized to capture the output of intermediate layers (hidden states) during the forward pass. This is particularly useful for examining the transformations applied by the model at each layer.

- Implementing a Forward Hook:
Here's a simple example of implementing a forward hook in PyTorch to extract hidden state tensors:

We use alexnet as example as below:
```python=
import torchvision.models as models
import torch
activation = {}
# Define a hook function
def get_activation(name):
    def hook(model, input, output):
        activation[name] = output.detach()
    return hook

# Load a pre-trained AlexNet model
model = models.alexnet(pretrained=True)
model.eval()
```

And we define hook function to extract output tensor of each linear layer:
```python=
# Define a hook function
def get_activation(name):
    def hook(model, input, output):
        activation[name] = output.detach()
    return hook


# Dictionary to store activations from each layer
activation = {}

# Register hook to each linear layer
for layer_name, layer in model.named_modules():
    if isinstance(layer, torch.nn.Linear):
        # Register forward hook
        layer.register_forward_hook(get_activation(layer_name))

# Run model inference
data = torch.randn(1, 3, 224, 224)
output = model(data)

# Access the saved activations
for layer in activation:
    print(f"Activation from layer {layer}: {activation[layer].shape}")
```

Output:
```
Activation from layer classifier.1: torch.Size([1, 4096])
Activation from layer classifier.4: torch.Size([1, 4096])
Activation from layer classifier.6: torch.Size([1, 1000])
```

Now we can get the output activation tensor from the dict:
```
{'classifier.1': tensor([[0.0000, 0.0000, 0.0000,  ..., 0.0000, 0.5093, 0.0000]]),
 'classifier.4': tensor([[0., 0., 0.,  ..., 0., 0., 0.]]),
 'classifier.6': tensor([[ 4.2778e-01, -1.1054e+00, -1.2107e+00, -6.1828e-01,  1.6858e+00,
           1.4879e+00,  1.7495e+00,  2.9466e-01, -1.3394e-02, -6.5429e-01,
          -1.5621e+00,  3.2837e-01, -2.2746e-01, -3.6484e-01,  2.6623e-01,
          -6.1137e-01, -1.6391e+00, -5.5829e-02,  1.0658e+00, -7.0587e-01,
          -2.5693e+00, -1.6809e+00, -2.1988e+00, -9.2078e-01, -8.6951e-01,
          -1.2209e+00, -1.2667e+00,  2.6577e-02,  2.7681e-01, -5.1653e-01,
          -6.6379e-01, -5.0862e-01, -1.8583e+00, -6.2556e-01, -1.2386e-02,
          -2.9088e-01,  8.9404e-02,  5.6164e-01, -4.9310e-01, -1.6731e-01,
           9.2343e-01, -1.6891e+00,  5.6517e-01, -8.2511e-02,  3.1320e-01,
           3.4748e-01,  1.5549e+00, -4.2947e-01,  9.3694e-01, -1.9459e-01,
          -5.7109e-01, -2.3149e+00, -6.7191e-01, -1.6065e+00, -9.6408e-01,
           1.4932e-01, -1.7972e+00, -7.8935e-01,  2.8394e-01,  5.1336e-01,
           5.2207e-01, -4.7761e-01, -1.3226e+00, -4.6183e-01,  7.3135e-01,
          -2.7826e-01, -3.3742e-01, -7.8703e-02,  1.6277e-01, -1.3880e-02,
           6.2975e-01, -1.0495e+00, -8.0159e-01, -1.1746e+00, -1.7621e+00,
          -4.3803e-01,  3.8563e-01,  1.7716e-01,  1.4280e+00,  9.9733e-01,
          -1.1905e+00, -4.1851e-01, -6.8484e-01, -2.1619e+00,  6.4601e-01,
           3.9895e-01, -1.0252e+00, -5.9419e-01,  1.2246e+00,  4.3896e-01,
           2.5073e-01, -1.7472e+00, -3.9835e-01,  7.1977e-01,  9.5988e-01,
          -3.1573e+00,  1.0682e+00, -1.4291e-01, -1.8975e+00,  1.2377e+00,
           2.1522e-01, -7.2547e-01, -1.3779e+00, -2.4386e-01,  3.7816e-01,
          -7.6676e-01, -4.6261e-01,  2.2002e+00, -1.0095e+00,  1.6165e+00,
          -1.1718e+00,  2.1346e+00, -4.6126e-01, -5.1585e-02,  1.3409e-01,
...
          -2.8940e-01, -1.4681e+00,  1.5635e-01,  3.9811e-01, -9.5001e-01,
          -1.2361e+00, -2.7122e-01, -5.3238e-01,  5.2874e-01,  8.7802e-02,
          -1.1497e-01, -5.5865e-04,  1.8349e-02, -2.9935e-01, -5.4613e-01,
           3.4049e-01,  2.9057e-01, -1.8520e+00, -3.0419e+00, -2.7991e+00,
          -2.2025e+00, -1.1032e+00, -1.3142e+00, -7.5288e-01,  1.2081e+00]])}
```

### Lab 2-3-3 Model Computation Requirement Analysis

Computational load in AI models refers to the amount of computational resources required to train and run the model. It encompasses aspects like the number of operations (such as multiplications and additions), memory usage, and bandwidth requirements. The computational load directly impacts model performance, influencing factors such as training speed, inference latency, and scalability. Understanding and optimizing the computational load is crucial for deploying models in resource-constrained environments like mobile devices or edge computing platforms.

#### Computation Requirement For Each Layer

This tutorial will cover the basics of calculating operations in neural networks, focusing on two types of layers: **Fully Connected (Dense) Layers** and **Convolutional Neural Networks (CNNs)**. We'll look into how to calculate Multiply-Accumulates (MACs) and Floating-Point Operations (FLOPs) for each layer type.

- **Fully Connected Layers:**

    In this section, we will create a simple neural network with 3 layers and demonstrate how to calculate the operations involved for each layer.

    **Step 1 : Identifying Layer Parameters**

    Consider a model with three linear layers defined as follows:

    ```python=
    import torch.nn.functional as F
    import torch.nn as nn

    class SimpleLinearModel(nn.Module):
        def __init__(self):
            super(SimpleLinearModel,self).__init__()
            self.fc1 = nn.Linear(in_features=10, out_features=20, bias=False)
            self.fc2 = nn.Linear(in_features=20, out_features=15, bias=False)
            self.fc3 = nn.Linear(in_features=15, out_features=1, bias=False)
        def forward(self, x):
            x = self.fc1(x)
            x = F.relu(x)
            x = self.fc2(x)
            F.relu(x)
            x = self.fc3(x)
            return x

    linear_model = SimpleLinearModel()
    sample_data = torch.randn(1, 10)
    ```

    - fc1: 10 input features, 20 output features
    - fc2: 20 input features, 15 output features
    - fc3: 15 input features, 1 output feature

    **Step 2 : Calculating FLOPs and MACs**

    To calculate MACs and FLOPs for each layer, use the formulas:

    - **MACs** : I * O, where I is the number of inputs and O is the number of outputs.

        $\text{Number of Operations} = \text{Input Features} \times \text{Output Features}$

     
    - **FLOPs** : 2 * (I * O) 


    **Layer Calculations:**

    - Layer fc1:
      - MACs = 10 * 20 = 200
      - FLOPs = 2 * 200 = 400
    - Layer fc2:
      - MACs = 20 * 15 = 300
      - FLOPs = 2 * 300 = 600
    - Layer fc3:
      - MACs = 15 * 1 = 15
      - FLOPs = 2 * 15 = 30

    **Step 3: Summing Up the Results**

    To find the total number of MACs and FLOPs for a single input passing through the entire network:

    - Total MACs = 515
    - Total FLOPs = 1030

- **Convolutional Layers:**

    Calculating operations for CNNs involves additional factors like stride, padding, and kernel size.


    ```python=
    class SimpleConv(nn.Module):
        def __init__(self):
            super(SimpleConv, self).__init__()
            self.conv1 = nn.Conv2d(in_channels=1, out_channels=16, kernel_size=3, stride=1, padding=1)
            self.conv2 = nn.Conv2d(in_channels=16, out_channels=32, kernel_size=3, stride=1, padding=1)
            self.fc =  nn.Linear(in_features=32*28*28, out_features=10)

        def forward(self, x):
            x = self.conv1(x)
            x = F.relu(x)
            x = self.conv2(x)
            x = F.relu(x)
            x = x.view(x.shape[0], -1)
            x = self.fc(x)
            return x

    x = torch.rand(1, 1, 28, 28)
    conv_model = SimpleConv()
    ```

    **Step 1: Identifying Layer Parameters**

    Consider a model with:

    - conv1: 1 input channel, 16 output channels, kernel size 3
    - conv2: 16 input channels, 32 output channels, kernel size 3
    - fc: 32 * 28* 28 input features, 10 output features

    **Step 2: Calculating FLOPs and MACs**
    
    To calculate MACs and FLOPs for each layer, use the formulas:

    - **MACs** : 

        The computational load of a convolutional layer can be calculated as:

         $\ \text{Number of Operations}=\text{Output Height} \times \text{Output Width}$
         $\times \text{Kernel Height} \times \text{Kernel Width}$ 
         $\times \text{Input Channels} \times \text{Output Channels}$

    - **FLOPs** : 2 * MACs 


    Layer Calculations:

    - Layer conv1:
      - MACs = 28 * 28 * 3 * 3 * 1 * 16 = 112,896
      - FLOPs = 2 * 112,896 = 225,792
    - Layer conv2:
      - MACs = 28 * 28 * 3 * 3 * 16 * 32 = 3,612,672
      - FLOPs = 2 * 3,612,672 = 7,225,344
    - Layer fc:
      - MACs = 32 * 28 * 28 * 10 = 250,880
      - FLOPs = 2 * 250,880 = 501,760

    **Step 3 : Summing Up the Results**

    To find the total number of MACs and FLOPs for a single input:

    - Total MACs = 3,976,448
    - Total FLOPs = 7,952,896


- **Other Layers**:
   - Activation functions, pooling layers, and normalization layers also contribute to the computational load, but their contribution is relatively smaller compared to convolutional and fully connected layers.



#### Tutorial: Calculating Operations for AlexNet

In this section, we will examine the computation requirements of AlexNet, a seminal CNN architecture. We'll calculate the Multiply-Accumulates (MACs) for each layer to understand the computational complexity involved.

- Step 1: Load the Pretrained AlexNet Model

    First, we load a pretrained AlexNet model from PyTorch's model zoo:

    ```python=
    import torch
    import torchvision.models as models
    import torch.nn as nn

    model = models.alexnet(pretrained=True)
    ```


    In Lab 2-1-3, we demonstrated how to display the composition of AlexNet using the print function. AlexNet consists of 5 convolutional layers, followed by 3 fully connected layers. Additionally, the network incorporates MaxPooling layers after certain convolutional layers and utilizes ReLU activation functions extensively.


- Step 2: Define Functions for Calculating Output Shapes and MACs

    We define two functions: one for calculating the output shape of `Conv2d`, `MaxPool2d`, and `Linear` layers, and another for calculating the MACs for `Conv2d` and `Linear` layers.

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
    ```

    Remark: Due to the output shape changing with the previous layer, the purpose of designing `calculate_output_shape` is to calculate conv MACs for AlexNet. Please note for other models, the shape changes between successive layers.



- Step 3: Iterate Through the Model's Layers

    We iterate through each layer of the AlexNet model, calculating and summing up the MACs for each `Conv2d` and `Linear` layer. The output shape of each layer is also calculated to accurately compute the MACs for subsequent layers.

    ```python=
    # Initial input shape
    input_shape = (3, 224, 224)
    total_macs = 0

    # Iterate through the layers of the model
    for name, layer in model.named_modules():
        if isinstance(layer, (nn.Conv2d, nn.MaxPool2d, nn.ReLU, nn.Linear)):
            output_shape = calculate_output_shape(input_shape, layer)
            macs = calculate_macs(layer, input_shape, output_shape)
            total_macs += macs
            if isinstance(layer, (nn.Conv2d, nn.Linear)):
                print(
                    f"Layer: {name}, Type: {type(layer).__name__}, Input Shape: {input_shape}, Output Shape: {output_shape}, MACs: {macs}"
                )
            elif isinstance(layer, nn.MaxPool2d):
                # Also print shape transformation for MaxPool2d layers (no MACs calculated)
                print(
                    f"Layer: {name}, Type: {type(layer).__name__}, Input Shape: {input_shape}, Output Shape: {output_shape}, MACs: N/A"
                )
            input_shape = output_shape  # Update the input shape for the next layer

    print(f"Total MACs: {total_macs}")
    ```

    After execution, you will obtain the total number of MACs as shown below:

    ```
    Layer: features.0, Type: Conv2d, Input Shape: (3, 224, 224), Output Shape: (64, 55, 55), MACs: 70276800
    Layer: features.2, Type: MaxPool2d, Input Shape: (64, 55, 55), Output Shape: (64, 27, 27), MACs: N/A
    Layer: features.3, Type: Conv2d, Input Shape: (64, 27, 27), Output Shape: (192, 27, 27), MACs: 223948800
    Layer: features.5, Type: MaxPool2d, Input Shape: (192, 27, 27), Output Shape: (192, 13, 13), MACs: N/A
    Layer: features.6, Type: Conv2d, Input Shape: (192, 13, 13), Output Shape: (384, 13, 13), MACs: 112140288
    Layer: features.8, Type: Conv2d, Input Shape: (384, 13, 13), Output Shape: (256, 13, 13), MACs: 149520384
    Layer: features.10, Type: Conv2d, Input Shape: (256, 13, 13), Output Shape: (256, 13, 13), MACs: 99680256
    Layer: features.12, Type: MaxPool2d, Input Shape: (256, 13, 13), Output Shape: (256, 6, 6), MACs: N/A
    Layer: classifier.1, Type: Linear, Input Shape: (256, 6, 6), Output Shape: (4096,), MACs: 37748736
    Layer: classifier.4, Type: Linear, Input Shape: (4096,), Output Shape: (4096,), MACs: 16777216
    Layer: classifier.6, Type: Linear, Input Shape: (4096,), Output Shape: (1000,), MACs: 4096000
    Total MACs: 714188480
    ```



### Lab 2-3-4 Use ONNX Library to manipulate AI Model

In this section, we use the official Python library of ONNX to demonstrate how to build extended tools for model analysis. 


#### Prepare The Target Model

In this Lab, we will use `lenet.onnx` for illustration. So please download the model using the following commands:

```
$ cd lab2-3
$ wget https://github.com/ONNC/onnc-tutorial/raw/master/models/lenet/lenet.onnx 
```


Based on the Python package `onnx`, we provides the following APIs:


1. Find all the unique onnx operators.(In find_operator.py)
    - depend / independ on input size / parameters
    - return the amount of each operator
    ```python=
    python3 find_operator.py
    ```

<!--

    We can counts unique ONNX operators in a model, independent of input size or parameters as below:

    ```python=
    import sclblonnx as so

    # Load the ONNX model from a file
    g = so.graph_from_file("googlenet.onnx")  # Replace with the path to your ONNX model file

    # Initialize an empty list to collect operator types
    operator_types = []

    # Iterate over each node in the graph
    for node in g.node:
        # Collect the type of operator (op_type) for each node
        operator_types.append(node.op_type)

    # Initialize a dictionary to count occurrences of each operator type
    operator_count = {}

    # Iterate over the set of unique operator types
    for op_type in set(operator_types):
        # Count and store the number of occurrences of each operator type
        operator_count[op_type] = operator_types.count(op_type)

    # Print the count of each operator type
    print(operator_count)
    ```


    The script output shows a dictionary where each key is a unique ONNX operator used in the model, and the corresponding value is the number of times that operator appears:
    ```
    {'AveragePool': 1, 'Reshape': 1, 'Conv': 57, 'Concat': 9, 'Dropout': 1, 'MaxPool': 13, 'LRN': 2, 'Softmax': 1, 'Gemm': 1, 'Relu': 57}
    ```
  -->
  
  
2. Find the attributes (input, output tensor, operator) of operators related to matrix extension.(在get_attribute.py中)
    ```python=
    python3 get_attribute.py
    ```
<!--
    We can extract and print attributes of these nodes, including the names of their input and output tensors, as well as the type of operation:

    ```python=
    import sclblonnx as so

    # Load the ONNX model
    g = so.graph_from_file("googlenet.onnx")  # Replace with your model path

    # Define matrix-related operators
    matrix_ops = ["Gemm", "MatMul"]

    # Initialize a dictionary to hold attributes of matrix-related operators
    matrix_op_attributes = {}

    # Iterate over each node in the graph
    for node in g.node:
        if node.op_type in matrix_ops:
            # Extract attributes such as input, output, and operator type
            attributes = {
                "inputs": node.input,  # List of input tensors to the node
                "outputs": node.output,  # List of output tensors from the node
                "operator": node.op_type  # Type of operator
            }
            matrix_op_attributes[node.op_type] = attributes

    # Print the collected attributes
    print(matrix_op_attributes)
    ```
    Result:
    ```
    {'Gemm': {'inputs': ['OC2_DUMMY_0', 'loss3/classifier_w_0', 'loss3/classifier_b_0'], 'outputs': ['loss3/classifier_1'], 'operator': 'Gemm'}}
    ```
  -->
3. Find the input and output tensor size.(在tensor_size.py中)
    ```python=
    python3 tensor_size.py
    ```
4. Memory bandwidth requirement(based on RV32I core)(在get_bandwidth.py中)
    ```python=
    python3 get_bandwidth.py
    ```


### Lab 2-3-5 Profiling with PyTorch
#### Introduction to Profiling in PyTorch
Profiling in PyTorch involves using tools and techniques to understand, diagnose, and optimize the performance of your models. The PyTorch Profiler is a utility that provides insights into the resource consumption and performance bottlenecks in your model. It helps in identifying the time-consuming operations, understanding the utilization of CPU and GPU resources, and analyzing the memory allocations. This information is crucial for optimizing models to run more efficiently and effectively.


In this recipe, we will use the AlexNet model to demonstrate how to use the profiler to analyze model performance.

- Steps
    - Import all necessary libraries:

    ```python=
    import torch
    import torchvision.models as models
    from torch.profiler import profile, record_function, ProfilerActivity
    ```

- Instantiate the AlexNet model:

    ```python=
    model = models.alexnet(pretrained=True)
    model.eval()  # Set the model to evaluation mode
    inputs = torch.randn(5, 3, 224, 224)
    ```

- Using profiler to analyze execution time:

    ```python=
    with profile(activities=[ProfilerActivity.CPU], record_shapes=True) as prof:
        with record_function("model_inference"):
            model(inputs)
    ```

    Here, we are profiling CPU activities and recording the shapes of the tensors.

- Analyzing the profiling data:

    ```python=
    print(prof.key_averages().table(sort_by="cpu_time_total", row_limit=10))
    ```

    This command prints out the top 10 operations consuming the most CPU time.

    ```
    ---------------------------------  ------------  ------------  ------------  ------------  ------------  ------------  
                                 Name    Self CPU %      Self CPU   CPU total %     CPU total  CPU time avg    # of Calls  
    ---------------------------------  ------------  ------------  ------------  ------------  ------------  ------------  
                      model_inference         1.34%       1.299ms       100.00%      96.701ms      96.701ms             1  
                         aten::conv2d         0.04%      37.000us        65.58%      63.421ms      12.684ms             5  
                    aten::convolution         0.15%     145.000us        65.55%      63.384ms      12.677ms             5  
                   aten::_convolution         0.08%      81.000us        65.40%      63.239ms      12.648ms             5  
             aten::mkldnn_convolution        65.21%      63.062ms        65.31%      63.158ms      12.632ms             5  
                         aten::linear         0.02%      24.000us        24.40%      23.593ms       7.864ms             3  
                          aten::addmm        24.21%      23.414ms        24.31%      23.505ms       7.835ms             3  
                     aten::max_pool2d         0.02%      18.000us         7.34%       7.102ms       2.367ms             3  
        aten::max_pool2d_with_indices         7.33%       7.084ms         7.33%       7.084ms       2.361ms             3  
                          aten::relu_         0.19%     185.000us         0.94%     910.000us     130.000us             7  
    ---------------------------------  ------------  ------------  ------------  ------------  ------------  ------------  
    Self CPU time total: 96.701ms
    ```

- Using profiler to analyze memory consumption:
    To enable memory profiling, pass profile_memory=True.

    ```python=
    with profile(activities=[ProfilerActivity.CPU], profile_memory=True, record_shapes=True) as prof:
        model(inputs)

    print(prof.key_averages().table(sort_by="self_cpu_memory_usage", row_limit=10))
    ```

    ```
    ---------------------------------  ------------  ------------  ------------  ------------  ------------  ------------  ------------  ------------  
                                 Name    Self CPU %      Self CPU   CPU total %     CPU total  CPU time avg       CPU Mem  Self CPU Mem    # of Calls  
    ---------------------------------  ------------  ------------  ------------  ------------  ------------  ------------  ------------  ------------  
                          aten::empty         0.21%     168.000us         0.21%     168.000us      15.273us       9.25 Mb       9.25 Mb            11  
        aten::max_pool2d_with_indices         9.87%       7.859ms         9.87%       7.859ms       2.620ms       5.05 Mb       5.05 Mb             3  
                        aten::resize_         0.02%      18.000us         0.02%      18.000us       3.000us     180.00 Kb     180.00 Kb             6  
                          aten::addmm        37.07%      29.504ms        37.17%      29.588ms       9.863ms     179.53 Kb     179.53 Kb             3  
                         aten::conv2d         0.11%      91.000us        49.44%      39.357ms       7.871ms       9.25 Mb           0 b             5  
                    aten::convolution         0.24%     193.000us        49.33%      39.266ms       7.853ms       9.25 Mb           0 b             5  
                   aten::_convolution         0.12%      99.000us        49.09%      39.073ms       7.815ms       9.25 Mb           0 b             5  
             aten::mkldnn_convolution        48.75%      38.801ms        48.96%      38.974ms       7.795ms       9.25 Mb           0 b             5  
                    aten::as_strided_         0.04%      35.000us         0.04%      35.000us       7.000us           0 b           0 b             5  
                          aten::relu_         0.31%     250.000us         1.63%       1.295ms     185.000us           0 b           0 b             7  
    ---------------------------------  ------------  ------------  ------------  ------------  ------------  ------------  ------------  ------------  
    Self CPU time total: 79.599ms
    ```


### Lab 2-3-6 Analyzing Profiling Results Using TensorBoard


This section demonstrates using the TensorBoard plugin with PyTorch Profiler specifically for profiling the inference performance of the RentNet model.

The updated PyTorch profiler can record CPU and CUDA activities, providing detailed insights when visualized through TensorBoard. This is especially useful for identifying bottlenecks during model inference.

#### Steps
1. Prepare the Model Training:
    Import necessary libraries and load the Resnet model.
    ```python=
    import torch.optim
    import torch.profiler
    import torch.utils.data
    import torchvision.datasets
    import torchvision.models
    import torchvision.transforms as T


    transform = T.Compose(
        [T.Resize(224),
         T.ToTensor(),
         T.Normalize((0.5, 0.5, 0.5), (0.5, 0.5, 0.5))])
    train_set = torchvision.datasets.CIFAR10(root='./data', train=True, download=True, transform=transform)
    train_loader = torch.utils.data.DataLoader(train_set, batch_size=32, shuffle=True)

    device = torch.device("cpu")
    model = torchvision.models.resnet18(weights='IMAGENET1K_V1')
    criterion = torch.nn.CrossEntropyLoss()
    optimizer = torch.optim.SGD(model.parameters(), lr=0.001, momentum=0.9)
    model.train()

    def train(data):
        inputs, labels = data[0].to(device=device), data[1].to(device=device)
        outputs = model(inputs)
        loss = criterion(outputs, labels)
        optimizer.zero_grad()
        loss.backward()
        optimizer.step()

    ```

2. Profile Model Training:
    Set up and execute the PyTorch Profiler for the training process.

    ```python=
    with torch.profiler.profile(
            schedule=torch.profiler.schedule(wait=1, warmup=1, active=3, repeat=1),
            on_trace_ready=torch.profiler.tensorboard_trace_handler('./log/resnet18'),
            record_shapes=True,
            profile_memory=True,
            with_stack=True
    ) as prof:
        for step, batch_data in enumerate(train_loader):
            prof.step()  # Need to call this at each step to notify profiler of steps' boundary.
            if step >= 1 + 1 + 3:
                break
            train(batch_data)
    ```

3. Viewing the Results with TensorBoard:

    - First, ensure torch_tb_profilerd are installed:

    ```bash=
    $ pip install torch_tb_profiler
    ```

    - Launch TensorBoard
    
    To start TensorBoard, run the following command in your terminal. Make sure to replace '~/projects/lab02/lab2-3/log/' with the path to your actual log directory.
    
    ```bash=
    $ tensorboard --logdir='~/projects/lab02/lab2-3/log/' --bind_all --port=10000 > tensorboard.stdout.log &> tensorboard.stderr.log &
    ```
    
    - Check if the TensorBoard server is running
    After running the command, you should see two files, `tensorboard.stdout.log` and `tensorboard.stderr.log` in the current working directory. In `tensorboard.stderr.log`, you will see the content similar to the following, indicating that TensorBoard is successfully monitoring your log directory and is accessible via a web browser:

        ```shell
        $ cat tensorboard.stderr.log
        I0213 11:37:36.770829 140107990624000 plugin.py:429] Monitor runs begin
        I0213 11:37:36.774981 140107990624000 plugin.py:444] Find run directory /home/user/projects/lab02/lab2-3/log/resnet18
        I0213 11:37:36.775716 140107973576448 plugin.py:493] Load run resnet18
        I0213 11:37:36.789404 140107973576448 loader.py:57] started all processing
        TensorBoard 2.12.1 at http://ACAL-WORKSPACE-MAIN:10000/ (Press CTRL+C to quit)
        ```

    - Access TensorBoard
    Bring up your TensorBoard URL, http://localhost:10000,  with your browser. 

    Open the URL provided by TensorBoard in a browser to analyze the profiling data.

    - stop Tensorboard server running
    To terminate the tensorboard service, you may use the following command. 
        ```shell
        $ kill $(ps -e | grep 'tensorboard' | awk '{print $1}')
        ```
    

    #### Overview

    ![](https://course.playlab.tw/md/uploads/8f1180dc-3df5-48a4-b406-2fadc2b74d61.png)


    如果有使用cuda的結果:
    ![](https://course.playlab.tw/md/uploads/a73239f2-ef7a-4bf0-a55d-181410b14fd5.png)


    #### Operator
    ![](https://course.playlab.tw/md/uploads/ebcab051-ea36-4b03-ae82-37d4b62ad1ec.png)



    #### Trace
    ![](https://course.playlab.tw/md/uploads/967450c0-790a-4add-aeec-2403646bd251.png)


    #### Memory
    ![](https://course.playlab.tw/md/uploads/927d6831-b4b7-4af0-adb6-6b03f45a7467.png)


    #### Module
    ![](https://course.playlab.tw/md/uploads/29b91321-3c73-4964-a6eb-695d0ed0ad2d.png)



## Lab 2-4 Model Analysis Using the Pytorch C++ frontend

  The PyTorch team created the C++ frontend to enable research in environments in which Python cannot be used, or is simply not the right tool for the job. The C++ frontend is not intended to compete with the Python frontend. It is meant to complement it. It's known that researchers and engineers alike love PyTorch for its simplicity, flexibility and intuitive API. The goal is to make sure you can take advantage of these core design principles in every possible environment especially you may be the owner of an existing C++ application doing anything from serving web pages in a backend server to rendering 3D graphics in photo editing software, and wish to integrate machine learning methods into your system. The C++ frontend allows you to remain in C++ and spare yourself the hassle of binding back and forth between Python and C++, while retaining much of the flexibility and intuitiveness of the traditional PyTorch (Python) experience. In this lab, we will introduce how to use the pytorch C++ frontend for model analysis. The material in this lab is part of the official Pytorch tutorial in [Loading a TorchScript Model in C++](https://pytorch.org/tutorials/advanced/cpp_export.html)

### Lab 2-4-1 Converting Your PyTorch Model to Torch Script
  A PyTorch model’s journey from Python to C++ is enabled by Torch Script, a representation of a PyTorch model that can be understood, compiled and serialized by the Torch Script compiler. There exist two ways of converting a PyTorch model to Torch Script. The first is known as tracing, a mechanism in which the structure of the model is captured by evaluating it once using example inputs, and recording the flow of those inputs through the model. This is suitable for models that make limited use of control flow. The second approach is to add explicit annotations to your model that inform the Torch Script compiler that it may directly parse and compile your model code, subject to the constraints imposed by the Torch Script language. We will take the first approach in this lab. 

#### Converting to Torch Script via Tracing

To convert a PyTorch model to Torch Script via tracing, you must pass an instance of your model along with an example input to the torch.jit.trace function. This will produce a torch.jit.ScriptModule object with the trace of your model evaluation embedded in the module’s forward method. 

```python=
import torch
import torchvision

# An instance of your model.
model = torchvision.models.resnet18()

# An example input you would normally provide to your model's forward() method.
example = torch.rand(1, 3, 224, 224)

# Use torch.jit.trace to generate a torch.jit.ScriptModule via tracing.
traced_script_module = torch.jit.trace(model, example)

# Serializing Your Script Module to a File
traced_script_module.save("traced_resnet_model.pt")

```
You may execute the above python script and it will produce a traced_resnet_model.pt file in your working directory. 

### Lab 2-4-2 Loading Your Script Module in C++

 To load your serialized PyTorch model in C++, your application must depend on the PyTorch C++ API – also known as LibTorch. The LibTorch distribution encompasses a collection of shared libraries, header files and CMake build configuration files. While CMake is not a requirement for depending on LibTorch, it is the recommended approach and will be well supported into the future. For this tutorial, we will be building a minimal C++ application using CMake and LibTorch that simply loads and executes a serialized PyTorch model.
 
 In the `~/projects/lab02/lab2-4/analyzer` folder, you can see a minimal C++ application, `analyzer.cc`, which loads the input torchscript model and print whether the module loading is successfully or not. You may follow the steps below to compile and run this minimal C++ application 

- install `cmake`
```shell
$ sudo pip3 install cmake
```

- install `libtorch` (not pytorch!)
The Pytorch official site provides binary distributions of all headers, libraries and CMake configuration files required to depend on PyTorch. We call this distribution LibTorch, and you can download ZIP archives containing the latest LibTorch distribution from this [website](https://pytorch.org/get-started/locally/). The following example is for the MAC OS (x86) platform. 


> ![](https://course.playlab.tw/md/uploads/7d5e1faa-0a35-4a13-a9f4-98db9cdf2df3.png)
> Download cxx11 ABI version (or you can use the following command!)
```shell=
## go to the working folder
$ cd ~/projects/lab02/lab2-4/analyzer

## download the library 
$ wget https://download.pytorch.org/libtorch/cpu/libtorch-cxx11-abi-shared-with-deps-2.2.0%2Bcpu.zip

## unzip the file
$ unzip libtorch-cxx11-abi-shared-with-deps-2.2.0+cpu.zip 
```

- build and compile
```shell
## create build folder
$ mkdir ~/projects/lab02/lab2-4/analyzer/build

## enter the build folder
$ cd ~/projects/lab02/lab2-4/analyzer/build

## create config
$ cmake -DCMAKE_PREFIX_PATH=./libtorch ..

## compile
$ cmake --build . --config Release -j ${nproc}
```

- run the analyzer on the torchscript file
```shell
## go to the project folder
$ cd ~/projects/lab02/lab2-4

## generate the torchscript reset18 model
$ ./reset18.py 

## run the `loadModel` application
$ ./analyzer/build/loadModel traced_resnet_model.pt
```

### Lab 2-4-3 Using the Pytorh C++ API to analyze a model

  After loading a torchscript model successfully, another example, `analyzer.cc`, create an input vector for the model and running inference as shown in the following code snippet.
```cpp=
  // Create a vector of inputs.
  std::vector<torch::jit::IValue> inputs;	
  inputs.push_back(torch::ones({1, 3, 224, 224}));

  // Execute the model and turn its output into a tensor.
  at::Tensor output = module.forward(inputs).toTensor();
  std::cout << output.slice(/*dim=*/1, /*start=*/0, /*end=*/5) << '\n';

  //dump the model information                                                                                                                                                   
  // source code can be found in                                                                                                                                                 
  // https://github.com/pytorch/pytorch/blob/main/torch/csrc/jit/api/module.cpp                                                                                                  
  std::cout << module.dump_to_str(true,true,true) << " (\n";
```
- Running model inference

```shell
~/projects/lab02/lab2-4/analyzer/build (main)$ ./analyzer ../../traced_resnet_model.pt 
load the torchscript model, ../../traced_resnet_model.pt, successfully 
 0.6580  0.0130  0.2015  0.4732 -0.3352
[ CPUFloatType{1,5} ]
```

- PyTorch has a document, [PyTorch C++ API](https://pytorch.org/cppdocs/index.html), which describes a complete list of supported C++ APIs. You may use it to write a C++ application for model analysis in the Homework. 


 
## Homeworks

The homework in this section intends to go deeper in the AI model analysis. When we make an AI chip, there are many assumptions in the hardware design. In order to deploy an AI model in the target hardware, we need to do the follows:
- understanding the computation requirement of an AI model
- figure out how the AI model can be mapped into the target hardware execution
    - we may need to transform a graph into another graph that fit the hardware design parameters
    - we may need to understand the characterisitcs of the AI model in order to determine the hardware design parameters
- extract and manipulate the AI model information in different levels of software abstraction. 


### HW 2-1 Model Analysis Using Pytorch

In HW2-1, we will use GoogleNet as the test model. You may use the following python code snippet to load the GoogleNet Pytorch model. 

```python=
import torchvision.models as models

# 加載 GoogLeNet 模型
model = models.googlenet(pretrained=True)
print(model)

input_shape = (3, 224, 224)
```

Please use Pytorch to complete the following exercises.

#### 2-1-1. Calculate the number of model parameters：
- Please include all trainable and non-trainable parameters

#### 2-1-2. Calculate memory requirements for storing the model weights.

#### 2-1-3. Use Torchinfo to print model architecture including the number of parameters and the output activation size of each layer 

#### 2-1-4. Calculate computation requirements
- The primary focus is on calculating MACs for linear and convolutional layers, with calculations for other layers being a bonus.
#### 2-1-5. Use forward hooks to extract the output activations of  the Conv2d layers.


### HW 2-2 Add more statistics to analyze the an ONNX model

For this assignment, you can use any ONNX model of your choice for analysis. You can refer to the [ONNX Model Zoo](https://github.com/onnx/models) to select a suitable model. Since LeNet has already been as a demonstration in this lab,  you are encouraged to choose and analyze any ONNX model other than LeNet.


If you're looking for a suggestion, [MobileNetV2](https://github.com/onnx/models/tree/main/validated/vision/classification/mobilenet) is a good example to begin with. You can follow the method described on the ONNX GitHub repository to download an ONNX model. Here is an example using `mobilenetv2-10.onnx`. 

Please replace the URL with the link to the model you want to analyze.

```
wget https://github.com/onnx/models/raw/main/validated/vision/classification/mobilenet/model/mobilenetv2-10.onnx
```


#### 2-2-1. model characteristics
- operator types
    - Print the number of onnx operators, list out unique onnx operator names
- For each operator
    - print out the attributes ranges
        e.g. For a Conv2D layer, print its width, height, channel, dialation, stride, and kernel size
        
#### 2-2-2. Data bandwidth requirement 
- Assuming all required data is moved only once, how much data bandwidth is required to do model inference. 

#### 2-2-3. activation memory storage requirement
- Assuming the activations are stored to local memory and reuse multiple times, how much local memory storage is required to keep the activations?


### HW 2-3 Build tool scripts to manipulate an ONNX model graph

Assuming the target hardware can only handle 2D matrix multiplication of size 64x64x64, For Lab2-1/models/alexnet.onnx, here is the operator summary.
```
AlexNet(
  (features): Sequential(
    (0): Conv2d(3, 64, kernel_size=(11, 11), stride=(4, 4), padding=(2, 2))
    (1): ReLU(inplace=True)
    (2): MaxPool2d(kernel_size=3, stride=2, padding=0, dilation=1, ceil_mode=False)
    (3): Conv2d(64, 192, kernel_size=(5, 5), stride=(1, 1), padding=(2, 2))
    (4): ReLU(inplace=True)
    (5): MaxPool2d(kernel_size=3, stride=2, padding=0, dilation=1, ceil_mode=False)
    (6): Conv2d(192, 384, kernel_size=(3, 3), stride=(1, 1), padding=(1, 1))
    (7): ReLU(inplace=True)
    (8): Conv2d(384, 256, kernel_size=(3, 3), stride=(1, 1), padding=(1, 1))
    (9): ReLU(inplace=True)
    (10): Conv2d(256, 256, kernel_size=(3, 3), stride=(1, 1), padding=(1, 1))
    (11): ReLU(inplace=True)
    (12): MaxPool2d(kernel_size=3, stride=2, padding=0, dilation=1, ceil_mode=False)
  )
  (avgpool): AdaptiveAvgPool2d(output_size=(6, 6))
  (classifier): Sequential(
    (0): Dropout(p=0.5, inplace=False)
    (1): Linear(in_features=9216, out_features=4096, bias=True)
    (2): ReLU(inplace=True)
    (3): Dropout(p=0.5, inplace=False)
    (4): Linear(in_features=4096, out_features=4096, bias=True)
    (5): ReLU(inplace=True)
    (6): Linear(in_features=4096, out_features=1000, bias=True)
  )
)
```

There are 3 `Linear` layers in the `AlexNet` model.  Each one is much larger than 64x64x64. Each `Linear` layer needs to be decomposed into multiple 64x64x64 2D Matrix Multiplication operation. In homework 2-3, you need to write a script to replace the `Linear` operator in the AlexNet mdoel with multiple customized `2DMM_64x64x64` operators.  

The following diagram shows how a tiled matrix multiplication is done. Suppose $C=AxB$. Matrix A, B, and C, are decomposed into 4 subtiles in each matrix.  

![](https://course.playlab.tw/md/uploads/d94fa063-9611-4518-bd87-bec32705115e.png)


We can express the tiled matrix multipliction with the following expressions. 
$C_{00}= A_{00}*B_{00} + A_{01}*B_{10}$
$C_{01}= A_{00}*B_{01} + A_{01}*B_{11}$
$C_{10}= A_{10}*B_{00} + A_{11}*B_{10}$
$C_{11}= A_{10}*B_{01} + A_{11}*B_{11}$

Suppose a `Linear` layer is doing a 128x128x128 matrix multiplication. 
It can be decomposed into 8 2DMM_64x64x64 operations. The subgraph(1) of a single `Linear_128x128x128` operator can be replaced with a subgraph (2) which consists of 
- 8 `2DMM_64x64x64` operators, 
- 2 `split` operators, 
- 4 `sum` operatior and 
- 1 `concat` operator. 

In homework 2-3, please write a script to do the model graph transformation.

#### 2-3-1. create a subgraph (1) that consist of a single Linear layer of size MxKxN
#### 2-3-2. create a subgraph (2) as shown in the above diagram for the subgraph (1)  
#### 2-3-3. replace the `Linear` layers in the AlexNet with the equivalent subgraphs (2)
- saved the transformed model graph for HW2-4

#### 2-3-4. Correctness Verification
- Write a script to verify that the transformed AlexNet model is mathmatically equivalent to the original Alexnet model. 

### HW 2-4 Using Pytorch C++ API to do model analysis on the transformed model graph

Before you start this homework, you need to store the transformed model graph from HW2-3 in the torch script format as mentioned in Lab 2-4. 

#### 2-4-1. Calculate memory requirements for storing the model weights.

#### 2-4-2. Calculate memory requirements for storing the activations

#### 2-4-3. Calculate computation requirements

#### 2-4-4. Compare your results to the result in HW2-1~HW2-3
- What conclusion will you draw based on your analysis data?

## Homework Submission Rule

- **Step 1**
    Please create a  `lab02` repository in your playlab Gitlab account. Push the Homework code to your acocunt. Remember to add the `Teaching Assistants` group into the member list for our staffs to access your code. 
    - <font style="color:red"> Please submit your code in python(.py) files. Do not submit Jupyter Notebook file.</font> 
    - 關於 gitlab 開權限給助教群組的方式可以參照以下連結
        - [ACAL 2024 Curriculum GitLab 作業繳交方式說明 : Manage Permission](https://course.playlab.tw/md/CW_gy1XAR1GDPgo8KrkLgg#Manage-Permission)
- **Step 2**
 Please copy the following Homework Submission Template and fill it out as you are doing your homework. 
    - [AIAS 2024 Lab 2 HW Submission Template](https://course.playlab.tw/md/XR9C6e-NQ9eZDsC-4st8Ww?view)

When you are done, please submit your homework document link to the Playlab 作業中心, <font style="color:blue"> 清華大學與陽明交通大學的同學請注意選擇對的作業中心鏈結</font>
- [清華大學Playlab 作業中心](https://nthu-homework.playlab.tw/course?id=2)
- [陽明交通大學作業繳交中心](https://course.playlab.tw/homework/course?id=2)

    
:::info
Deadline:11:59:59pm 2022/3/10
:::

## References

