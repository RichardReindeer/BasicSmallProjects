package com.bambi.socket;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

/**
 * 聊天室客户端
 *
 * 区分客户端和服务端
 *      谁发起链接谁是客户端
 */
public class Client {
    /**
     * java.net.Socket  套接字
     *      Socket封装了TCP的通讯细节，我们使用Socket与服务端建立链接后，只需要通过两条流的
     *      读写来完成与服务端的交互操作
     *
     */
    private Socket socket;

    public Client() {
        /**
         *
         * 实例化Socket时需要传入两个参数，分别表示服务端的IP地址与端口号
         * IP地址:用于找到网络上服务端的计算机
         * 端口:用于找到服务端计算机上的服务端应用程序  端口号取值范围0~65535
         *
         */
        try {
            System.out.println("正在链接服务端");
            socket = new Socket("localhost",8088);//其实就是连接的过程
            System.out.println("与服务端建立链接");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void start(){
        /**
         * 在start方法中完成与服务端的交互
         */
        try {

            //启动线程用于读取服务端发送过来的消息
            ServerHandler serverHandler = new ServerHandler();
            Thread thread = new Thread(serverHandler);
            thread.setDaemon(true);
            thread.start();

            /**
             * Socket提供的方法:
             *      OutputStream getOutputStream()
             *      该方法会返回一个字节输出流，通过这个流写出的数据会通过网络发送给远端计算机
             */
            OutputStream outputStream = socket.getOutputStream();//相当于获取一个低级流

            //按行发送字符串给服务端
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream,"UTF-8");
            BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter);
            PrintWriter printWriter = new PrintWriter(bufferedWriter,true);//自动行刷新 autoFlush
            Scanner scanner = new Scanner(System.in) ;


            while (true){
                String line = scanner.nextLine();
                if("exit".equals(line)){
                    break;
                }
                printWriter.println(line);

            }

        } catch (IOException e) {
            e.printStackTrace();
        }finally {

            try {
                socket.close();//与服务器断开连接
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 程序的入口方法
     * @param args
     */
    public static void main(String[] args) {
        Client client = new Client();
        client.start();
    }

    /**
     * 该线程负责读取服务端发送过来的信息
     */
    private class ServerHandler implements Runnable{

        public void run() {
            try{
                //通过socket获取输入流读取服务端发过来的信息
                InputStream inputStream = socket.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream,"UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String line;
                while ((line = bufferedReader.readLine())!=null){
                    System.out.println(line);
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
