package com.td.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import com.td.models.BuildStatus;
import com.td.models.CommitDiff;
import com.td.models.CommitModel;

import org.junit.Test;

public class ChangeSetCalculatorTest {

    @Test
    public void getTotalChangesTest() {
        List<CommitModel> allCommits = new ArrayList<>();
        List<String> diffs = Arrays.asList("ClassA.java", "ClassB.java");
        CommitDiff diff1 = createDiff(diffs, diffs, diffs);
        CommitModel commit = createCommit("", BuildStatus.SUCCESSFUL);
        commit.setDiff(diff1);
        allCommits.add(commit);

        Set<String> changes = ChangeSetCalculator.getTotalChanges(allCommits);

        assertFalse(changes.isEmpty());
        assertEquals(2, changes.size());
        assertTrue(changes.contains("ClassA.java"));
        assertTrue(changes.contains("ClassB.java"));
    }

    @Test
    public void getTotalChangesNullTest() {
        List<CommitModel> allCommits = new ArrayList<>();
        List<String> diffs = Arrays.asList("ClassA.java", "ClassB.java");
        CommitDiff diff1 = createDiff(diffs, null, null);
        CommitModel commit = createCommit("", BuildStatus.SUCCESSFUL);
        commit.setDiff(diff1);
        allCommits.add(commit);

        Set<String> changes = ChangeSetCalculator.getTotalChanges(allCommits);

        assertFalse(changes.isEmpty());
        assertEquals(2, changes.size());
        assertTrue(changes.contains("ClassA.java"));
        assertTrue(changes.contains("ClassB.java"));
    }

    @Test
    public void getTotalChangesDuplicateTest() {
        List<CommitModel> allCommits = new ArrayList<>();

        List<String> diffs1 = Arrays.asList("ClassA.java", "ClassA.java", "ClassC.java");
        List<String> diffs2 = Arrays.asList("ClassA.java", "ClassA.java", "ClassC.java");
        CommitDiff diff1 = createDiff(diffs1, diffs1, diffs1);
        CommitDiff diff2 = createDiff(diffs2, diffs2, diffs2);
        CommitModel commit1 = createCommit("", BuildStatus.SUCCESSFUL);
        CommitModel commit2 = createCommit("", BuildStatus.SUCCESSFUL);
        commit1.setDiff(diff1);
        commit2.setDiff(diff2);
        allCommits.add(commit1);
        allCommits.add(commit2);

        Set<String> changes = ChangeSetCalculator.getTotalChanges(allCommits);

        assertFalse(changes.isEmpty());
        assertEquals(2, changes.size());
        assertTrue(changes.contains("ClassA.java"));
        assertTrue(changes.contains("ClassC.java"));
    }

    private CommitModel createCommit(String sha, BuildStatus status) {
        CommitModel commit = new CommitModel();
        commit.setSha(sha);
        commit.setBuildStatus(status);
        commit.setRepositoryId("1"); //does not matter
        CommitDiff diff = createDiff(Arrays.asList("ClassA.java"), Arrays.asList("ClassC.java"),
                Arrays.asList("ClassB.java"));
        commit.setDiff(diff);
        return commit;
    }

    private CommitDiff createDiff(List<String> additions, List<String> modifications, List<String> deletions) {
        CommitDiff diff = new CommitDiff();
        diff.setAdditionSet(additions);
        diff.setModificationSet(modifications);
        diff.setDeletionSet(deletions);
        return diff;
    }

}