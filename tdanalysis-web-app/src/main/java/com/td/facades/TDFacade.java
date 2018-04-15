package com.td.facades;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.td.db.CommitRepository;
import com.td.db.TDReferenceRepository;
import com.td.models.BuildStatus;
import com.td.models.ChangeSetStats;
import com.td.models.ChangeTD;
import com.td.models.CommitModel;
import com.td.models.TDStats;
import com.td.models.TechnicalDebt;
import com.td.models.TechnicalDebtItem;
import com.td.models.TechnicalDebtItem.CompositeKey;
import com.td.models.WorkEffort;
import com.td.models.WorkItem;
import com.td.models.WorkTD;
import com.td.utils.ChangeSetCalculator;
import com.td.utils.TDCalculator;
import com.td.utils.WorkEffortCalculator;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

@Component
public class TDFacade {

    private TDReferenceRepository tdRepository;

    private CommitRepository commitRepository;

    private RepositoryFacade repositoryFacade;

    public TDFacade(TDReferenceRepository referenceRepository, CommitRepository commitRepository,
            RepositoryFacade repositoryFacade) {
        this.tdRepository = referenceRepository;
        this.commitRepository = commitRepository;
        this.repositoryFacade = repositoryFacade;
    }

    /**
     * Returns all technical debt reference items as a list.
     */
    public List<TechnicalDebtItem> getAllTDItems() {
        return tdRepository.findAll();
    }

    /**
     * Retrieves a single TD reference item by category initial and code.
     */
    public TechnicalDebtItem getTDItem(String category, String code) {
        return tdRepository.findBy_id(new CompositeKey(category, code));
    }

    /**
     * Returns technical debt associated with a commit.
     */
    public TechnicalDebt getTechnicalDebtOfCommit(String id, String sha) {
        CommitModel commit = commitRepository.findCommitModelByShaAndRepositoryId(sha, id);
        return commit == null ? null : commit.getTechnicalDebt();
    }

    /**
    * Returns statistics about the technical debt items which the developer had
    * to deal with.
    */
    public Optional<TDStats> getTechnicalDebtForIssue(String repoId, List<CommitModel> commits) {
        List<CommitModel> allCommits = repositoryFacade.getAllCommits(repoId);
        return TDCalculator.getTechnicalDebtForIssue(commits, allCommits);
    }

    /**
     * Returns a stream of technical debt statistics that specify the evolution
     * of technical debt in the project.
     */
    public Stream<TechnicalDebt> getTechnicalDebtTimeline(String id) {
        List<CommitModel> commits = commitRepository.findByRepositoryIdAndBuildStatusOrderByTimestampAsc(id,
                BuildStatus.SUCCESSFUL);
        return commits.stream().filter(c -> c.getTechnicalDebt() != null).map(c -> {
            TechnicalDebt t = c.getTechnicalDebt();
            t.setTdItems(null); // ignore TD items, just need counts
            return t;
        });
    }

    /**
     * Returns a list of changeset-technical debt statistics.
     */
    @Cacheable("changeTD")
    public List<ChangeTD> getChangeSetTechnicalDebt(String id) {
        List<WorkItem> items = repositoryFacade.getWorkItemSingleAuthor(id).filter(item -> item.getCommits().size() > 0)
                .collect(Collectors.toList());

        // get all changesets per issue
        List<ChangeSetStats> changeSets = items.stream()
                .map(item -> ChangeSetCalculator.getChangeSetStats(item.getCommits())).collect(Collectors.toList());

        // get technical debt per issue
        List<Optional<TDStats>> td = getTechnicalDebt(id, items);

        // combine change and TD into results
        List<ChangeTD> result = new ArrayList<>(changeSets.size());
        for (int i = 0; i < changeSets.size(); i++) {
            ChangeTD change = new ChangeTD();
            change.setChangeSet(changeSets.get(i));
            change.setTechnicalDebt(td.get(i).orElse(null));
            result.add(change);
        }

        return result;
    }

    /**
     * Returns a list of work effort-technical debt statistics using ticket
     * calculation method for work effort.
     */
    @Cacheable("workTDTicket")
    public List<WorkTD> getWorkTDByTicket(String id) {

        // filter by number of commits and status
        List<WorkItem> items = repositoryFacade.getWorkItemSingleAuthor(id).filter(item -> item.getCommits().size() > 0)
                .filter(item -> !item.getIssue().getStatus().equalsIgnoreCase("OPEN")).collect(Collectors.toList());

        List<WorkEffort> workEffort = items.stream()
                .map(item -> WorkEffortCalculator.getWorkEffortByTicketTimestamp(item.getIssue()))
                .collect(Collectors.toList());

        // get technical debt per issue
        List<Optional<TDStats>> td = getTechnicalDebt(id, items);

        List<WorkTD> result = new ArrayList<>();
        for (int i = 0; i < workEffort.size(); i++) {
            WorkTD workTd = new WorkTD();
            workTd.setWorkEffort(workEffort.get(i));
            workTd.setTechnicalDebt(td.get(i).orElse(null));
            result.add(workTd);
        }

        return result;
    }

    /**
     * Returns a list of work effort-technical debt statistics using commit
     * calculation method for work effort.
     */
    @Cacheable("workTDCommit")
    public List<WorkTD> getWorkTDByCommit(String id) {

        // filter by number of commits and status
        List<WorkItem> items = repositoryFacade.getWorkItemSingleAuthor(id).filter(item -> item.getCommits().size() > 0)
                .filter(item -> !item.getIssue().getStatus().equalsIgnoreCase("OPEN")).collect(Collectors.toList());

        // calculate work effort
        List<WorkEffort> workEffort = items.stream().map(item -> {

            List<CommitModel> issueCommits = item.getCommits();

            // get first commits and author
            CommitModel firstCommit = issueCommits.get(0);
            CommitModel lastCommit = issueCommits.get(issueCommits.size() - 1);
            String author = firstCommit.getAuthor();

            // find all commits by author, sorted by timestamp
            List<CommitModel> authorCommits = repositoryFacade.getAllCommitsByAuthor(id, author);

            // get the commit before firstCommit
            Optional<CommitModel> previousOpt = getPreviousCommitByAuthor(firstCommit, authorCommits);

            // if it does not exist, just use the firstCommit
            CommitModel previousCommit = previousOpt.isPresent() ? previousOpt.get() : firstCommit;

            return WorkEffortCalculator.getWorkEffortByCommitTimestamp(previousCommit, lastCommit);

        }).collect(Collectors.toList());

        // get technical debt per issue
        List<Optional<TDStats>> td = getTechnicalDebt(id, items);

        List<WorkTD> result = new ArrayList<>();
        for (int i = 0; i < workEffort.size(); i++) {
            WorkTD workTd = new WorkTD();
            workTd.setWorkEffort(workEffort.get(i));
            workTd.setTechnicalDebt(td.get(i).orElse(null));
            result.add(workTd);
        }

        return result;
    }

    /**
     * Returns the technical debt statistics of the repository with the
     * specified work items.
     */
    private List<Optional<TDStats>> getTechnicalDebt(String repoId, List<WorkItem> items) {
        return items.stream().map(item -> TDCalculator.getTechnicalDebtForIssue(item.getCommits(),
                repositoryFacade.getAllCommits(repoId))).collect(Collectors.toList());
    }

    /**
    * Returns the previous commit by the same author.
    */
    private Optional<CommitModel> getPreviousCommitByAuthor(CommitModel commit, List<CommitModel> authorCommits) {
        // binary search for current commit
        int index = authorCommits.indexOf(commit);

        // return previous commit
        return index < 1 ? Optional.empty() : Optional.of(authorCommits.get(index - 1));
    }
}