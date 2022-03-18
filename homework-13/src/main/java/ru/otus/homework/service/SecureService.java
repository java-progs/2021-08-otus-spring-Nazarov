package ru.otus.homework.service;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
public class SecureService {

    @PreAuthorize("hasRole('ROLE_USER')")
    public void checkRoleUser() {}

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public void checkRoleAdmin() {}
}
