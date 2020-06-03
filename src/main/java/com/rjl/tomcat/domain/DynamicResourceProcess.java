package com.rjl.tomcat.domain;


import com.rjl.tomcat.inner.HttpServlet;
import com.rjl.tomcat.inner.ServletConcurrentHashMap;

import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

/**
 * @author ：jl.ruan
 * @date ：Created in 2020/6/1
 * @description ：处理动态资源的类
 * @version: 1.0
 */
public class DynamicResourceProcess {

    // 处理动态资源的方法
    public void process(HttpRequest httpRequest, HttpResponse httpResponse) {
        //根据url动态执行servlet,截取?前面的部分
        String requestURI = httpRequest.getRequestURI();
        String[] split = requestURI.split("[?]");
        HttpServlet httpServlet = ServletConcurrentHashMap.concurrentHashMap.get(split[0]);
        //判断执行的servlet是否存在，不存在则返回404
        if (httpServlet != null) {
            httpServlet.service(httpRequest, httpResponse);
        } else {
            response404(httpResponse);
        }

    }

    // 响应404的方法
    public void response404(HttpResponse httpResponse) {

        try {

            // 响应行，响应头，响应空行数据准备
            String responseLine = "HTTP/1.1 404 NOT FOUNT\r\n";
            String reponseHeader = "Content-Type: text/html;charset=UTF-8\r\n";
            String emptyLine = "\r\n";
            String result = responseLine + reponseHeader + emptyLine;

            // 响应数据
            SelectionKey selectionKey = httpResponse.getSelectionKey();
            SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
            socketChannel.write(ByteBuffer.wrap(result.getBytes("UTF-8")));
            socketChannel.write(ByteBuffer.wrap("Servlet 404 NOT FOUND...........".getBytes("UTF-8")));
            socketChannel.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
