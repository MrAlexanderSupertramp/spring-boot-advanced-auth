package com.microserviceproject.springauth.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.microserviceproject.springauth.model.User;


// @Repository
public interface UserRepository extends JpaRepository<User, Long> {
    public User findByUsername(String username);
    
}
