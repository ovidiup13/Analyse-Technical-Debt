package com.td.utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.td.models.BuildStatus;
import com.td.models.CommitModel;
import com.td.models.CommitTD;
import com.td.models.IssueStats.TDStats;
import com.td.models.TechnicalDebt;
import com.td.models.TechnicalDebtPriority;

public class TDCalculator {

    /**
    * Returns statistics about the technical debt items which the developer had
    * to deal with.
    */
    public static Optional<TDStats> getTechnicalDebtForIssue(List<CommitModel> issueCommits,
            List<CommitModel> allCommits) {

        if (issueCommits.size() < 1) {
            return Optional.empty();
        }

        CommitModel first = issueCommits.get(0);
        CommitModel last = issueCommits.get(issueCommits.size() - 1);

        // if the last build has failed, find an earlier commit that is successful
        if (last.getBuildStatus().equals(BuildStatus.FAILED)) {
            Optional<CommitModel> optLast = TDCalculator.getPreviousCommit(last, issueCommits);

            if (first.equals(last)) {
                return Optional.empty();
            }

            if (!optLast.isPresent()) {
                return Optional.empty();
            }

            last = optLast.get();
        }

        // get the commit before the first
        Optional<CommitModel> previous = TDCalculator.getPreviousCommit(first, allCommits);

        // if previous does not exist, added is the number of TD items in the last commit.
        if (!previous.isPresent()) {
            TDStats tdStats = new TDStats();
            TechnicalDebt lastTd = last.getTechnicalDebt();
            tdStats.setAdded(lastTd.getTotalCount());
            tdStats.setHigh(lastTd.getHighCount());
            tdStats.setMedium(lastTd.getMediumCount());
            tdStats.setLow(lastTd.getLowCount());
            return Optional.of(tdStats);
        }

        return TDCalculator.computeTechnicalDebtStats(previous.get(), last);
    }

    /**
     * Computes technical debt statistics between two commits.
     */
    public static Optional<TDStats> computeTechnicalDebtStats(CommitModel c1, CommitModel c2) {

        TechnicalDebt td1 = c1.getTechnicalDebt();
        TechnicalDebt td2 = c2.getTechnicalDebt();

        if (td1 == null || td2 == null) {
            return Optional.empty();
        }

        TDStats tdStats = new TDStats();
        TechnicalDebt lastTd = c2.getTechnicalDebt();
        tdStats.setHigh(lastTd.getHighCount());
        tdStats.setMedium(lastTd.getMediumCount());
        tdStats.setLow(lastTd.getLowCount());
        tdStats.setTotalPain(c1.getTechnicalDebt().getTotalCount());
        tdStats.setAdded(calculateTechnicalDebtIntroduced(td1, td2));
        tdStats.setRemoved(calculateTechnicalDebtRemoved(td1, td2));

        return Optional.of(tdStats);
    }

    /**
     * This method returns the previous commit within an ordered list of
     * commits. If technical debt for the previous commit is null, we return the
     * second previous one, and so on.
     */
    public static Optional<CommitModel> getPreviousCommit(CommitModel commit, List<CommitModel> commits) {

        int index = commits.indexOf(commit);

        // if it is the first commit, there is no "previous"
        if (index < 1) {
            return Optional.empty();
        }

        // find the previous successful commit
        CommitModel target = commits.get(--index);
        while (target.getBuildStatus().equals(BuildStatus.FAILED) && index > 0) {
            target = commits.get(--index);
        }

        if (index <= 0 && target.getBuildStatus().equals(BuildStatus.FAILED)) {
            return Optional.empty();
        }

        return Optional.of(target);
    }

    /**
     * Calculates the technical debt introduced.
     */
    static int calculateTechnicalDebtIntroduced(TechnicalDebt td1, TechnicalDebt td2) {
        List<CommitTD> td1Items = td1.getTdItems();
        List<CommitTD> td2Items = new ArrayList<>(td2.getTdItems());

        td2Items.removeAll(td1Items);

        return td2Items.size();
    }

    /**
    * Calculates the technical debt removed.
    */
    static int calculateTechnicalDebtRemoved(TechnicalDebt td1, TechnicalDebt td2) {
        List<CommitTD> td1Items = new ArrayList<>(td1.getTdItems());
        List<CommitTD> td2Items = td2.getTdItems();

        td1Items.removeAll(td2Items);

        return td1Items.size();
    }

    /**
     * Calculates the technical debt relevant for the changeset within a list of commits.
     */
    static TechnicalDebt getTechnicalDebtForCommits(List<CommitModel> commits) {

        // get changes
        Set<String> changes = getTotalChanges(commits);

        // get all distinct TD items within list
        Stream<CommitTD> tdStream = getDistinctCommitTDs(commits);

        // filter technical debt items by changes
        List<CommitTD> commitTDs = tdStream.filter(commitTD -> changes.contains(commitTD.getLocation().getFileName()))
                .collect(Collectors.toList());

        // return technical debt object
        TechnicalDebt debt = new TechnicalDebt();
        debt.setTdItems(commitTDs);
        debt.setHighCount(
                (int) commitTDs.stream().filter(td -> td.getPriority().equals(TechnicalDebtPriority.HIGH)).count());
        debt.setMediumCount(
                (int) commitTDs.stream().filter(td -> td.getPriority().equals(TechnicalDebtPriority.MEDIUM)).count());
        debt.setLowCount(
                (int) commitTDs.stream().filter(td -> td.getPriority().equals(TechnicalDebtPriority.LOW)).count());

        return debt;
    }

    /**
     * Returns all the distinct file names that have suffered changes over a period of commits.  
     */
    static Set<String> getTotalChanges(List<CommitModel> commits) {
        Stream<String> totalAdditions = commits.stream().map(commit -> commit.getDiff())
                .map(diff -> diff.getAdditionSet()).filter(item -> item != null).flatMap(List::stream);
        Stream<String> totalDeletions = commits.stream().map(commit -> commit.getDiff())
                .map(diff -> diff.getDeletionSet()).filter(item -> item != null).flatMap(List::stream);
        Stream<String> totalModifications = commits.stream().map(commit -> commit.getDiff())
                .map(diff -> diff.getModificationSet()).filter(item -> item != null).flatMap(List::stream);

        Stream<String> totalChanges = Stream.concat(Stream.concat(totalAdditions, totalModifications), totalDeletions)
                .sorted().distinct();

        return totalChanges.filter(change -> {
            return change != null;
        }).map(change -> {
            return TDCalculator.getFileNameFromPath(change);
        }).peek(change -> System.out.println(change)).collect(Collectors.toCollection(HashSet::new));
    }

    static String getFileNameFromPath(String path) {
        String[] pathElements = path.split("/");
        return pathElements[pathElements.length - 1];
    }

    /**
     * Returns all distinct technical debt items that have been found in a list of commits.
     */
    static Stream<CommitTD> getDistinctCommitTDs(List<CommitModel> commits) {
        return commits.stream().filter(commit -> commit.getTechnicalDebt() != null)
                .map(commit -> commit.getTechnicalDebt().getTdItems()).flatMap(List::stream).sorted().distinct();
    }
}