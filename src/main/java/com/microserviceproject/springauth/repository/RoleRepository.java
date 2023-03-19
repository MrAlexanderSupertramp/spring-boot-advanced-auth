package com.microserviceproject.springauth.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.microserviceproject.springauth.model.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {

    Role findByName(String name);
    
}
