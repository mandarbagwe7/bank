package com.codewithdevil.bank.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.AllArgsConstructor;

import javax.crypto.SecretKey;
import java.util.Date;

@AllArgsConstructor
public class Jwt {
    private Claims claims;
    private SecretKey secretKey;

    public boolean isExpired() {
        return claims.getExpiration().before(new Date());
    }

    public String getUserId() {
        return claims.getSubject();
    }

    public String toString(){
        return Jwts.builder().claims(claims).signWith(secretKey).compact();
    }

}