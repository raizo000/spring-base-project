package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class TestAPiService {

  @Autowired
  RestClient restClient;


  public void getTestData() {

    var result = restClient.get().uri("https://jsonplaceholder.typicode.com/todos/1").retrieve()
        .body(String.class);
    System.out.println(result);
  }
}
