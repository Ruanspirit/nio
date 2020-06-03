package com.rjl.tomcat.inner;


import com.rjl.tomcat.domain.HttpRequest;
import com.rjl.tomcat.domain.HttpResponse;

/**
 * @author ：jl.ruan
 * @date ：Created in 2020/6/1
 * @description ：规范Servlet接口
 * @version: 1.0
 */
public interface HttpServlet {

    /**
     * 定义动态资源业务处理接口方法
     * @param httpRequest
     * @param httpResponse
     */
    void service(HttpRequest httpRequest, HttpResponse httpResponse) ;

}
