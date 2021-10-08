package bambi.core.common;

import java.lang.reflect.Method;

/**
 * 为了方便 利用java反射机制去调用一个对象的方法
 * 而设计的一个辅助类
 */
public class Handler {
    //obj_---->处理器实例
    private Object object;
    //处理器实例的方法对象
    private Method method;

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }
}
