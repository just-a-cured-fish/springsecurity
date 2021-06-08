package com.yxh.springsecurity.Controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class TestController {
    @GetMapping("hello")
    public String add(){
        return "helloworld";
    }
    @GetMapping("index")
    public String add2(){
        return "helloindex";
    }
}
