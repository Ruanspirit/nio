package com.rjl.tomcat.exception;

/**
 * @author ：jl.ruan
 * @date ：Created in 2020/6/1
 * @description ：未实现HttpServlet接口异常
 * @version: 1.0
 */
public class IsNotImplementsHttpServletException extends RuntimeException {

    public IsNotImplementsHttpServletException(){ }                             // 无参构造
    public IsNotImplementsHttpServletException(String msg) { super(msg); }      // 有参构造

}
