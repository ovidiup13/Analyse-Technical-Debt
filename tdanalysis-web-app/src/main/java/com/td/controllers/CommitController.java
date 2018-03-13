package com.td.controllers;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.td.facades.RepositoryFacade;
import com.td.models.CommitModel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CommitController extends BaseController {

    @Autowired
    private RepositoryFacade repositoryFacade;

    @GetMapping("/repos/{id}/commits")
    public List<CommitModel> getCommits(@PathVariable("id") String repoId) {
        return repositoryFacade.getAllCommits(repoId);
    }

    @GetMapping("/repos/{id}/commits/{sha}")
    public ResponseEntity<CommitModel> getCommit(@PathVariable("id") String repoId, @PathVariable("sha") String sha) {
        Optional<CommitModel> commit = repositoryFacade.getCommit(repoId, sha);
        return commit.isPresent() ? ResponseEntity.ok().body(commit.get()) : ResponseEntity.notFound().build();
    }

    /***
     * Returns an array of map objects, where each key-value pair is described by issueKey - list of commits.
     */
    @GetMapping("/repos/{id}/issue-commits")
    public List<Map<String, List<CommitModel>>> getCommitsByIssue(@PathVariable String id) {
        return repositoryFacade.getIssuesAndCommitsFiltered(id);
    }
}