package com.group3.metaBlog.Authentication.DataTransferObject;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class ResetPasswordRequestDto {

    @NotEmpty(message = "New password is required")
    private String newPassword;

    @NotEmpty(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;
}
