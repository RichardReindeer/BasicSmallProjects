package bambi.servlet;


import bambi.http.HttpRequest;
import bambi.http.HttpResponse;
import org.apache.log4j.Logger;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * 完成登录逻辑
 *
 * 与的优先级大于或
 */
public class LoginServlet extends HttpServlet{
    public static Logger logger = Logger.getLogger(LoginServlet.class);

    public void service(HttpRequest httpRequest , HttpResponse httpResponse){
        System.out.println("开始处理登录");
        logger.info("开始处理登录");
        String userName = httpRequest.getParameter("username");
        String password = httpRequest.getParameter("password");
        System.out.println("用户名:"+userName);
        System.out.println("密码:"+password);
        if(userName==null||password==null){
            System.out.println("用户名或者密码为空");
            File file = new File("./WebServer/webapps/myweb/login_fail.html");
            httpResponse.setEntity(file);
        }else {
            try(
                    RandomAccessFile randomAccessFile = new RandomAccessFile("user.dat","rw");
                    ){
                boolean exist = false;
                for(int i =0;i<randomAccessFile.length()/132;i++){
                    randomAccessFile.seek(i*132);
                    byte[] data = new byte[32];
                    randomAccessFile.read(data);
                    String username = new String(data,"UTF-8").trim();
                    if(userName.equals(username)){
                        randomAccessFile.read(data);
                        String passWord = new String(data,"UTF-8").trim();
                        if(passWord.equals(password)){
                            exist = true;
                            System.out.println("检查到这个用户");
                            File logSuccess = new File("./WebServer/webapps/myweb/login_success.html");
                            httpResponse.setEntity(logSuccess);
                            return;
                        }
                        break;
                    }
                }
                if(exist==false){
                    System.out.println("没有检测到这个用户");
                    File fileFail = new File("./WebServer/webapps/myweb/login_fail.html");
                    httpResponse.setEntity(fileFail);
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                /**
                 * 异常信息\异常对象
                 * 异常信息用error记录
                 * 其他信息使用info记录
                 */
                logger.error(e.getMessage(),e);
            }
        }
    }
}
