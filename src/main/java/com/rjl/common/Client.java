package com.rjl.common;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Random;

/**
 * @author ：jl.ruan
 * @date ：Created in 2020/6/1
 * @description ： nio客户端
 * @version: 1.0
 */
public class Client {

    public static void main(String[] args) throws Exception {

        SocketChannel socketChannel = SocketChannel.open();

        socketChannel.connect(new InetSocketAddress("127.0.0.1",8080));

        Random random = new Random();
        String str = "NIO,我来了" + random.nextInt(100);

        ByteBuffer byteBuffer = ByteBuffer.wrap(str.getBytes("UTF-8"));

        socketChannel.write(byteBuffer);

        System.out.println("----客户端发送数据完毕------------------------------");


        // 读取服务端返回过来的数据
        ByteBuffer allocate = ByteBuffer.allocate(1024);
        int read = socketChannel.read(allocate);
        System.out.println(new String(allocate.array() , 0 , read));

        socketChannel.close();
    }

}
