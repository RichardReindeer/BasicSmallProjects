package bambi.core.Exception;

public class RequestNotFoundException extends Exception{
    public RequestNotFoundException() {
        super();
    }

    public RequestNotFoundException(String message) {
        super(message);
    }

    public RequestNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public RequestNotFoundException(Throwable cause) {
        super(cause);
    }

    protected RequestNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
