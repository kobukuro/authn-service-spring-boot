package com.peter.authnservicespringboot.repository;


import com.peter.authnservicespringboot.domain.entity.AppUser;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<AppUser, Long> {
    boolean existsByEmail(String email);

    Optional<AppUser> findByEmail(String email);
}
