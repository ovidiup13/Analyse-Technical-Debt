package com.td.controllers;

import java.util.List;

import com.td.db.ProjectRepository;
import com.td.models.RepositoryModel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RepositoryController extends BaseController {

    @Autowired
    ProjectRepository repository;

    @GetMapping("/repos")
    public List<RepositoryModel> getProjects() {
        Sort sort = new Sort(Sort.Direction.ASC, "id");
        return repository.findAll(sort);
    }

    @GetMapping("/repos/{id}")
    public ResponseEntity<RepositoryModel> getRepository(@PathVariable("id") String id) {
        RepositoryModel result = repository.findOne(id);
        return result == null ? ResponseEntity.notFound().build() : ResponseEntity.ok().body(result);
    }
}
