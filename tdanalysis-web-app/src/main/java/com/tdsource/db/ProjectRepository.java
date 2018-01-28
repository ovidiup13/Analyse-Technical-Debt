package com.tdsource.db;

import com.tdsource.models.RepositoryModel;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectRepository extends MongoRepository<RepositoryModel, String> {
}
