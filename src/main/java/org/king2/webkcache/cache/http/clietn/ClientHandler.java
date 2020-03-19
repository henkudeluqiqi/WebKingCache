package org.king2.webkcache.cache.http.clietn;

/**
 * =======================================================
 * 说明:  服务端处理器
 * 作者		                时间					    注释
 *
 * @author 俞烨             2020/1/7/11:14          创建
 * =======================================================
 */

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.king2.webkcache.cache.http.aspect.HttpWebKingCache;
import org.king2.webkcache.cache.http.pojo.CacheResponse;
import org.king2.webkcache.cache.http.pojo.MsgType;

public class ClientHandler extends SimpleChannelInboundHandler<CacheResponse> {

    //处理服务端返回的数据
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, CacheResponse response) throws Exception {

        // 查询方法是否是异步
        if (response != null && response.getValue() != null && response.getValue().equals(MsgType.A_SYN + "")) {
            // 说明是异步方法 只需要删除对应的RMap不需要唤醒
            HttpWebKingCache.REQUEST_MAP.remove(response.getToken());
            return;
        }

        try {
            // 将值put进缓存中
            HttpWebKingCache.YB_RETURN_OBJ.put(response.getToken(), response.getValue() == null ? null + "" : response.getValue());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 唤醒对应的线程
            try {
                HttpWebKingCache.LATCH.remove(response.getToken()).countDown();
            } catch (Exception e) {
            }
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }
}
