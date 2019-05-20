package com.onlydb.httpServer.handler;

import com.onlydb.data.mac.dao.TestMapper;
import com.onlydb.httpServer.service.HttpService;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import io.netty.util.ReferenceCountUtil;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

public class HttpHandler extends AHttpHandler {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Object object) throws Exception {
        HttpService httpService = getHttpService(object,channelHandlerContext);
        if(httpService!=null){
            httpService.resolveUrl();
        }else{
            channelHandlerContext.close();
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("通信断开");
        ctx.fireChannelInactive();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println(cause);
        ctx.fireExceptionCaught(cause);
    }

}
