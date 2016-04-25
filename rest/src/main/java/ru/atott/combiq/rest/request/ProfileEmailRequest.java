package ru.atott.combiq.rest.request;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

public class ProfileEmailRequest {

    public static ProfileEmailRequest EXAMPLE;

    static {
        EXAMPLE = new ProfileEmailRequest();
        EXAMPLE.setEmail("ivanov@gmail.com");
    }

    @NotEmpty
    @Email
    private String email;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
