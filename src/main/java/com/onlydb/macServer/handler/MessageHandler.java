package com.onlydb.macServer.handler;

import com.onlydb.data.mac.dao.TestMapper;
import com.onlydb.data.mac.entity.JZXX;
import com.onlydb.data.mac.entity.NormalSJ;
import com.onlydb.global.prop.SocketPool;
import com.onlydb.macServer.MessageServer;
import com.onlydb.util.CRC16Util;
import com.onlydb.util.SocketUtil;
import com.onlydb.util.TransUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.buffer.UnpooledDirectByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.SimpleDateFormat;
import java.util.*;

public class MessageHandler extends AMessageHandler {

    private JZXX jzxx = new JZXX();
//    private String command = "";
//    private boolean validAll = false;

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
//        ByteBuf b = Unpooled.buffer();
        if (evt instanceof IdleStateEvent){
            IdleStateEvent event = (IdleStateEvent) evt;
            if(event.state() == IdleState.READER_IDLE) {
                logger.error("15分钟内未收到信息，强制关闭连接");
                ctx.close();
            }
//            } else if(event.state() == IdleState.WRITER_IDLE) {
////                b.writeBytes(CRC16Util.getSendBuf("000400000000"));
////                ctx.writeAndFlush(b);
//                TransUtil.state.put(address,3);
//                sendMes("000400000000");
//            }
        }else {
            super.userEventTriggered(ctx,evt);
        }

    }

    @Override
    public void channelActive(ChannelHandlerContext ctx){
        String[] info = SocketUtil.getNetMes(ctx);
        ip = info[0];
        port = info[1];
        address = ip+":"+port;
        jzxx.setJzip(ip);
        jzxx.setJzport(port);
        SocketPool.addInvalid(address,this);
        logger.info(address+"已加入非法连接库");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        byte[] bytes = msgToByte(msg);
        logger.info("JZ:"+jzxx.getJzid()+":"+CRC16Util.getBufHexStr(bytes));
        try {
            if(!valid){
                if(passValid(bytes)){
                    valid = true;
                    saveJzxx(bytes);
                    if(jzxx.getId()!=null) logger.info("jqxx数据库插入成功");
                    else {
                        logger.error("jqxx数据库插入失败");
                        ctx.close();
                    }
                    TransUtil.state.put(address,0);
                    TransUtil.mes.put(address,"");
                    this.ctx = ctx;
                    SocketPool.addValid(address,this);
                    logger.warn(address+"已在非法库中删除");
                    logger.warn(address+"已加入合法连接库");
                }else{
                    ctx.close();
                }

            } else {
                if(CRC16Util.checkBuf(bytes)){
                    saveAll(bytes);
                }
            }
        } finally {
            ReferenceCountUtil.release(msg);
        }

    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        if(SocketPool.removeInvalid(address,this)){
            logger.info(address+"已在非法库中删除");
        } else {
            deleteJzxx();
            SocketPool.removeValid(address,this);
            logger.info(address+"已在合法库中删除");
        }
        TransUtil.removeIp(address);
        logger.warn("JZ:"+jzxx.getJzid()+":"+address+"已删除");
        ctx.fireChannelInactive();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) { // (4)
        // Close the connection when an exception is raised.
        logger.error("系统出现异常，请检查"+cause);
        ctx.fireExceptionCaught(cause);
        ctx.close();
    }

    private boolean saveJzxx(byte[] bytes){
        String jzsj = CRC16Util.getBufHexStr(Arrays.copyOfRange(bytes,3,bytes.length));
        if(jzsj.length()<38) return false;
        jzxx.setJzid(jzsj.substring(0,4));
        jzxx.setId(testMapper.getJzUUID(jzxx.getJzid()));
        Integer num = Integer.parseInt(jzsj.substring(4,6),16);
        for(int i=0;i<num;i++){
            jqs.add("0"+(i+1));
        }
        testMapper.insertJzxx(jzxx,"0");
        testMapper.updateJzxx(jzxx,new Date(),"0");
//        testMapper.connectedJq(jzxx,jqs,"0");
        return true;
    }

    private boolean deleteJzxx(){
        TransUtil.removeIp(address);
        if(jzxx.getId()!=null&&testMapper.validIp(jzxx)>0) {
            testMapper.insertJzxx(jzxx,"1");
            testMapper.updateJzxx(jzxx,new Date(),"1");
            testMapper.unconnectedJq(jzxx,"1");
        }else{
            return false;
        }
        return true;
    }

    private boolean saveJqxx(String re){
        if (re!=null&&re.length()>=18){
            if(re.substring(0,12).equals("000400000000")){
//                String jqxx = CRC16Util.getBufHexStr(Arrays.copyOfRange(bytes,3,bytes.length-2));
                String jqxx = re.substring(14,re.length()-4);
                Integer jqsl = Integer.parseInt(re.substring(12,14));
                for(int b=0;b<jqsl;b++){
                    int start = b*18;
                    int end = (b+1)*18;
                    NormalSJ normalSJ = new NormalSJ(jqxx.substring(start,end),
                            testMapper.getJqUUID(jzxx.getId(),jqxx.substring(start,start+2)));
                    testMapper.updateJqsj(normalSJ,new Date());
                    testMapper.inserJqsj(normalSJ,jzxx);
                }
                return true;
            }
        }
        return false;
    }

    private boolean savePumb(String re){
        if(re!=null&&re.length()>=18){
            if(re.substring(0,12).equals("FF0300940001")){
                String sbzt = re.substring(12,re.length()-4);
                jzxx.setSbzt(sbzt);
                testMapper.updateSBZT(jzxx);
                testMapper.insertSBZT(jzxx,"0");
                return true;
            }
        }
        return false;
    }

    private boolean saveElectry(String re){
        if(re!=null&&re.length()>=20){
            if(re.substring(0,12).equals("FF0300980004")){
                int k = Integer.valueOf(re.substring(12,20),16);
                Float dbds = -1f;
                try {
                    dbds = Float.intBitsToFloat(k);
                } catch (NumberFormatException e){
                    dbds = -1f;
                }
                jzxx.setDbds(dbds);
                testMapper.updateSBZT(jzxx);
                return true;
            }
        }
        return false;
    }

    private void saveAll(byte[] bytes){
        String re = CRC16Util.getBufHexStr(bytes);
        if(TransUtil.state.get(address).equals(1)){
            if(TransUtil.zls.get(address).equals(re.substring(0,12))){
                updateCom(re);
                special(re);
                TransUtil.mes.put(address,re);
                TransUtil.state.put(address,0);
                logger.info(address+"请求参数插入成功");
            }
        } else if(TransUtil.state.get(address).equals(2)){
            Integer i = TransUtil.validQueue(address,re);
            if(i==1) {
                updateCom(re);
                logger.warn(address+"单条指令执行完毕");
            } else if(i==0) {
                updateCom(re);
                logger.info(address+"全部指令执行完毕");
                TransUtil.state.put(address,0);
            } else if(i==2) {
                logger.error(address+"指令异常");
                TransUtil.state.put(address,0);
            }
//            validCommand(re);
        } else if(TransUtil.state.get(address).equals(3)){
            try {
                saveJqxx(re);
                logger.info(address+"常用参数插入成功");
            } finally {
                TransUtil.state.put(address,0);
            }
        } else if(TransUtil.state.get(address).equals(4)){
            try {
                savePumb(re);
                logger.info(address+"机组水泵状态插入成功");
            } finally {
                TransUtil.state.put(address,0);
            }
        } else if(TransUtil.state.get(address).equals(5)){
            try {
                saveElectry(re);
                logger.info(address+"机组电表读数插入成功");
            } finally {
                TransUtil.state.put(address,0);
            }
        }
    }

    private void special(String re){
        if(re.substring(2,12).equals("0300000077")){
            NormalSJ normalSJ = new NormalSJ();
            normalSJ.setJqid(testMapper.getJqUUID(jzxx.getId(),re.substring(0,2)));
            normalSJ.setMsxz(re.substring(14,16));
            if(normalSJ.getMsxz().equals("00")){
                normalSJ.setSdwd(Integer.parseInt(re.substring(18,20),16));
                normalSJ.setCswd(Integer.parseInt(re.substring(176,178),16));
                normalSJ.setHswd(Integer.parseInt(re.substring(174,176),16));
            } else if(normalSJ.getMsxz().equals("01")) {
                normalSJ.setSdwd(Integer.parseInt(re.substring(26,28),16));
                normalSJ.setCswd(Integer.parseInt(re.substring(234,236),16));
                normalSJ.setHswd(Integer.parseInt(re.substring(190,192),16));
            } else {
                normalSJ.setSdwd(Integer.parseInt(re.substring(22,24),16));
                normalSJ.setCswd(Integer.parseInt(re.substring(234,236),16));
                normalSJ.setHswd(Integer.parseInt(re.substring(190,192),16));
            }
            normalSJ.setKzbz(re.substring(12,14));
            normalSJ.setGzdm(re.substring(154,156));
            normalSJ.setJqlx(re.substring(222,224));
            testMapper.insertAllJqsj(re.substring(12,re.length()-4),jzxx,normalSJ);
            testMapper.updateJqsj(normalSJ,new Date());
            testMapper.inserJqsj(normalSJ,jzxx);
        }
    }

    //更新机器操作记录
    public boolean updateCom(String re){
        String jqbh = null;
        if(re.length()>=16) jqbh = re.substring(0,2);
        if(jqbh!=null||!jqbh.equals("")) {
            if(re.substring(2,4).equals("06")){
                testMapper.updateJQCZ(re.substring(2,12),
                        testMapper.getJqUUID(jzxx.getId(),jqbh),new Date());
                return true;
            }
        }
        return false;
    }

}
