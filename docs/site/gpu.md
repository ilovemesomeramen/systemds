---
layout: site
title: Run SystemDS with GPU
---
<!--
{% comment %}
Licensed to the Apache Software Foundation (ASF) under one or more
contributor license agreements.  See the NOTICE file distributed with
this work for additional information regarding copyright ownership.
The ASF licenses this file to you under the Apache License, Version 2.0
(the "License"); you may not use this file except in compliance with
the License.  You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
{% endcomment %}
-->

This guide covers the GPU hardware and software setup for using SystemDS `gpu` mode.

- [Requirements](#requirements)
- [Windows](#windows)
- [Command-line users](#command-line-users)
- [Scala Users](#scala-users)
- [Advanced Configuration](#advanced-configuration)
  - [Using single precision](#using-single-precision)
- [Training very deep network](#training-very-deep-network)
  - [Shadow buffer](#shadow-buffer)
  - [Unified memory allocator](#unified-memory-allocator)

## Requirements

### Hardware

The following GPUs are supported:

* NVIDIA GPU cards with CUDA architectures 5.0, 6.0, 7.0, 7.5, 8.0 and higher than 8.0.
For CUDA enabled gpu cards at [CUDA GPUs](https://developer.nvidia.com/cuda-gpus)
* For GPUs with unsupported CUDA architectures, or to avoid JIT compilation from PTX, or to
use difference versions of the NVIDIA libraries, build on Linux from source code.
* Release artifacts contain PTX code for the latest supported CUDA architecture. In case your
architecture specific PTX is not available enable JIT PTX with instructions compiler driver `nvcc`
[GPU Compilation](https://docs.nvidia.com/cuda/cuda-compiler-driver-nvcc/index.html#gpu-compilation).
  
  > For example, with `--gpu-code` use actual gpu names, `--gpu-architecture` is the name of virtual
  > compute architecture
  > 
  > ```sh
  > nvcc SystemDS.cu --gpu-architecture=compute_50 --gpu-code=sm_50,sm_52
  > ```

### Software

The following NVIDIA software is required to be installed in your system:

CUDA toolkit

  1. [NVIDIA GPU drivers](https://www.nvidia.com/drivers) - CUDA 10.2 requires >= 440.33 driver. see
     [CUDA compatibility](https://docs.nvidia.com/deploy/cuda-compatibility/index.html).
  3. [CUDA 10.2](https://developer.nvidia.com/cuda-10.2-download-archive)
  4. [CUDNN 7.x](https://developer.nvidia.com/cudnn)

## Windows

Install the hardware and software requirements.

Add CUDA, CUPTI, and cuDNN installation directories to `%PATH%` environmental
variable. Neural networks won't run without cuDNN `cuDNN64_7*.dll`.
See [Windows install from source guide](./windows-source-installation.md).

```sh
SET PATH=C:\Program Files\NVIDIA GPU Computing Toolkit\CUDA\v10.2\bin;%PATH%
SET PATH=C:\Program Files\NVIDIA GPU Computing Toolkit\CUDA\v10.2\extras\CUPTI\lib64;%PATH%
SET PATH=C:\Program Files\NVIDIA GPU Computing Toolkit\CUDA\v10.2\include;%PATH%
SET PATH=C:\tools\cuda\bin;%PATH%
```

## Command-line users

To enable the GPU backend via command-line, please provide `systemds-*-extra.jar` in the classpath and `-gpu` flag.

```
spark-submit --jars systemds-*-extra.jar SystemDS.jar -f myDML.dml -gpu
``` 

To skip memory-checking and force all GPU-enabled operations on the GPU, please provide `force` option to the `-gpu` flag.

```
spark-submit --jars systemds-*-extra.jar SystemDS.jar -f myDML.dml -gpu force
``` 

## Scala users

To enable the GPU backend via command-line, please provide `systemds-*-extra.jar` in the classpath and use 
the `setGPU(True)` method of MLContext API to enable the GPU usage.

```
spark-shell --jars systemds-*-extra.jar,SystemDS.jar
``` 

## Advanced Configuration

### Using single precision

By default, SystemDS uses double precision to store its matrices in the GPU memory.
To use single precision, the user needs to set the configuration property `sysds.floating.point.precision`
to `single`. However, with exception of BLAS operations, SystemDS always performs all CPU operations
in double precision.

### Training very deep network

#### Shadow buffer

To train very deep network with double precision, no additional configurations are necessary.
But to train very deep network with single precision, the user can speed up the eviction by 
using shadow buffer. The fraction of the driver memory to be allocated to the shadow buffer can  
be set by using the configuration property `sysds.gpu.eviction.shadow.bufferSize`.
In the current version, the shadow buffer is currently not guarded by SystemDS
and can potentially lead to OOM if the network is deep as well as wide.

#### Unified memory allocator

SystemDS uses CUDA's memory allocator and performs on-demand eviction using only
the Least Recently Used (LRU) eviction policy as per `sysds.gpu.eviction.policy`.
To use CUDA's unified memory allocator that performs page-level eviction instead,
please set the configuration property `sysml.gpu.memory.allocator` to `unified_memory`.
