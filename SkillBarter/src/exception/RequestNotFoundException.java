package exception;

public class RequestNotFoundException extends RuntimeException {

    public RequestNotFoundException(String requestId) {
        super("No barter request found with ID: " + requestId);
    }
}

