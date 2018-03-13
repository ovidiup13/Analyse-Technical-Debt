package com.td.facades;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.td.models.CommitModel;
import com.td.models.Stats;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class StatsFacade {

    @Autowired
    private RepositoryFacade repositoryFacade;

    /***
     * Returns a map of statistics of the calculated work effort and technical debt.
     */
    public List<Stats> getSimpleStats(String repositoryId) {
        List<Map<String, List<CommitModel>>> commits = repositoryFacade.getIssuesAndCommitsFiltered(repositoryId);
        List<Stats> result = new ArrayList<>(commits.size());
        commits.forEach(item -> {
            Entry<String, List<CommitModel>> entry = item.entrySet().iterator().next();
            String issueKey = entry.getKey();
            List<CommitModel> issueCommits = entry.getValue();

            // generate simple stats
            Stats stats = new Stats();
            stats.setIssueKey(issueKey);
            stats.setTechnicalDebt(getSimpleTechnicalDebt(issueCommits));
            stats.setWorkEffort(getSimpleWorkEffort(issueCommits));
            stats.setTotalCommits(issueCommits.size());
            stats.setAuthor(issueCommits.get(0).getAuthor());
            result.add(stats);
        });
        return result;
    }

    /***
     * Returns the overall work effort spent on a sequence of commits, based on commit timestamps.
     * It is a crude way to calculate work effort, there are better approaches.
     */
    private double getSimpleWorkEffort(List<CommitModel> commits) {
        int numberOfCommits = commits.size();

        // one commit does not mean work effort is 0!
        // keep it simple, for now
        if (numberOfCommits < 2) {
            return 0L;
        }

        CommitModel first = commits.get(0);
        CommitModel last = commits.get(numberOfCommits - 1);
        return Duration.between(first.getTimestamp(), last.getTimestamp()).toMinutes() / 60.0;
    }

    /***
     * Returns a count of the unique issues available in the list of commits. 
     */
    private long getSimpleTechnicalDebt(List<CommitModel> commits) {
        return commits.stream().map(commit -> commit.getBugs()).flatMap(bugs -> bugs.stream()).distinct().count();
    }

}