package com.td.readers;

import com.td.db.ProjectRepository;
import com.td.models.RepositoryModel;
import org.springframework.batch.item.ItemReader;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MongoRepositoryReader implements ItemReader<RepositoryModel> {

    int next;
    List<RepositoryModel> results;

    public MongoRepositoryReader(ProjectRepository repository){
        results = repository.findAll();
    }

    @Override
    public RepositoryModel read() {
        RepositoryModel repositoryModel = null;

        if(next < results.size()){
            repositoryModel = results.get(next++);
        }

        return repositoryModel;
    }
}
