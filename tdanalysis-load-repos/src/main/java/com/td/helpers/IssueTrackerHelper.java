package com.td.helpers;

import com.td.models.IssueModel;
import java.util.List;

public interface IssueTrackerHelper {
    IssueModel getIssue(String issueId);

    List<String> getKeys(String description);
}