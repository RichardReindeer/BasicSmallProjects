package bambi.core;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 小鸟WebServer
 * 模拟TomCat的基础功能，实现一个简易版的web容器
 * 基于TCP协议作为通讯协议，使用HTTP协议与客户端进行交互，完成一系列网络操作
 */
public class WebServer {
    private ServerSocket serverSocket;
    private ExecutorService executorService;
    public WebServer() {
        try {
            System.out.println("正在启服务器端");
            serverSocket = new ServerSocket(8088);
            executorService = Executors.newFixedThreadPool(30);
            System.out.println("服务器端启动完毕");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void start(){
        try {
            while (true){
                System.out.println("等待客户端链接");
                Socket socket = serverSocket.accept();
                System.out.println("一个客户端连接了");

                //启动一个线程处理该客户端交互
                ClientHandler clientHandler = new ClientHandler(socket);
                /*Thread thread = new Thread(clientHandler);
                thread.start();*/
                executorService.execute(clientHandler);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
            WebServer webServer = new WebServer();
            webServer.start();
    }
}
