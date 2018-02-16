package com.td.readers;

import com.td.db.ProjectRepository;
import com.td.models.RepositoryModel;
import org.springframework.batch.item.ItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MongoRepositoryReader implements ItemReader<RepositoryModel> {

    @Autowired
    ProjectRepository projectRepository;

    @Override
    public RepositoryModel read() {
        return (RepositoryModel) projectRepository.findAll();
    }
}
