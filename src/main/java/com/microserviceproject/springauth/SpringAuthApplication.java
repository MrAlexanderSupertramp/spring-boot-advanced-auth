package com.microserviceproject.springauth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

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

	// @Bean
	// public BCryptPasswordEncoder passwordEncoder2() {
    //     return new BCryptPasswordEncoder();
    // }

	// @Bean
	// CommandLineRunner run(UserService userService) {
	// 	return args -> {
	// 		userService.saveRole(new Role(null, "ROLE_USER"));
	// 		userService.saveRole(new Role(null, "ROLE_ADMIN"));

	// 		userService.saveUser(new User(null, "Jiril Zala", "jirilzala", passwordEncoder2().encode("1234"), new ArrayList<>()));
	// 		userService.saveUser(new User(null, "Dash Zala", "dashzala", passwordEncoder2().encode("1234"), new ArrayList<>()));

	// 		userService.saveRoleToUser("jirilzala", "ROLE_USER");
	// 		userService.saveRoleToUser("jirilzala", "ROLE_ADMIN");
	// 		userService.saveRoleToUser("dashzala", "ROLE_USER");
	// 	};
	// }

}
