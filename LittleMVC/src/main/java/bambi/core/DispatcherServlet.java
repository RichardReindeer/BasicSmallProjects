package bambi.core;

import com.bambi.core.common.Handler;
import com.bambi.core.common.HandlerMapping;
import com.bambi.core.common.ThymeleafViewResolver;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import javax.servlet.*;
import javax.servlet.annotation.*;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * dispatcher分配
 *
 * *.do 后缀匹配
 * 即所有以.do结尾的请求都会被处理
 *
 * 只有收到请求才会将Servlet实例化
 *
 * loadOnStartUp 值为>0的整数 值越小优先级越高 加上参数之后 就会在服务器启动的时候直接创建实例
 */

@WebServlet(name = "DispatcherServlet",urlPatterns = "*.do",loadOnStartup = 1)
public class DispatcherServlet extends HttpServlet {
    //因为init和service方法都调用 所以将其写成成员变量
    private HandlerMapping handlerMapping;
    private ThymeleafViewResolver thymeleafViewResolver;//只需要创建一次

    public DispatcherServlet() {
        System.out.println("构造器被调用了");
    }

    /**
     * 需要在初始化方法中读取配置文件
     * 1.读取配置文件(smartMVC.xml)中的处理器类名
     * 2.将处理器实例化
     * 3.将处理器实例交给handlerMapping来处理
     *4.创建视图解析器
     *
     * 方法重写的时候 异常不能超过原方法的异常******
     * @throws ServletException
     */
    @Override
    public void init() throws ServletException {
        System.out.println("init方法被调用了");
        //创建配置文件解析器
        SAXReader saxReader = new SAXReader();
        //创建一个指向配置文件的输入流  ResourceAsStream 从resource文件夹中拿文件
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("smartMVC.xml");
        try {
            //解析配置文件
            Document document = saxReader.read(inputStream);
            //获得根节点
            Element root = document.getRootElement();
            //获得其所有子节点
            List<Element> elements = root.elements("bean");
            //beans 用于存放处理器的实例
            List beans = new ArrayList();
            //遍历所有子节点
            for(Element element: elements){
                //获取处理器类名
                String className = element.attributeValue("class");
                System.out.println("测试 输出类名:"+className);
                //将处理器实例化
                Object obj = Class.forName(className).newInstance();
                beans.add(obj);
            }
            System.out.println("beans:"+beans);
            //创建其实例
            handlerMapping = new HandlerMapping();
            //将处理器的实例交给HandlerMapping来处理
            handlerMapping.process(beans);
            //创建视图解析器
            thymeleafViewResolver = new ThymeleafViewResolver();
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServletException(e);//将异常扔给容器来处理
        }
    }

    //当请求到达servlet之后就要执行就绪的service方法
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("DispatcherServlet正在执行");
        //用来处理表单中文参数值的问题
        request.setCharacterEncoding("UTF-8");
        //获取请求路径
        String path = request.getServletPath();//直接调用该方法就可以获取请求路径
        System.out.println("path:"+path);
        //依据请求路径 获取对应的handler对象
        Handler handler = handlerMapping.getHandler(path);
        if(handler==null){
            //发送404状态码
            response.sendError(404);
            System.out.println("请求路径为:"+path+"没有对应的处理器");
            return ;
        }
        //调用处理器的方法
        Object obj = handler.getObject();
        Method method = handler.getMethod();
        //处理器方法的返回值
        Object rv = null;
        try {
            //获得处理器方法的参数类型信息
            Class[] types = method.getParameterTypes();
            if(types.length==0){
                //处理器方法不带参数
                rv = method.invoke(obj);
            }else {
                //params数组用于存放实际参数值
                Object[] params = new Object[types.length];
                for(int i = 0;i<params.length;i++){
                    if(types[i]==HttpServletRequest.class){
                        params[i] = request;
                    }
                    if(types[i] ==HttpServletResponse.class){
                        params[i] = response;
                    }
                }
                rv = method.invoke(obj,params);
                String viewName = (String) rv;
                System.out.println("视图名:"+viewName);
            }
            //获得视图名
            String viewName = rv.toString();//转发机制
            //调用视图解析器的方法来处理视图名
            thymeleafViewResolver.processViewName(viewName,request,response);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServletException(e);//将异常交给容器处理 (需要包装)
        }
    }
}
