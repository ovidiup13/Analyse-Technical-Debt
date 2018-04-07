package com.td.facades;

import java.text.DecimalFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.td.models.BuildStatus;
import com.td.models.CommitModel;
import com.td.models.CommitStats;
import com.td.models.IssueModel;
import com.td.models.IssueModel.Transition;
import com.td.models.IssueStats;
import com.td.models.TDStats;
import com.td.models.WorkEffort;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class StatsFacade {

    private static final int WORK_HOURS_PER_DAY = 8;
    private static final DecimalFormat formatter = new DecimalFormat("#0.00");

    @Autowired
    private RepositoryFacade repositoryFacade;

    @Autowired
    private TDFacade tdFacade;

    /***
     * Returns list of issue stats by calculating work effort using commit
     * timestamps.
     */
    public List<IssueStats> getIssueStatsByCommitTimestamp(String repositoryId) {
        List<Map<String, List<CommitModel>>> commits = repositoryFacade.getIssuesAndCommitsFiltered(repositoryId);
        List<IssueStats> result = new ArrayList<>(commits.size());

        commits.forEach(item -> {
            Entry<String, List<CommitModel>> entry = item.entrySet().iterator().next();
            String issueKey = entry.getKey();
            List<CommitModel> issueCommits = entry.getValue();

            // generate simple stats
            IssueStats stats = new IssueStats();
            stats.setIssueKey(issueKey);
            // stats.setTechnicalDebt(getTechnicalDebtCount(issueCommits));
            stats.setTotalCommits(issueCommits.size());
            stats.setAuthor(issueCommits.get(0).getAuthor());
            stats.setWorkEffort(getWorkEffortByCommitTimestamp(issueCommits));

            Optional<TDStats> opt = tdFacade.getTechnicalDebtForIssue(repositoryId, issueCommits);
            stats.setTdStats(opt.orElse(null));

            result.add(stats);
        });
        return result;
    }

    /***
    * Returns a list of issue stats by calculating work effort using ticket
    * timestamps.
    */
    public List<IssueStats> getIssueStatsByIssueTimestamp(String repositoryId) {
        List<Map<String, List<CommitModel>>> commits = repositoryFacade.getIssuesAndCommitsFiltered(repositoryId);
        List<IssueStats> result = new ArrayList<>(commits.size());

        commits.forEach(item -> {
            Entry<String, List<CommitModel>> entry = item.entrySet().iterator().next();
            String issueKey = entry.getKey();

            // get issue model
            Optional<IssueModel> issueOpt = repositoryFacade.getIssue(repositoryId, issueKey);
            if (!issueOpt.isPresent()) {
                return;
            }
            IssueModel issue = issueOpt.get();
            if (issue.getStatus().equalsIgnoreCase("Open")) {
                return;
            }

            List<CommitModel> issueCommits = entry.getValue();

            // generate simple stats
            IssueStats stats = new IssueStats();
            stats.setIssueKey(issueKey);
            // stats.setTechnicalDebt(getTechnicalDebtCount(issueCommits));
            stats.setTotalCommits(issueCommits.size());
            stats.setAuthor(issueCommits.get(0).getAuthor());
            stats.setWorkEffort(getWorkEffortByTicketTimestamp(issue));

            Optional<TDStats> opt = tdFacade.getTechnicalDebtForIssue(repositoryId, issueCommits);
            stats.setTdStats(opt.orElse(null));

            result.add(stats);
        });
        return result;
    }

    /**
     * Returns a list of total number of commits for all issues.
     */
    public List<IssueStats> getIssueStatsRaw(String repositoryId) {
        List<Map<String, List<CommitModel>>> issues = repositoryFacade.getIssuesAndCommitsRaw(repositoryId);
        List<IssueStats> result = new ArrayList<>(issues.size());
        issues.forEach(item -> {
            Entry<String, List<CommitModel>> entry = item.entrySet().iterator().next();
            String issueKey = entry.getKey();
            List<CommitModel> issueCommits = entry.getValue();

            IssueStats stats = new IssueStats();
            stats.setTotalCommits(issueCommits.size());
            // stats.setTechnicalDebt(getTechnicalDebtCount(issueCommits));
            stats.setIssueKey(issueKey);
            result.add(stats);
        });

        return result;
    }

    /***
     * Returns statistics for commits in the repository.
     * @param id repository ID 
     */
    public CommitStats getCommitStats(String id) {
        CommitStats result = new CommitStats();

        List<CommitModel> commits = this.repositoryFacade.getAllCommits(id).stream()
                .filter(commit -> commit.getBuildStatus().equals(BuildStatus.SUCCESSFUL)).collect(Collectors.toList());

        int totalCommits = commits.size();
        int withIssues = (int) commits.stream().filter(commit -> commit.getIssueIds().size() > 0).count();
        int withoutIssues = totalCommits - withIssues;
        int numberOfAuthors = repositoryFacade.getCollaborators(id).size();

        double meanTicketsPerCommit = commits.stream().map(commit -> commit.getIssueIds().size()).reduce(0,
                (prev, cur) -> prev + cur) / ((double) totalCommits);
        double meanTDItemsPerCommit = commits.stream().map(commit -> commit.getTechnicalDebt().getTotalCount())
                .reduce(0, (prev, cur) -> prev + cur) / ((double) totalCommits);

        result.setTotalCommits(totalCommits);
        result.setCommitsWithIssues(withIssues);
        result.setCommitsWithoutIssues(withoutIssues);
        result.setNumberOfAuthors(numberOfAuthors);
        result.setMeanTicketsPerCommit(meanTicketsPerCommit);
        result.setMeanTDItemsPerCommit(meanTDItemsPerCommit);

        return result;
    }

    /***
     * Returns the overall work effort spent on a sequence of commits, based on
     * commit timestamps.
     */
    private WorkEffort getWorkEffortByCommitTimestamp(List<CommitModel> commits) {
        int numberOfCommits = commits.size();
        CommitModel firstCommit = commits.get(0);
        CommitModel lastCommit = commits.get(numberOfCommits - 1);

        // get last commit before ticket start
        Optional<CommitModel> previousOpt = getPreviousCommitByAuthor(firstCommit);

        // if "previous" commit does not exist, use first commit
        CommitModel previousCommit = previousOpt.isPresent() ? previousOpt.get() : firstCommit;

        double normalized = normalizeWorkEffort(lastCommit.getTimestamp(), previousCommit.getTimestamp());
        return new WorkEffort(Double.parseDouble(formatter.format(normalized)));
    }

    /***
    * Returns the overall work effort spent on a sequence of commits, based on
    * ticket timestamps.
    */
    private WorkEffort getWorkEffortByTicketTimestamp(IssueModel issue) {
        LocalDateTime started = getWorkStarted(issue.getTransitions()).orElse(issue.getCreated());
        LocalDateTime ended = issue.getClosed() == null ? issue.getUpdated() : issue.getClosed();
        double normalized = normalizeWorkEffort(started, ended);
        return new WorkEffort(Double.parseDouble(formatter.format(normalized)));
    }

    /**
     * Retrieves the time that work has started by looking at the issue
     * transitions. If there is a transition from the state "Open" to "In
     * Progress", then that is the start time. Otherwise, return an empty
     * Optional.
     */
    private Optional<LocalDateTime> getWorkStarted(List<Transition> transitions) {
        if (transitions == null) {
            return Optional.empty();
        }

        Predicate<Transition> condition = (transition) -> transition.getFrom().equals("Open")
                && transition.getFrom().equals("In Progress");
        long count = transitions.stream().filter(condition).count();

        if (count <= 0) {
            return Optional.empty();
        }

        Transition t = transitions.stream().filter(condition).findFirst().get();
        return Optional.of(t.getCreated());
    }

    /**
     * Returns the previous commit by the same author.
     */
    private Optional<CommitModel> getPreviousCommitByAuthor(CommitModel commit) {
        String author = commit.getAuthor();
        String repoId = commit.getRepositoryId();

        // sorted by timestamp
        List<CommitModel> allCommits = repositoryFacade.getAllCommitsByAuthor(repoId, author);

        // binary search for current commit
        int index = allCommits.indexOf(commit);

        // return previous commit
        return index < 1 ? Optional.empty() : Optional.of(allCommits.get(index - 1));
    }

    /**
     * Calculates the work effort between two dates, assuming that the normal
     * working day is 8 hours.
     */
    private double normalizeWorkEffort(LocalDateTime t1, LocalDateTime t2) {
        Duration duration = Duration.between(t1, t2);
        long days = Math.abs(duration.toDays());

        // case same day
        if (days < 1) {
            double hours = Math.abs(duration.toMinutes()) / 60.0;
            return hours < WORK_HOURS_PER_DAY ? hours : WORK_HOURS_PER_DAY;
        }

        // case any other day
        return (days + 1) * WORK_HOURS_PER_DAY;
    }

    /***
    * Returns a count of the unique issues available in the list of commits.
    * @deprecated Using TechnicalDebt model instead of bugs.
    */
    private long getTechnicalDebtCount(List<CommitModel> commits) {
        return commits.stream().map(commit -> commit.getBugs()).flatMap(bugs -> bugs.stream()).distinct().count();
    }
}