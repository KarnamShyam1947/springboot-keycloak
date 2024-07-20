package com.shyam.controllers;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shyam.dto.UserRequest;
import com.shyam.services.KeyCloakService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final KeyCloakService service;
    
    @PostMapping("/register")
    public void register(@RequestBody UserRequest request) {
        service.addUser(request);
    }

}