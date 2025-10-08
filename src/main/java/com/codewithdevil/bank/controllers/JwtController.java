package com.codewithdevil.bank.controllers;

import com.codewithdevil.bank.services.Jwt;
import com.codewithdevil.bank.services.JwtService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;

@Controller
@AllArgsConstructor
public class JwtController {
    private JwtService jwtService;

    public Jwt getJwt(String token) {
        token = token.replace("Bearer ", "");
        return jwtService.parse(token);
    }
}
