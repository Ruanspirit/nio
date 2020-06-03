package com.rjl.tomcat.inner;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author ：jl.ruan
 * @date ：Created in 2020/6/1
 * @description ：定义集合常量接口
 * @version: 1.0
 */
public interface ServletConcurrentHashMap {

    // 存储请求路径和Servet对象映射关系的Map集合
    ConcurrentHashMap<String , HttpServlet> concurrentHashMap = new ConcurrentHashMap<String , HttpServlet>() ;

}
