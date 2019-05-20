package com.onlydb.util;

public class HttpUtil {

    public static String[] getPath(String url){
        if(url!=null&&url.indexOf("?")>0)
        {
            return url.substring(0,url.indexOf("?")).split("/");
        } else {
            return url.split("/");
        }
    }

    public static String getPaths(String url){
        if(url!=null&&url.indexOf("?")>0)
        {
            return url.substring(0,url.indexOf("?"));
        }
        return url;
    }

}
