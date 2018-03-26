package com.td.helpers.tracker;

import java.net.URI;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;
import java.util.regex.Pattern;

import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.RestClientException;
import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.api.domain.TimeTracking;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;
import com.td.models.IssueModel;
import com.td.models.IssueModel.TimeTracker;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JiraTrackerHelper extends IssueTrackerHelper {

    private static final Logger logger = LoggerFactory.getLogger(JiraTrackerHelper.class);

    // private static final String STORY_POINTS_FIELD = "Story Points";
    private static final String PATTERN = "[A-Z]+-[0-9]+";

    private JiraRestClient jiraRestClient;

    public JiraTrackerHelper(URI uri, String username, String password) {
        this.issuePattern = Pattern.compile(PATTERN);
        initialise(uri, username, password);
    }

    /***
     * Retrieves the issue details associated with the issue key.
     */
    public Optional<IssueModel> getIssue(String issueKey) {
        try {
            Issue issue = jiraRestClient.getIssueClient().getIssue(issueKey).claim();
            return Optional.of(jiraIssueToIssueModel(issue));
        } catch (RestClientException e) {
            logger.error("Could not retrieve issue " + issueKey);
            return Optional.empty();
        }
    }

    AsynchronousJiraRestClientFactory getJiraFactory() {
        return new AsynchronousJiraRestClientFactory();
    }

    void initialise(URI uri, String username, String password) {
        this.jiraRestClient = getJiraFactory().createWithBasicHttpAuthentication(uri, username, password);
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

        // String repositoryName = repository.getName();
        // result.setRepositoryId(this.repository.getId());
        // result.setIssueId(repositoryName + "/" + issueKey);

        String issueKey = issue.getKey();

        // meta
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

        return result;
    }
}
