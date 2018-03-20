package com.td;

import java.net.URI;

import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.domain.ChangelogGroup;
import com.atlassian.jira.rest.client.api.domain.ChangelogItem;
import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;

public class Test {

    public static void main(String[] args) {

        URI jiraUri = URI.create("https://issues.apache.org/jira");
        String username = "tdanalysis";
        String password = "pX6G7NlgyU6a";

        JiraRestClient client = new AsynchronousJiraRestClientFactory().createWithBasicHttpAuthentication(jiraUri,
                username, password);

        String issueKey = "ZOOKEEPER-2933";
        Issue issue = client.getIssueClient().getIssue(issueKey).claim();

        try {
            Iterable<ChangelogGroup> changeLog = issue.getChangelog();
            System.out.println(changeLog);
            for (ChangelogGroup changeGroup : changeLog) {
                Iterable<ChangelogItem> changeItems = changeGroup.getItems();
                for (ChangelogItem changeItem : changeItems) {
                    System.out.println(changeItem.toString());
                }
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        URI transitions = issue.getTransitionsUri();
        assert transitions != null;
        System.out.println(transitions.toString());
    }

}