package app.notification;

public class UserNotificationEvent {

    private String email;
    private String operation;

    public UserNotificationEvent() {
    }

    public UserNotificationEvent(String email, String operation) {
        this.email = email;
        this.operation = operation;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }
}