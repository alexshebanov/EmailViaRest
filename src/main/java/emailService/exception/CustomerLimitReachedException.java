package emailService.exception;

public class CustomerLimitReachedException extends Exception {
    public CustomerLimitReachedException(String message) {
        super(message);
    }
}
