package com.example.lostfound.util;

import com.example.lostfound.dto.UserDto;
import com.example.lostfound.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AuthUtil {

    private final UserService userService;

    @Autowired
    public AuthUtil(UserService userService) {
        this.userService = userService;
    }

    public UserDto getAuthenticatedUser(HttpSession session, HttpServletRequest request) {
        if (session != null) {
            UserDto sessionUser = (UserDto) session.getAttribute("LOGGED_IN_USER");
            if (sessionUser != null) {
                return sessionUser;
            }
        }
        if (request != null) {
            String userIdHeader = request.getHeader("X-User-Id");
            if (userIdHeader != null && !userIdHeader.trim().isEmpty()) {
                try {
                    Long userId = Long.parseLong(userIdHeader.trim());
                    return userService.getUserById(userId);
                } catch (Exception ignored) {}
            }
            String userEmailHeader = request.getHeader("X-User-Email");
            if (userEmailHeader != null && !userEmailHeader.trim().isEmpty()) {
                try {
                    return userService.getUserByEmail(userEmailHeader.trim());
                } catch (Exception ignored) {}
            }
        }
        return null;
    }
}
