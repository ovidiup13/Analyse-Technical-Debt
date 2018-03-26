package com.td.helpers.tracker;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.td.models.IssueModel;

public abstract class IssueTrackerHelper {

    protected Pattern issuePattern;

    abstract Optional<IssueModel> getIssue(String issueId);

    /**
     * Returns all the issue IDs from the commit description.
     * @param description - commit text
     */
    public List<String> getKeys(String description) {
        List<String> keys = new ArrayList<>();

        Matcher matcher = issuePattern.matcher(description);
        while (matcher.find()) {
            keys.add(matcher.group());
        }

        return keys;
    }

}