package com.bambi.socket;


import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collection;

/**
 * 聊天室服务端
 *
 */
public class Server {
    /**
     * java.net.ServerSocket 运行在服务端的ServerSocket相当于是"总机",只要负责：
     *      1.向系统申请服务端口，客户端就是通过这个端口与服务端建立链接的；
     *      2：监听服务端口，一旦一个客户端链接，ServerSocket就会立即创建一个Socket与之对应，
     *         通过这个Socket就可以与客户端对等交互了
     */
    private ServerSocket socket ;

    //private PrintWriter[] allOut = {};
    private Collection<PrintWriter> allOut = new ArrayList<PrintWriter>();

    public Server() {
        try {
            System.out.println("正在启动服务端......服务端的打桩");
            socket = new ServerSocket(8088);//服务端决定客户端的端口号
            System.out.println("服务端启动完毕!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void start(){
        try {
            /**
             * ServerSocket提供的方法:
             *      Socket accept()
             *      该方法是一个阻塞方法，调用后开始等待客户端的链接，一旦一个客户端建立链接
             *      此时该方法会立即返回一个Socket，通过这个Socket实例与客户端交互
             *      多次调用该方法可以接收多个客户端的链接
             */
            while (true){
                System.out.println("等待客户端链接");
                Socket clientSocket =  socket.accept();//一个阻塞方法，等待客户端传来一个socket
                System.out.println("客户端建立连接");

                //当一个客户端链接之后，启动一个线程，来与其进行交互
                ClientHandler clientHandler = new ClientHandler(clientSocket);
                Thread thread = new Thread(clientHandler);
                thread.start();
            }

        } catch (IOException e) {
            /*e.printStackTrace();*/
            System.out.println("线程退出");
        }
    }

    public static void main(String[] args) {
        Server server = new Server();
        server.start();
    }

    /**
     * 将线程定义成内部类
     * 这个线程的任务是负责与特定的客户端进行交互
     * 服务端运行后，每当一个客户端链接，都会启动一个线程来执行一个这样的任务。
     *
     *
     * 内部类可以访问到外部类的属性
     */
    class ClientHandler implements Runnable{
        private Socket clientSocket;
        private String host ;//当前客户的IP地址信息

        public ClientHandler(Socket clientSocket) {
            this.clientSocket = clientSocket;
            //通过Socket获取远端计算机的地址信息
            /**
             * getHostAddress()将获取到的4段地址合并成字符串信息返回
             */
            host = clientSocket.getInetAddress().getHostAddress();
        }

        public void run() {
            PrintWriter printWriter = null;
            try{
                /**
                 * Socket提供的方法:
                 *      InputStream getInputStream()
                 *      通过Socket获取的字节输入流可以读取远端计算机发送过来的字节
                 *
                 */
                InputStream inputStream = clientSocket.getInputStream();
                InputStreamReader reader = new InputStreamReader(inputStream,"UTF-8");//两边指定的字符集需要保持一致
                BufferedReader bufferedReader = new BufferedReader(reader);
                /* *
                 * BufferedReader.readline()//如果返回值是null，则表明这个流已经读到了末尾*/


                /**
                 * 通过Socket获取输出流，用于给客户端发送消息    2021/1/12新
                 */
                OutputStream outputStream = clientSocket.getOutputStream();
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream,"UTF-8");
                BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter);
                printWriter = new PrintWriter(bufferedWriter,true);

                /**
                 *  将该输出流存入数组allOut中，这样其他的ClientHandler实例就可以得到
                 *  当前ClientHandler实例中的这个输出流，以便将信息发送给该客户端
                 */

                //synchronized (socket) {    <------这里的socket是ServerSocket
                synchronized (Server.this) { //还可以锁所有ClientHandler的外部类对象Server的实例    ClientHandler所属的外部类对象Server的实例
                    /*//1对ALLOUT数组扩容
                    allOut = Arrays.copyOf(allOut, allOut.length + 1);
                    //2.将输出流存入该数组
                    allOut[allOut.length - 1] = printWriter;*/
                    allOut.add(printWriter);
                }

                System.out.println(host+"上线了，当前在线人数:"+allOut.size());


                String line ;
                /**
                 * 不是null才会继续输出，如果是null,则 表明客户端已经和你断开
                 * 循环就应该停止了*/

                while ((line = bufferedReader.readLine())!=null){
                    System.out.println(host+"说:"+line);

                    /**
                     * 如果两段代码都被synchronized锁定着，而且锁定项相同的情况下，这两段代码互斥
                     * 即只能同时执行一个
                     */
                    synchronized (Server.this){
                        //回复给客户端
                        /*for(int i =0;i<allOut.length;i++){
                            allOut[i].println(host+"说:"+line);
                        }*/
                        for(PrintWriter printWriter1 : allOut){
                            printWriter1.println(host+"说:"+line);
                        }
                    }
                }
            }catch (IOException e){
                e.printStackTrace();
            }finally {
                synchronized (Server.this) {
                    //处理当客户端断开连接之后的操作

                    //将当前客户端的输出流从allOut数组中删除
                   /* for(int i=0;i<allOut.length;i++){
                        if(allOut[i] == printWriter){
                            allOut[i] = allOut[allOut.length-1];
                            allOut = Arrays.copyOf(allOut,allOut.length-1);
                            break;
                        }
                    }*/
                    allOut.remove(printWriter);
                }
                    System.out.println(host+"下线了，当前在线人数:"+allOut.size());

                    try {
                        socket.close();//与客户端也断开链接
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
