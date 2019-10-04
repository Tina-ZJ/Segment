package com.jz.tcp;

import java.net.ServerSocket;
import java.net.Socket;
public class Seg_tcp_Server {
    public static void main(String[] args) throws Exception{

        //服务端在20006端口接听客户端请求的TCP连接
        ServerSocket server = new ServerSocket(20006);
        Socket client = null;
        boolean f = true;
        while(f) {
            client = server.accept();
            System.out.println("与客户端连接成功！");
            //为每个客户端连接开启一个线程
            new Thread(new ServerThread(client)).start();
        }

        server.close();
    }
}
