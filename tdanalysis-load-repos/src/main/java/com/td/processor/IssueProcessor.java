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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IssueProcessor {

    private static Logger logger = LoggerFactory.getLogger(IssueProcessor.class);

    private String username;
    private String password;
    private RepositoryModel repositoryModel;
    private IssueTrackerHelper issueTrackerHelper;

    public IssueProcessor(String username, String password, RepositoryModel repo) {
        this.username = username;
        this.password = password;
        this.repositoryModel = repo;
        this.issueTrackerHelper = getTrackerHelper();
        logger.info(String.format("Successfully initialised IssueTrackerHelper for repository %s", repo.getName()));
    }

    /***
     * Retrieve all issues associated with the commit.
     */
    public List<IssueModel> getIssues(CommitModel commit) {
        logger.info(String.format("Retrieving issues for commit %s", commit.getSha()));
        List<String> issueKeys = getIssueKeys(commit);

        // retrieve issues and set id
        List<IssueModel> issues = issueKeys.stream().map(issueTrackerHelper::getIssue).flatMap(o -> streamopt(o))
                .collect(Collectors.toList());
        issues.forEach(issue -> issue.setIssueId(repositoryModel.getName() + "/" + issue.getIssueKey()));

        return issues;
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
            URI uri = URI.create(repositoryModel.getIssueTrackerURI());
            return new JiraTrackerHelper(uri, username, password);
        } else {
            String repoId = repositoryModel.getAuthor() + "/" + repositoryModel.getName();
            return new GithubTrackerHelper(repoId, username, password);
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
