public class ValidationException extends RuntimeException {
    public ValidationException(String message) { super(message); }
}

public class ConflictException extends RuntimeException {
    public ConflictException(String message) { super(message); }
}
