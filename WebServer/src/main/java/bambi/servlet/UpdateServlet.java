package bambi.servlet;


import bambi.http.HttpRequest;
import bambi.http.HttpResponse;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * 对密码进行的更新操作
 */
public class UpdateServlet extends HttpServlet{
    public void service(HttpRequest httpRequest , HttpResponse httpResponse){
        String username = httpRequest.getParameter("username");
        String oldPassword = httpRequest.getParameter("password");
        String newPassword = httpRequest.getParameter("newPassword");

        System.out.println("新密码:"+newPassword);
        System.out.println("用户名:"+username);
        System.out.println("老密码:"+oldPassword);

        if(username==null||oldPassword==null||newPassword==null){
            System.out.println("更新密码有问题");
            File file = new File("./WebServer/webapps/myweb/UPW_fail.html");
            httpResponse.setEntity(file);
        }else {
            try(
                    RandomAccessFile randomAccessFile = new RandomAccessFile("user.dat","rw")
                    ){
                for(int i = 0;i<randomAccessFile.length();i+=132){
                    randomAccessFile.seek(i*132);
                    byte[] data = new byte[32];
                    randomAccessFile.read(data);
                    String checkUsername =  new String(data,"UTF-8").trim();
                    if(username.equals(checkUsername)){
                        randomAccessFile.read(data);
                        String checkPassword = new String(data,"UTF-8").trim();
                        if(checkPassword.equals(oldPassword)){
                            byte[] update = newPassword.getBytes(StandardCharsets.UTF_8);
                            update = Arrays.copyOf(update,32);
                            randomAccessFile.seek(i*132+32);
                            randomAccessFile.write(update);
                            System.out.println("密码更改成功");
                            File file = new File("./WebServer/webapps/myweb/UPW_success.html");
                            httpResponse.setEntity(file);
                            return;
                        }
                        break;
                    }
                }

                System.out.println("更新密码遇到问题");
                File file = new File("./WebServer/webapps/myweb/UPW_fail.html");
                httpResponse.setEntity(file);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
