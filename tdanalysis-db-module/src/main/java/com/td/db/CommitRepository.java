package com.td.db;

import com.td.models.CommitModel;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommitRepository extends MongoRepository<CommitModel, String>{

    List<CommitModel> findCommitModelsByRepositoryId(String id);

}
