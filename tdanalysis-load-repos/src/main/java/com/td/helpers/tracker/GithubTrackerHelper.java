package com.td.helpers.tracker;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.td.models.IssueModel;

import org.kohsuke.github.GHIssue;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GHUser;
import org.kohsuke.github.GitHub;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GithubTrackerHelper extends IssueTrackerHelper {

    private static final Logger logger = LoggerFactory.getLogger(GithubTrackerHelper.class);
    private static final String PATTERN = "#[0-9]+";

    private GitHub github;
    private GHRepository repository;

    public GithubTrackerHelper(String repo, String login, String token) {
        this.issuePattern = Pattern.compile(PATTERN);
        initialise(repo, login, token);
    }

    void initialise(String repo, String login, String token) {
        try {
            this.github = GitHub.connect(login, token);
            this.repository = github.getRepository(repo);
        } catch (IOException e) {
            logger.error("An error occurred when connecting to GitHub", e);
        }
    }

    @Override
    public Optional<IssueModel> getIssue(String issueId) {
        GHIssue issue;
        try {
            issue = repository.getIssue(Integer.parseInt(issueId.substring(1)));
            return Optional.of(githubToIssueModel(issue));
        } catch (NumberFormatException | IOException e) {
            logger.error("An error occurred when retrieving issue " + issueId);
            return Optional.empty();
        }
    }

    /**
     * Converts a GHIssue object into an IssueModel.
     * @param issue - GHIssue object
     * @throws IOException if the assignee of the issue does not exist
     * @return IssueModel object 
     */
    private IssueModel githubToIssueModel(GHIssue issue) throws IOException {
        IssueModel result = new IssueModel();

        String repoName = repository.getName();
        String issueKey = Integer.toString(issue.getNumber());

        // meta
        result.setIssueId(repoName + "/" + issueKey);
        result.setIssueKey(issueKey);
        result.setSummary(issue.getTitle());
        result.setDescription(issue.getBody());
        result.setLabels(issue.getLabels().stream().map(gh -> gh.getName()).collect(Collectors.toSet()));

        // assignee
        List<GHUser> assignees = issue.getAssignees();
        result.setAssignee(assignees.size() > 0 ? assignees.get(0).getName() : "None");
        result.setStatus(issue.getState().toString());

        // dates
        result.setCreated(dateToLocalDateTime(issue.getCreatedAt()));
        Date closed = issue.getClosedAt();
        if (closed != null) {
            result.setClosed(dateToLocalDateTime(closed));
        }

        return result;
    }

    /**
     * Converts a {@link Date} object into a {@link LocalDateTime} object.
     */
    private LocalDateTime dateToLocalDateTime(Date date) throws IOException {
        return new Timestamp(date.getTime()).toLocalDateTime();
    }
}