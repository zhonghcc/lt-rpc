package com.zhonghcc.ltrpc.server;

import com.google.common.base.Throwables;
import io.undertow.Undertow;
import io.undertow.UndertowOptions;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.protocol.http.HttpOpenListener;
import io.undertow.util.Headers;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Tolerate;
import lombok.extern.slf4j.Slf4j;
import org.xnio.*;
import org.xnio.channels.AcceptingChannel;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.Random;

import static io.undertow.UndertowOptions.ENABLE_HTTP2;

@Slf4j
@Builder
@Getter
public class UndertowHttpServer implements LtRpcServer{

    private final static String DEFAULT_HOST = "localhost";
    private final static int DEFAULT_IO_NUM = 4;
    private final static int DEFAULT_WORKER_NUM = 1;
    private final static int DEFAULT_TIMEOUT = 600;

    private int port;
    private String host;
    private int ioThreadNum;
    private int workerNum;
    private int timeout;

    @Tolerate
    private UndertowHttpServer() {

    }
    @Tolerate
    public UndertowHttpServer(int port){
        this(port,DEFAULT_HOST);
    }
    @Tolerate
    public UndertowHttpServer(int port,String host){
        this.port = port;
        this.host = host;
        this.ioThreadNum = DEFAULT_IO_NUM;
        this.workerNum = DEFAULT_WORKER_NUM;
        this.timeout = DEFAULT_TIMEOUT;

    }

    public void start(){
        log.info("server starting... port{}",port);

        Undertow server = Undertow.builder()
                .addHttpListener(port, host)
                .setServerOption(ENABLE_HTTP2,true)
                .setWorkerOption(Options.WORKER_IO_THREADS,ioThreadNum)
                .setWorkerOption(Options.WORKER_TASK_CORE_THREADS,workerNum)
                .setWorkerOption(Options.WORKER_TASK_MAX_THREADS,workerNum)
//                .setWorkerOption(Options.TCP_NODELAY, true)
//                .setWorkerOption(Options.REUSE_ADDRESSES, true)
                .setSocketOption(Options.READ_TIMEOUT,timeout)
                .setSocketOption(Options.WRITE_TIMEOUT,timeout)


                .setHandler(new HttpHandler() {
                    @Override
                    public void handleRequest(final HttpServerExchange exchange) throws Exception {
                        int i = new Random().nextInt();
                        log.info("receive {}",i);
                        if (exchange.isInIoThread()) {
                            exchange.dispatch(this);
                            return;
                        }
                        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/plain");
                        exchange.getResponseSender().send("Hello World");
                        log.info("done {}", i);
                    }
                }
                ).build();
        server.start();
    }
//    public void start2(){
//        try {
//            Xnio xnio = Xnio.getInstance();
//            int bufferSize = 1024*10;
//            int buffersPerRegion = 10;
//
//            HttpHandler rootHandler = new HttpHandler() {
//                @Override
//                public void handleRequest(final HttpServerExchange exchange) throws Exception {
//                    log.info("receive");
//                    Thread.sleep(8000);
//                    exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/plain");
//                    exchange.getResponseSender().send("Hello World");
//                    log.info("done");
//                }
//            };
//
//            XnioWorker worker = xnio.createWorker(OptionMap.builder()
//                    .set(Options.WORKER_IO_THREADS, ioThreadNum)
//                    .set(Options.WORKER_TASK_CORE_THREADS, workerNum)
//                    .set(Options.WORKER_TASK_MAX_THREADS, workerNum)
//                    .set(Options.TCP_NODELAY, true)
//                    .getMap());
//
//            OptionMap socketOptions = OptionMap.builder()
//                    .set(Options.WORKER_IO_THREADS, ioThreadNum)
//                    .set(Options.TCP_NODELAY, true)
//                    .set(Options.REUSE_ADDRESSES, true)
//                    .getMap();
//
//            OptionMap serverOptions = OptionMap.builder()
//                    .set(ENABLE_HTTP2,true)
//                    .getMap();
//
//            Pool<ByteBuffer> buffers = new ByteBufferSlicePool(BufferAllocator.DIRECT_BYTE_BUFFER_ALLOCATOR, bufferSize, bufferSize * buffersPerRegion);
//
//            HttpOpenListener openListener = new HttpOpenListener(buffers, OptionMap.builder().set(UndertowOptions.BUFFER_PIPELINED_DATA, true).addAll(serverOptions).getMap());
//            openListener.setRootHandler(rootHandler);
//            ChannelListener<AcceptingChannel<StreamConnection>> acceptListener = ChannelListeners.openListenerAdapter(openListener);
//            AcceptingChannel<? extends StreamConnection> server = worker.createStreamConnectionServer(new InetSocketAddress(Inet4Address.getByName(host), port), acceptListener, socketOptions);
//            server.resumeAccepts();
//        }catch (IOException ioe){
//            log.error("create server fail {}", Throwables.getStackTraceAsString(ioe));
//        }
//    }
    public static void main(final String[] args) {
        LtRpcServer ltRpcServer = new UndertowHttpServer(8080);
        ltRpcServer.start();
    }
}
