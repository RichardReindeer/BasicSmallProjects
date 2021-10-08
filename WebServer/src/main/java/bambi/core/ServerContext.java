package bambi.core;


import bambi.servlet.HttpServlet;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 服务端会重用的内容都放在这里
 */
public class ServerContext {
    private static Map<String, HttpServlet> httpServletMap = new HashMap<>();
    static {
        initHttpServletMapping();
    }

    /**
     * 定义一个私有方法来完成细节的初始化
     */
    private static void initHttpServletMapping()  {
//        httpServletMap.put("/myweb/regUser",new RegServlet());
//        httpServletMap.put("/myweb/loginUser",new LoginServlet());
//        httpServletMap.put("/myweb/updatePW",new UpdateServlet());
//        httpServletMap.put("/myweb/showAllUser",new ShowAllUserServlet());

        /**
         * 解析config/servlets.xml
         * 将根标签下所有子标签<servlet>获取到，将其中的属性path的值作为key
         * 将className属性的值利用反射机制实例化对应的类并作为value
         * 初始化servletMapping 这个map
         */
        try {
            SAXReader saxReader = new SAXReader();
            Document document = saxReader.read("./WebServer/config/servlets.xml");
            Element root = document.getRootElement();
            List<Element>elements = root.elements("servlet");
            for(Element element: elements){
                String key = element.attributeValue("path");
                String value = element.attributeValue("className");
                Class cls = Class.forName(value);
                HttpServlet httpServlet = (HttpServlet) cls.newInstance();
                httpServletMap.put(key,httpServlet);
            }
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }


    }

    public static HttpServlet getHttpServletMap(String path) {
        return httpServletMap.get(path);
    }
}
