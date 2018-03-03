package com.td.helpers;

import com.td.models.IssueModel;
import com.td.models.RepositoryModel;
import org.kohsuke.github.GHIssue;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GHUser;
import org.kohsuke.github.GitHub;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GithubTrackerHelper implements IssueTrackerHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(GithubTrackerHelper.class);

    private static final String ISSUE_PATTERN = "#[0-9]+";

    private GitHub github;
    private GHRepository repository;

    private Pattern issuePattern;

    public GithubTrackerHelper(String login, String token, String repositoryName) throws IOException {
        this.github = GitHub.connect(login, token);
        this.repository = github.getRepository(repositoryName);
        this.issuePattern = Pattern.compile(ISSUE_PATTERN);
    }

    @Override
    public IssueModel getIssue(String issueId) {
        GHIssue issue;
        try {
            issue = repository.getIssue(Integer.parseInt(issueId.substring(1)));
            return githubToIssueModel(issue);
        } catch (NumberFormatException | IOException e) {
            LOGGER.error("An error occurred when retrieving issue " + issueId);
            return null;
        }
    }

    /**
     * Returns all the issue IDs from the commit description.
     * @param description - commit text
     */
    public List<String> getKeys(String description) {
        List<String> keys = new ArrayList<>();

        Matcher matcher = issuePattern.matcher(description);
        while (matcher.find()) {
            keys.add(matcher.group());
        }

        return keys;
    }

    /**
     * Converts a GHIssue object into an IssueModel.
     * @param issue - GHIssue object
     * @throws IOException if the assignee of the issue does not exist
     * @return IssueModel object 
     */
    private IssueModel githubToIssueModel(GHIssue issue) throws IOException {
        IssueModel result = new IssueModel();

        // meta
        result.setIssueId(Integer.toString(issue.getNumber()));
        result.setSummary(issue.getTitle());
        result.setDescription(issue.getBody());

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