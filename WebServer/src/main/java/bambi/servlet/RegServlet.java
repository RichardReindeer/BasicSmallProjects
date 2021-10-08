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
 * 用于处理用户注册业务
 */
public class RegServlet extends HttpServlet{
    public void service(HttpRequest httpRequest, HttpResponse httpResponse){
        System.out.println("RegServlet:开始处理用户注册....");
        //1.通过request获取用户在页面上表单中输入的信息
        String userName = httpRequest.getParameter("userName");
        String password = httpRequest.getParameter("password");
        String checkSex = httpRequest.getParameter("checkSex");
        String nickName = httpRequest.getParameter("NickName");
        String age = httpRequest.getParameter("age");

        /**
         * 验证数据，如果上述几项中存在null,或年龄的字符串表示不是一个整数的时候，直接响应一个验证错误页面
         * reg_info_error.html上面居中显示一行字，注册失败，输入信息有误
         * 此时不应当再执行下面的注册操作了
         */

        if(userName==null||password==null||checkSex==null||nickName==null||age==null||!age.matches("^(?:[1-9][0-9]?|1[01][0-9]|120)$")){
            File file = new File("./WebServer/webapps/myweb/reg_info_error.html");
            httpResponse.setEntity(file);
            return;
        }
            //为了测试这几个值是否都获取到了，可以在后面进行一次打桩输出
            System.out.println("userName:"+userName);
            System.out.println("password:"+password);
            System.out.println("checkSex:"+checkSex);
            System.out.println("nickName:"+nickName);
            System.out.println("age:"+age);

            /**
             * 如果用户在年龄这一栏不写或者写的不是整形，会出现异常，后期会学到前端验证
             */
            int ageByInt = Integer.parseInt(age);
            //2.将信息写入文件user.dat中

            /**
             * 2 将信息写入文件user.dat中
             * 每个用户的信息都占用100字节，其中用户名，密码，昵称为字符串，各占用32个字节
             * 年龄为int值，占用4字节
             */
            try(
                    RandomAccessFile randomAccessFile = new RandomAccessFile("user.dat","rw");
            ){

                /**
                 * 判定是否为重复用户
                 * 先读取user.dat文件中现有的所有用户名，如果与当前注册的用户名一致，则直接响应页面:hava_user.html
                 * 提示该用户名已存在，请重新注册
                 * 否则才将该用户信息写入文件user.dat中完成注册
                 */
                boolean exist = false;
                for(int i =0;i<randomAccessFile.length()/132;i++){
                    System.out.println("走这了里面?");
                    randomAccessFile.seek(i*132);
                    byte[] read = new byte[32];
                    randomAccessFile.read(read);
                    String name = new String(read,"UTF-8").trim();
                    System.out.println("你读到啥了就过去了:"+name);
                    if(name.equals(userName)){
                        System.out.println("用户信息已存在");
                        exist = true;
                    }
                }

                if(exist){
                    File file = new File("./WebServer/webapps/myweb/have_user.html");
                    httpResponse.setEntity(file);
                    return;
                }else {
                    //先将指针移动到文件末尾
                    randomAccessFile.seek(randomAccessFile.length());

                    byte[] data = userName.getBytes(StandardCharsets.UTF_8);
                    data = Arrays.copyOf(data,32);
                    randomAccessFile.write(data);

                    data = password.getBytes(StandardCharsets.UTF_8);
                    data = Arrays.copyOf(data,32);
                    randomAccessFile.write(data);
                    data = checkSex.getBytes(StandardCharsets.UTF_8);
                    data = Arrays.copyOf(data,32);
                    randomAccessFile.write(data);

                    data = nickName.getBytes(StandardCharsets.UTF_8);
                    data = Arrays.copyOf(data,32);
                    randomAccessFile.write(data);
                    randomAccessFile.writeInt(ageByInt);

                    //3.响应客户端注册结果页面
                    File regFile = new File("./WebServer/webapps/myweb/Reg_Success.html");
                    httpResponse.setEntity(regFile);
                    //注册成功，设置response响应注册成功页面
                }

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            System.out.println("RegServlet:处理用户注册完毕");
        }

}
