package com.user.userService.service;

import com.user.userService.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {
    UserResponse register(RegisterRequest registerRequest);
    LoginResponse login(LoginRequest loginRequest);
    UserResponse getProfile(String email);
    UserResponse getUserById(Long id);
    Page<UserResponse> getAllUsers(Pageable pageable);
    UserResponse updateUser(Long id, UpdateUserRequest updateUserRequest);
    void deleteUser(Long id);
    void activateUser(Long id);
    void deactivateUser(Long id);
    boolean userExists(Long id);
}
