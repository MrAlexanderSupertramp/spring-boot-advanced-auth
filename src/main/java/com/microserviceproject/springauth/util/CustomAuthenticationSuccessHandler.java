package com.microserviceproject.springauth.util;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;

import com.microserviceproject.springauth.exception.CustomEmailNotConfirmedException;
import com.microserviceproject.springauth.model.User;


public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler{

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        User user = (User) authentication.getPrincipal();

        if (user.isDisabled() == true) { // user.emailNotConfirmed()

            System.out.println("user's email is not confirmed");

            System.out.println("loggin out user");

            System.out.println("response sent");
            
            SecurityContextHolder.clearContext();
            HttpSession session = request.getSession(false);
            if (session != null) {
                session.invalidate();
            }
            response.sendRedirect("/auth/confirm_email");

            // SecurityContextLogoutHandler securityContextLogoutHandler = new SecurityContextLogoutHandler();
            // securityContextLogoutHandler.logout(request, response, authentication);

            
        } else {
            response.sendRedirect("/admin");
        }


    }
    
}
