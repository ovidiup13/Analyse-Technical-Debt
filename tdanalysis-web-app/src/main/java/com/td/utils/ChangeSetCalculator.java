package com.td.utils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.td.models.ChangeSetStats;
import com.td.models.CommitModel;

public class ChangeSetCalculator {

    /**
    * Returns all the distinct file names that have suffered changes over a period of commits.  
    */
    public static Set<String> getTotalChanges(List<CommitModel> commits) {
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
        }).collect(Collectors.toCollection(HashSet::new));
    }

    /**
     * Aggregates all changesets within a list of commits.
     */
    public static ChangeSetStats getChangeSetStats(List<CommitModel> commits) {
        ChangeSetStats stats = new ChangeSetStats();

        int additions = commits.stream().map(commit -> commit.getDiff().getAdditions()).reduce(0, (a, b) -> a + b);
        int deletions = commits.stream().map(commit -> commit.getDiff().getDeletions()).reduce(0, (a, b) -> a + b);
        int modifications = commits.stream().map(commit -> commit.getDiff().getModifications()).reduce(0,
                (a, b) -> a + b);
        int total = commits.stream().map(commit -> commit.getDiff().getTotalChanges()).reduce(0, (a, b) -> a + b);

        stats.setAdditions(additions);
        stats.setDeletions(deletions);
        stats.setModifications(modifications);
        stats.setTotalChanges(total);

        return stats;
    }

}