package com.onlydb.data.mac.entity;

public class PassValid {

    private String VALUE;

    public String getVALUE() {
        return VALUE;
    }

    public void setVALUE(String VALUE) {
        this.VALUE = VALUE;
    }

    public boolean containsKey(String s){
        if(this.VALUE.equals(s)) return true;
        return false;
    }
}
