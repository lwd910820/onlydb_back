package com.onlydb.macServer;

import com.onlydb.config.utils.LocalUtils;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class MessageServer {

    private static Logger logger = LoggerFactory.getLogger(MessageServer.class);

    private int port;

    public MessageServer(int port){
        this.port = port;
    }

    public void run() throws Exception {

        EventLoopGroup bossGroup = new NioEventLoopGroup(2);
        EventLoopGroup workerGroup = new NioEventLoopGroup(4);
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup,workerGroup)
             .channel(NioServerSocketChannel.class)
             .childHandler(new ChannelInitializer<SocketChannel>() {
                 @Override
                 protected void initChannel(SocketChannel socketChannel) throws Exception {
                     socketChannel.pipeline().addLast("count", (ChannelInboundHandlerAdapter) LocalUtils.context.getBean("countHandler"));
                     socketChannel.pipeline().addLast("idle",new IdleStateHandler(900, 600, 0, TimeUnit.SECONDS));
                     socketChannel.pipeline().addLast("mac", (ChannelInboundHandlerAdapter) LocalUtils.context.getBean("messageHandler"));
                 }
             })
             .option(ChannelOption.SO_BACKLOG,256)
             .childOption(ChannelOption.SO_KEEPALIVE,true);
            ChannelFuture f = b.bind(port).sync();
            logger.info("Socket通信端口启动："+port);
            f.channel().closeFuture().sync();
        } catch (Exception e){
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }

    }

}
