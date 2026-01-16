package com.digivikings.saml.controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class PrivateController {
    @GetMapping("/private")
    public Map<String, Object> privatePage(Authentication auth) {
        return Map.of("ok", true, "user", auth.getName());
    }
}
