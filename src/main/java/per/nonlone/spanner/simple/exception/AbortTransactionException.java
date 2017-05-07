package per.nonlone.spanner.simple.exception;

public class AbortTransactionException extends RuntimeException{

    public AbortTransactionException(Throwable throwable){
        super(throwable);
    }
}