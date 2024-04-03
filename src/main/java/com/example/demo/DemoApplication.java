package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import com.example.demo.service.TestAPiService;

@SpringBootApplication
@EnableScheduling
public class DemoApplication {

  @Autowired
  TestAPiService testAPiService;

  public static void main(String[] args) {

    SpringApplication.run(DemoApplication.class, args);
  }


}
