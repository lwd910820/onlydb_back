package com.onlydb.macServer.handler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ChannelHandler.Sharable
public class CountHandler extends ChannelInboundHandlerAdapter {

    private static Logger logger = LoggerFactory.getLogger(CountHandler.class);

    private volatile Integer count = 0;

    @Override
    public void channelActive(ChannelHandlerContext ctx){
        synchronized (count){
            count++;
            logger.info("当前连接数"+count);
            ctx.fireChannelActive();
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx){
        synchronized (count){
            count--;
            logger.info("当前连接数"+count);
            ctx.fireChannelInactive();
        }
    }

}
