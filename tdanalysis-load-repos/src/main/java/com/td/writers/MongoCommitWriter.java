package com.td.writers;

import com.td.db.CommitRepository;
import com.td.models.CommitModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.MongoItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class MongoCommitWriter implements ItemWriter<List<CommitModel>> {

    private static final Logger LOGGER = LoggerFactory.getLogger(MongoRepositoryWriter.class);

    @Autowired
    private CommitRepository commitRepository;

    @Override
    public void write(List<? extends List<CommitModel>> items) {

        LOGGER.info("Writing all commits to DB...");

        // flatten all lists
        List<CommitModel> flattenedItems = items.parallelStream().flatMap(List::stream).collect(Collectors.toList());

        commitRepository.save(flattenedItems);
    }
}
