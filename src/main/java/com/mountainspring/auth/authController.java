package com.mountainspring.auth;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class authController {

    @GetMapping("/hello")
    String hello() {
        return "I am authenticated";
    }

}
