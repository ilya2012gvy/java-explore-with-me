package ru.practicum.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@Builder
@AllArgsConstructor
public class NewUserRequest {
    @Size(min = 2, max = 250)
    @NotBlank
    private String name;
    @Email
    @Size(min = 6, max = 254)
    @NotBlank
    private String email;
}