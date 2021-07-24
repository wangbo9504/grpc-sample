package io.grpc.sample.helloworld;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 * Server that manages startup/shutdown of a {@code Greeter} server.
 * @author wangbo
 * @since 2021/7/6
 */
public class HelloWorldServer {
    private static final Logger logger = Logger.getLogger(HelloWorldServer.class.getName());

    private Server server;

    private void start() throws IOException {
        int port = 50051;
        // 创建server对象, 监听50051端口
        server = ServerBuilder.forPort(port)
                // 注册服务
                .addService(new GreeterImpl())
                .build()
                .start();
        logger.info("Server started, listening on " + port);
        // 添加JVM停止时的回调函数
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                // Use stderr here since the logger may have been reset by its JVM shutdown hook.
                System.err.println("*** shutting down gRPC server since JVM is shutting down");
                try {
                    HelloWorldServer.this.stop();
                } catch (InterruptedException e) {
                    e.printStackTrace(System.err);
                }
                System.err.println("*** server shut down");
            }
        });
    }

    private void stop() throws InterruptedException {
        if (server != null) {
            // 等待所有提交的任务执行结束关闭服务
            server.shutdown().awaitTermination(30, TimeUnit.SECONDS);
        }
    }

    /**
     * Await termination on the main thread since the grpc library uses daemon threads.
     */
    private void blockUntilShutdown() throws InterruptedException {
        if (server != null) {
            server.awaitTermination();
        }
    }

    /**
     * Main launches the server from the command line.
     */
    public static void main(String[] args) throws IOException, InterruptedException {
        final HelloWorldServer server = new HelloWorldServer();
        // 启动服务
        server.start();
        // 阻塞当前主线程
        server.blockUntilShutdown();
    }

    // 扩展grpc自动生成的服务接口抽象, 实现业务功能
    static class GreeterImpl extends GreeterGrpc.GreeterImplBase {

        @Override
        public void sayHello(HelloRequest req, StreamObserver<HelloReply> responseObserver) {
            // 构建响应消息, 从请求消息中获取姓名并在前面拼接上"Hello "
            HelloReply reply = HelloReply.newBuilder().setMessage("Hello " + req.getName()).build();
            // 在流关闭或抛出异常前可以调用多次
            responseObserver.onNext(reply);
            // 关闭流
            responseObserver.onCompleted();
        }
    }
}
