package com.peter.authnservicespringboot.service.impl;

import com.peter.authnservicespringboot.domain.dto.UserRegistrationRequest;
import com.peter.authnservicespringboot.domain.entity.AppUser;
import com.peter.authnservicespringboot.exception.EmailAlreadyExistsException;
import com.peter.authnservicespringboot.repository.UserRepository;
import com.peter.authnservicespringboot.service.EmailService;
import com.peter.authnservicespringboot.service.UserService;
import com.peter.authnservicespringboot.util.JwtUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
@Transactional // Utilize Spring's transaction management to roll back the transaction if an exception occurs
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final JwtUtils jwtUtils;
    @Value("${app-name}")
    private String appName;
    @Value("${jwt.verification.expiration}")
    private long verificationTokenExpirationInMilliseconds;
    @Value("${frontend-url}")
    private String frontendUrl;

    public UserServiceImpl(UserRepository userRepository, EmailService emailService, JwtUtils jwtUtils) {
        this.userRepository = userRepository;
        this.emailService = emailService;
        this.jwtUtils = jwtUtils;
    }

    @Override
    public AppUser register(UserRegistrationRequest request) {
        String firstName = request.firstName();
        String lastName = request.lastName();
        String email = request.email();
        if (userRepository.existsByEmail(email)) {
            throw new EmailAlreadyExistsException("This email has been registered.");
        }
        String verificationToken = jwtUtils.generateVerificationToken(email);
        String verificationLink = frontendUrl + "/activate?token=" + verificationToken;
        int verificationTokenExpirationInHours = (int) (verificationTokenExpirationInMilliseconds / 3600000);
        Map<String, Object> templateModel = new HashMap<>();
        templateModel.put("appName", appName);
        templateModel.put("firstName", firstName);
        templateModel.put("lastName", lastName);
        templateModel.put("verificationLink", verificationLink);
        templateModel.put("expirationHours", verificationTokenExpirationInHours);
        emailService.sendHtmlEmail(
                email,
                "Account activation on " + appName, // email subject
                "email/verification-email",  // Thymeleaf template name
                templateModel
        );
        String hashedPassword = BCrypt.hashpw(request.password(), BCrypt.gensalt());
        return userRepository.save(new AppUser(firstName, lastName, email,
                hashedPassword, false));
    }
}
