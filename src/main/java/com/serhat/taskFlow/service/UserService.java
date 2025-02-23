package com.serhat.taskFlow.service;

import com.serhat.taskFlow.entity.AppUser;
import com.serhat.taskFlow.exception.UserNotFoundException;
import com.serhat.taskFlow.interfaces.UserInterface;
import com.serhat.taskFlow.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService implements UserInterface {
    private final UserRepository userRepository;

    @Override
    public AppUser findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(()-> new UserNotFoundException("User not found by id : "+id));
    }

    @Override
    public AppUser findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(()-> new UserNotFoundException("User not found by username : "+username));
    }
}
