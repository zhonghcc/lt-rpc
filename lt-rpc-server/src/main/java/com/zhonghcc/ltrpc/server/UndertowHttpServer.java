package com.zhonghcc.ltrpc.server;

import com.google.common.base.Throwables;
import com.zhonghcc.ltrpc.protocal.*;
import io.undertow.Undertow;
import io.undertow.UndertowOptions;
import io.undertow.connector.ByteBufferPool;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.protocol.http.HttpOpenListener;
import io.undertow.util.HeaderMap;
import io.undertow.util.Headers;
import io.undertow.util.HttpString;
import lombok.Builder;
import lombok.Data;
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
@Getter
@Setter
public class UndertowHttpServer extends AbstractRpcServer{

    private final static String DEFAULT_HOST = "0.0.0.0";
    private final static int DEFAULT_IO_NUM = 4;
    private final static int DEFAULT_WORKER_NUM = 1;
    private final static int DEFAULT_TIMEOUT = 600;


    private int ioThreadNum;
    private int workerNum;
    private int timeout;


    @Tolerate
    public UndertowHttpServer(){
        this.ioThreadNum = DEFAULT_IO_NUM;
        this.workerNum = DEFAULT_WORKER_NUM;
        this.timeout = DEFAULT_TIMEOUT;
    }

    public void startRpcServer(){
        log.info("server starting... port{}",getPort());

        Undertow server = Undertow.builder()
                .addHttpListener(this.getPort(), DEFAULT_HOST)
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
//                        int i = new Random().nextInt();
                        if (exchange.isInIoThread()) {
                            exchange.dispatch(this);
                            return;
                        }
                        ByteBufferPool byteBufferPool = exchange.getConnection().getByteBufferPool();
                        ByteBuffer byteBuffer = byteBufferPool.allocate().getBuffer();
                        exchange.getRequestChannel().read(byteBuffer);
                        byteBuffer.flip();
                        int pos = byteBuffer.position();
                        int remaining = byteBuffer.remaining();
//                        int capacity = byteBuffer.capacity();
                        HeaderMap headerMap = exchange.getRequestHeaders();
                        String methodName = headerMap.getFirst(LtRpcMessage.FIELD_METHOD_NAME);
                        String traceId = headerMap.getFirst(LtRpcMessage.FIELD_TRACE_ID);
                        log.info("receive {},{}",methodName,traceId);
                        byte[] requestData = new byte[remaining];
                        byteBuffer.get(requestData,pos,remaining);
                        LtRpcRawRequest ltRpcRequest = LtRpcRawRequest.builder()
                                .methodName(methodName)
                                .traceId(traceId)
                                .data(requestData)
                                .build();
                        LtRpcRawResponse ltRpcResponse = getProcessor().processRawRpc(ltRpcRequest);
                        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/ltrpc");
                        exchange.getResponseHeaders().put(new HttpString(LtRpcMessage.FIELD_TRACE_ID),ltRpcResponse.getTraceId());
                        exchange.getResponseHeaders().put(new HttpString(LtRpcMessage.FIELD_MSG),ltRpcResponse.getMsg());
                        exchange.getResponseHeaders().put(new HttpString(LtRpcMessage.FIELD_SUCCESS),String.valueOf(ltRpcResponse.isSuccess()));
                        exchange.getResponseSender().send(ByteBuffer.wrap(ltRpcResponse.getData()));
                        log.info("done {},{},{}", methodName,traceId,ltRpcResponse);
                    }
                }
                ).build();
        server.start();
    }
}
