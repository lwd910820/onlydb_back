package com.onlydb.httpServer.handler;

import com.onlydb.httpServer.service.HttpService;
import com.onlydb.util.HttpUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.multipart.Attribute;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;
import io.netty.handler.codec.http.multipart.InterfaceHttpPostRequestDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AHttpHandler extends SimpleChannelInboundHandler {

    protected static Logger logger = LoggerFactory.getLogger(AHttpHandler.class);

    protected FullHttpRequest fhr;
    protected Map<String,HttpService> services = new HashMap<>();


    /*获取post请求参数*/
    public HttpService getHttpService(Object object, ChannelHandlerContext ctx) throws Exception{
        Map<String,Object> parmMap = new HashMap<>();
        HttpService service = null;
        if((service=getHttpRequest(object))!=null) {
            InterfaceHttpPostRequestDecoder decoder = new HttpPostRequestDecoder(fhr);
            decoder = decoder.offer(fhr);
            List<InterfaceHttpData> parmList = decoder.getBodyHttpDatas();
            for (InterfaceHttpData parm : parmList) {
                Attribute data = (Attribute) parm;
                parmMap.put(data.getName(), data.getValue());
            }
            service.setParam(parmMap);
            service.setCtx(ctx);
            return service;
        }
        return service;
    }

    public Boolean isHttpRequest(Object object){
        if(object!=null&&object instanceof FullHttpRequest) return true;
        else return false;
    }

    public Boolean isValidRequest(FullHttpRequest request){
        if(request==null) return false;
        if(services.containsKey(HttpUtil.getPaths(request.uri()))
                &&services.get(HttpUtil.getPaths(request.uri())).getMethods().contains(request.method())) return true;
        return false;
    }

    public HttpService getHttpRequest(Object object){
        if(isHttpRequest(object)&&isValidRequest((FullHttpRequest) object)){
            this.fhr = (FullHttpRequest) object;
            return services.get(HttpUtil.getPaths(fhr.uri()));
        }
        return null;
    }

    /*操作预设置url属性*/
    public AHttpHandler addService(HttpService... services){
        for(HttpService service:services){
            this.services.put(service.getUrl(),service);
        }
        return this;
    }

}
