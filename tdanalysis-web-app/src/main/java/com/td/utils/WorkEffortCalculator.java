package com.td.utils;

import java.text.DecimalFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import com.td.models.CommitModel;
import com.td.models.IssueModel;
import com.td.models.WorkEffort;
import com.td.models.IssueModel.Transition;

public class WorkEffortCalculator {

    private static final int WORK_HOURS_PER_DAY = 8;
    private static final DecimalFormat formatter = new DecimalFormat("#0.00");

    /***
     * Returns the overall work effort spent on a sequence of commits, based on
     * commit timestamps.
     */
    public static WorkEffort getWorkEffortByCommitTimestamp(CommitModel first, CommitModel last) {
        double normalized = WorkEffortCalculator.normalizeWorkEffort(first.getTimestamp(), last.getTimestamp());
        return new WorkEffort(Double.parseDouble(formatter.format(normalized)));
    }

    /***
    * Returns the overall work effort spent on a sequence of commits, based on
    * ticket timestamps.
    */
    public static WorkEffort getWorkEffortByTicketTimestamp(IssueModel issue) {
        LocalDateTime started = getWorkStartedTicket(issue.getTransitions()).orElse(issue.getCreated());
        LocalDateTime ended = issue.getClosed() == null ? issue.getUpdated() : issue.getClosed();
        double normalized = WorkEffortCalculator.normalizeWorkEffort(started, ended);
        return new WorkEffort(Double.parseDouble(formatter.format(normalized)));
    }

    /**
    * Calculates the work effort between two dates, assuming that the normal
    * working day is 8 hours.
    */
    public static double normalizeWorkEffort(LocalDateTime t1, LocalDateTime t2) {
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

    /**
    * Retrieves the time that work has started by looking at the issue
    * transitions. If there is a transition from the state "Open" to "In
    * Progress", then that is the start time. Otherwise, return an empty
    * Optional.
    */
    public static Optional<LocalDateTime> getWorkStartedTicket(List<Transition> transitions) {
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

}