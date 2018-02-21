package com.td.store;

import com.td.models.RepositoryModel;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// Use a concurrent list with Collections.synchronizedList(new ArrayList<>());
@Component()
public class InMemoryStore {

    private List<RepositoryModel> repositoryModels;

    public InMemoryStore(){
        repositoryModels = Collections.synchronizedList(new ArrayList<>());
    }

    public InMemoryStore(List<RepositoryModel> repositoryModels) {
        this.repositoryModels = repositoryModels;
    }

    public List<RepositoryModel> getRepositoryModels() {
        return repositoryModels;
    }

    public void setRepositoryModels(List<RepositoryModel> repositoryModels) {
        this.repositoryModels = repositoryModels;
    }

    public void addRepositories(List<RepositoryModel> repos){
        this.repositoryModels.addAll(repos);
    }


}
