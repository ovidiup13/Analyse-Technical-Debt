package com.td.readers;

import com.td.models.RepositoryModel;
import com.td.store.InMemoryStore;
import org.springframework.batch.item.ItemReader;
import org.springframework.stereotype.Component;

/**
 * ItemReader implementation that reads the repositories from the in-memory store.
 */
@Component
public class InMemoryReader implements ItemReader<RepositoryModel> {

    private int next;
    private InMemoryStore store;

    public InMemoryReader(InMemoryStore store){
        assert store != null;
        this.store = store;
    }

    @Override
    public RepositoryModel read() throws Exception {
        RepositoryModel repositoryModel = null;

        if(next < store.getRepositoryModels().size()){
            repositoryModel = store.getRepositoryModels().get(next++);
        }

        return repositoryModel;
    }
}
