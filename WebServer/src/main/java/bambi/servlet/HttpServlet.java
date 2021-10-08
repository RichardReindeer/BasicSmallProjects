package bambi.servlet;


import bambi.http.HttpRequest;
import bambi.http.HttpResponse;

/**
 * 所有Servlet的超类
 */
public abstract class HttpServlet {
    /**
     * 抽象类与接口的区别
     * 抽象类中允许有实现的方法
     */

    /**
     * 定义成抽象方法的目的
     * 强制子类去重写这个方法
     *
     * 抽象类中可以写实现方法，但是实现方法子类可以选择性重写，也就是不重写也可以，但是之后在ClientHandler中调用serivce的时候调用的就会使抽象类的
     * service方法，就会是空白的，所以要定义成抽象方法，让继承的类必须去做
     * @param httpRequest
     * @param httpResponse
     */
    public abstract void service(HttpRequest httpRequest, HttpResponse httpResponse);
}
