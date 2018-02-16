package com.td.processor;

import com.td.helpers.StaticAnalysisHelper;
import com.td.models.BugModel;
import com.td.models.RepositoryModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AnalysisProcessor implements ItemProcessor<RepositoryModel, RepositoryModel> {

    private static final Logger LOGGER = LoggerFactory.getLogger(AnalysisProcessor.class);

    @Value("${findbugs.home.path}")
    private String findBugsHomePath;

    @Override
    public RepositoryModel process(RepositoryModel item) throws Exception {

        LOGGER.info("Starting static analysis for repository {}", item.getName());

        StaticAnalysisHelper staticAnalysisHelper = new StaticAnalysisHelper(findBugsHomePath);

        List<BugModel> results = staticAnalysisHelper.executeAnalysis(item);

        item.getCommits().get(0).setBugs(results);

        return item;
    }
}
