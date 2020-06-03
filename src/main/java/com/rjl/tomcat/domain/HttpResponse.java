package com.rjl.tomcat.domain;

import com.rjl.tomcat.constant.FileConstant;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;

/**
 * @author ：jl.ruan
 * @date ：Created in 2020/6/1
 * @description ：用于封装HTTP响应协议数据
 * @version: 1.0
 */
public class HttpResponse {

    // 版本号
    private String version;
    // 响应状态码
    private String status;
    // 描述信息
    private String desc;
    // 封装响应头数据
    private List<Header> headers = new ArrayList<Header>();
    // contentType属性，告知服务端给客户端响应数据的MIME类型
    private String contentType;
    // 用于获取通道，进行数据的输出
    private SelectionKey selectionKey;
    // 用于获取请求的相关数据，进行逻辑判断
    private HttpRequest httpRequest;

    public HttpResponse(SelectionKey selectionKey) {
        this.selectionKey = selectionKey;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public List<Header> getHeaders() {
        return headers;
    }

    public void setHeaders(List<Header> headers) {
        this.headers = headers;
    }

    public String getContentType() {
        return contentType;
    }

    //单独定义方法便于接收外部传入的格式并直接封装成header对象存入相应头集合中
    public void setContentType(String contentType) {
        this.contentType = contentType;
        Header header = new Header();
        header.setKey("Content-Type");
        header.setValue(contentType);
        headers.add(header);
    }

    public SelectionKey getSelectionKey() {
        return selectionKey;
    }

    public void setSelectionKey(SelectionKey selectionKey) {
        this.selectionKey = selectionKey;
    }

    public HttpRequest getHttpRequest() {
        return httpRequest;
    }

    public void setHttpRequest(HttpRequest httpRequest) {
        this.httpRequest = httpRequest;
    }

    //处理静态资源响应
    public void sendStaticResource() {

        String requestURI = httpRequest.getRequestURI();
        //判断是否为心跳检测，如果是则requestURI为空，直接返回空
        if ("".equals(requestURI) || requestURI == null) {
            return;
        }

        //构建响应行
        this.version = "HTTP/1.1";
        //判断资源是否存在，默认访问路径"/"在当前不为null,打成jar包后为null,所以这里再加一个||
        if(this.getClass().getResource(requestURI)!=null||requestURI.equals("/")){
            this.status = "200";
            this.desc = "OK";
        }else{
            this.status = "404";
            this.desc = "NOT FOUND";
        }

        String responseLine = this.getVersion() + " " + this.getStatus() + " " + this.getDesc() + "\r\n";

        //构建响应头
        //根据用户请求来判定contentType类型
        //动态判断用户调用的资源,如果404则设置为图片返回
        if (!this.status.equals("404")) {
            if (httpRequest.getRequestURI().endsWith(".txt")) {
                this.setContentType("text/html;charset=UTF-8");
            } else if (httpRequest.getRequestURI().endsWith(".jpg")) {
                this.setContentType("image/jpeg");
            } else if (httpRequest.getRequestURI().endsWith(".ico")) {
                //如果是图标的话设置图标的格式,因为图标不变
                System.out.println("请求图标--------------------------------");
                this.setContentType("image/x-icon");
            } else if (httpRequest.getRequestURI().endsWith("/")) {
                //如果是默认127.0.0.1，返回一个提示输出
                this.setContentType("text/html;charset=UTF-8");
            }
        } else {
            this.setContentType("image/jpeg");
        }

        //拼接响应头
        StringBuilder sb = new StringBuilder();
        for (Header header : headers) {
            sb.append(header.getKey() + ":" + header.getValue() + "\r\n");
        }
        //定义响应头
        String responseHead = sb.toString();
        //定义响应空行
        String emptyLine = "\r\n";
        //整合数据
        String responseLineAndHeadAndEmptyLine = responseLine + responseHead + emptyLine;

        try {

            ByteBuffer wrap = ByteBuffer.wrap(responseLineAndHeadAndEmptyLine.getBytes("utf-8"));
            SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
            socketChannel.write(wrap);

            //填写返回客户端信息
            ByteBuffer buffer = ByteBuffer.wrap(getContent());
            socketChannel.write(buffer);

            socketChannel.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 响应数据的方法,使用commons-io工具类
    public byte[] getContent() {
        try {
            String requestURI = httpRequest.getRequestURI();
            InputStream fileInputStream;

            if (status.equals("200")) {
                if (requestURI.endsWith("favicon.ico")) {
                    //如果是图标则返回图标
                    System.out.println("反馈图标----------------------------------");
                    fileInputStream = HttpResponse.class.getResourceAsStream(requestURI);
                    return IOUtils.toByteArray(fileInputStream);

                } else if (httpRequest.getRequestURI().endsWith("/")) {
                    //当输入的是127.0.0.1时返回的提示字符串
                    fileInputStream = HttpResponse.class.getResourceAsStream(FileConstant.MAIN_PAGE);
                    return IOUtils.toByteArray(fileInputStream);

                } else {
                    //根据用户访问路径动态选择返回数据
                    fileInputStream = HttpResponse.class.getResourceAsStream(requestURI);
                    return IOUtils.toByteArray(fileInputStream);
                }
            } else if (status.equals("404")) {
                //如果不存在，则返回404
                fileInputStream = HttpResponse.class.getResourceAsStream(FileConstant.ERROR_IMAGE_PATH);
                return IOUtils.toByteArray(fileInputStream);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //处理动态资源响应
    public void write(String content) {

        //设置响应头
        this.setVersion("HTTP/1.1");
        this.setStatus("200");
        this.setDesc("ok");

        // 构建要响应的字符串
        // 响应头信息构建
        String responseLine = this.getVersion() + " " + this.getStatus() + " " + this.getDesc() + "\r\n";
        List<Header> headers = this.getHeaders();
        StringBuilder sb = new StringBuilder();
        for (Header header : headers) {
            sb.append(header.getKey()).append(": ").append(header.getValue()).append("\r\n");
        }
        String responseHeader = sb.toString();
        String emptyLine = "\r\n";

        // 拼接字符串
        String result = responseLine + responseHeader + emptyLine;

        try {

            // 构建ByteBuffer对象，然后通过SocketChannel进行输出
            ByteBuffer byteBuffer = ByteBuffer.wrap(result.getBytes("utf-8"));
            SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
            socketChannel.write(byteBuffer);

            socketChannel.write(ByteBuffer.wrap(content.getBytes("UTF-8")));
            socketChannel.close();

        } catch (IOException e) {
            System.out.println("响应数据失败.....");
            e.printStackTrace();
        }

    }

}
