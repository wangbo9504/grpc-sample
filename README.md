## 项目介绍
> 该项目使用 `Java` 语言作为 `GRPC` 的服务端<br> 
> `Java` 和 `Python` 语言分别作为 `GRPC` 的两个客户端<br>
> 使用一个简单 `rpc` 方法展示了 `GRPC` 的多语言特性

### 准备工作
#### IDEA 安装 Protobuf Support 或 Protobuf Editor 插件用于识别 proto 文件
https://github.com/ksprojects/protobuf-jetbrains-plugin/releases<br>
https://plugins.jetbrains.com/plugin/14004-protocol-buffers/

#### 准备好 python 环境并安装 grpc 相关插件
```
pip3 install grpcio
pip3 install grpcio-tools
```

#### 使用 protobuf-maven-plugin 插件编译proto文件生成Java文件
我们在使用时，只需要执行以下两个指令<br>
* protobuf:compile<br> 
默认在 target/generated-sources/protobuf/java 目录下生成消息文件<br>
* protobuf:compile-custom<br>
默认在 target/generated-sources/protobuf/grpc-java 目录下生成接口服务文件<br>
执行完成后会生成如下文件<br>

```
target/generated-sources/
├── annotations
└── protobuf
    ├── grpc-java
    │   └── io/grpc/sample/helloworld
    │       └── GreeterGrpc.java
    └── java
        └── io/grpc/sample/helloworld
            ├── HelloReply.java
            ├── HelloReplyOrBuilder.java
            ├── HelloRequest.java
            ├── HelloRequestOrBuilder.java
            └── HelloWorldProto.java
```

#### 使用 grpcio-tools 工具编译proto文件生成Python文件
```
# 进入proto目录
cd proto
# 编译proto文件并将对应的python文件输出到python目录
python -m grpc_tools.protoc --python_out=../python --grpc_python_out=../python -I. helloworld.proto
```
执行完成后会在python目录下生成如下文件
```
python
├── helloworld_pb2.py # 消息文件
└── helloworld_pb2_grpc.py # 接口服务文件
```

#### 执行步骤
1. 执行 `io.grpc.sample.helloworld.HelloWorldServer#main` 启动Java服务端
2. 执行 `io.grpc.sample.helloworld.HelloWorldClient#main` 发起Java客户端调用
3. 执行 `python src/main/python/helloworld_client.py` 发起Python客户端调用
