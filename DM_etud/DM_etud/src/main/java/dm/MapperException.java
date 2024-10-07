package dm;

/**
 * Custom exception class for errors related to data mapping operations.
 */
public class MapperException extends Exception {
    private String sqlState;
    private int errorCode;

    // Constructor with message only
    public MapperException(String message) {
        super(message);
    }

    // Constructor with message, SQL state, and error code
    public MapperException(String message, String sqlState, int errorCode) {
        super(message);
        this.sqlState = sqlState;
        this.errorCode = errorCode;
    }

    // Getter for SQL state
    public String getSqlState() {
        return sqlState;
    }

    // Getter for error code
    public int getErrorCode() {
        return errorCode;
    }

    @Override
    public String toString() {
        return "MapperException{" +
                "message=" + getMessage() +
                ", sqlState='" + sqlState + '\'' +
                ", errorCode=" + errorCode +
                '}';
    }


}
