package com.td.controllers;

import java.util.List;

import com.td.db.ProjectRepository;
import com.td.models.RepositoryModel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProjectController extends BaseController {

    @Autowired
    ProjectRepository repository;

    @GetMapping("/repos")
    public List<RepositoryModel> getProjects() {
        return repository.findAll();
    }

    @GetMapping("/repos/{id}")
    public RepositoryModel getRepository(@PathVariable("id") String id) {
        return repository.findOne(id);
    }
}
