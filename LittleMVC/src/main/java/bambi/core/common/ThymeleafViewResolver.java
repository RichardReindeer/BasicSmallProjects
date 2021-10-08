package bambi.core.common;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 视图解析器类:
 *  用于处理视图名
 */
public class ThymeleafViewResolver {
    /**
     * 处理视图名的方法，该方法采用thymeleaf技术
     * 处理视图名.
     * @param viewName
     * @param request
     * @param response
     * @throws IOException
     */
    public void processViewName(String viewName, HttpServletRequest request, HttpServletResponse response) throws IOException {
        /**
         * 处理视图名：
         *      如果视图名是以 redirect开头 则重定向
         *      否则转发
         */
        if(viewName.startsWith("redirect:")){
            //获得重定向地址
            String redirectPath = request.getContextPath()+"/"+viewName.substring("redirect:".length());
            System.out.println("获得到的重定向地址:"+redirectPath);
            /**
             * 重定向 从应用名开始写
             */
            response.sendRedirect(redirectPath);
        }else {
            //不是重定向则转发
            TemplateEngine engine = ResourceManager.getInstance(request.getServletContext()).getTemplateEngine();
            //创建web上下文 需要请求、响应和servlet上下文 用来获取动态数据
            WebContext webContext = new WebContext(request,response,request.getServletContext());
            //webContext.setVariable("hello",);//绑定数据到web上下文
            //设置Mime类型 设置相应字符集
            response.setContentType("text/html;charset=utf-8");
            //调用模板引擎的方法来处理模板文件 模板文件名 web上下文(拿到动态数据) 获取输出流(发送给客户端)
            /*
                这个方法会依据模板文件名、前缀和后缀找到模板文件(hello.html) 从web上下文中获取数据
                生成动态页面 将动态页面发送给浏览器。
             */
            engine.process(viewName,webContext,response.getWriter());
        }
    }
}
