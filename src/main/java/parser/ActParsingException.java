package parser;

public class ActParsingException extends RuntimeException {
    public ActParsingException() {
        super();
    }

    public ActParsingException(String message) {
        super(message);
    }

    public ActParsingException(String message, Throwable cause) {
        super(message, cause);
    }

    public ActParsingException(Throwable cause) {
        super(cause);
    }
}
