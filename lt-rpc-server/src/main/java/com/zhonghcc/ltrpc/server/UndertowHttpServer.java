package com.zhonghcc.ltrpc.server;

import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Tolerate;
import lombok.extern.slf4j.Slf4j;
import org.xnio.Options;

import static io.undertow.UndertowOptions.ENABLE_HTTP2;

@Slf4j
@Builder
@Getter
public class UndertowHttpServer {

    private final static String DEFAULT_HOST = "localhost";
    private final static int DEFAULT_IO_NUM = 20;
    private final static int DEFAULT_WORKER_NUM = 200;
    private final static int DEFAULT_TIMEOUT = 6000;

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
                .setServerOption(Options.WORKER_IO_THREADS,ioThreadNum)
                .setServerOption(Options.WORKER_TASK_CORE_THREADS,workerNum)
                .setServerOption(Options.WORKER_TASK_MAX_THREADS,workerNum)
                .setServerOption(Options.READ_TIMEOUT,timeout)
                .setServerOption(Options.WRITE_TIMEOUT,timeout)


                .setHandler(new HttpHandler() {
                    @Override
                    public void handleRequest(final HttpServerExchange exchange) throws Exception {
                        Thread.sleep(6000);
                        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/plain");
                        exchange.getResponseSender().send("Hello World");
                    }
                }).build();
        server.start();
    }
    public static void main(final String[] args) {
        UndertowHttpServer undertowHttpServer = new UndertowHttpServer(8080);
        undertowHttpServer.start();
    }
}
