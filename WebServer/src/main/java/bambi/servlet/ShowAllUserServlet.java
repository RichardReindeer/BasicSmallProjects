package bambi.servlet;



import bambi.http.HttpRequest;
import bambi.http.HttpResponse;
import bambi.vo.User;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templateresolver.FileTemplateResolver;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * 生成包含user.dat文件中所有用户信息的动态页面
 */
public class ShowAllUserServlet extends HttpServlet{
    public void service(HttpRequest httpRequest , HttpResponse httpResponse){
        System.out.println("ShowAllUserServlet:开始处理用户列表页面....");
        //先将user.dat文件中所有记录读取出来
        List<User> list = new ArrayList<>();
        try (
                RandomAccessFile randomAccessFile = new RandomAccessFile("user.dat","r")
        ){
            for(int i =0;i<randomAccessFile.length()/132;i++){
                //读取用户名
                byte[] data = new byte[32];
                randomAccessFile.read(data);
                String username = new String(data,"UTF-8").trim();
                //读取密码
                randomAccessFile.read(data);
                String password = new String(data,"UTF-8").trim();
                //读取性别
                randomAccessFile.read(data);
                String checkSex = new String(data,"UTF-8").trim();
                //读取昵称
                randomAccessFile.read(data);
                String nickName = new String(data,"UTF-8").trim();
                //读取年龄
                int age = randomAccessFile.readInt();
                User user = new User(username,password,nickName,checkSex,age);
                System.out.println("user:"+user);
                list.add(user);
                System.out.println("读取完毕:"+list.size());
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        /**
         * 使用thymeleaf将数据与页面整合生成动态页面
         * 导入thymeleaf类中的Context
         *
         * Context 用于保存所有需要在页面上展示的动态数据
         * 可以想象成是个Map
         */

        Context context = new Context();
        context.setVariable("list",list);//(String,Object)类似key,value ,不过key只能是String

        //初始化Thymeleaf模板引擎  告诉模板引擎关于页面的相关信息
        //1.初始化模板解释器，用来告知模板引擎有关模板页面的相关信息
        FileTemplateResolver resolver = new FileTemplateResolver();
        resolver.setTemplateMode("html");//模板类型
        resolver.setCharacterEncoding("UTF-8");//模板使用的字符集(我们的页面都是UTF-8格式编码的)
        //2.实例化模板引擎
        TemplateEngine templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(resolver);//设置模板解释器，使其了解模板相关信息

        /**
         * String process(String path.Context ctx)
         * 模板引擎生成动态页面的方法
         * 参数1:模板页面的路径
         * 参数2:需要在页面上显示的数据(数据应当都放在这个Context中)
         * 返回值为生成好的html代码
         */
        String html = templateEngine.process("./WebServer/webapps/myweb/userList.html",context);

        //将生成好的html代码写入一个文件
        byte[] data = html.getBytes(StandardCharsets.UTF_8);
        //将生成的页面内容设置到response中
        httpResponse.setData(data);
        httpResponse.setHeadMap("Content-Type","text/html");
        System.out.println("ShowAllUserServlet:处理用户列表页面完毕");
    }
}
