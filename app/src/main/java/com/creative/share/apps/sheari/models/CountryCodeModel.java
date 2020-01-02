package com.creative.share.apps.sheari.models;

import java.io.Serializable;

public class CountryCodeModel implements Serializable {

    private String ar_name;
    private String en_name;
    private String code;
    private int flag;

    public CountryCodeModel(String ar_name, String en_name, String code, int flag) {
        this.ar_name = ar_name;
        this.en_name = en_name;
        this.code = code;
        this.flag = flag;
    }

    public String getAr_name() {
        return ar_name;
    }

    public String getEn_name() {
        return en_name;
    }

    public String getCode() {
        return code;
    }

    public int getFlag() {
        return flag;
    }
}
