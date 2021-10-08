package bambi.core.common;

import com.bambi.core.annotation.RequestMapping;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 映射处理器类------------>核心类
 *      负责提供请求路径与处理器及方法的对应关系的
 *      比如:请求路径为/hello.do 则该请求由helloController的hello方法来处理
 */
public class HandlerMapping {

    /**
     * HandlerMap 用于存放请求路径与处理器以及方法的对应关系
     * 注:handler对象里面包含了处理器实例以及对应的方法对象
     */
    private Map<String,Handler> handlerMap = new HashMap<>();

    /**
     * 负责建立请求路径与处理器及方法的对应关系
     * @param beans 处理器实例组成的集合
     */
    public void process(List beans) {
        System.out.println("好习惯 打桩方法是否在执行");
        for(Object o : beans){
            //获取加在处理器类前面的requestMapping注解
            RequestMapping requestMapping2 = o.getClass().getAnnotation(RequestMapping.class);
            //path2---->是父路径 加在处理器前的路径
            String path2 = "";
            //处理器类前面@RequestMapping注解可以选择加或者不加 进行非空判断 避免空指针异常
            if(requestMapping2!=null){
                path2 = requestMapping2.value();

            }

            //获得处理器的所有方法
            Method[] methods = o.getClass().getDeclaredMethods();
            //遍历所有方法
            for(Method method : methods){
                //获得方法前的@RequestMapping注解
                /**
                 * 没有进行空指针异常判断 如果方法没有注解 即requestMapping的值为null,则调用value()方法会出现空指针异常
                 */
                RequestMapping requestMapping = method.getAnnotation(RequestMapping.class);
                //获取请求路径  子路径
                String path = requestMapping.value();
                System.out.println("打桩输出请求路径:"+path);

                //将处理器实例以及方法对象封装成Handler对象
                Handler handler = new Handler();
                handler.setObject(o);
                handler.setMethod(method);
                //将请求路径与处理器以及方法的对应关系存放到HandlerMap中
                handlerMap.put(path2+path,handler);
            }
        }
        System.out.println("打桩测试 输出map:"+handlerMap);
    }

    /**
     * 依据请求路径返回相应的handler对象
     * @param s 请求路径
     * @return
     */
    public Handler getHandler(String s) {
        return handlerMap.get(s);
    }
}
