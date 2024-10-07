package com.example.ranchat.user.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;


@Getter
public class JoinDTO {
    @NotNull(message = "Username is required")
    @NotEmpty(message = "Username cannot be empty")
    @Size(min = 5, max = 20, message = "Username must be between 5 and 20 characters")
    private String username;

    private String password;
}
