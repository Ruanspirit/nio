package com.rjl.tomcat.main;

import com.rjl.tomcat.domain.DynamicResourceProcess;
import com.rjl.tomcat.domain.HttpRequest;
import com.rjl.tomcat.domain.HttpResponse;
import com.rjl.tomcat.handler.AcceptHandler;
import com.rjl.tomcat.thread.LoaderResourceRunnable;

import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * @author ：jl.ruan
 * @date ：Created in 2020/6/1
 * @description ：服务端
 * @version: 1.0
 */
public class HttpServer {

    public static void main(String[] args) throws Exception {
        // 初始化Servlet
        new Thread(new LoaderResourceRunnable()).start();
        System.out.println("初始化Servlet成功...........................");

        //创建服务端通道，绑定端口并设置为非阻塞
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.socket().bind(new InetSocketAddress(8080));
        serverSocketChannel.configureBlocking(false);

        //开启选择器，将服务端通道注册进去
        Selector selector = Selector.open();
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        while (true) {
            //获得就绪通道个数
            int select = selector.select();
            if (select != 0) {
                //获取就绪通道Set集合，使用迭代器遍历
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = selectionKeys.iterator();
                while (iterator.hasNext()) {
                    SelectionKey selectionKey = iterator.next();
                    if (selectionKey.isAcceptable()) {
                        //如果是accept类型，则获取客户端并注册
                        SocketChannel socketChannel = new AcceptHandler().connSocketChannel(selectionKey);
                    } else if (selectionKey.isReadable()) {
                        //如果是read类型，则从客户端中读取数据
                        HttpRequest httpRequest = new HttpRequest(selectionKey);
                        httpRequest.parse();

                        //避免因通道存在数据而再次选择该通道，当解析完毕后剔除该通道
                        if ("".equals(httpRequest.getRequestURI()) || httpRequest.getRequestURI() == null) {
                            //把这个通道的注册信息从选择器中取消
                            selectionKey.cancel();
                            continue;
                        }
                        System.out.println("解析Http请求数据完毕......");

                        // 给客户端进行数据响应
                        HttpResponse httpResponse = new HttpResponse(selectionKey);
                        httpResponse.setHttpRequest(httpRequest);
                        //以servlet来判断是否为动态资源
                        if (httpRequest.getRequestURI().startsWith("/servlet")) {
                            DynamicResourceProcess dynamicResourceProcess = new DynamicResourceProcess();
                            dynamicResourceProcess.process(httpRequest, httpResponse);
                        } else {
                            httpResponse.sendStaticResource();
                        }
                    }

                    iterator.remove();
                }
            }
        }


    }

}
