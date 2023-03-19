package com.microserviceproject.springauth.controller.api;

import java.io.IOException;
import java.net.URI;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.core.exc.StreamWriteException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microserviceproject.springauth.helper.Messenger;
import com.microserviceproject.springauth.helper.Tokens;
import com.microserviceproject.springauth.model.Role;
import com.microserviceproject.springauth.model.User;
import com.microserviceproject.springauth.repository.UserRepository;
import com.microserviceproject.springauth.service.UserService;
import com.microserviceproject.springauth.util.GlobalObject;

import lombok.Data;



@RestController
// @RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
@SuppressWarnings({"rawtypes", "unchecked"})
public class ApiAuthController {

    @Autowired
    public UserService userService;

    @Autowired
    public UserRepository userRepository;

    @Autowired
    public BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public Environment env;

    @GetMapping("/public")
    public String publicPath() {
        return "Done Public Bro!";
    }

    @GetMapping("/private")
    public String privatePath() {
        return "Done Private Bro!";
    }

    @PostMapping("/register")
    public ResponseEntity<Messenger> registerUser(@RequestBody Map<String, String> params) { //Map<String, Object> for advance nested shit
        Messenger messenger = userService.saveUser(params);

        if(messenger.getStatus().equals("failure")) {
            return ResponseEntity.status(400).body(messenger);
        }
        return ResponseEntity.status(200).body(messenger);
    }


    @PostMapping("/login")
    public ResponseEntity<Messenger> loginUser(@RequestBody Map<String, String> params, HttpServletRequest request) {

        // check for empty params
        if (params.getOrDefault("username", null) == null || params.getOrDefault("password", null) == null) {
            return ResponseEntity.status(400).body(
                Messenger.builder()
                    .status("failure")
                    .message("Missing authentication parameters")
                    .data(null)
                    .build()
            );
        }

        // fetch user
        Messenger messenger = userService.getUser(params.get("username"));
        if (messenger.getStatus() == "failure") {
            return ResponseEntity.status(400).body(messenger);
        }
        User user = (User) messenger.getData();
        if (user.isDisabled() == true) {
            messenger.setMessage("Account disabled. Reach out to admin.");
            messenger.setData(null);
            return ResponseEntity.status(400).body(messenger);
        }

        // authenticate
        if (!passwordEncoder.matches(params.get("password"), user.getPassword())) {
            messenger.setMessage("improper credentials provided.");
            messenger.setData(null);
            return ResponseEntity.status(400).body(messenger);
        }

        // build token
        Algorithm algorithm = Algorithm.HMAC256(env.getProperty("custom.secretKey").getBytes());
        String access_token = JWT.create()
            .withSubject(params.get("username"))
            .withExpiresAt(new Date(System.currentTimeMillis() + 2*24*60*60*1000L))
            .withIssuer(request.getRequestURL().toString())
            .withClaim("roles", user.getRoles().stream().map(Role::getName).collect(Collectors.toList()))
            .sign(algorithm);

        String refresh_token = JWT.create()
            .withSubject(params.get("username"))
            .withExpiresAt(new Date(System.currentTimeMillis() + 30*24*60*60*1000L))
            .withIssuer(request.getRequestURL().toString())
            .withClaim("roles", user.getRoles().stream().map(Role::getName).collect(Collectors.toList()))
            .sign(algorithm);

        // modifying messenger
        messenger.setMessage("Authentication successful");
        messenger.setData(
            Tokens.builder()
            .access_token(access_token)
            .refresh_token(refresh_token)
            .build()
        );

        return ResponseEntity.status(200).body(messenger);
    }
    

    @GetMapping("/validate")
    public ResponseEntity<?> validateaToken(@RequestParam(name = "token", required = true) String token) {
        
        Algorithm algorithm = Algorithm.HMAC256(env.getProperty("custom.secretKey").getBytes());
        JWTVerifier verifier = JWT.require(algorithm).build();

        try {
            DecodedJWT decodedJWT = verifier.verify(token);
            return ResponseEntity.status(200).body("Token verified");
        } catch (Exception e) {
            return ResponseEntity.status(401).body(e.getMessage());
        }
        
    }
    

    @GetMapping("/token/refresh")
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws StreamWriteException, DatabindException, IOException {
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            try {
                String refresh_token = authorizationHeader.substring("Bearer ".length());
                Algorithm algorithm = Algorithm.HMAC256(env.getProperty("custom.secretKey").getBytes());
                JWTVerifier verifier = JWT.require(algorithm).build();
                DecodedJWT decodedJWT = verifier.verify(refresh_token);
                String username = decodedJWT.getSubject();
                User user = (User) userService.getUser(username).getData();
                String access_token = JWT.create()
                    .withSubject(user.getUsername())
                    .withExpiresAt(new Date(System.currentTimeMillis() + 10*60*1000L))
                    .withIssuer(request.getRequestURL().toString())
                    .withClaim("roles", user.getRoles().stream().map(Role::getName).collect(Collectors.toList()))
                    .sign(algorithm);
                Map<String, String> tokens = new HashMap<>();
                tokens.put("access_token", access_token);
                tokens.put("refresh_token", refresh_token);
                response.setContentType("application/json");
                new ObjectMapper().writeValue(response.getOutputStream(), tokens);
            } catch (Exception exception) {
                response.setHeader("error", exception.getMessage());
                response.setStatus(401);
                Map<String, String> error = new HashMap<>();
                error.put("error_message", exception.getMessage());
                response.setContentType("application/json");
                new ObjectMapper().writeValue(response.getOutputStream(), error);
            }
        } else {
            throw new RuntimeException("Refresh token is missing");
        }
    }


    @GetMapping("/users")
    public ResponseEntity<Messenger<List<User>>> getUsers() {
        return ResponseEntity.status(200).body(userService.getUsers());
    }


    @GetMapping("/user/{username}")
    public ResponseEntity<Messenger> getUser(@PathVariable String username) {
        Messenger messenger = userService.getUser(username);
        return ResponseEntity.status(messenger.getStatus().equals("success") ? 200 : 404).body(messenger);
    }


    @PutMapping("/user/update")
    public ResponseEntity<Messenger> updateUser(@RequestBody Map<String, String> params) {
        Messenger messenger = userService.updateUser(params);
        return ResponseEntity.status(messenger.getStatus().equals("failure") ? 500 : 200).body(messenger);
    }


    @PostMapping("/user/add-role")
    public ResponseEntity<Messenger> addRoleToUser(@RequestBody Map<String, String> params) {
        Messenger messenger = userService.addRoleToUser(params.get("username"), params.get("name"));
        return ResponseEntity.status(messenger.getStatus().equals("failure") ? 500 : 200).body(messenger);
    }


    @PostMapping("/user/remove-role")
    public ResponseEntity<Messenger> removeRoleFromUser(@RequestBody Map<String, String> params) {
        Messenger messenger = userService.removeRoleFromUser(params.get("username"), params.get("name"));
        return ResponseEntity.status(messenger.getStatus().equals("failure") ? 500 : 200).body(messenger);
    }


    @GetMapping("/roles")
    public ResponseEntity<Messenger<List<Role>>> getRoles() {
        return ResponseEntity.status(200).body(userService.getRoles());
    }


    @GetMapping("/role/{name}")
    public ResponseEntity<Messenger<Role>> getRole(@PathVariable String name) {
        return ResponseEntity.status(200).body(userService.getRole(name));
    }

    
}

@Data
class RoleToUserForm {
    String username;
    String roleName;
}
