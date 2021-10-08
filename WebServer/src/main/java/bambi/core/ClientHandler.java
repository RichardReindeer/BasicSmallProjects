package bambi.core;



import bambi.http.EmptyRequestException;
import bambi.http.HttpRequest;
import bambi.http.HttpResponse;
import bambi.servlet.HttpServlet;

import java.io.File;
import java.io.IOException;
import java.net.Socket;

/**
 * 处理与某个客户端的HTTP交互
 * 由于HTTP要求客户端与服务端的交互采取一问一答，因此当前处理流程为三步:
 * 1:解析请求:读取客户端发送过来的HTTP请求内容
 * 2:处理请求
 * 3:响应客户端(发送一个HTTP响应给客户端)
 *
 *
 *
 *
 *
 * ***********************************
 * 高内聚低耦合
 * ***********************************
 */
public class ClientHandler implements Runnable{
    private Socket socket;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {

        //只负责流程控制
        try{
            //1.解析请求
            HttpRequest httpRequest = new HttpRequest(socket);
            HttpResponse httpResponse = new HttpResponse(socket);
            //2.处理请求

            //通过请求对象获取抽象路径
            String path = httpRequest.getRequestURI();
            //根据抽象路径去webapps下找到对应的资源
            HttpServlet servlet = ServerContext.getHttpServletMap(path);
            System.out.println(servlet);
            //首先判断该请求是否为一些特殊的值，用于判定是否为处理业务
            if(servlet!=null){
                servlet.service(httpRequest,httpResponse);
            }
            else{
                File file = new File("./WebServer/webapps"+path);
                //检查该资源是否真实存在
                //并且需要判断file获取到的是一个文件而不是一个目录
                if(file.exists()&&file.isFile()){
                    //根据资源文件名获取后缀名
                    System.out.println("该资源已找到");
                    //响应该资源
                    httpResponse.setEntity(file);

                    System.out.println();
                }else {
                    System.out.println("该资源不存在");
                    //响应404
                    File notFoundPage = new File("./WebServer/webapps/root/404.html");
                    httpResponse.setEntity(notFoundPage);
                    httpResponse.setStatusCode(404);
                    httpResponse.setStatusReadson("NOTFOUND");
                }
            }
            //3.响应客户端
            httpResponse.flush();
        }
        catch (EmptyRequestException e){
            //单独捕获孔庆其异常，但是不需要做任何处理，这个异常抛出仅仅为了忽略处理操作

        }
        catch (Exception e){
            e.printStackTrace();
        }finally {
            try {
                //响应客户端之后断开链接
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }



}
