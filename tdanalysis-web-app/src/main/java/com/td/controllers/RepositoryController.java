package com.td.controllers;

import java.util.List;
import java.util.Optional;

import com.td.facades.RepositoryFacade;
import com.td.models.RepositoryModel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RepositoryController extends BaseController {

    @Autowired
    private RepositoryFacade repositoryFacade;

    @GetMapping("/repos")
    public List<RepositoryModel> getRepositories() {
        return repositoryFacade.getRepositories();
    }

    @GetMapping("/repos/{id}")
    public ResponseEntity<RepositoryModel> getRepository(@PathVariable String id) {
        Optional<RepositoryModel> repo = repositoryFacade.getRepository(id);
        return repo.isPresent() ? ResponseEntity.ok().body(repo.get()) : ResponseEntity.notFound().build();
    }
}
