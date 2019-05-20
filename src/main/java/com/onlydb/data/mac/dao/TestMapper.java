package com.onlydb.data.mac.dao;

import com.onlydb.data.mac.entity.JZXX;
import com.onlydb.data.mac.entity.NormalSJ;
import com.onlydb.data.mac.entity.PassValid;
import com.onlydb.data.mac.entity.SYS_USER_ROLE;
import org.apache.ibatis.annotations.*;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Mapper
public interface TestMapper {

//    @Select("select * from SYS_USER_ROLE t")
//    public List<SYS_USER_ROLE> getRole();

    @Select("select VALUE from T_ZD_PROPERTIES where name='rk' and del_flag='0'")
    public List<String> getRK();

    @Select("select jzip,jzport from T_ZD_JZXX a,T_HC_JZXX b " +
            "where a.id = b.jzid and a.jzid=#{jzbh} and a.del_flag='0' and b.ljzt='0'")
    public Map<String,String> getJzAddr(@Param("jzbh") String jzbh);

    @Select("select VALUE from T_ZD_PROPERTIES where del_flag='0' " +
            "and name = 'vk' ")
    public String getValidKey();

    @Select("select t.id from T_ZD_JZXX t where t.jzid=#{jzbh} and del_flag='0'")
    public String getJzUUID(@Param("jzbh") String jzbh);

    @Insert("insert into t_grgl_jzsj(jzuuid,ip,port,jzljzt) " +
            "values " +
            "(#{jzxx.id}," +
            "#{jzxx.jzip,jdbcType=VARCHAR}," +
            "#{jzxx.jzport,jdbcType=VARCHAR}," +
            "#{jzzt})")
    public Integer insertJzxx(@Param("jzxx")JZXX jzxx,@Param("jzzt") String jzzt);

    @Update("update T_HC_JZXX set " +
            "jzip=#{jzxx.jzip,jdbcType=VARCHAR}," +
            "jzport=#{jzxx.jzport,jdbcType=VARCHAR}," +
            "dlsj=#{date,jdbcType=TIMESTAMP}," +
            "ljzt=#{zt} " +
            "where jzid=#{jzxx.id}")
    public Integer updateJzxx(@Param("jzxx")JZXX jzxx, @Param("date")Date date,@Param("zt")String zt);

    @Select("select id from T_ZD_JQXX t where jzxxuuid=#{jzuuid} and jqid=#{jqbh} and del_flag='0'")
    public String getJqUUID(@Param("jzuuid") String jzuuid, @Param("jqbh") String jqbh);

    @Update("update T_GRGL_JQSJ set " +
            "kzbz=#{jqsj.kzbz,jdbcType=VARCHAR}," +
            "model=#{jqsj.msxz,jdbcType=VARCHAR}," +
            "gzdm=#{jqsj.gzdm,jdbcType=VARCHAR}," +
            "hswd=#{jqsj.hswd,jdbcType=TINYINT}," +
            "cswd=#{jqsj.cswd,jdbcType=TINYINT}," +
            "sdwd=#{jqsj.sdwd,jdbcType=TINYINT}," +
            "jqlx=#{jqsj.jqlx,jdbcType=TINYINT}," +
            "sbzt=#{jqsj.sbzt,jdbcType=VARCHAR}," +
            "jqljzt=#{jqsj.jqljzt,jdbcType=CHAR}," +
            "update_date=#{date,jdbcType=TIMESTAMP} " +
            "where jquuid=#{jqsj.jqid,jdbcType=VARCHAR}")
    public Integer updateJqsj(@Param("jqsj")NormalSJ jqsj,@Param("date")Date date);

    @Insert("insert into T_GRGL_JQSJ_HIS(jquuid,jzuuid,kzbz,model,gzdm,hswd,cswd,sdwd,jqlx,sbzt) " +
            "values (" +
            "#{jqsj.jqid,jdbcType=VARCHAR}," +
            "#{jzxx.id,jdbcType=VARCHAR}," +
            "#{jqsj.kzbz,jdbcType=VARCHAR}," +
            "#{jqsj.msxz,jdbcType=VARCHAR}," +
            "#{jqsj.gzdm,jdbcType=VARCHAR}," +
            "#{jqsj.hswd,jdbcType=TINYINT}," +
            "#{jqsj.cswd,jdbcType=TINYINT}," +
            "#{jqsj.sdwd,jdbcType=TINYINT}," +
            "#{jqsj.jqlx,jdbcType=TINYINT}," +
            "#{jqsj.sbzt,jdbcType=VARCHAR})" )
    public Integer inserJqsj(@Param("jqsj")NormalSJ jqsj,@Param("jzxx")JZXX jzxx);

    @Insert("insert into T_GRGL_ALLJQSJ(qbcs,jzuuid,jquuid) " +
            "values (" +
            "#{alljqsj,jdbcType=VARCHAR}," +
            "#{jzxx.id,jdbcType=VARCHAR}," +
            "#{jqsj.jqid,jdbcType=VARCHAR})")
    public Integer insertAllJqsj(@Param("alljqsj")String alljqsj,@Param("jzxx")JZXX jzxx,@Param("jqsj")NormalSJ jqsj);

//    @Update("update T_HC_JZXX set " +
//            "jzip=#{jzxx.jzip,jdbcType=VARCHAR}," +
//            "jzport=#{jzxx.jzport,jdbcType=VARCHAR}," +
//            "dlsj=#{date,jdbcType=TIMESTAMP}," +
//            "ljzt=#{zt} " +
//            "where jzid=#{jzxx.id}")
//    public Integer updateJzxxByIp(@Param("jzxx")JZXX jzxx, @Param("date")Date date,@Param("zt")String zt);

    @Select("select count(*) from T_HC_JZXX t " +
            "where jzip=#{jzxx.jzip,jdbcType=VARCHAR} " +
            "and jzport=#{jzxx.jzport,jdbcType=VARCHAR}")
    public Integer validIp(@Param("jzxx")JZXX jzxx);

    @Update("update t_grgl_jqsj a set a.jqljzt=#{zt} " +
            "where a.jquuid in(" +
            "select b.id from t_zd_jzxx a,t_zd_jqxx b " +
            "where a.id(+)=b.jzxxuuid " +
            "and a.id=#{jzxx.id,jdbcType=VARCHAR})")
    public Integer unconnectedJq(@Param("jzxx")JZXX jzxx,@Param("zt")String zt);

//    @Update("<script>" +
//            "update t_grgl_jqsj a set a.jqljzt=#{zt} " +
//            "where a.jquuid in(" +
//            "select b.id from t_zd_jzxx a,t_zd_jqxx b " +
//            "where a.id(+)=b.jzxxuuid " +
//            "and a.id=#{jzxx.id,jdbcType=VARCHAR} " +
//            "and b.jqid in " +
//            "<foreach item='item' index='index' collection='list' open='(' separator=',' close=')'> #{item} </foreach>" +
//            ")" +
//            "</script>")
//    public Integer connectedJq(@Param("jzxx")JZXX jzxx, @Param("list")List<String> list,@Param("zt")String zt);

    @Select("<script>" +
            "select a.jzip,a.jzport,b.jzid from T_HC_JZXX a,t_zd_jzxx b where a.jzid(+)=b.id and b.jzid in " +
            "<foreach item='jzbh' index='index' collection='list' open='(' separator=',' close=')'> #{jzbh} </foreach>" +
            " and ljzt='0' and b.del_flag='0'" +
            "</script>")
    public List<Map<String,Object>> getAddresses(@Param("list")List<String> jzbhs);

    @Update("update T_HC_JZXX set " +
            "sbzt=#{jzxx.sbzt,jdbcType=VARCHAR}," +
            "dbds=#{jzxx.dbds,jdbcType=FLOAT} " +
            "where " +
            "jzid = #{jzxx.id,jdbcType=VARCHAR}")
    public Integer updateSBZT(@Param("jzxx")JZXX jzxx);

    @Insert("insert into t_grgl_jzsj(jzuuid,ip,port,dbds,sbzt,jzljzt) " +
            "values " +
            "(#{jzxx.id}," +
            "#{jzxx.jzip,jdbcType=VARCHAR}," +
            "#{jzxx.jzport,jdbcType=VARCHAR}," +
            "#{jzxx.dbds,jdbcType=FLOAT}," +
            "#{jzxx.sbzt,jdbcType=VARCHAR}," +
            "#{jzzt})")
    public Integer insertSBZT(@Param("jzxx")JZXX jzxx,@Param("jzzt")String jzzt);

    @Update("update T_HC_JZXX a set a.ljzt='1'")
    public Integer initJZZT();

    @Update("update t_grgl_jqsj a set a.jqljzt='1'")
    public Integer initJQZT();

    @Update("update T_GRGL_JQSJ t set " +
            "t.lastml=#{jqzl,jdbcType=VARCHAR}," +
            "t.last_date=#{date,jdbcType=TIMESTAMP} " +
            "where " +
            "t.jquuid=#{jquuid,jdbcType=VARCHAR}")
    public Integer updateJQCZ(@Param("jqzl")String jqzl,@Param("jquuid")String jquuid,@Param("date")Date date);
}
