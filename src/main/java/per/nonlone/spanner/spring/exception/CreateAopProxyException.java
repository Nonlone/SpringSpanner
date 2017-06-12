package per.nonlone.spanner.spring.exception;

public class CreateAopProxyException extends RuntimeException {

    private static final long serialVersionUID = -8717946722671773806L;

    public CreateAopProxyException(String message) {
        super(message);
    }

    public CreateAopProxyException(String message, Throwable throwable) {
        super(message, throwable);
    }

}
