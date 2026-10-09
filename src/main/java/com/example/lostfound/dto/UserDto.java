package com.example.lostfound.dto;

import com.example.lostfound.entity.User;

import java.time.LocalDateTime;

public class UserDto {

    private Long id;
    private String fullName;
    private String email;
    private String phoneNumber;
    private String studentEmpId;
    private String role;
    private LocalDateTime createdAt;

    public UserDto() {}

    public UserDto(User user) {
        this.id = user.getId();
        this.fullName = user.getFullName();
        this.email = user.getEmail();
        this.phoneNumber = user.getPhoneNumber();
        this.studentEmpId = user.getStudentEmpId();
        this.role = user.getRole().name();
        this.createdAt = user.getCreatedAt();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getStudentEmpId() {
        return studentEmpId;
    }

    public void setStudentEmpId(String studentEmpId) {
        this.studentEmpId = studentEmpId;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
