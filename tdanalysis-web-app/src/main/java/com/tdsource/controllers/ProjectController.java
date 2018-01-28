package com.tdsource.controllers;

import db.ProjectRepository;
import models.RepositoryModel;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin("*")
public class ProjectController {

    ProjectRepository repository;

    @RequestMapping("")
    public String index() {
        return "Greetings dear friend!";
    }

    @RequestMapping("/projects")
    public List<RepositoryModel> getProjects(){
        return repository.findAll();
    }
}
