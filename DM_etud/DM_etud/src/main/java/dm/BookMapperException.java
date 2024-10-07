package dm;

/**
 * Exception thrown when a book mapper detects a problem
 * during a persistence operation.
 */
public class BookMapperException extends MapperException {
    public BookMapperException(String message) {
        super(message);
    }

    public BookMapperException(String message, String sqlState, int errorCode) {
        super(message, sqlState, errorCode);
    }
}
