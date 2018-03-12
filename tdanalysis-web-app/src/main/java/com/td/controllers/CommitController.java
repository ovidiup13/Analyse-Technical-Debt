package com.td.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.td.db.CommitRepository;
import com.td.db.IssueRepository;
import com.td.models.CommitModel;
import com.td.models.IssueModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CommitController extends BaseController {

    @Autowired
    private CommitRepository commitRepository;

    @Autowired
    private IssueRepository issueRepository;

    @GetMapping("/repos/{id}/commits")
    public List<CommitModel> getCommits(@PathVariable("id") String repoId) {
        Sort sort = new Sort(Sort.Direction.ASC, "timestamp");
        return commitRepository.findCommitModelsByRepositoryId(repoId, sort);
    }

    @GetMapping("/repos/{id}/commits/{sha}")
    public ResponseEntity<CommitModel> getCommit(@PathVariable("id") String repoId, @PathVariable("sha") String sha) {
        Sort sort = new Sort(Sort.Direction.ASC, "timestamp");
        CommitModel result = commitRepository.findCommitModelByShaAndRepositoryId(sha, repoId, sort);
        return result == null ? ResponseEntity.notFound().build() : ResponseEntity.ok().body(result);
    }

    // @GetMapping("/repos/{id}/commits?{author}")
    // public List<CommitModel> getCommitsByAuthor(@PathVariable("id") String repoId,
    //         @RequestParam("author") String author) {
    //     Sort sort = new Sort(Sort.Direction.ASC, "timestamp");
    //     return commitRepository.findCommitModelsByRepositoryIdAndAuthor(repoId, author, sort);
    // }

    @GetMapping("/repos/{id}/issue-commits")
    public Map<String, List<CommitModel>> getCommitsByIssue(@PathVariable String id) {
        Sort sortCreated = new Sort(Sort.Direction.ASC, "created");
        Sort sortTimestamp = new Sort(Sort.Direction.ASC, "timestamp");
        List<IssueModel> issues = issueRepository.findIssueModelsByRepositoryId(id, sortCreated);

        Map<String, List<CommitModel>> result = new HashMap<>();
        issues.forEach(issue -> {
            List<CommitModel> commits = commitRepository.findCommitModelsByIssueModels(issue.getIssueId(),
                    sortTimestamp);
            result.put(issue.getIssueKey(), commits);
        });

        return result;
    }
}