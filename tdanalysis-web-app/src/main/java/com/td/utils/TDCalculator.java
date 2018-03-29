package com.td.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.td.models.BuildStatus;
import com.td.models.CommitModel;
import com.td.models.CommitTD;
import com.td.models.TechnicalDebt;
import com.td.models.TechnicalDebtStats;

public class TDCalculator {

    /**
    * Returns statistics about the technical debt items which the developer had
    * to deal with.
    */
    public static Optional<TechnicalDebtStats> getTechnicalDebtForIssue(List<CommitModel> issueCommits,
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
            TechnicalDebtStats tdStats = new TechnicalDebtStats();
            tdStats.setAdded(last.getTechnicalDebt().getTotalCount());
            return Optional.of(tdStats);
        }

        return TDCalculator.computeTechnicalDebtStats(previous.get(), last);
    }

    /**
     * Computes technical debt statistics between two commits.
     */
    public static Optional<TechnicalDebtStats> computeTechnicalDebtStats(CommitModel c1, CommitModel c2) {

        TechnicalDebt td1 = c1.getTechnicalDebt();
        TechnicalDebt td2 = c2.getTechnicalDebt();

        if (td1 == null || td2 == null) {
            return Optional.empty();
        }

        TechnicalDebtStats tdStats = new TechnicalDebtStats();
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
}