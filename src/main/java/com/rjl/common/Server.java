package com.rjl.common;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * @author ：jl.ruan
 * @date ：Created in 2020/6/1
 * @description ： nio服务端
 * @version: 1.0
 */
public class Server {

    public static void main(String[] args) throws Exception {

        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.socket().bind(new InetSocketAddress(8080));
        serverSocketChannel.configureBlocking(false);

        //开启一个选择器
        Selector selector = Selector.open();

        //将当前管道注册到选择器中
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        while (true) {

            //获得就绪的通道，得到就绪的通道个数
            int select = selector.select();

            if (select != 0) {

                //获取就绪通道的集合,使用迭代器遍历
                Set<SelectionKey> selectionKeys = selector.selectedKeys();

                Iterator<SelectionKey> iterator = selectionKeys.iterator();
                while (iterator.hasNext()) {

                    SelectionKey selectionKey = iterator.next();

                    //当通道是服务器accpet通道，则获取客户端通道并注册到选择器中
                    if (selectionKey.isAcceptable()) {

                        ServerSocketChannel serverSocketChannel1 = (ServerSocketChannel) selectionKey.channel();

                        SocketChannel socketChannel = serverSocketChannel1.accept();
                        socketChannel.configureBlocking(false);
                        socketChannel.register(selector, SelectionKey.OP_READ);

                        //当通道是客户端通道，则读取数据
                    } else if (selectionKey.isReadable()) {

                        ByteBuffer allocate = ByteBuffer.allocate(1024);

                        SocketChannel socketChannel = (SocketChannel) selectionKey.channel();

                        int read = socketChannel.read(allocate);

                        while (read > 0) {

                            allocate.flip();
                            System.out.println(new String(allocate.array(), 0, read));
                            allocate.clear();
                            read = socketChannel.read(allocate);
                        }

                        ByteBuffer byteBuffer = ByteBuffer.wrap("OK,收到了".getBytes("UTF-8"));
                        socketChannel.write(byteBuffer);

                        socketChannel.close();
                    }

                    iterator.remove();
                }

            }

        }

    }

}



