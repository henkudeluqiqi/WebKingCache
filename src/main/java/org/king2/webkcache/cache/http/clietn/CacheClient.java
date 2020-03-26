package org.king2.webkcache.cache.http.clietn;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.log4j.Log4j;
import lombok.extern.slf4j.Slf4j;
import org.king2.webkcache.cache.http.aspect.HttpWebKingCache;
import org.king2.webkcache.cache.http.encoding.RpcDecoder;
import org.king2.webkcache.cache.http.encoding.RpcEncoder;
import org.king2.webkcache.cache.http.pojo.CacheRequest;
import org.king2.webkcache.cache.http.pojo.CacheResponse;
import org.king2.webkcache.cache.http.pojo.MsgType;
import org.king2.webkcache.cache.http.retry.RequestRetry;

import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
public class CacheClient {
    private final String host;
    private final int port;
    private Channel channel;
    private AtomicBoolean isActive = new AtomicBoolean(false);

    public AtomicBoolean getIsActive() {
        return isActive;
    }

    //连接服务端的端口号地址和端口号
    public CacheClient(String host, int port) {
        this.host = host;
        this.port = port;
    }


    public void start() throws Exception {
        if (!isActive.get()) {
            isActive.set(true);
            final EventLoopGroup group = new NioEventLoopGroup();
            Bootstrap b = new Bootstrap();
            b.group(group).channel(NioSocketChannel.class)  // 使用NioSocketChannel来作为连接用的channel类
                    .handler(new ChannelInitializer<SocketChannel>() { // 绑定连接初始化器
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            log.info("正在连接WebKingCache服务器...");
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast(new RpcEncoder(CacheRequest.class)); //编码request
                            pipeline.addLast(new RpcDecoder(CacheResponse.class)); //解码response
                            pipeline.addLast(new ClientHandler()); //客户端处理类
                        }
                    });
            //发起异步连接请求，绑定连接端口和host信息
            final ChannelFuture future = b.connect(host, port).sync();

            future.addListener(new ChannelFutureListener() {

                @Override
                public void operationComplete(ChannelFuture arg0) throws Exception {
                    if (future.isSuccess()) {
                        log.info("连接WebKingCache服务器成功");
                        ping();
                        RequestRetry.monitor();
                    } else {
                        log.error("连接WebKingCache服务器失败");
                        future.cause().printStackTrace();
                        group.shutdownGracefully(); //关闭线程组
                    }
                }
            });

            this.channel = future.channel();
        }
    }

    /**
     * ping
     */
    private void ping() {


        new Thread(() -> {
            HttpWebKingCache instance = HttpWebKingCache.getInstance();
            while (true) {
                String token = UUID.randomUUID().toString() + System.currentTimeMillis();
                CountDownLatch value = new CountDownLatch(1);
                HttpWebKingCache.LATCH.put(token, value);
                instance.send(new CacheRequest(null, null, MsgType.PING, token));
                // 发送消息后 等待五秒钟 如果五秒钟没有响应就认定为断开连接
                try {
                    value.await(5, TimeUnit.SECONDS);
                    // 查询是否存在返回的数据
                    if (HttpWebKingCache.YB_RETURN_OBJ.get(token) == null) {
                        isActive.set(false);
                        // 没有数据认定为断开连接
                        // 需要重新进行连接
                        log.error("与WebKingCache服务器断开连接");
                        instance.close();
                        // 进行重新连接
                        log.error("正在尝试重新连接....");
                        instance.start();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        Thread.sleep(20000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    public Channel getChannel() {
        return channel;
    }
}
