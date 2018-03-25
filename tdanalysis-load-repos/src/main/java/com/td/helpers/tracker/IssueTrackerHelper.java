package com.td.helpers.tracker;

import com.td.models.IssueModel;
import java.util.List;
import java.util.Optional;

public interface IssueTrackerHelper {

    Optional<IssueModel> getIssue(String issueId);

    List<String> getKeys(String description);
}