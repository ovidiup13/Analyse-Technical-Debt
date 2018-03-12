package com.td.db;

import com.td.models.CommitModel;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommitRepository extends MongoRepository<CommitModel, String> {

    CommitModel findCommitModelByShaAndRepositoryId(String sha, String repositoryId);

    List<CommitModel> findCommitModelsByRepositoryId(String id);

    @Query(value = "{'issueIds': '?0'}")
    List<CommitModel> findCommitModelsByIssueModels(String issueId);

}
