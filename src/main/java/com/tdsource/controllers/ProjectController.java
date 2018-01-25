package com.tdsource.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProjectController {

    @RequestMapping("/")
    public String index() {
        return "Greetings dear friend!";
    }

}
