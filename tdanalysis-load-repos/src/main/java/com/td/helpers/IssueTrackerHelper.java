package com.td.helpers;

import com.td.models.IssueModel;

public interface IssueTrackerHelper {
    IssueModel getIssue(String issueId);
}