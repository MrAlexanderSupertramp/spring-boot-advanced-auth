package com.microserviceproject.springauth.service;

import java.util.List;
import java.util.Map;

import com.microserviceproject.springauth.helper.Messenger;
import com.microserviceproject.springauth.model.Role;
import com.microserviceproject.springauth.model.User;


@SuppressWarnings({"rawtypes", "null"})
public interface UserService {

    Messenger saveUser(Map<String, String> params);
    Messenger<List<User>> getUsers();
    Messenger getUser(String username);
    Messenger updateUser(Map<String, String> params);
    Messenger deleteUser(String userID);
    Messenger addRoleToUser(String username, String name);
    Messenger removeRoleFromUser(String username, String name);
    Messenger<List<Role>> getRoles();
    Messenger getRole(String name);
    
}
