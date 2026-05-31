package app.notification;

public class UserNotificationEvent {
    private String operation;
    private String email;

    public UserNotificationEvent() {}

    public UserNotificationEvent(String operation, String email) {
        this.operation = operation;
        this.email = email;
    }

    public String getOperation() { return operation; }
    public void setOperation(String operation) { this.operation = operation; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}