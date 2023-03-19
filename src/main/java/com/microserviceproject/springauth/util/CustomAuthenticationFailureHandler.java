package com.microserviceproject.springauth.util;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import com.microserviceproject.springauth.exception.CustomEmailNotConfirmedException;


public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {

        if (exception instanceof UsernameNotFoundException){
            response.sendRedirect("/auth/login?error=user-not-found");
        } else if (exception instanceof BadCredentialsException){
            response.sendRedirect("/auth/login?error=bad-credentials");
        } else if(exception.getClass() == DisabledException.class) {
            response.sendRedirect("/auth/login?error=user-is-disabled");
        }
        else {
            response.sendRedirect("/auth/login?error=internal-error");
        }
        
    }

    
    
}
