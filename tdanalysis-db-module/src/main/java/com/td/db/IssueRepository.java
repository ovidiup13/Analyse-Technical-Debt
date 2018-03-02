package com.td.db;

import com.td.models.IssueModel;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IssueRepository extends MongoRepository<IssueModel, String> {

    List<IssueModel> findIssueModelsByRepositoryId(String repositoryId);

    IssueModel findIssueModelByIssueId(String issueId);
}