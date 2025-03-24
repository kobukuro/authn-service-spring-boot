package com.peter.authnservicespringboot.service;

import com.peter.authnservicespringboot.domain.dto.UserRegistrationRequest;
import com.peter.authnservicespringboot.domain.entity.AppUser;

public interface UserService {
    AppUser register(UserRegistrationRequest request);
}
