package com.boldyrev.pdfbillcreator.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/health")
@CrossOrigin
public class HealthController {

    @GetMapping
    public ResponseEntity checkAvailability() {

        return new ResponseEntity(HttpStatus.OK);
    }
}
