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
import java.util.stream.Stream;

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
import org.springframework.cache.annotation.Cacheable;

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
    @Cacheable("issues")
    public Optional<IssueModel> getIssue(String issueKey) {
        try {
            Issue issue = jiraRestClient.getIssueClient().getIssue(issueKey).claim();
            return Optional.of(jiraIssueToIssueModel(issue));
        } catch (RestClientException e) {
            logger.error("Could not retrieve issue " + issueKey);
            e.printStackTrace();
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

        // transitions
        List<Transition> transitions = getTransitions(issueKey);
        result.setTransitions(transitions);

        // time
        DateTime created = issue.getCreationDate();
        if (created != null) {
            result.setCreated(LocalDateTime.parse(created.toString(DATE_TIME_PATTERN), df));
        }

        DateTime updated = issue.getUpdateDate();
        if (updated != null) {
            result.setUpdated(LocalDateTime.parse(updated.toString(DATE_TIME_PATTERN), df));
        }

        DateTime due = issue.getDueDate();
        if (due != null) {
            result.setDue(LocalDateTime.parse(due.toString(DATE_TIME_PATTERN), df));
        }

        // closed date is given by transitions
        if (!result.getStatus().equals("Open") && !result.getStatus().equals("In Progress")) {
            System.out.println(result.getStatus());
            result.setClosed(getClosedDate(transitions));
        }

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

    /**
     * Returns the date and time that the ticket was closed based on the ticket
     * transitions. The last transition time which had its status set to
     * "Resolved" or "Closed" is regarded as the closed time. 
     */
    private LocalDateTime getClosedDate(List<Transition> transitions) {

        // filter and count
        Stream<Transition> stream = filterTransitionByClosed(transitions.stream());
        long count = stream.count();

        if (count < 1) {
            return null;
        }

        // get last "closed element" element
        // need to create and filter the stream again since count() is a terminal operation
        stream = filterTransitionByClosed(transitions.stream());
        Transition last = stream.skip(count - 1).findFirst().get();

        return last.getCreated();
    }

    private Stream<Transition> filterTransitionByClosed(Stream<Transition> transitions) {
        return transitions
                .filter(transition -> transition.getTo().equals("Resolved") || transition.getTo().equals("Closed"));
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

                    // only interested in status transitions                    
                    if (!field.equals("status")) {
                        break;
                    }

                    String fromString = item.getString("fromString");
                    String toString = item.getString("toString");

                    Transition transition = new Transition();
                    transition.setCreated(created);
                    transition.setField("status");
                    transition.setFrom(fromString);
                    transition.setTo(toString);
                    transition.setAuthor(author);

                    transitions.add(transition);
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
