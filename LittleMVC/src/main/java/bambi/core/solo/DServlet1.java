package bambi.core.solo;

import com.bambi.demo.HelloService;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import javax.servlet.*;
import javax.servlet.annotation.*;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet(name = "DServlet1", urlPatterns = "/do")
public class DServlet1 extends HttpServlet {
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("DispatcherServlet 正在运行");
        //1.获取请求路径 直接调用ServletPath()方法
        String path = req.getServletPath();
        //2.依据请求路径调用对应的模型来处理
        if("/hello.do".equals(path)){
            //创建模型类对象
            HelloService helloService = new HelloService();
            String hello = helloService.hello();
            /**
             * 依据模型返回的结果调用对应的视图来处理
             *
             * 1.创建模板解析器(默认从WEB-INF下寻找文件)
             * getServletContext() 方法继承自GenericServlet
             * GenericServlet 是HttpServlet的父类
             *
             * 该方法的作用是获得servlet上下文对象
             */
            ServletContextTemplateResolver resolver = new ServletContextTemplateResolver(getServletContext());
            //给模板解析器设置特性  设置模板的类型
            resolver.setTemplateMode(TemplateMode.HTML);
            resolver.setCharacterEncoding("UTF-8");
            resolver.setPrefix("/WEB-INF/templates/"); //设置前缀
            resolver.setSuffix(".html");//设置后缀
            //设置是否缓存---->false
            resolver.setCacheable(false);
            //创建模板引擎
            TemplateEngine templateEngine = new TemplateEngine();
            //给模板引擎设置模板解析器
            templateEngine.setTemplateResolver(resolver);
            //创建web上下文对象
            WebContext webContext = new WebContext(req,resp,getServletContext());
            webContext.setVariable("hello",hello);
            //设置Mime类型 设置响应字符集
            resp.setContentType("text/html;charset=utf-8");

            templateEngine.process("Hello",webContext,resp.getWriter());

        }
    }
}
