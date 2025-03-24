package com.peter.authnservicespringboot.controller;

import com.peter.authnservicespringboot.domain.dto.UserRegistrationRequest;
import com.peter.authnservicespringboot.domain.dto.UserRegistrationResponse;
import com.peter.authnservicespringboot.domain.entity.AppUser;
import com.peter.authnservicespringboot.service.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    private final UserService userService;

    @PostMapping
    // @Valid annotation is used to validate the request body
    public ResponseEntity<UserRegistrationResponse> register(@Valid @RequestBody UserRegistrationRequest request) {
        AppUser registeredUser = userService.register(request);
        UserRegistrationResponse response = new UserRegistrationResponse(
                registeredUser.getId(),
                registeredUser.getEmail(),
                registeredUser.getCreatedAt()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
