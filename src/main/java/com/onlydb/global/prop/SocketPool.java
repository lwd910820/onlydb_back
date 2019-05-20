package com.onlydb.global.prop;

import com.onlydb.macServer.handler.MessageHandler;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;

import java.util.*;

public class SocketPool {

    private static final Map<String,MessageHandler> validpool = new Hashtable<>();
    private static final Map<String,MessageHandler> invalidpool = new Hashtable<>();

    /*socketpool基础操作*/
    public static boolean addInvalid(String socketname, MessageHandler channel){
        if(channel.equals(invalidpool.put(socketname,channel))) return true;
        return false;
    }

    public static boolean addValid(String socketname, MessageHandler channel){
        if(invalidpool.containsKey(socketname)) {
            validpool.put(socketname,invalidpool.remove(socketname));
            return true;
        } else {
            validpool.put(socketname,channel);
        }
        return false;
    }

    public static Integer validNum(){
        return validpool.size();
    }

    public static Integer invalidNum() { return invalidpool.size(); }

    public static Collection<MessageHandler> getInvalidChannels(){
        return invalidpool.values();
    }

    public static Collection<MessageHandler> getValidChannels(){
        return validpool.values();
    }

    public static boolean removeInvalid(String socketname, MessageHandler channel){
        return channel.equals(invalidpool.remove(socketname));
    }

    public static boolean removeValid(String socketname, MessageHandler channel){
        return channel.equals(validpool.remove(socketname));
    }

    public static Set<String> getInvalidNames(){
        return invalidpool.keySet();
    }

    public static Set<String> getValidNames(){
        return validpool.keySet();
    }

    public static MessageHandler getValidChannel(String socketname){ return validpool.get(socketname); }

    public static MessageHandler getInvalidChannel(String socketname){ return invalidpool.get(socketname); }

}
