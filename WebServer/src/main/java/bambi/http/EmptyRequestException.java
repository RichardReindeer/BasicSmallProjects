package bambi.http;

/**
 * 空请求异常
 * 当HttpRequest解析请求时发现本次请求为空请求时会抛出的异常
 *
 * 想要java识别这个异常，需要继承异常类，并且重写所有的构造器
 */
public class EmptyRequestException extends Exception{
    public EmptyRequestException() {
        super();
    }

    public EmptyRequestException(String message) {
        super(message);
    }

    public EmptyRequestException(String message, Throwable cause) {
        super(message, cause);
    }

    public EmptyRequestException(Throwable cause) {
        super(cause);
    }

    protected EmptyRequestException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
