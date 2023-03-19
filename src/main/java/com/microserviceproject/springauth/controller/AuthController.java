package com.microserviceproject.springauth.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.microserviceproject.springauth.helper.Messenger;
import com.microserviceproject.springauth.service.UserService;


@Controller
@RequestMapping("/auth")
@SuppressWarnings({"rawtypes"})
public class AuthController {

    @Autowired
    private UserService userService;


    @GetMapping(value = {"/login", "/login/"})
    public String login(@RequestParam Map<String, String> params, Model model, RedirectAttributes redirectAttributes) { // can also use if single param : @RequestParam(name="error", required=false) String error

        // prevent logged in user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(!(authentication == null || authentication instanceof AnonymousAuthenticationToken)) {
            return "redirect:/admin";
        }
        
        if (params.get("error") != null && params.get("error").matches("user-not-found")) {
            Messenger messenger = new Messenger<>("failure", "User does not exist");
            model.addAttribute("messenger", messenger);

        } else if (params.get("error") != null && params.get("error").matches("user-is-disabled")) {
            Messenger messenger = new Messenger<>("failure", "The account is disabled");
            model.addAttribute("messenger", messenger);

        } else if (params.get("error") != null && params.get("error").matches("bad-credentials")) {
            Messenger messenger = new Messenger<>("failure", "Improper credentials provided");
            model.addAttribute("messenger", messenger);

        } else if (params.get("error") != null && params.get("error").matches("internal-error")) {
            Messenger messenger = new Messenger<>("failure", "Internal error. Please try again later");
            model.addAttribute("messenger", messenger);
        }
        
        if (params.get("logout") != null && params.get("logout").equals("success")) {
            Messenger messenger = new Messenger<>("success", "Logout successful");
            model.addAttribute("messenger", messenger);
        }

        return "AuthLogin.html";
    }


    @GetMapping(value = {"/register", "/register/"})
    public String registerGet() {
        return "AuthRegister.html";
    }


    @PostMapping(value = {"/register", "/register/"})
    public String registerPost(@RequestParam Map<String, String> params, RedirectAttributes redirectAttributes) {

        Messenger messenger = userService.saveUser(params);

        if(messenger.getStatus().equals("failure")) {
            redirectAttributes.addFlashAttribute("messenger", messenger);
            return "redirect:/auth/register";
        }

        redirectAttributes.addFlashAttribute("messenger", messenger);
        return "redirect:/auth/login";
    }
    
}
