package com.td.controllers;

import com.td.db.CommitRepository;
import com.td.models.CommitModel;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CommitController extends BaseController {

    @Autowired
    private CommitRepository commitRepository;

    @GetMapping("/repos/{id}/commits")
    public List<CommitModel> getCommits(@PathVariable("id") String repoId) {
        return commitRepository.findCommitModelsByRepositoryId(repoId);
    }

    @GetMapping("/repos/{id}/commits/{sha}")
    public CommitModel getCommit(@PathVariable("id") String repoId, @PathVariable("sha") String sha) {
        return commitRepository.findCommitModelByShaAndRepositoryId(sha, repoId);
    }

}