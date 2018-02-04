package com.td.writers;

import com.td.models.RepositoryModel;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class NoOpWriter implements ItemWriter<RepositoryModel> {

    @Override
    public void write(List<? extends RepositoryModel> items) throws Exception {
        for(RepositoryModel repo: items){
            System.out.printf("%s %s %s", repo.getName(), repo.getAuthor(), repo.getURI());
        }
    }
}
