package com.onlydb.util;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.InputStream;

/**
 * 创建sqlSession
 */
public class SessionFactory {
    private static SqlSessionFactory sqlSessionFactory;
    private SessionFactory(){

    }

    public static SqlSession getAutoSqlSession() {
        if(sqlSessionFactory==null){
            return getSqlSessionFactory().openSession(true);
        } else {
            return sqlSessionFactory.openSession(true);
        }
    }

    public static SqlSession getNorSqlSession() {
        if(sqlSessionFactory==null){
            return getSqlSessionFactory().openSession();
        } else {
            return sqlSessionFactory.openSession();
        }
    }

    synchronized public static SqlSessionFactory getSqlSessionFactory(){
        if(sqlSessionFactory==null){
            String resources="config/mybatis/mybatis-config.xml";
            InputStream inputStream=null;
            try {
                inputStream= Resources.getResourceAsStream(resources);
            }catch (Exception e){
                e.printStackTrace();
            }
            sqlSessionFactory=new SqlSessionFactoryBuilder().build(inputStream);
        }
        return sqlSessionFactory;

    }
}