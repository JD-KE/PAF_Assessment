package vttp2023.batch4.paf.assessment.Excecptions;

public class SQLInsertionException extends RuntimeException {
    public SQLInsertionException() {
        super();
    }

    public SQLInsertionException(String msg) {
        super(msg);
    }
}
