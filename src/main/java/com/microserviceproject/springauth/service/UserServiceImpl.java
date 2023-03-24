package com.microserviceproject.springauth.service;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
// import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.microserviceproject.springauth.exception.CustomEmailNotConfirmedException;
import com.microserviceproject.springauth.helper.Messenger;
import com.microserviceproject.springauth.model.Role;
import com.microserviceproject.springauth.model.User;
import com.microserviceproject.springauth.repository.RoleRepository;
import com.microserviceproject.springauth.repository.UserRepository;

import lombok.extern.slf4j.Slf4j;


@Slf4j
@Service
@SuppressWarnings({"rawtypes", "unchecked"})
public class UserServiceImpl implements UserService, UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private Environment env;

    // UserService impl
    @Override
    public Messenger saveUser(Map<String, String> params) {
        try {
            String password = params.get("password");
            String confirm_password = params.get("confirm_password");

            // password check
            if(!password.equals(confirm_password)) {
                Messenger messenger = new Messenger<>("failure", "Passwords did not match");
                return messenger;
            }

            if(password.length() < 8) {
                Messenger messenger = new Messenger<>("failure", "Password too short.");
                return messenger;
            }

            // check if email exists
            if(userRepository.findByUsername(params.getOrDefault("email", "")) != null) {
                Messenger messenger = new Messenger<>("failure", "Email is already used.");
                return messenger;
            }

            // fetch role
            Role role = roleRepository.findByName(env.getProperty("custom.defaultRole"));
            if(role == null) {
                log.error("Could not find ROLE : {}, while registering new user", env.getProperty("custom.defaultRole"));
                Messenger messenger = new Messenger<>("failure", "Internal Error");
                return messenger;
            }

            // USER role granted as default
            Collection<Role> roles = Arrays.asList(role);

            // saving user to DB
            try {
                User user = User.builder()
                .name(params.get("name"))
                .username(params.get("email"))
                .password(passwordEncoder.encode(params.get("password")))
                .rawPassword(params.get("password"))
                .isDisabled(false)
                .roles(roles)
                .build();

                userRepository.save(user);
            } catch (Exception e) {
                log.error("Something went wrong while saving the user. Could be because of missing fields.");
                Messenger messenger = new Messenger<>("failure", "Internal Error");
                return messenger;
            }

            Messenger messenger = new Messenger<>("success", "Account created successfully");

            return messenger;

        } catch (Exception e) {
            log.error("Something went wrong. Generic error.");
            Messenger messenger = new Messenger<>("failure", "Internal Error");
            return messenger;
        }
        
    }

    @Override
    public Messenger<List<User>> getUsers() {
        Messenger messenger = new Messenger<>();
        messenger.setStatus("success");
        messenger.setMessage("All users retrieved successfully");
        messenger.setData((List<User>) userRepository.findAll());
        return messenger;
    }

    @Override
    public Messenger getUser(String username) {
        User user = userRepository.findByUsername(username);

        return Messenger.builder()
            .status(user != null ? "success" : "failure" )  // ternary operators
            .message(user != null ? "User was found" : "User not found")
            .data(user != null ? user : null)
            .build();
    }

    @Override
    public Messenger updateUser(Map<String, String> params) {

        try {

            String password = params.get("password");
            if(password.length() < 8) {
                Messenger messenger = new Messenger<>("failure", "Password too short");
                return messenger;
            }
    
            User user = userRepository.findByUsername(params.get("username"));
            if(user == null) {
                Messenger messenger = new Messenger<>("failure", "User not found");
                return messenger;
            }
    
            user.setName(params.get("name"));
            user.setPassword(passwordEncoder.encode(password));
            user.setRawPassword(password);
            user.setDisabled(Boolean.parseBoolean(params.get("isDisabled") == null || params.get("isDisabled").isEmpty() ? "false" : "true"));
            userRepository.save(user);
    
            Messenger messenger = new Messenger<>("success", "User info is updated");
    
            return messenger;

        } catch (Exception e) {
            Messenger messenger = new Messenger<>("failure", "Something went wrong");
            return messenger;
        }

    }
    
    @Override
    public Messenger deleteUser(String userID) {
        try {
            User user = userRepository.findByUsername(userID);
            if (user == null) {
                return Messenger.builder().status("failure").message("No user found.").build();
            }
            userRepository.delete(user);
            return Messenger.builder().status("success").message("User was deleted from the database.").build();
        } catch (Exception e) {
            return Messenger.builder().status("failure").message("Something went wrong").build();
        }
    }

    @Override
    public Messenger addRoleToUser(String username, String name) {

        try {
            User user = userRepository.findByUsername(username);
            if (user == null) {
                return Messenger.builder()
                    .status("failure")
                    .message("User not found. Try hitting 'http://falana/api/v1/users")
                    .build();
            }
    
            Role role = roleRepository.findByName(name.toUpperCase());
            if (role == null) {
                return Messenger.builder()
                    .status("failure")
                    .message("Role not found. Try hitting 'http://falana/api/v1/roles")
                    .build();
            }
    
            Collection<Role> userRoles = user.getRoles();
            for (final Role temp_role : userRoles) {
                if (temp_role.equals(role)) {
                    return Messenger.builder()
                        .status("failure")
                        .message("Role is already assigned to user")
                        .build();
                }
            }
            user.getRoles().add(role);
            userRepository.save(user);
    
            return Messenger.builder()
                .status("success")
                .message("New role is assigned to user")
                .build();

        } catch (Exception e) {
            return Messenger.builder()
                .status("failure")
                .message("Something went wrong")
                .build();
        }

    }

    @Override
    public Messenger removeRoleFromUser(String username, String name) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            return Messenger.builder()
                .status("failure")
                .message("User not found. Try hitting 'http://falana/api/v1/users")
                .build();
        }

        Role role = roleRepository.findByName(name.toUpperCase());
        if (role == null) {
            return Messenger.builder()
                .status("failure")
                .message("Role not found. Try hitting 'http://falana/api/v1/roles")
                .build();
        }

        Collection<Role> userRoles = user.getRoles();
        if (!userRoles.stream().anyMatch(role::equals)) {
            return Messenger.builder()
                .status("failure")
                .message("Role does not exist for user")
                .build();
        }
        user.getRoles().remove(role);
        userRepository.save(user);

        return Messenger.builder()
            .status("success")
            .message("Role is removed from user")
            .build();
    }

    @Override
    public Messenger<List<Role>> getRoles() {
        Messenger messenger = new Messenger<>();
        messenger.setStatus("success");
        messenger.setMessage("all roles fetched successfully");
        messenger.setData(roleRepository.findAll());
        return messenger;
    }

    @Override
    public Messenger getRole(String name) {
        Role role = roleRepository.findByName(name);

        return Messenger.builder()
            .status(role != null ? "success" : "failure" )  // ternary operators
            .message(role != null ? "User was found" : "User not found")
            .data(role != null ? role : null)
            .build();
    }


    // UserDetailsService impl
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException, CustomEmailNotConfirmedException {

        User user = userRepository.findByUsername(username);

        if (user == null) {
            log.error("User not found with the username {}", username);
            throw new UsernameNotFoundException("");
        }

        return user; // return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), authorities);
    }

    @Override
    public void saveRoleToUser(String username, String role_name) {
        try {
            User user = userRepository.findByUsername(username);
            Role role = roleRepository.findByName(role_name);
    
            user.setRoles(Arrays.asList(role));
            userRepository.save(user);
        } catch (Exception e) {
            System.out.println("Error happened : " + e.getMessage());
        }
    }

}

