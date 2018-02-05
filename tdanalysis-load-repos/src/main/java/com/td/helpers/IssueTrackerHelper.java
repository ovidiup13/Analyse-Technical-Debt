package com.td.helpers;

import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.api.domain.Project;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;

public class IssueTrackerHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(IssueTrackerHelper.class);

    private String username;
    private String password;
    private String jiraUrl;

    private JiraRestClient jiraRestClient;

    public IssueTrackerHelper() {
    }

    public IssueTrackerHelper(String username, String password, String jiraUrl) {
        this.username = username;
        this.password = password;
        this.jiraUrl = jiraUrl;
        this.jiraRestClient = initialise();
    }

    private JiraRestClient initialise() {
        return new AsynchronousJiraRestClientFactory()
                .createWithBasicHttpAuthentication(getJiraURI(), this.username, this.password);
    }

    private URI getJiraURI() {
        return URI.create(this.jiraUrl);
    }

    public Project getProject(String projectKey) {
        return jiraRestClient.getProjectClient().getProject(projectKey).claim();
    }


    public Issue getIssue(String issueKey) {
        return jiraRestClient
                .getIssueClient()
                .getIssue(issueKey)
                .claim();
    }
}
