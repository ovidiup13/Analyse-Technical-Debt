package com.td.writers;

import com.td.db.ProjectRepository;
import com.td.models.RepositoryModel;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MongoRepositoryWriter implements ItemWriter<RepositoryModel> {

    @Autowired
    private ProjectRepository projectRepository;

    @Override
    public void write(List<? extends RepositoryModel> items) {
        projectRepository.insert(items);
    }
}
