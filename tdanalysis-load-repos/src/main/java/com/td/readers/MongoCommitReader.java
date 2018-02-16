package com.td.readers;

import com.td.db.CommitRepository;
import com.td.models.CommitModel;
import org.springframework.batch.item.ItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MongoCommitReader implements ItemReader<CommitModel> {

    @Autowired
    CommitRepository commitRepository;

    @Override
    public CommitModel read() {
        return (CommitModel) commitRepository.findAll();
    }
}
