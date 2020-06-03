package com.rjl.tomcat.handler;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

/**
 * @author ：jl.ruan
 * @date ：Created in 2020/6/1
 * @description ：接收客户端连接,并注册到选择器中
 * @version: 1.0
 */
public class AcceptHandler {

    public SocketChannel connSocketChannel(SelectionKey selectionKey) {
        try {
            // 通过ServerSocketChannel获取SocketChannel
            ServerSocketChannel ss = (ServerSocketChannel) selectionKey.channel() ;
            // 将SocketChannel设置为非阻塞状态
            SocketChannel socketChannel = ss.accept();
            socketChannel.configureBlocking(false) ;
            //将SocketChannel注册到Selector上,此时类型为read
            socketChannel.register(selectionKey.selector() , SelectionKey.OP_READ) ;
            return socketChannel ;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
