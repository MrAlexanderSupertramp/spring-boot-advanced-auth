package com.microserviceproject.springauth.exception;

import org.springframework.web.bind.annotation.ExceptionHandler;


public class CustomGlobalExceptionHandler {

    @ExceptionHandler(CustomEmailNotConfirmedException.class)
    public String handleCustomEmailNotConfirmedException() {
        return "redirect:/auth/register?error=trueeee";
    }
    
}
