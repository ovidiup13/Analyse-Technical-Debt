package com.td.facades;

import java.util.List;

import com.td.db.CommitRepository;
import com.td.db.TDReferenceRepository;
import com.td.models.CommitModel;
import com.td.models.TechnicalDebt;
import com.td.models.TechnicalDebtItem;
import com.td.models.TechnicalDebtItem.CompositeKey;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TDFacade {

    @Autowired
    private TDReferenceRepository tdRepository;

    @Autowired
    private CommitRepository commitRepository;

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

}