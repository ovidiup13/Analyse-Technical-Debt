package com.td.helpers;

import java.net.URI;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.api.domain.TimeTracking;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;
import com.td.models.IssueModel;
import com.td.models.TimeTracker;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JiraTrackerHelper implements IssueTrackerHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(JiraTrackerHelper.class);

    private static final String STORY_POINTS_FIELD = "Story Points";

    private String username;
    private String password;
    private String jiraUrl;

    private JiraRestClient jiraRestClient;

    public JiraTrackerHelper(String username, String password, String jiraUrl) {
        this.username = username;
        this.password = password;
        this.jiraUrl = jiraUrl;
        this.jiraRestClient = initialise();
    }

    /***
     * Initializes the REST client to retrieve Jira details.
     */
    private JiraRestClient initialise() {
        return new AsynchronousJiraRestClientFactory().createWithBasicHttpAuthentication(getJiraURI(), this.username,
                this.password);
    }

    /***
     * Retrieves the issue details associated with the issue key.
     */
    public IssueModel getIssue(String issueKey) {

        IssueModel result = new IssueModel();

        Issue issue = jiraRestClient.getIssueClient().getIssue(issueKey).claim();

        // meta
        result.setIssueId(issueKey);
        result.setType(issue.getIssueType().getName());
        result.setStatus(issue.getStatus().getName());

        // story points
        System.out.println(issue.getFieldByName(STORY_POINTS_FIELD).getValue());
        // result.setStoryPoints();

        // description
        result.setSummary(issue.getSummary());
        result.setDescription(issue.getDescription());
        result.setPriority(issue.getPriority().getName());
        result.setAssignee(issue.getAssignee().getDisplayName());

        // time
        result.setCreated(fromDateTime(issue.getCreationDate()));
        result.setClosed(fromDateTime(issue.getUpdateDate()));
        result.setDue(fromDateTime(issue.getDueDate()));

        //tracking
        TimeTracking tracking = issue.getTimeTracking();
        TimeTracker tracker = new TimeTracker();
        tracker.setEstimate(tracking.getOriginalEstimateMinutes());
        tracker.setRemaining(tracking.getRemainingEstimateMinutes());
        tracker.setLogged(tracking.getTimeSpentMinutes());
        result.setTimeTracker(tracker);

        return result;
    }

    /***
     * Returns a URI object of the Jira instance.
     */
    private URI getJiraURI() {
        return URI.create(this.jiraUrl);
    }

    /***
     * Converts from Joda DateTime object to Java LocalDateTime object.
     */
    private LocalDateTime fromDateTime(DateTime dt) {
        Instant instant = dt.toGregorianCalendar().toInstant();
        ZoneId zone = dt.getZone().toTimeZone().toZoneId();
        return LocalDateTime.ofInstant(instant, zone);
    }
}
