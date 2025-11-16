package com.tpe.oauth2jwt.dto;

import com.tpe.oauth2jwt.domain.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateRoleResponse {
    private String message;
    private Set<User.Role> roles;
}
