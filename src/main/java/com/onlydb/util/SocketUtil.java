package com.onlydb.util;

import io.netty.channel.ChannelHandlerContext;

public class SocketUtil {

    public static String[] getNetMes(ChannelHandlerContext ctx){
        String[] mes = new String[2];
        String address = ctx.channel().remoteAddress().toString();
        int k = address.indexOf(":");
        if(address!=null&&!address.equals("")&&k>2){
            mes[0] = address.substring(1,k);
            mes[1] = address.substring(k+1,address.length());
        }
        return mes;
    }

}
