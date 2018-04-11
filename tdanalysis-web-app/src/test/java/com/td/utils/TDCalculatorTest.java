package com.td.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.td.models.BuildStatus;
import com.td.models.CommitDiff;
import com.td.models.CommitModel;
import com.td.models.CommitTD;
import com.td.models.CommitTD.CodeLocation;
import com.td.models.IssueStats.TDStats;
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

    private CommitModel createCommit(String sha, BuildStatus status) {
        CommitModel commit = new CommitModel();
        commit.setSha(sha);
        commit.setBuildStatus(status);
        commit.setRepositoryId("1"); //does not matter
        return commit;
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
    public void getTechnicalDebtForIssueTestPreviousAllFailed() {
        List<CommitModel> allCommits = new ArrayList<>();
        allCommits.add(createCommit("1", BuildStatus.FAILED));
        allCommits.add(createCommit("2", BuildStatus.FAILED));
        allCommits.add(createCommit("3", BuildStatus.FAILED));
        allCommits.add(createCommit("4", BuildStatus.FAILED));
        allCommits.add(createCommit("5", BuildStatus.FAILED));
        allCommits.add(createCommit("6", BuildStatus.SUCCESSFUL));
        allCommits.add(createCommit("7", BuildStatus.SUCCESSFUL));

        List<CommitModel> issueCommits = new ArrayList<>();
        issueCommits.add(createCommit("6", BuildStatus.SUCCESSFUL));

        // last commit
        CommitModel last = createCommit("7", BuildStatus.SUCCESSFUL);
        TechnicalDebt td = new TechnicalDebt();
        int totalExpected = 100;
        td.setTotalCount(totalExpected);
        last.setTechnicalDebt(td);
        issueCommits.add(last);

        Optional<TDStats> result = TDCalculator.getTechnicalDebtForIssue(issueCommits, allCommits);

        assertTrue(result.isPresent());
        TDStats actual = result.get();
        assertEquals(totalExpected, actual.getAdded());
    }

    @Test
    public void computeTechnicalDebtStatsTestNull() {

        Optional<TDStats> stats = TDCalculator.computeTechnicalDebtStats(new CommitModel(), new CommitModel());

        assertFalse(stats.isPresent());
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

    @Test
    public void getDistinctCommitTDsTest() {
        List<CommitModel> allCommits = new ArrayList<>();
        List<String> classes = Arrays.asList("ClassA.java", "ClassA.java", "ClassB.java");
        List<CodeLocation> locations = createLocations(classes);
        CommitModel commit = createCommitWithTD(locations);
        allCommits.add(commit);

        List<CommitTD> actual = TDCalculator.getDistinctCommitTDs(allCommits).collect(Collectors.toList());

        assertEquals(2, actual.size());

        List<CommitTD> b = actual.stream().filter(td -> td.getLocation().getFileName().equals("ClassB.java"))
                .collect(Collectors.toList());

        assertFalse(b.isEmpty());
    }

    @Test
    public void getDistinctCommitTDsNoDistinctTest() {
        List<CommitModel> allCommits = new ArrayList<>();
        List<String> classes = Arrays.asList("ClassA.java", "ClassB.java");
        List<CodeLocation> locations = createLocations(classes);
        CommitModel commit = createCommitWithTD(locations);
        allCommits.add(commit);

        List<CommitTD> actual = TDCalculator.getDistinctCommitTDs(allCommits).collect(Collectors.toList());

        assertEquals(2, actual.size());
    }

    @Test
    public void getTotalChangesTest() {
        List<CommitModel> allCommits = new ArrayList<>();
        List<String> diffs = Arrays.asList("ClassA.java", "ClassB.java");
        CommitDiff diff1 = createDiff(diffs, diffs, diffs);
        CommitModel commit = createCommit("", BuildStatus.SUCCESSFUL);
        commit.setDiff(diff1);
        allCommits.add(commit);

        Set<String> changes = TDCalculator.getTotalChanges(allCommits);

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

        Set<String> changes = TDCalculator.getTotalChanges(allCommits);

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

        Set<String> changes = TDCalculator.getTotalChanges(allCommits);

        assertFalse(changes.isEmpty());
        assertEquals(2, changes.size());
        assertTrue(changes.contains("ClassA.java"));
        assertTrue(changes.contains("ClassC.java"));
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