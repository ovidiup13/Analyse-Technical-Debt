package com.td.helpers.tracker;

import java.net.URI;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.RestClientException;
import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.api.domain.TimeTracking;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;
import com.td.models.IssueModel;
import com.td.models.RepositoryModel;
import com.td.models.IssueModel.TimeTracker;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JiraTrackerHelper implements IssueTrackerHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(JiraTrackerHelper.class);

    private static final String STORY_POINTS_FIELD = "Story Points";
    private static final String ISSUE_PATTERN = "[A-Z]+-[0-9]+";

    private String username;
    private String password;

    private RepositoryModel repository;

    private Pattern issuePattern;

    private JiraRestClient jiraRestClient;

    public JiraTrackerHelper(String username, String password, RepositoryModel repositoryModel) {
        this.username = username;
        this.password = password;
        this.repository = repositoryModel;
        this.issuePattern = Pattern.compile(ISSUE_PATTERN);
        this.jiraRestClient = initialise();
    }

    /***
     * Initializes the REST client to retrieve Jira details.
     */
    private JiraRestClient initialise() {
        LOGGER.info("Initialising the Jira REST client");
        return new AsynchronousJiraRestClientFactory().createWithBasicHttpAuthentication(getJiraURI(), this.username,
                this.password);
    }

    /***
     * Retrieves the issue details associated with the issue key.
     */
    public Optional<IssueModel> getIssue(String issueKey) {
        LOGGER.info(
                String.format("Retrieving issue %s from Jira URL %s", issueKey, this.repository.getIssueTrackerURI()));

        try {
            Issue issue = jiraRestClient.getIssueClient().getIssue(issueKey).claim();
            return Optional.of(jiraIssueToIssueModel(issue));
        } catch (RestClientException e) {
            LOGGER.error("Could not retrieve issue " + issueKey);
            return Optional.empty();
        }
    }

    public List<String> getKeys(String description) {
        List<String> keys = new ArrayList<>();

        Matcher matcher = issuePattern.matcher(description);
        while (matcher.find()) {
            keys.add(matcher.group());
        }

        return keys;
    }

    /***
     * Returns a URI object of the Jira instance.
     */
    private URI getJiraURI() {
        return URI.create(this.repository.getIssueTrackerURI());
    }

    /***
     * Converts from Joda DateTime object to Java LocalDateTime object.
     */
    private LocalDateTime fromDateTime(DateTime dt) {

        if (dt == null) {
            return null;
        }

        Instant instant = dt.toGregorianCalendar().toInstant();
        ZoneId zone = dt.getZone().toTimeZone().toZoneId();
        return LocalDateTime.ofInstant(instant, zone);
    }

    private IssueModel jiraIssueToIssueModel(Issue issue) {
        IssueModel result = new IssueModel();

        String repositoryName = repository.getName();
        String issueKey = issue.getKey();

        // meta
        result.setIssueId(repositoryName + "/" + issueKey);
        result.setIssueKey(issueKey);
        result.setType(issue.getIssueType().getName());
        result.setStatus(issue.getStatus().getName());

        // story points
        // System.out.println(issue.getFieldByName(STORY_POINTS_FIELD));
        // result.setStoryPoints();

        // description
        result.setSummary(issue.getSummary());
        result.setDescription(issue.getDescription());
        result.setPriority(issue.getPriority() != null ? issue.getPriority().getName() : "None");
        result.setAssignee(issue.getAssignee() != null ? issue.getAssignee().getDisplayName() : "None");
        result.setLabels(issue.getLabels());

        // time
        result.setCreated(fromDateTime(issue.getCreationDate()));
        result.setClosed(fromDateTime(issue.getUpdateDate()));
        result.setDue(fromDateTime(issue.getDueDate()));

        //tracking
        TimeTracking tracking = issue.getTimeTracking();
        TimeTracker tracker = new IssueModel.TimeTracker();
        tracker.setEstimate(tracking.getOriginalEstimateMinutes() != null ? tracking.getOriginalEstimateMinutes() : 0);
        tracker.setRemaining(
                tracking.getRemainingEstimateMinutes() != null ? tracking.getRemainingEstimateMinutes() : 0);
        tracker.setLogged(tracking.getTimeSpentMinutes() != null ? tracking.getTimeSpentMinutes() : 0);
        result.setTimeTracker(tracker);

        result.setRepositoryId(this.repository.getId());

        return result;
    }
}
