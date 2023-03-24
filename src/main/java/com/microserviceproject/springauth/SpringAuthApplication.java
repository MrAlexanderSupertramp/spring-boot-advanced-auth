package com.microserviceproject.springauth;

import java.util.ArrayList;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.client.RestTemplate;

import com.microserviceproject.springauth.model.Role;
import com.microserviceproject.springauth.model.User;
import com.microserviceproject.springauth.repository.RoleRepository;
import com.microserviceproject.springauth.repository.UserRepository;
import com.microserviceproject.springauth.service.UserService;

@SpringBootApplication
@EnableEurekaClient
public class SpringAuthApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringAuthApplication.class, args);
	}

	@Bean
	public RestTemplate getRestTemplate() {
		return new RestTemplate();
	}


	// Below config will auto-populate database with two roles which are needed (ROLE_USER and ROLE_ADMIN) along with two users for each role.
	// @Bean
	// public BCryptPasswordEncoder passwordEncoder2() {
    //     return new BCryptPasswordEncoder();
    // }

	// @Bean
	// CommandLineRunner run(UserRepository userRepository, RoleRepository roleRepository, UserService userService) {
	// 	return args -> {
	// 		roleRepository.save(new Role("ROLE_USER"));
	// 		roleRepository.save(new Role("ROLE_ADMIN"));

	// 		userRepository.save(new User("Jiril Admin", "jiril@admin.com", passwordEncoder2().encode("12345678"), "12345678", false, new ArrayList<>()));
	// 		userRepository.save(new User("Jiril User", "jiril@user.com", passwordEncoder2().encode("12345678"), "12345678", false, new ArrayList<>()));

	// 		userService.saveRoleToUser("jiril@admin.com", "ROLE_ADMIN");
	// 		userService.saveRoleToUser("jiril@user.com", "ROLE_USER");
	// 	};
	// }

}
