package com.tpe.oauth2jwt.mapper;

import com.tpe.oauth2jwt.domain.Product;
import com.tpe.oauth2jwt.domain.User;
import com.tpe.oauth2jwt.dto.*;
import jakarta.persistence.Column;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    default void userRequestToUser(RegisterRequest dto,User user){
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setEmail(dto.getEmail());
        user.setPassword(dto.getPassword());
    }


    default RegisterResponse userToUserResponse(User user) {
        if (user == null) {
            return null;
        }

        return RegisterResponse.builder()
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .username(user.getEmail())
                .build();
    }

}

