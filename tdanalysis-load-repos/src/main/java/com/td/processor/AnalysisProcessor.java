package com.td.processor;

import com.td.helpers.StaticAnalysisHelper;
import com.td.models.RepositoryModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AnalysisProcessor implements ItemProcessor<RepositoryModel, RepositoryModel> {

    private static final Logger LOGGER = LoggerFactory.getLogger(AnalysisProcessor.class);

    @Override
    public RepositoryModel process(RepositoryModel item) throws Exception {

        LOGGER.info("Starting static analysis for repository {}", item.getName());

        StaticAnalysisHelper staticAnalysisHelper = new StaticAnalysisHelper();

        List<String> results = staticAnalysisHelper.executeAnalysis(item);

        System.out.println(results);

        return item;
    }
}
