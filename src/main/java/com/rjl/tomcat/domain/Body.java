package com.rjl.tomcat.domain;

/**
 * @author ：jl.ruan
 * @date ：Created in 2020/6/1
 * @description ：封装请求体数据
 * @version: 1.0
 */
public class Body {

    private String key;

    private String value;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

}
