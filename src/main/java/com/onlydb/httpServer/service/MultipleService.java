package com.onlydb.httpServer.service;

import com.onlydb.data.mac.dao.TestMapper;
import com.onlydb.data.mac.entity.NormalSJ;
import com.onlydb.global.prop.SocketPool;
import com.onlydb.global.task.NormalTask;
import com.onlydb.macServer.handler.MessageHandler;
import com.onlydb.util.TransUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpResponseStatus;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

public class MultipleService extends HttpService {

    @Autowired
    private TestMapper testMapper;

    public MultipleService(String url) {
        super(url);
    }

    @Override
    public void resolveUrl() {
        if(!testMapper.getRK().contains(param.get("rk"))){
            send(ctx,"非法请求", HttpResponseStatus.OK);
            return;
        } else {
            if(TransUtil.zlqueue.size()!=0) {
                send(ctx,"当前有群发任务正在查询",HttpResponseStatus.OK);
                return;
            }
            String allxx = (String) param.get("jqs");
            String jqzl = (String) param.get("jqzl");
            if(allxx==null||allxx.equals("")||jqzl==null||jqzl.equals("")) send(ctx,"参数错误",HttpResponseStatus.OK);
            String[] jzs = allxx.split(";");
            Map<String,List<String>> jqs = new HashMap<>();
            List<String> jqlist = new ArrayList<>();
            List<String> jzbhs = new ArrayList<>();
            for(String jz:jzs){
                String[] jzxx = jz.split(":");
                if(jzxx!=null&&jzxx.length==2){
                    List<String> list = new ArrayList<>();
                    for(String jq:jzxx[1].split(",")){
                        list.add(jq+jqzl);
                        jqlist.add(jzxx[0]+jq);
                    }
                    jqs.put(jzxx[0], list);
                    jzbhs.add(jzxx[0]);
                } else {
                    send(ctx,"请求参数有误",HttpResponseStatus.OK);
                    return;
                }
            }
            List<Map<String,Object>> jzxxs = testMapper.getAddresses(jzbhs);
            Map<String,MessageHandler> channels = new HashMap<>();
            List<String> workIp = new ArrayList<>();
            for(Map<String,Object> jzxx:jzxxs){
                String ip = jzxx.get("JZIP")+":"+jzxx.get("JZPORT");
                String jzbh = (String) jzxx.get("JZID");
                if(TransUtil.state.get(ip)==null){
                    send(ctx,ip+"机组未连接",HttpResponseStatus.OK);
                    return;
                }
                if(!TransUtil.state.get(ip).equals(0)) {
                    workIp.add(jzbh);
                    continue;
                } else {
                    TransUtil.state.put(ip,2);
                    TransUtil.zlqueue.put(ip,jqs.get(jzbh));
                }
            }
            sendMes(workIp);
//            testMapper.updateJQCZ(jqlist);
        }
    }

    private void sendMes(List<String> workip){
        TransUtil.notifyQueue();
        Integer k = 0;
        while(TransUtil.zlqueue.size()>0&&k<10){
            try {
                Thread.sleep(1000);
                k++;
            } catch (Exception e) {
                send(ctx,"系统请求异常",HttpResponseStatus.OK);
                for(String key:TransUtil.zlqueue.keySet()){
                    if(TransUtil.state.get(key)==2) TransUtil.state.put(key,0);
                }
                TransUtil.zlqueue.clear();
            }
        }
        StringBuilder s = new StringBuilder("未处理机组号:");
        for(String bh:workip){
            s.append(bh+",");
        }
        s.append(";");
        if(TransUtil.zlqueue.size()==0) send(ctx,s.toString()+"处理成功",HttpResponseStatus.OK);
        else {
            for(String key:TransUtil.zlqueue.keySet()){
                if(TransUtil.state.get(key)==2) {
                    s.append(key+"#");
                    for(String zl:TransUtil.zlqueue.get(key)){
                        s.append(zl.substring(0,2)+",");
                    }
                    s.append("*");
                    TransUtil.state.put(key,0);
                }else if(TransUtil.state.get(key)==null){
                    s.append(key+"已断开*");
                }
            }
            TransUtil.zlqueue.clear();
            send(ctx,s.toString(),HttpResponseStatus.OK);
        }
    }

}
