package com.onlydb.httpServer.service;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class HttpService {

    protected Map<String,Object> param;
    protected List<HttpMethod> methods = new ArrayList<>();
    protected ChannelHandlerContext ctx;
    protected String url;

    public HttpService(String url){
        this.url = url;
    }

    public abstract void resolveUrl();

    public Map<String, Object> getParam() {
        return param;
    }

    public void setParam(Map<String, Object> param) {
        this.param = param;
    }

    public List<HttpMethod> getMethods() {
        return methods;
    }

    public void setMethods(HttpMethod... methods) {
        for(HttpMethod method:methods){
            this.methods.add(method);
        }
    }

    public ChannelHandlerContext getCtx() {
        return ctx;
    }

    public void setCtx(ChannelHandlerContext ctx) {
        this.ctx = ctx;
    }

    protected void send(ChannelHandlerContext ctx, String context,HttpResponseStatus status) {
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,
                status, Unpooled.copiedBuffer(context, CharsetUtil.UTF_8));
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain; charset=UTF-8");
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
        ctx.close();
    }

    public void setUrl(String url){
        this.url = url;
    }

    public String getUrl() {
        return url;
    }
}
