package com.serhat.taskFlow.repository;

import com.serhat.taskFlow.entity.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<AppUser,Long> {
    Optional<AppUser> findByUsername(String username);

    Optional<AppUser> findByEmailOrUsernameOrPhone(String email, String username, String phone);
}
