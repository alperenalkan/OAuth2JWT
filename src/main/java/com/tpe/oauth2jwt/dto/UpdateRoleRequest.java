package com.tpe.oauth2jwt.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.tpe.oauth2jwt.domain.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.Set;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateRoleRequest {

    @NotEmpty(message = "Role list cannot be empty")
    private Set<User.Role> roles;
}

