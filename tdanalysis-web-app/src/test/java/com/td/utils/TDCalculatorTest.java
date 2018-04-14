package com.td.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.td.models.BuildStatus;
import com.td.models.CommitDiff;
import com.td.models.CommitModel;
import com.td.models.CommitTD;
import com.td.models.CommitTD.CodeLocation;
import com.td.models.TDStats;
import com.td.models.TechnicalDebt;
import com.td.models.TechnicalDebtItem.CompositeKey;
import com.td.models.TechnicalDebtPriority;

import org.junit.Test;

public class TDCalculatorTest {

    @Test
    public void testGetPreviousCommit() {
        List<CommitModel> commits = new ArrayList<>();
        commits.add(createCommit("1", BuildStatus.SUCCESSFUL));
        commits.add(createCommit("2", BuildStatus.SUCCESSFUL));

        CommitModel target = createCommit("3", BuildStatus.SUCCESSFUL);
        commits.add(target);

        Optional<CommitModel> opt = TDCalculator.getPreviousCommit(target, commits);

        assertTrue(opt.isPresent());

        CommitModel result = opt.get();
        assertEquals(result.getSha(), "2");
    }

    @Test
    public void testGetPreviousCommitFailed() {
        List<CommitModel> commits = new ArrayList<>();
        commits.add(createCommit("1", BuildStatus.SUCCESSFUL));
        commits.add(createCommit("2", BuildStatus.FAILED));

        CommitModel target = createCommit("3", BuildStatus.SUCCESSFUL);
        commits.add(target);

        Optional<CommitModel> opt = TDCalculator.getPreviousCommit(target, commits);

        assertTrue(opt.isPresent());

        CommitModel result = opt.get();
        assertEquals(result.getSha(), "1");
    }

    @Test
    public void testGetPreviousCommitFirst() {
        List<CommitModel> commits = new ArrayList<>();
        CommitModel target = createCommit("1", BuildStatus.SUCCESSFUL);
        commits.add(target);
        commits.add(createCommit("2", BuildStatus.SUCCESSFUL));
        commits.add(createCommit("3", BuildStatus.FAILED));

        Optional<CommitModel> opt = TDCalculator.getPreviousCommit(target, commits);

        assertFalse(opt.isPresent());
    }

    @Test
    public void testGetPreviousCommitAllFailed() {
        List<CommitModel> commits = new ArrayList<>();
        commits.add(createCommit("2", BuildStatus.FAILED));
        commits.add(createCommit("3", BuildStatus.FAILED));
        commits.add(createCommit("3", BuildStatus.FAILED));
        commits.add(createCommit("3", BuildStatus.FAILED));

        CommitModel target = createCommit("5", BuildStatus.SUCCESSFUL);
        commits.add(target);

        Optional<CommitModel> opt = TDCalculator.getPreviousCommit(target, commits);

        assertFalse(opt.isPresent());
    }

    @Test
    public void getTechnicalDebtForIssueTestLessCommits() {
        List<CommitModel> allCommits = new ArrayList<>();
        allCommits.add(createCommit("1", BuildStatus.FAILED));
        allCommits.add(createCommit("2", BuildStatus.FAILED));
        allCommits.add(createCommit("3", BuildStatus.FAILED));
        allCommits.add(createCommit("4", BuildStatus.FAILED));
        allCommits.add(createCommit("5", BuildStatus.FAILED));
        allCommits.add(createCommit("6", BuildStatus.FAILED));
        allCommits.add(createCommit("7", BuildStatus.FAILED));

        List<CommitModel> issueCommits = new ArrayList<>();

        Optional<TDStats> result = TDCalculator.getTechnicalDebtForIssue(issueCommits, allCommits);

        assertFalse(result.isPresent());
    }

    @Test
    public void getTechnicalDebtForIssueTestOneCommitFailed() {
        List<CommitModel> allCommits = new ArrayList<>();
        allCommits.add(createCommit("1", BuildStatus.FAILED));
        allCommits.add(createCommit("2", BuildStatus.FAILED));
        allCommits.add(createCommit("3", BuildStatus.FAILED));
        allCommits.add(createCommit("4", BuildStatus.FAILED));
        allCommits.add(createCommit("5", BuildStatus.FAILED));
        allCommits.add(createCommit("6", BuildStatus.FAILED));
        allCommits.add(createCommit("7", BuildStatus.FAILED));

        List<CommitModel> issueCommits = new ArrayList<>();
        issueCommits.add(createCommit("7", BuildStatus.FAILED));

        Optional<TDStats> result = TDCalculator.getTechnicalDebtForIssue(issueCommits, allCommits);

        assertFalse(result.isPresent());
    }

    @Test
    public void getTechnicalDebtForIssueTestLastCommitFailed() {
        List<CommitModel> allCommits = new ArrayList<>();
        allCommits.add(createCommit("1", BuildStatus.FAILED));
        allCommits.add(createCommit("2", BuildStatus.FAILED));
        allCommits.add(createCommit("3", BuildStatus.FAILED));
        allCommits.add(createCommit("4", BuildStatus.FAILED));
        allCommits.add(createCommit("5", BuildStatus.FAILED));
        allCommits.add(createCommit("6", BuildStatus.FAILED));
        allCommits.add(createCommit("7", BuildStatus.FAILED));

        List<CommitModel> issueCommits = new ArrayList<>();
        issueCommits.add(createCommit("7", BuildStatus.FAILED));

        Optional<TDStats> result = TDCalculator.getTechnicalDebtForIssue(issueCommits, allCommits);

        assertFalse(result.isPresent());
    }

    @Test
    public void calculateTechnicalDebtIntroducedTest() {
        TechnicalDebt td1 = new TechnicalDebt();
        List<CommitTD> itemsTd1 = new ArrayList<>();
        itemsTd1.add(createCommitTD(new CompositeKey("A", "DB"), TechnicalDebtPriority.HIGH,
                new CodeLocation("ClassA.java", "1")));
        itemsTd1.add(createCommitTD(new CompositeKey("B", "DB"), TechnicalDebtPriority.MEDIUM,
                new CodeLocation("ClassA.java", "2")));
        itemsTd1.add(createCommitTD(new CompositeKey("C", "DB"), TechnicalDebtPriority.HIGH,
                new CodeLocation("ClassA.java", "3")));
        itemsTd1.add(createCommitTD(new CompositeKey("D", "DB"), TechnicalDebtPriority.LOW,
                new CodeLocation("ClassA.java", "4")));

        td1.setTdItems(itemsTd1);

        TechnicalDebt td2 = new TechnicalDebt();
        List<CommitTD> itemsTd2 = new ArrayList<>();
        itemsTd2.add(createCommitTD(new CompositeKey("A", "DB"), TechnicalDebtPriority.HIGH,
                new CodeLocation("ClassA.java", "1")));
        itemsTd2.add(createCommitTD(new CompositeKey("E", "DB"), TechnicalDebtPriority.MEDIUM,
                new CodeLocation("ClassA.java", "2")));
        itemsTd2.add(createCommitTD(new CompositeKey("F", "DB"), TechnicalDebtPriority.HIGH,
                new CodeLocation("ClassA.java", "3")));
        itemsTd2.add(createCommitTD(new CompositeKey("D", "DB"), TechnicalDebtPriority.LOW,
                new CodeLocation("ClassA.java", "4")));

        td2.setTdItems(itemsTd2);

        int expected = 2;
        int actual = TDCalculator.calculateTechnicalDebtIntroduced(td1, td2);

        assertEquals(expected, actual);
    }

    @Test
    public void calculateTechnicalDebtRemovedTest() {
        TechnicalDebt td1 = new TechnicalDebt();
        List<CommitTD> itemsTd1 = new ArrayList<>();
        itemsTd1.add(createCommitTD(new CompositeKey("A", "DB"), TechnicalDebtPriority.HIGH,
                new CodeLocation("ClassA.java", "1")));
        itemsTd1.add(createCommitTD(new CompositeKey("B", "DB"), TechnicalDebtPriority.MEDIUM,
                new CodeLocation("ClassA.java", "2")));
        itemsTd1.add(createCommitTD(new CompositeKey("C", "DB"), TechnicalDebtPriority.HIGH,
                new CodeLocation("ClassA.java", "3")));
        itemsTd1.add(createCommitTD(new CompositeKey("D", "DB"), TechnicalDebtPriority.LOW,
                new CodeLocation("ClassA.java", "4")));

        td1.setTdItems(itemsTd1);

        TechnicalDebt td2 = new TechnicalDebt();
        List<CommitTD> itemsTd2 = new ArrayList<>();
        itemsTd2.add(createCommitTD(new CompositeKey("A", "DB"), TechnicalDebtPriority.HIGH,
                new CodeLocation("ClassA.java", "1")));
        itemsTd2.add(createCommitTD(new CompositeKey("E", "DB"), TechnicalDebtPriority.MEDIUM,
                new CodeLocation("ClassA.java", "2")));
        itemsTd2.add(createCommitTD(new CompositeKey("D", "DB"), TechnicalDebtPriority.LOW,
                new CodeLocation("ClassA.java", "4")));

        td2.setTdItems(itemsTd2);

        int expected = 2;
        int actual = TDCalculator.calculateTechnicalDebtRemoved(td1, td2);

        assertEquals(expected, actual);
    }

    @Test
    public void getFileNameFromPathTest() {
        String path = "src/main/java/org/apache/commons/collections4/bag/CollectionBag.java";
        String expected = "CollectionBag.java";

        String actual = TDCalculator.getFileNameFromPath(path);

        assertEquals(expected, actual);
    }

    @Test
    public void getFileNameFromPathSingleTest() {
        String path = "CollectionBag.java";
        String expected = "CollectionBag.java";

        String actual = TDCalculator.getFileNameFromPath(path);

        assertEquals(expected, actual);
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

    private List<CodeLocation> createLocations(List<String> classes) {
        List<CodeLocation> locations = new ArrayList<>();
        classes.forEach(className -> locations.add(new CodeLocation(className, "1")));
        return locations;
    }

    private CommitDiff createDiff(List<String> additions, List<String> modifications, List<String> deletions) {
        CommitDiff diff = new CommitDiff();
        diff.setAdditionSet(additions);
        diff.setModificationSet(modifications);
        diff.setDeletionSet(deletions);
        return diff;
    }

    private CommitModel createCommitWithTD(List<CodeLocation> locations) {
        CommitModel commit = createCommit("1", BuildStatus.SUCCESSFUL);
        TechnicalDebt debt = new TechnicalDebt();

        List<CommitTD> tds = new ArrayList<>();
        locations.forEach(location -> {
            tds.add(createCommitTD(new CompositeKey("A", "DB"), TechnicalDebtPriority.HIGH, location));
        });

        debt.setTdItems(tds);
        commit.setTechnicalDebt(debt);

        return commit;
    }

    private CommitTD createCommitTD(CompositeKey key, TechnicalDebtPriority priority, CodeLocation location) {
        CommitTD td = new CommitTD();
        td.setId(key);
        td.setPriority(priority);
        td.setLocation(location);
        return td;
    }
}