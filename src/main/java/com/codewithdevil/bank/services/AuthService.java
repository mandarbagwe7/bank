package com.codewithdevil.bank.services;

import com.codewithdevil.bank.config.JwtConfig;
import com.codewithdevil.bank.dtos.*;
import com.codewithdevil.bank.entities.User;
import com.codewithdevil.bank.exceptions.EmailConflictException;
import com.codewithdevil.bank.exceptions.JwtInvalidException;
import com.codewithdevil.bank.mapper.UserMapper;
import com.codewithdevil.bank.repositories.UserRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Set;

@Service
@AllArgsConstructor
public class AuthService {
    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private UserMapper userMapper;
    private AuthenticationManager authenticationManager;
    private JwtService jwtService;
    private JwtConfig jwtConfig;

    public RegisterResponse registerUser(RegisterRequest request) {
        var user = userRepository.findByEmail(request.getEmail().toLowerCase()).orElse(null);
        if (user != null) {
            throw new EmailConflictException();
        }

        user = User.builder()
                .email(request.getEmail().toLowerCase())
                .password(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .roles(Set.of())
                .build();

        userRepository.save(user);

        return userMapper.toRegisterResponse(user);
    }

    public LoginResponse loginUser(
            LoginRequest request,
            HttpServletResponse response
    ){

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        var user = userRepository.findByEmail(request.getEmail().toLowerCase()).orElse(null);

        var accessToken = jwtService.generateAccessToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);

        var cookie = new Cookie("refreshToken", refreshToken.toString());
        cookie.setPath("/auth/refresh");
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setMaxAge(jwtConfig.getRefreshTokenExpiration());
        response.addCookie(cookie);

        return new LoginResponse(accessToken.toString());
    }

    public RefreshResponse refreshToken(String refreshToken){
        var jwt = jwtService.parse(refreshToken);
        if(jwt == null || jwt.isExpired()) {
            throw new JwtInvalidException();
        }

        var user = userRepository.findById(Long.parseLong(jwt.getUserId())).orElseThrow();
        var accessToken = jwtService.generateAccessToken(user);

        return new RefreshResponse(accessToken.toString());
    }
}
