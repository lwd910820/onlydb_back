package com.onlydb.config.utils;

import com.onlydb.httpServer.HttpServer;
import com.onlydb.httpServer.handler.HttpHandler;
import com.onlydb.httpServer.service.HttpService;
import com.onlydb.httpServer.service.MultipleService;
import com.onlydb.httpServer.service.SingleService;
import com.onlydb.macServer.MessageServer;
import com.onlydb.macServer.handler.CountHandler;
import com.onlydb.macServer.handler.MessageHandler;
import io.netty.handler.codec.http.HttpMethod;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

@SpringBootConfiguration
public class InitConfig {

    @Value("${netty.mac.port}")
    private Integer macport;
    @Value("${netty.http.port}")
    private Integer httpport;
    @Value("${httpurl.single}")
    private String single;
    @Value("${httpurl.multiple}")
    private String multiple;

    @Bean
    @Scope("prototype")
    public Timer one(){
        return new Timer();
    }

    @Bean
    @Scope("singleton")
    public MessageServer macserver(){
        return new MessageServer(macport);
    }

    @Bean
    @Scope("singleton")
    public HttpServer httpserver() { return new HttpServer(httpport); }

    @Bean
    @Scope("prototype")
    public MessageHandler messageHandler(){
        return new MessageHandler();
    }

    @Bean
    @Scope("singleton")
    public CountHandler countHandler(){
        return new CountHandler();
    }

    @Bean
    @Scope("prototype")
    public HttpService singleService(){
        HttpService singleService = new SingleService(single);
        singleService.setMethods(HttpMethod.POST);
        return singleService;
    }

    @Bean
    @Scope("prototype")
    public HttpService multipleService(){
        HttpService multipleService = new MultipleService(multiple);
        multipleService.setMethods(HttpMethod.POST);
        return multipleService;
    }

    @Bean
    @Scope("prototype")
    public HttpHandler httpHandler(){
        HttpHandler httpHandler = new HttpHandler();
        httpHandler.addService(singleService(),multipleService());
        return httpHandler;
    }

}
