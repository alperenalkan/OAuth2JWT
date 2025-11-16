package com.tpe.oauth2jwt.service;

import com.tpe.oauth2jwt.domain.User;
import com.tpe.oauth2jwt.dto.*;
import com.tpe.oauth2jwt.exception.ResourseNotFoundException;
import com.tpe.oauth2jwt.mapper.UserMapper;
import com.tpe.oauth2jwt.repository.UserRepository;
import com.tpe.oauth2jwt.security.JwtTokenProvider;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private UserMapper userMapper;

    public JwtAuthResponse register(RegisterRequest registerRequest) {
        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            throw new RuntimeException("Username is already taken!");
        }

        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new RuntimeException("Email is already in use!");
        }

        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setEmail(registerRequest.getEmail());
        user.setFirstName(registerRequest.getFirstName());
        user.setLastName(registerRequest.getLastName());

        Set<User.Role> roles = new HashSet<>();
        roles.add(User.Role.ROLE_USER);
        user.setRoles(roles);

        userRepository.save(user);

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        registerRequest.getUsername(),
                        registerRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtTokenProvider.generateToken(authentication);

        JwtAuthResponse response = new JwtAuthResponse();
        response.setToken(jwt);
        response.setUsername(user.getUsername());
        return response;
    }

    public JwtAuthResponse login(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtTokenProvider.generateToken(authentication);

        JwtAuthResponse response = new JwtAuthResponse();
        response.setToken(jwt);
        response.setUsername(loginRequest.getUsername());
        return response;
    }

    public Map<String, Object> updateUserById(Long id, RegisterRequest updateRequest, Authentication authentication) {

        String currentUsername = authentication.getName();

        User currentUser = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new RuntimeException("Current user not found"));

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourseNotFoundException("User not found with id:" + id));

        // sadece kendini güncelleyebilsin (istersen admin check de ekleyebilirsin)
        if (!currentUser.getId().equals(user.getId()) &&
                !currentUser.getRoles().contains(User.Role.ROLE_ADMIN)) {

            throw new AccessDeniedException("You are not allowed to update this user");
        }


        userMapper.userRequestToUser(updateRequest, user);
        User updatedUser = userRepository.save(user);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "User updated successfully!");
        response.put("user", userMapper.userToUserResponse(updatedUser));
        return response;
    }

    @Transactional
    public UpdateRoleResponse updateRoleById(Long id, UpdateRoleRequest request, Authentication authentication) {

        // ➤ Giriş yapan kullanıcıyı bul
        String currentUsername = authentication.getName();
        User currentUser = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new ResourseNotFoundException("Current user not found: " + currentUsername));

        // ➤ Rolü güncellenecek kullanıcıyı bul
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourseNotFoundException("User not found with id: " + id));

        // ➤ Yetki kontrolü (admin)

        boolean isAdmin = currentUser.getRoles().contains(User.Role.ROLE_ADMIN);

        if (!isAdmin) {
            throw new AccessDeniedException("You are not allowed to update roles of this user");
        }

        // ➤ Rolleri güncelle
        user.setRoles(request.getRoles());
        userRepository.save(user);

        // ➤ Response dön
        return new UpdateRoleResponse(
                "Role updated successfully!",
                user.getRoles()
        );
    }

}

