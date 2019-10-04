package com.jz.tcp;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import com.sun.deploy.util.StringUtils;

import com.jz.seg.Segmentor;
import com.jz.utility.MyPair;

public class ServerThread implements Runnable {
    private Socket client = null;

    public ServerThread(Socket client) {
        this.client = client;
    }
    Segmentor segger = Segmentor.getInstance();


    @Override
    public void run(){
      try{
          //socket的输出流，向客户端发送数据
          PrintStream out = new PrintStream(client.getOutputStream());
          //socket的输入流，接收从客户端发送过来的数据
          BufferedReader buf = new BufferedReader(new InputStreamReader((client.getInputStream())));
          
          boolean flag = true;
          while (flag){

              //接收从客户端发送过来的数据
              String str = buf.readLine();
              if (str == null || "".equals(str)){
                  flag = false;
              }else{
                  if("q".equals(str)){
                      flag = false;
                  }else{
                      String text = str.split("\t")[0].toLowerCase();
                      System.out.println(text);
                      ArrayList<List<MyPair<String,String>>>  tags = segger.tag(text);
                      List<String> list = new ArrayList<String>();

                      for( MyPair<String,String> tag : tags.get(0) )
                      {
                          list.add(tag.key);  // list.add(tag.key+"/"+tag.value);
                      }
                      System.out.println(StringUtils.join(list,";"));
                      //将结果发送到客户端
                      out.println(StringUtils.join(list,";"));
                  }
              }

          }

          out.close();
          client.close();
      } catch (Exception e ){
          e.printStackTrace();
      }
    }


}
