package com.rjl.tomcat.domain;

/**
 * @author ：jl.ruan
 * @date ：Created in 2020/6/1
 * @description ：测试用户类
 * @version: 1.0
 */
public class User {

    private String username;

    private String password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                '}';
    }

}
