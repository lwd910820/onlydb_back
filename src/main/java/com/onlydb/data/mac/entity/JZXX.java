package com.onlydb.data.mac.entity;

import java.util.Date;

public class JZXX {

    private String id;
    private String jzid;
    private String jzip;
    private String jzport;
    private String sbzt="";
    private float dbds=0;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getJzid() {
        return jzid;
    }

    public void setJzid(String jzid) {
        this.jzid = jzid;
    }

    public String getJzip() {
        return jzip;
    }

    public void setJzip(String jzip) {
        this.jzip = jzip;
    }

    public String getJzport() {
        return jzport;
    }

    public void setJzport(String jzport) {
        this.jzport = jzport;
    }

    public String getSbzt() {
        return sbzt;
    }

    public void setSbzt(String sbzt) {
        this.sbzt = sbzt;
    }

    public float getDbds() {
        return dbds;
    }

    public void setDbds(float dbds) {
        this.dbds = dbds;
    }

    @Override
    public String toString() {
        return "JZXX{" +
                "id='" + id + '\'' +
                ", jzid='" + jzid + '\'' +
                ", jzip='" + jzip + '\'' +
                ", jzport='" + jzport + '\'' +
                ", sbzt='" + sbzt + '\'' +
                ", dbds=" + dbds +
                '}';
    }
}
