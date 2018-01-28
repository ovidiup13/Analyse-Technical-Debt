package com.td.db;

import com.td.models.RepositoryModel;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectRepository extends MongoRepository<RepositoryModel, String> {
}
