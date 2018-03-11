package com.td.controllers;

import com.td.db.IssueRepository;
import com.td.models.IssueModel;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class IssueController extends BaseController {

    @Autowired
    private IssueRepository issueRepository;

    @GetMapping("/repos/{id}/issues")
    public List<IssueModel> getIssues(@PathVariable("id") String repoId) {
        return issueRepository.findIssueModelsByRepositoryId(repoId);
    }

    @GetMapping("/repos/{id}/issues/{issueId}")
    public ResponseEntity<IssueModel> getIssueById(@PathVariable("id") String repoId,
            @PathVariable("issueId") String issueId) {
        IssueModel issue = issueRepository.findIssueModelByIssueIdAndRepositoryId(issueId, repoId);
        return issue == null ? ResponseEntity.notFound().build() : ResponseEntity.ok().body(issue);
    }

}