package com.microserviceproject.springauth.security;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.microserviceproject.springauth.filter.CustomAuthenticationFilter;
import com.microserviceproject.springauth.filter.CustomAuthorizationFilter;
import com.microserviceproject.springauth.service.UserService;
import com.microserviceproject.springauth.service.UserServiceImpl;
import com.microserviceproject.springauth.util.CustomAuthenticationFailureHandler;
import com.microserviceproject.springauth.util.CustomAuthenticationSuccessHandler;



@Configuration
@EnableWebSecurity
public class SecurityConfig {

    public List<String> public_urls = Arrays.asList("/auth/login/**", "/api/v1/auth/public", "/api/v1/auth/register", "/api/v1/auth/login", "/api/v1/auth/validate", "/api/v1/auth/token/refresh");
    public List<String> static_urls = Arrays.asList("/css/**", "/fonts/**", "/images/**", "/js/**");
    // public final String[] public_urls = {"/auth/login", "/auth/login/**"};

    @Autowired
    public AuthenticationConfiguration authenticationConfiguration;

    @Autowired
    public Environment env;

    @Bean
    public UserDetailsService getUserDetailsService() {
        return new UserServiceImpl();
    }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setHideUserNotFoundExceptions(false); // this exception is hidden by default in SS (spring security). It is replaced by BadCredentialsException which we dont want so telling spring SS to not to hide. Details: https://www.springcloud.io/post/2022-05/spring-security-badcredentialsexception/#gsc.tab=0
        // provider.setHideCustomEmailNotConfirmedException(false);
        provider.setUserDetailsService(getUserDetailsService());
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    // @Bean
    // public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) {
    //     return authenticationManager(authenticationConfiguration);
    // }

    @Order(48)
    @Bean
    public SecurityFilterChain mainFilterChain(HttpSecurity httpSecurity) throws Exception {

        httpSecurity
            .requestMatchers().antMatchers("/admin/**", "/auth/**") // or antMatcher("/admin")
            .requestMatchers().antMatchers(static_urls.stream().toArray(String[]::new))
            .and()
            .csrf().disable()
            .authorizeHttpRequests()
                .antMatchers(public_urls.stream().toArray(String[]::new)) // ~ toArray(element -> new String[element])
                .permitAll()
                .antMatchers(static_urls.stream().toArray(String[]::new))
                .permitAll()
            .and()
            .authorizeHttpRequests()
                .antMatchers("/admin/**")
                .hasRole("ADMIN")
                .and()
            .rememberMe()
                .key(env.getProperty("custom.secretKey"))
                .rememberMeCookieName("ExtraJunk")
                .rememberMeParameter("RememberMe") //remember-me is default value
                .tokenValiditySeconds(864000)
                .userDetailsService(getUserDetailsService())
                .and()
            .formLogin()
                .loginPage("/auth/login")
                .defaultSuccessUrl("/admin/dashboard")
                .successHandler(customAuthenticationSuccessHandler())
                .failureHandler(customAuthenticationFailureHandler()) // .failureUrl("/auth/login?error=true")
                .and()
            .logout()
                .invalidateHttpSession(true)
                .clearAuthentication(true)
                .deleteCookies("JSESSIONID")
                .deleteCookies("ExtraJunk")
                .logoutRequestMatcher(new AntPathRequestMatcher("/auth/logout"))
                .logoutSuccessUrl("/auth/login?logout=success")
                .permitAll();

        return httpSecurity.build();

    }

    @Order(49)
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception{

        httpSecurity.csrf().disable();
        httpSecurity.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        httpSecurity.authorizeHttpRequests().antMatchers(public_urls.stream().toArray(String[]::new)).permitAll();
        httpSecurity.authorizeHttpRequests().antMatchers("/api/v1/**").hasRole("ADMIN");
        // httpSecurity.authorizeHttpRequests().anyRequest().permitAll().and().formLogin();
        httpSecurity.authorizeHttpRequests().anyRequest().authenticated();
        // httpSecurity.addFilter(new CustomAuthenticationFilter(authenticationConfiguration.getAuthenticationManager(), env));
        httpSecurity.addFilterBefore(new CustomAuthorizationFilter(env, public_urls), UsernamePasswordAuthenticationFilter.class);

        // httpSecurity.authenticationProvider(daoAuthenticationProvider());

        return httpSecurity.build();
        
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CustomAuthenticationFailureHandler customAuthenticationFailureHandler() {
        return new CustomAuthenticationFailureHandler();
    }
    @Bean
    public CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler() {
        return new CustomAuthenticationSuccessHandler();
    }
    
}
