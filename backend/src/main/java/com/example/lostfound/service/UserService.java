package com.example.lostfound.service;

import com.example.lostfound.dto.AuthResponse;
import com.example.lostfound.dto.LoginRequest;
import com.example.lostfound.dto.RegisterRequest;
import com.example.lostfound.dto.UserDto;
import com.example.lostfound.entity.Role;
import com.example.lostfound.entity.User;
import com.example.lostfound.exception.DuplicateUserException;
import com.example.lostfound.exception.ResourceNotFoundException;
import com.example.lostfound.exception.UnauthorizedException;
import com.example.lostfound.repository.UserRepository;
import com.example.lostfound.util.PasswordEncoderUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public AuthResponse register(RegisterRequest request) {
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new IllegalArgumentException("Passwords do not match");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateUserException("Email address already registered: " + request.getEmail());
        }

        Role role = Role.USER;
        if (request.getRole() != null && request.getRole().equalsIgnoreCase("ADMIN")) {
            role = Role.ADMIN;
        }

        User user = new User();
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail().toLowerCase().trim());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setStudentEmpId(request.getStudentEmpId());
        user.setPassword(PasswordEncoderUtil.encode(request.getPassword()));
        user.setRole(role);

        User savedUser = userRepository.save(user);
        return new AuthResponse("User registered successfully", new UserDto(savedUser));
    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail().toLowerCase().trim())
                .orElseThrow(() -> new UnauthorizedException("Invalid email or password"));

        if (!PasswordEncoderUtil.matches(request.getPassword(), user.getPassword())) {
            throw new UnauthorizedException("Invalid email or password");
        }

        return new AuthResponse("Login successful", new UserDto(user));
    }

    public UserDto getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        return new UserDto(user);
    }

    public UserDto getUserByEmail(String email) {
        User user = userRepository.findByEmail(email.toLowerCase().trim())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
        return new UserDto(user);
    }

    public User getUserEntityById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }

    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserDto::new)
                .collect(Collectors.toList());
    }
}
