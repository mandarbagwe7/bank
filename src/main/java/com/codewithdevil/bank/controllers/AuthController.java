package com.codewithdevil.bank.controllers;

import com.codewithdevil.bank.dtos.LoginRequest;
import com.codewithdevil.bank.dtos.RegisterRequest;
import com.codewithdevil.bank.exceptions.EmailConflictException;
import com.codewithdevil.bank.services.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@AllArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(
        @Valid @RequestBody RegisterRequest request
    ) {
        var response = authService.registerUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);

    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(
            @Valid @RequestBody LoginRequest request,
            HttpServletResponse response
    ){
        var result = authService.loginUser(request, response);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(
            @CookieValue(name = "refreshToken") String refreshToken
    ){
        var response = authService.refreshToken(refreshToken);
        return ResponseEntity.ok(response);
    }

    @ExceptionHandler(EmailConflictException.class)
    public ResponseEntity<Map<String, String>> handleEmailConflictException(){
        return ResponseEntity.status(HttpStatus.CONFLICT).body(
                Map.of("Error", "Email Already Taken.")
        );
    }
}
