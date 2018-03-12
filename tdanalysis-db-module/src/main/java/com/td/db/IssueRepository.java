package com.td.db;

import com.td.models.IssueModel;
import java.util.List;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IssueRepository extends MongoRepository<IssueModel, String> {

    @Cacheable("issues")
    List<IssueModel> findIssueModelsByRepositoryId(String repositoryId, Sort sort);

    @Cacheable("issue")
    IssueModel findIssueModelByIssueKeyAndRepositoryId(String issueKey, String repositoryId);
}