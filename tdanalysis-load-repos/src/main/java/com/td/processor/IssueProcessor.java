package com.td.processor;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.td.helpers.tracker.GithubTrackerHelper;
import com.td.helpers.tracker.IssueTrackerHelper;
import com.td.helpers.tracker.JiraTrackerHelper;
import com.td.models.CommitModel;
import com.td.models.IssueModel;
import com.td.models.RepositoryModel;

public class IssueProcessor {

    private String username;
    private String password;
    private RepositoryModel repositoryModel;
    private IssueTrackerHelper issueTrackerHelper;

    public IssueProcessor(String username, String password, RepositoryModel repo) {
        this.username = username;
        this.password = password;
        this.repositoryModel = repo;
        this.issueTrackerHelper = getTrackerHelper();
    }

    public List<IssueModel> getIssues(CommitModel commit) {
        List<String> issueKeys = getIssueKeys(commit);

        return issueKeys.stream().map(issueTrackerHelper::getIssue).flatMap(o -> streamopt(o))
                .collect(Collectors.toList());
    }

    public List<String> getIssueIds(List<IssueModel> issues) {
        return issues.stream().map(issue -> issue.getIssueId()).collect(Collectors.toList());
    }

    Optional<IssueModel> getIssue(String issueKey) {
        Optional<IssueModel> optIssue = issueTrackerHelper.getIssue(issueKey);
        if (!optIssue.isPresent()) {
            return optIssue;
        }

        IssueModel issue = optIssue.get();
        issue.setRepositoryId(repositoryModel.getId());
        issue.setIssueId(repositoryModel.getName() + "/" + issueKey);

        return Optional.of(issue);
    }

    List<String> getIssueKeys(CommitModel commit) {
        return issueTrackerHelper.getKeys(commit.getMessage());
    }

    /**
    * Method that returns an instance of IssueTrackerHelper based on the type of issue tracker for each repository.
    */
    private IssueTrackerHelper getTrackerHelper() {
        if (repositoryModel.getIssueTrackerURI().contains("jira")) {
            return new JiraTrackerHelper(URI.create(repositoryModel.getIssueTrackerURI()), username, password);
        } else {
            return new GithubTrackerHelper(repositoryModel.getIssueTrackerURI(), username, password);
        }
    }

    /**
    * Turns an Optional<T> into a Stream<T> of length zero or one depending upon
    * whether a value is present.
    */
    static <T> Stream<T> streamopt(Optional<T> opt) {
        if (opt.isPresent())
            return Stream.of(opt.get());
        else
            return Stream.empty();
    }

}
