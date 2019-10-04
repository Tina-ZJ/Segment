package com.jz.tcp;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;

import java.net.Socket;
import java.net.SocketTimeoutException;

public class Seg_Tcp_Client {
    public static void main(String[] args) throws IOException{
        //part 20006建立TCP连接
        Socket client = new Socket("127.0.0.1", 20006);
        client.setSoTimeout(10000);

        //获取键盘输入
        BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
        PrintStream out = new PrintStream(client.getOutputStream());

        //socket输入流，接受从服务端发送过来的数据
        BufferedReader buf = new BufferedReader( new InputStreamReader(client.getInputStream()));
        boolean flag = true;
        while(flag){
            System.out.print("请输入query: ");
            String str = input.readLine();

            //发送数据到服务端
            out.println(str);
            if("q".equals(str)){
                flag = false;
            } else{
                try {
                    //获取服务端发送过来的数据，打印结果
                    String seg = buf.readLine();
                    System.out.println(seg);

                } catch(SocketTimeoutException e) {
                    System.out.println("Time out, No response");
                }
            }
        }
        input.close();
        if(client != null ){
            client.close();
        }
    }

}
