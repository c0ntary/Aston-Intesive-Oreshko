package app.dto;

public class MailRequestDto {

    private String email;

    public MailRequestDto() {
    }

    public MailRequestDto(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}