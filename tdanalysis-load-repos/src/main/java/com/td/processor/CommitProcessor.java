package com.td.processor;

import java.util.List;

import com.td.db.CommitRepository;
import com.td.helpers.analysis.FindBugsAnalysisHelper;
import com.td.helpers.building.MavenBuildHelper;
import com.td.models.BuildStatus;
import com.td.models.CommitModel;
import com.td.models.RepositoryModel;
import com.td.models.TechnicalDebt;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class CommitProcessor {

    private static final Logger logger = LoggerFactory.getLogger(CommitProcessor.class);

    @Autowired
    @Qualifier("findBugsAnalysisHelper")
    private FindBugsAnalysisHelper findBugsAnalysisHelper;

    @Autowired
    private MavenBuildHelper mavenBuildHelper;

    @Autowired
    private CommitRepository commitRepository;

    public CommitModel processCommit(CommitModel commit, RepositoryModel repo) {
        // // set the repository id
        commit.setRepositoryId(repo.getId());

        BuildStatus buildStatus = buildRevision(repo);
        commit.setBuildStatus(buildStatus);

        if (buildStatus.equals(BuildStatus.SUCCESSFUL)) {
            TechnicalDebt debt = analyseDebt(repo);
            commit.setTechnicalDebt(debt);
        }

        return commit;
    }

    /**
     * Builds the current revision of the repository.
     * TODO: make it work for gradle builds
     */
    public BuildStatus buildRevision(RepositoryModel repo) {
        return mavenBuildHelper.buildRepository(repo);
    }

    public TechnicalDebt analyseDebt(RepositoryModel repo) {
        return findBugsAnalysisHelper.executeAnalysis(repo);
    }

    public void saveCommit(CommitModel commit) {
        commitRepository.save(commit);
    }
}
