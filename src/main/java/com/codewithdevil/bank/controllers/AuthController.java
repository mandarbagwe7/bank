package com.codewithdevil.bank.controllers;

import com.codewithdevil.bank.config.JwtConfig;
import com.codewithdevil.bank.dtos.LoginRequest;
import com.codewithdevil.bank.dtos.LoginResponse;
import com.codewithdevil.bank.dtos.RegisterRequest;
import com.codewithdevil.bank.dtos.RefreshResponse;
import com.codewithdevil.bank.entities.Role;
import com.codewithdevil.bank.entities.User;
import com.codewithdevil.bank.mapper.UserMapper;
import com.codewithdevil.bank.repositories.RoleRepository;
import com.codewithdevil.bank.repositories.UserRepository;
import com.codewithdevil.bank.services.JwtService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/auth")
@AllArgsConstructor
public class AuthController {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private AuthenticationManager authenticationManager;
    private JwtService jwtService;
    private JwtConfig jwtConfig;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(
        @Valid @RequestBody RegisterRequest request
    ) {
        var user = userRepository.findByEmail(request.getEmail().toLowerCase()).orElse(null);
        if (user != null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(
                    Map.of("Error", "Email Already Taken.")
            );
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



        return ResponseEntity.status(HttpStatus.CREATED).body(userMapper.toRegisterResponse(user));
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(
            @Valid @RequestBody LoginRequest request,
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

        return ResponseEntity.ok(new LoginResponse(accessToken.toString()));
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(
            @CookieValue(name = "refreshToken") String refreshToken
    ){
        var jwt = jwtService.parse(refreshToken);
        if(jwt == null || jwt.isExpired()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        var user = userRepository.findById(Long.parseLong(jwt.getUserId())).orElseThrow();
        var accessToken = jwtService.generateAccessToken(user);

        return ResponseEntity.ok(new RefreshResponse(accessToken.toString()));
    }
}
