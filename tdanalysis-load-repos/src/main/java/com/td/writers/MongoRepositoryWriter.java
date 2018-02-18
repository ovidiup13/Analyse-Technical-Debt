package com.td.writers;

import com.td.db.ProjectRepository;
import com.td.models.RepositoryModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.MongoItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MongoRepositoryWriter implements ItemWriter<RepositoryModel> {

    private static final Logger LOGGER = LoggerFactory.getLogger(MongoRepositoryWriter.class);

    @Autowired
    ProjectRepository projectRepository;

    @Override
    public void write(List<? extends RepositoryModel> items) {
        LOGGER.info("Writing repositories to DB...");
        projectRepository.save(items);
    }
}
