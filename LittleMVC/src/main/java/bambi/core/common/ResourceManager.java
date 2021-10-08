package bambi.core.common;


import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import javax.servlet.ServletContext;

/**
 * 资源管理器:------>确保系统的一些重要的资源只被创建一次
 *                 避免资源的浪费
 *      将模板引擎等只需要创建一次 且避免创建多次的类
 *      放入到单例模式中
 */
public class ResourceManager {

    private static ResourceManager resourceManager;
    private TemplateEngine templateEngine;

    /**
     * 在构造器里面创建资源  比如创建模板引擎(templateEngine)
     */
    private ResourceManager(ServletContext servletContext) {
        System.out.println("资源管理器的构造器呗调用了");
        //依据模型返回的结果调用对应的视图来处理
        /**
         * 创建模板解析器(默认从web inf 下寻找文件)
         * getServletContext() 方法继承自GenericServlet
         * GenericServlet是HttpServlet的父类
         *
         * 该方法的作用是获得servlet上下文对象
         *  当TOMCAT等服务器启动的时候 会自动创建一个Servlet上下文对象 该对象会一直存在 一个web应用对应一个上下文
         *  可以将一些全体用户都需要共享的东西全都放到上下文里面
         */
        ServletContextTemplateResolver resolver = new ServletContextTemplateResolver(servletContext);
        //给模板解析器设置一些特性 设置模板的类型(模板所处理的文件)
        resolver.setTemplateMode(TemplateMode.HTML);
        //设置前缀和后缀 告诉模板解析器查找模板文件的路径
        resolver.setPrefix("/WEB-INF/templates/");
        resolver.setSuffix(".html");//后缀 告诉模板解析器后缀类型
        resolver.setCharacterEncoding("UTF-8");//设置编码类型
        //设置是否使用缓存 false--->禁止使用缓存 (使用缓存可以提升性能)
        resolver.setCacheable(false);
        //创建模板引擎
        templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(resolver);//给模板引擎设置模板解析器
    }

    public TemplateEngine getTemplateEngine() {
        return templateEngine;
    }

    public synchronized static ResourceManager getInstance(ServletContext servletContext){
        if(resourceManager==null){
            resourceManager = new ResourceManager(servletContext);
        }
        return resourceManager;

    }
}
