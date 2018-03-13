package com.td.controllers;

import java.util.List;
import java.util.Optional;

import com.td.facades.RepositoryFacade;
import com.td.models.CommitModel;
import com.td.models.IssueModel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class IssueController extends BaseController {

    @Autowired
    private RepositoryFacade repositoryFacade;

    @GetMapping("/repos/{id}/issues")
    public List<IssueModel> getIssues(@PathVariable("id") String repoId) {
        return repositoryFacade.getAllIssues(repoId);
    }

    @GetMapping("/repos/{id}/issues/{key}")
    public ResponseEntity<IssueModel> getIssueById(@PathVariable("id") String repoId,
            @PathVariable("key") String issueKey) {
        Optional<IssueModel> issue = repositoryFacade.getIssue(repoId, issueKey);
        return issue.isPresent() ? ResponseEntity.ok().body(issue.get()) : ResponseEntity.notFound().build();
    }

    @GetMapping("/repos/{id}/issues/{key}/commits")
    public List<CommitModel> getCommitsOfIssue(@PathVariable("id") String repoId,
            @PathVariable("key") String issueKey) {
        return repositoryFacade.getAllCommitsByIssue(repoId, issueKey);
    }

}