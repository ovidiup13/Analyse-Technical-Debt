package com.td.helpers.tracker;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.RestClientException;
import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.api.domain.TimeTracking;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;
import com.td.models.IssueModel;
import com.td.models.IssueModel.TimeTracker;
import com.td.models.IssueModel.Transition;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JiraTrackerHelper extends IssueTrackerHelper {

    private static final Logger logger = LoggerFactory.getLogger(JiraTrackerHelper.class);

    // private static final String STORY_POINTS_FIELD = "Story Points";
    private static final String PATTERN = "[A-Z]+-[0-9]+";
    private static final String EXPAND_CHANGELOG = "?expand=changelog";
    private static final String DATE_TIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";

    DateTimeFormatter df = DateTimeFormatter.ofPattern(DATE_TIME_PATTERN);

    private URI uri;
    private JiraRestClient jiraRestClient;

    public JiraTrackerHelper(URI uri, String username, String password) {
        this.uri = uri;
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
        DateTime created = issue.getCreationDate();
        DateTime due = issue.getDueDate();
        result.setCreated(LocalDateTime.parse(created.toString(DATE_TIME_PATTERN), df));
        // result.(LocalDateTime.parse(created.toString(DATE_TIME_PATTERN), df));
        result.setDue(LocalDateTime.parse(due.toString(DATE_TIME_PATTERN), df));
        // result.setClosed(fromDateTime(issue.getUpdateDate()));
        // result.setDue(fromDateTime(issue.getDueDate()));

        //tracking
        TimeTracking tracking = issue.getTimeTracking();
        TimeTracker tracker = new IssueModel.TimeTracker();
        tracker.setEstimate(tracking.getOriginalEstimateMinutes() != null ? tracking.getOriginalEstimateMinutes() : 0);
        tracker.setRemaining(
                tracking.getRemainingEstimateMinutes() != null ? tracking.getRemainingEstimateMinutes() : 0);
        tracker.setLogged(tracking.getTimeSpentMinutes() != null ? tracking.getTimeSpentMinutes() : 0);
        result.setTimeTracker(tracker);

        // transitions
        result.setTransitions(getTransitions(issueKey));

        return result;
    }

    /**
     * Method that sends a request to the Jira URI and retrieves a list of issue
     * transitions.
     */
    List<Transition> getTransitions(String issueKey) {
        String uri = this.uri.toString() + "/rest/api/latest/issue/" + issueKey + EXPAND_CHANGELOG;
        try (CloseableHttpClient httpClient = HttpClientBuilder.create().build()) {

            // http get request and body
            HttpGet request = new HttpGet(uri);
            request.addHeader("content-type", "application/json");
            HttpResponse response = httpClient.execute(request);
            String body = convertStreamToString(response.getEntity().getContent());
            JSONObject json = new JSONObject(body);

            return parseResponseBody(json);
        } catch (IOException e) {
            logger.error("An error occurred when retrieving changelog.");
            e.printStackTrace();
            return new ArrayList<>();
        } catch (JSONException e) {
            logger.error("An error occurred when parsing response body as JSON.");
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * Method that parses the reponse body of the changelog and retrieves a list
     * of transitions that the issue has been through.
     */
    List<Transition> parseResponseBody(JSONObject json) {
        List<Transition> transitions = new ArrayList<>();

        try {
            JSONObject changelog = json.getJSONObject("changelog");
            JSONArray histories = changelog.getJSONArray("histories");
            for (int i = 0; i < histories.length(); i++) {

                JSONObject history = histories.getJSONObject(i);
                String s = history.getString("created");
                String author = history.getJSONObject("author").getString("displayName");
                LocalDateTime created = LocalDateTime.parse(s, df);
                JSONArray items = history.getJSONArray("items");
                for (int j = 0; j < items.length(); j++) {
                    JSONObject item = items.getJSONObject(j);
                    String field = item.getString("field");
                    String fromString = item.getString("fromString");
                    String toString = item.getString("toString");

                    // only interested in status transitions
                    if (field.equals("status")) {
                        Transition transition = new Transition();
                        transition.setCreated(created);
                        transition.setField("status");
                        transition.setFrom(fromString);
                        transition.setTo(toString);
                        transition.setAuthor(author);

                        transitions.add(transition);
                    }
                }
            }
        } catch (JSONException e) {
            logger.error("An error occurred when parsing response body");
            e.printStackTrace();
        }

        return transitions;
    }

    /**
     * Utility method that turns a stream into a string.
     */
    private static String convertStreamToString(InputStream is) {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return sb.toString();
    }
}
