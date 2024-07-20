package com.shyam.controllers;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DemoController {
    
    @GetMapping
    public String home() {
        return "This is Home Page";
    }

    @GetMapping("/about")
    public Object about() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication;
    }

    @GetMapping("/user")
    @PreAuthorize("hasRole('client_user')")
    public String user() {
        return "This is User Page";
    }
    
    @GetMapping("/admin")
    @PreAuthorize("hasRole('client_admin')")
    public String Admin() {
        return "This is admin Page";
    }

}
