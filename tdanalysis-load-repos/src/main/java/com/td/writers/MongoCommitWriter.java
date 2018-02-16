package com.td.writers;

import com.td.db.CommitRepository;
import com.td.models.CommitModel;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MongoCommitWriter implements ItemWriter<CommitModel> {

    @Autowired
    private CommitRepository commitRepository;

    @Override
    public void write(List<? extends CommitModel> items) {
        commitRepository.insert(items);
    }
}
