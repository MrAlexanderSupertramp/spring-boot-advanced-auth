package com.microserviceproject.springauth.controller;

import java.util.Map;

import javax.servlet.ServletContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.microserviceproject.springauth.helper.Messenger;
import com.microserviceproject.springauth.model.User;
import com.microserviceproject.springauth.service.UserService;
import com.microserviceproject.springauth.util.GlobalObject;



@Controller
@RequestMapping("/admin")
@SuppressWarnings({"rawtypes", "unchecked"})
public class AdminController {

    @Autowired
    private UserService userService;

    @Autowired
    ServletContext servletContext;

    @Autowired
    GlobalObject globalObject;

    @Autowired
    private RestTemplate restTemplate;

    private String baseUrl = "http://localhost:8005";

    @RequestMapping("")
    public String admin() {
        return "redirect:/admin/dashboard";
    }

    @RequestMapping("/dashboard")
    public String dashboard(Model model) {

        // HttpHeaders httpHeaders = new HttpHeaders();
        // httpHeaders.add("Authorization", "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ6amlyaWxAZ21haWwuY29tIiwicm9sZXMiOlsiUk9MRV9VU0VSIiwiUk9MRV9BRE1JTiJdLCJpc3MiOiJodHRwOi8vbG9jYWxob3N0OjgwMDUvYXBpL3YxL2xvZ2luIiwiZXhwIjoxNjgwNDcyNDkxfQ.3BlKF-XF-FPYFHFM6eAYPO7oely7Gh9jNCuVd0r77FU");
        // HttpEntity<String> reqEntity = new HttpEntity<String>(httpHeaders);

        // ResponseEntity<Messenger> responseEntity = restTemplate.exchange(baseUrl + "/api/v1/users", HttpMethod.GET, reqEntity, Messenger.class);

        return "index.html";
    }

    @RequestMapping("/user-manager/user/list")
    public String userList(Model model) {
        Messenger messenger = userService.getUsers();
        if (messenger.getStatus().equals("failure")) {
            model.addAttribute("messenger", messenger);
            return "UserList.html";
        }

        Messenger messenger2 = userService.getRoles();
        if (messenger2.getStatus().equals("failure")) {
            model.addAttribute("messenger", messenger2);
            return "UserList.html";
        }

        model.addAttribute("users", messenger.getData());
        model.addAttribute("roles", messenger2.getData());

        return "UserList.html";
    }

    @RequestMapping(value = "/user-manager/user/add", method = RequestMethod.GET)
    public String userAdd(Model model) {
        return "UserAdd.html";
    }

    @RequestMapping(value = "/user-manager/user/add", method = RequestMethod.POST)
    public String userAdd(@RequestParam Map<String, String> params, RedirectAttributes redirectAttributes) {
        Messenger messenger = userService.saveUser(params);

        if (messenger.getStatus().equals("failure")) {
            redirectAttributes.addFlashAttribute("messenger", messenger);
        } else {
            redirectAttributes.addFlashAttribute("messenger", Messenger.builder()
                .status("success")
                .message("New user added to the database")
                .build()
            );
        }

        return "redirect:/admin/user-manager/user/list";
    }

    @RequestMapping(value = "/user-manager/user/edit", method = RequestMethod.POST)
    public String userEdit(@RequestParam Map<String, String> params, RedirectAttributes redirectAttributes) {
        Messenger messenger = userService.updateUser(params);

        redirectAttributes.addFlashAttribute("messenger", messenger);

        return "redirect:/admin/user-manager/user/list";
    }

    @RequestMapping(value="/user-manager/user/delete", method = RequestMethod.GET)
    public String userDelete(@RequestParam(name = "user-id", required = true) String userId, RedirectAttributes redirectAttributes) {
        Messenger messenger = userService.deleteUser(userId);

        redirectAttributes.addFlashAttribute("messenger", messenger);

        return "redirect:/admin/user-manager/user/list";
    }
    
}
