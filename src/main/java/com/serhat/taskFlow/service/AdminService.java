package com.serhat.taskFlow.service;

import com.serhat.taskFlow.entity.Admin;
import com.serhat.taskFlow.exception.AdminNotFoundException;
import com.serhat.taskFlow.interfaces.AdminInterface;
import com.serhat.taskFlow.repository.AdminRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminService implements AdminInterface {
    private final AdminRepository adminRepository;

    @Override
    public Admin findById(Long id) {
        return adminRepository.findById(id)
                .orElseThrow(()-> new AdminNotFoundException("Admin not found by id : "+id));
    }

    @Override
    public Admin findByUsername(String username) {
        return adminRepository.findByUsername(username)
                .orElseThrow(()-> new AdminNotFoundException("Admin not found by username : "+username));
    }
}
