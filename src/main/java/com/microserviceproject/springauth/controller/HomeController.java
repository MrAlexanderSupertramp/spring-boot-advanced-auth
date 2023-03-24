package com.microserviceproject.springauth.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/")
public class HomeController {

    @GetMapping
    @ResponseBody
    public String home() {
        return "Wanakkam. Auth application is up and running!";
    }
    
}
