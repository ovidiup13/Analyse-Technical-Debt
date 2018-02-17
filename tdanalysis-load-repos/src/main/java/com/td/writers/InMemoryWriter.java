package com.td.writers;

import com.td.models.RepositoryModel;
import com.td.store.InMemoryStore;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;

@Component
public class InMemoryWriter implements ItemWriter<RepositoryModel> {

    @Autowired
    public InMemoryStore store;

    @Override
    public void write(List<? extends RepositoryModel> items) {
        store.setRepositoryModels((List<RepositoryModel>)Collections.synchronizedList(items));
    }
}
