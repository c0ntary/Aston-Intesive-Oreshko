package app.notification;

import com.fasterxml.jackson.annotation.JsonProperty;

public record UserNotificationEvent(
        @JsonProperty("operation") String operation,
        @JsonProperty("email") String email
) {}
