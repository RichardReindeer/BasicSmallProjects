package bambi.demo;

import com.bambi.core.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

/**
 * 处理器类 --->负责业务逻辑的处理
 */
public class HelloController {
    //需要写一个注解 RequestMapping()
    @RequestMapping("/hello.do")
    public String hello(HttpServletRequest httpServletRequest){
        System.out.println("HelloController's hello()");
        //绑定数据
        httpServletRequest.setAttribute("hello","Hello SmartMVC !!!");
        /**
         * 视图名需要跟模板文件的名字匹配
         * 因为Template API需要根据前缀后缀和视图名去匹配视图文件
         */
        return "Hello";//方法需要返回视图名
    }
}
