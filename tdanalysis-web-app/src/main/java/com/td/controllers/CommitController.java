package com.td.controllers;

import com.td.db.CommitRepository;
import com.td.models.CommitModel;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<CommitModel> getCommit(@PathVariable("id") String repoId, @PathVariable("sha") String sha) {
        CommitModel result = commitRepository.findCommitModelByShaAndRepositoryId(sha, repoId);
        return result == null ? ResponseEntity.notFound().build() : ResponseEntity.ok().body(result);
    }

}