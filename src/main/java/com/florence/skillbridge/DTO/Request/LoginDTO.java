package com.florence.skillbridge.DTO.Request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class LoginDTO {
    @NotBlank(message = "Username or Email is required")
    @Size(min = 3, message = "Username or email must be at least 3 characters")
    @Size(max = 100, message = "Username or Email must not exceed to 100 characters")
    private String usernameOrEmail;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be atleast 8 characters")
    @Size(max = 50, message = "Password must not exceed to 50 characters")
    private String password;

    public void setUsernameOrEmail(String usernameOrEmail) {
        this.usernameOrEmail = usernameOrEmail;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsernameOrEmail() {
        return usernameOrEmail;
    }

    public String getPassword() {
        return password;
    }
}
