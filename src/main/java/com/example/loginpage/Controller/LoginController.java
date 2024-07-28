package com.example.loginpage.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {
    @GetMapping("/sign")
    public String sign() {
        return "sign";
    }
}