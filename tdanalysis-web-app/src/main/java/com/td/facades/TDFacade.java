package com.td.facades;

import java.util.List;
import java.util.Optional;

import com.td.db.CommitRepository;
import com.td.db.TDReferenceRepository;
import com.td.models.CommitModel;
import com.td.models.TechnicalDebt;
import com.td.models.TechnicalDebtItem;
import com.td.models.TechnicalDebtItem.CompositeKey;
import com.td.models.TDStats;
import com.td.utils.TDCalculator;

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
}