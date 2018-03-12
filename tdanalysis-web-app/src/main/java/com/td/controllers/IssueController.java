package com.td.controllers;

import com.td.db.CommitRepository;
import com.td.db.IssueRepository;
import com.td.models.CommitModel;
import com.td.models.IssueModel;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class IssueController extends BaseController {

    @Autowired
    private IssueRepository issueRepository;

    @Autowired
    private CommitRepository commitRepository;

    @GetMapping("/repos/{id}/issues")
    public List<IssueModel> getIssues(@PathVariable("id") String repoId) {
        Sort sort = new Sort(Sort.Direction.ASC, "issueKey");
        return issueRepository.findIssueModelsByRepositoryId(repoId, sort);
    }

    @GetMapping("/repos/{id}/issues/{key}")
    public ResponseEntity<IssueModel> getIssueById(@PathVariable("id") String repoId,
            @PathVariable("key") String issueKey) {
        IssueModel issue = issueRepository.findIssueModelByIssueKeyAndRepositoryId(issueKey, repoId);
        return issue == null ? ResponseEntity.notFound().build() : ResponseEntity.ok().body(issue);
    }

    @GetMapping("/repos/{id}/issues/{key}/commits")
    public List<CommitModel> getCommitsOfIssue(@PathVariable("id") String repoId,
            @PathVariable("key") String issueKey) {
        IssueModel issue = issueRepository.findIssueModelByIssueKeyAndRepositoryId(issueKey, repoId);
        Sort sort = new Sort(Sort.Direction.ASC, "timestamp");
        return commitRepository.findCommitModelsByIssueModels(issue.getIssueId(), sort);
    }

}