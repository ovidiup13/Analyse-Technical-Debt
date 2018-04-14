package com.td.db;

import java.util.List;

import com.td.models.BuildStatus;
import com.td.models.CommitModel;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CommitRepository extends MongoRepository<CommitModel, String> {

    @Cacheable("commit")
    CommitModel findCommitModelByShaAndRepositoryId(String sha, String repositoryId);

    @Cacheable("commits")
    List<CommitModel> findCommitModelsByRepositoryId(String id, Sort sort);

    @Cacheable("issueCommits")
    @Query(value = "{'issueIds.0': '?0'}")
    List<CommitModel> findCommitModelsByIssueModels(String issueId, Sort sort);

    @Cacheable("authorCommits")
    List<CommitModel> findCommitModelsByRepositoryIdAndAuthor(String id, String author, Sort sort);

    @Cacheable("timeline")
    List<CommitModel> findByRepositoryIdAndBuildStatusOrderByTimestampAsc(String id, BuildStatus buildStatus);
}
