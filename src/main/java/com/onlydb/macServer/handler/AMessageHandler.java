package com.onlydb.macServer.handler;

import com.onlydb.data.mac.dao.TestMapper;
import com.onlydb.util.CRC16Util;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public abstract class AMessageHandler extends ChannelInboundHandlerAdapter {

    protected static Logger logger = LoggerFactory.getLogger(AMessageHandler.class);

    @Autowired
    protected TestMapper testMapper;

    protected boolean valid = false;
    protected String ip;
    protected String port;
    protected String address;
    protected ChannelHandlerContext ctx;
    protected List<String> jqs = new ArrayList<>();

    protected byte[] msgToByte(Object msg) {
        ByteBuf b = (ByteBuf) msg;
        byte[] rb = new byte[b.readableBytes()];
        b.readBytes(rb);
        return rb;
    }

    /*判断连接是否合法*/
    protected boolean passValid(byte[] bytes) {
        byte[] vkey = new byte[3];
        if(bytes.length<3) return false;
        for(int i=0;i<3;i++){
            vkey[i] = bytes[i];
        }
        String key = testMapper.getValidKey();
        if(key.equals(CRC16Util.getBufHexStr(vkey))) return true;
        return false;
    }

    public boolean sendMes(String mes){
        ByteBuf b = Unpooled.buffer();
        b.writeBytes(CRC16Util.getSendBuf(mes));
        ctx.writeAndFlush(b);
        //存在内存溢出时启用，待观察
        //ReferenceCountUtil.release(b);
        return true;
    }

    public ChannelHandlerContext getCtx() {
        return ctx;
    }


    public String getAddress() { return address; }
}
