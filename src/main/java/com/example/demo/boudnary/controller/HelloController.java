package com.example.demo.boudnary.controller;

import java.security.Principal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class HelloController {

  @GetMapping("/hello")
  public String getMethodName(Principal principal) {
    return "Hello" + principal.getName();
  }

}
