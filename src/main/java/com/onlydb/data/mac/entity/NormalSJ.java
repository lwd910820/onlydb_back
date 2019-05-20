package com.onlydb.data.mac.entity;

public class NormalSJ {

    private String jqid;
    private String kzbz;
    private String msxz;
    private String gzdm;
    private Integer hswd;
    private Integer cswd;
    private Integer sdwd;
    private String sbzt;
    private String jqlx;
    private String jqljzt = "0";

    public NormalSJ(){}

    public NormalSJ(String s,String jqid){
        this.jqid = jqid;
        if(s.length()==18){
            setKzbz(s.substring(2,4));
            setMsxz(s.substring(4,6));
            setGzdm(s.substring(6,8));
            setHswd(Integer.valueOf(s.substring(8,10),16));
            setCswd(Integer.valueOf(s.substring(10,12),16));
            setSdwd(Integer.valueOf(s.substring(12,14),16));
            setSbzt(s.substring(14,16));
            setJqlx(s.substring(16,18));
        }
    }

    public String getJqid() {
        return jqid;
    }

    public void setJqid(String jqid) {
        this.jqid = jqid;
    }

    public String getKzbz() {
        return kzbz;
    }

    public void setKzbz(String kzbz) {
        this.kzbz = kzbz;
    }

    public String getMsxz() {
        return msxz;
    }

    public void setMsxz(String msxz) {
        this.msxz = msxz;
    }

    public String getGzdm() {
        return gzdm;
    }

    public void setGzdm(String gzdm) {
        this.gzdm = gzdm;
        if(gzdm.equals("FF")) setJqljzt("0");
        else if(gzdm.equals("FE")) setJqljzt("1");
        else setJqljzt("2");
    }

    public Integer getHswd() {
        return hswd;
    }

    public void setHswd(Integer hswd) {
        this.hswd = hswd;
    }

    public Integer getCswd() {
        return cswd;
    }

    public void setCswd(Integer cswd) {
        this.cswd = cswd;
    }

    public Integer getSdwd() {
        return sdwd;
    }

    public void setSdwd(Integer sdwd) {
        this.sdwd = sdwd;
    }

    public String getSbzt() {
        return sbzt;
    }

    public void setSbzt(String sbzt) {
        this.sbzt = sbzt;
    }

    public String getJqlx() {
        return jqlx;
    }

    public void setJqlx(String jqlx) {
        this.jqlx = jqlx;
    }

    public String getJqljzt() { return jqljzt; }

    public void setJqljzt(String jqljzt) { this.jqljzt = jqljzt; }

    @Override
    public String toString() {
        return "NormalSJ{" +
                "jqid='" + jqid + '\'' +
                ", kzbz='" + kzbz + '\'' +
                ", msxz='" + msxz + '\'' +
                ", gzdm='" + gzdm + '\'' +
                ", hswd=" + hswd +
                ", cswd=" + cswd +
                ", sdwd=" + sdwd +
                ", sbzt='" + sbzt + '\'' +
                ", jqlx='" + jqlx + '\'' +
                ", jqljzt='" + jqljzt + '\'' +
                '}';
    }
}
