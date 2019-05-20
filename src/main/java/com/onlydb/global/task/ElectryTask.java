package com.onlydb.global.task;

import com.onlydb.global.prop.SocketPool;
import com.onlydb.macServer.handler.MessageHandler;
import com.onlydb.util.TransUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ElectryTask implements Runnable {

    protected static Logger logger = LoggerFactory.getLogger(ElectryTask.class);

    @Override
    public void run() {
        if(SocketPool.validNum().equals(0)) return;
        logger.info("请求电表"+"合法连接数:"+SocketPool.validNum());
        try {
            for(MessageHandler mh:SocketPool.getValidChannels()){
                if(TransUtil.state.get(mh.getAddress())==null) {
                    logger.error("异常地址"+mh.getAddress());
                    continue;
                }
                if(TransUtil.state.get(mh.getAddress()).equals(0)){
                    TransUtil.state.put(mh.getAddress(),5);
                    mh.sendMes("ff0300980004");
                }
            }
            Thread.sleep(10000);
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            for(MessageHandler mh:SocketPool.getValidChannels()){
                if(TransUtil.state.get(mh.getAddress()).equals(5)){
                    TransUtil.state.put(mh.getAddress(),0);
                }
            }
        }
    }
}
