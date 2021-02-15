package com.cursor.library.controllers;

import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WelcomeController {

    @GetMapping("/helloworld")
    @Secured({"ROLE_READ", "ROLE_ADMIN"})
    String helloWorld() {
        return "Hello world";
    }
}
