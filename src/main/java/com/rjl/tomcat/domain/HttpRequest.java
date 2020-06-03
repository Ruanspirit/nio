package com.rjl.tomcat.domain;

import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;

/**
 * @author ：jl.ruan
 * @date ：Created in 2020/6/1
 * @description ：用于封装HTTP请求协议数据
 * @version: 1.0
 */
public class HttpRequest {

    // 请求方式
    private String method;
    // 请求的uri
    private String requestURI;
    // http协议版本
    private String version;
    // 封装请求头
    private List<Header> headers;
    // 用于获取通道
    private SelectionKey selectionKey;
    //封装请求体
    private List<Body> bodies;

    public HttpRequest(SelectionKey selectionKey) {
        this.selectionKey = selectionKey;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getRequestURI() {
        return requestURI;
    }

    public void setRequestURI(String requestURI) {
        this.requestURI = requestURI;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public List<Header> getHeaders() {
        return headers;
    }

    public void setHeaders(List<Header> headers) {
        this.headers = headers;
    }

    public SelectionKey getSelectionKey() {
        return selectionKey;
    }

    public void setSelectionKey(SelectionKey selectionKey) {
        this.selectionKey = selectionKey;
    }

    public List<Body> getBodies() {
        return bodies;
    }

    public void setBodies(List<Body> bodies) {
        this.bodies = bodies;
    }

    //解析浏览器请求协议数据
    public void parse() {
        try {
            StringBuilder stringBuilder = new StringBuilder();
            ByteBuffer byteBuffer = ByteBuffer.allocate(1024);

            //读取数据，存储到stringBuilder中
            SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
            int read = socketChannel.read(byteBuffer);
            while (read > 0) {
                byteBuffer.flip();
                byte[] array = byteBuffer.array();
                String str = new String(array, 0, read);
                stringBuilder.append(str);
                byteBuffer.clear();
                read=socketChannel.read(byteBuffer);
            }
            //解析数据
            parseHttRequest(stringBuilder);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    //解析stringBuilder数据进行字段赋值
    public void parseHttRequest(StringBuilder str) {
        String httpRequestString = str.toString();
        //请求体所在行数
        int bodyNum = 0;

        //当心跳检测时，判空来决定是否为心跳检测，是则返回空
        if ("".equals(httpRequestString) || httpRequestString == null) {
            return;
        }

        String[] split = httpRequestString.split("\r\n");
        //封装请求行
        String requestLineString = split[0];
        String[] requestLineSplit = requestLineString.split(" ");
        this.setMethod(requestLineSplit[0]);
        this.setRequestURI(requestLineSplit[1]);
        this.setVersion(requestLineSplit[2]);

        //封装请求头
        List<Header> headers = new ArrayList<>();
        for (int i = 1; i < split.length; i++) {

            if (!split[i].contains(":") && this.getMethod().equals("POST")) {
                //如果是POST请求，则存储请求体所在行数
                bodyNum = i + 1;
                break;
            }
            String[] headArr = split[i].split(": ");
            Header header = new Header();
            header.setKey(headArr[0]);
            header.setValue(headArr[1]);
            headers.add(header);
        }
        this.setHeaders(headers);

        //如果是POST请求，封装请求体
        List<Body> bodies = new ArrayList<>();
        if (bodyNum != 0) {
            String bodyStr = split[bodyNum];
            String[] param = bodyStr.split("&");
            for (String paramStr : param) {
                String[] paramSplit = paramStr.split("=");
                Body body = new Body();
                body.setKey(paramSplit[0]);
                body.setValue(paramSplit[1]);
                bodies.add(body);
            }
        }
        this.setBodies(bodies);
    }

}
