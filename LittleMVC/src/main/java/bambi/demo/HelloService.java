package bambi.demo;

/**
 * 将业务逻辑写入到单独的java类中
 */
public class HelloService {

    public String hello(){
        System.out.println("Hello Service 's Hello()");
        return "Hello SmartMVC";
    }
}
