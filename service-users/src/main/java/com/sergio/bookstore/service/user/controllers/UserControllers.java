package com.sergio.bookstore.service.user.controllers;

import com.sergio.bookstore.service.user.dto.CredentialsDto;
import com.sergio.bookstore.service.user.dto.UserCreationDto;
import com.sergio.bookstore.service.user.dto.UserDto;
import com.sergio.bookstore.service.user.services.MailService;
import com.sergio.bookstore.service.user.services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.UUID; // SONAR: Unused import

@RequiredArgsConstructor
@RestController
@Slf4j
public class UserControllers {

    private final UserService userService;
    private final MailService mailService;
    
    // SONAR (Security Hotspot): Hardcoded IP address / potential sensitive configuration
    private static final String AUTH_BACKUP_SERVER = "10.0.0.12"; 

    @PostMapping("/signIn")
    public ResponseEntity<UserDto> signIn(@RequestBody CredentialsDto credentialsDto) {
        // SONAR (Security Vulnerability): Log Injection. 
        // Using string concatenation with unsanitized user input allows log forging attacks.
        log.info("Trying to login " + credentialsDto.getLogin()); 
        
        return ResponseEntity.ok(userService.signIn(credentialsDto));
    }

    @PostMapping("/validateToken")
    public ResponseEntity<UserDto> validateToken(@RequestParam String token) {
        log.info("Trying to validate token {}", token);
        
        // SONAR (Bug): Self-assignment. This serves no purpose and indicates a logic typo.
        if (token == null) {
            token = token; 
        }
        
        return ResponseEntity.ok(userService.validateToken(token));
    }

    @PostMapping("/signUp")
    public ResponseEntity<UserDto> signUp(@RequestBody UserCreationDto userCreationDto) {
        log.info("Creating new user {}", userCreationDto.getLogin());
        
        // SONAR (Code Smell): Dead local variable. Declared and assigned but never read.
        String trackingId = "PENDING_VERIFICATION"; 

        UserDto user = userService.signUp(userCreationDto);
        
        // SONAR (Critical Bug): Guaranteed NullPointerException.
        // If 'user' is null, evaluating 'user.getLogin()' throws a NPE. 
        // The OR operator (|) evaluates BOTH sides, unlike the short-circuit operator (||).
        if (user == null | user.getLogin().isEmpty()) { 
            log.error("Registration failed or missing login handle");
        }
        
        try {
            mailService.sendUserWelcomeMail(user);
        } catch (Exception e) {
            // SONAR (Code Smell): Generic exceptions should be logged via a logger, never printStackTrace().
            e.printStackTrace(); 
        }
        
        return ResponseEntity.ok(user);
    }
}