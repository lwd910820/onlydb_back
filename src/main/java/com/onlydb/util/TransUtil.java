package com.onlydb.util;

import com.onlydb.global.prop.SocketPool;
import com.onlydb.macServer.handler.MessageHandler;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

public class TransUtil {

    public volatile static Map<String,Integer> state = new Hashtable<>();
    public volatile static Map<String,String> mes = new Hashtable<>();
    public volatile static Map<String,String> zls = new Hashtable<>();
    public volatile static Map<String,List<String>> zlqueue = new Hashtable<>();

    public static void notifyQueue(){
        for(String ip:zlqueue.keySet()){
            MessageHandler channel = SocketPool.getValidChannel(ip);
            List<String> zls = zlqueue.get(ip);
            ByteBuf buf = Unpooled.buffer();
            buf.writeBytes(CRC16Util.getSendBuf(zls.get(0)));
            channel.getCtx().writeAndFlush(buf);
        }
    }

    public static Integer validQueue(String ip,String zl){
        List<String> zls = zlqueue.get(ip);
        if(zl.substring(0,12).equals(zls.get(0))){
            zls.remove(0);
            if(zls.size()>0){
                MessageHandler channel = SocketPool.getValidChannel(ip);
                ByteBuf buf = Unpooled.buffer();
                buf.writeBytes(CRC16Util.getSendBuf(zls.get(0)));
                channel.getCtx().writeAndFlush(buf);
                return 1;
            } else {
                zlqueue.remove(ip);
                return 0;
            }
        } else {
          zlqueue.remove(ip);
          return 2;
        }
    }

    public static synchronized void removeIp(String addr){
        state.remove(addr);
        mes.remove(addr);
        zls.remove(addr);
        zlqueue.remove(addr);
    }

}
