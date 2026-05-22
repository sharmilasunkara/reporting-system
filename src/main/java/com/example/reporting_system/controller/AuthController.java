package com.example.reporting_system.controller;

import com.example.reporting_system.dto.LoginRequestDto;
import com.example.reporting_system.dto.LoginResponseDto;
import com.example.reporting_system.security.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@Slf4j
public class AuthController {

    private final JwtUtil jwtUtil;

    public AuthController(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public LoginResponseDto login(
            @RequestBody LoginRequestDto request) {

        log.info("user details  name:{},password:{}",request.getUsername(),request.getPassword());
        if ("admin".equals(request.getUsername())
                && "admin123".equals(request.getPassword())) {


            String token =
                    jwtUtil.generateToken(
                            request.getUsername()
                    );
log.info("Token returning:{}",token);
            return new LoginResponseDto(token);
        }

        throw new RuntimeException("Invalid Credentials");
    }
}
