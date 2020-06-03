package com.rjl.tomcat.servlet;


import com.rjl.tomcat.domain.HttpRequest;
import com.rjl.tomcat.domain.HttpResponse;
import com.rjl.tomcat.inner.HttpServlet;
import com.rjl.tomcat.inner.Servlet;

/**
 * @author ：jl.ruan
 * @date ：Created in 2020/6/1
 * @description ：AServlet
 * @version: 1.0
 */
@Servlet("AServlet")
public class AServlet implements HttpServlet {

    @Override
    public void service(HttpRequest httpRequest, HttpResponse httpResponse) {
        System.out.println("AServlet的service方法执行了................");
        httpResponse.setContentType("text/html;charset=UTF-8");
        httpResponse.write("AServlet........执行了.....");
    }

}
