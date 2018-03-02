package com.td.config;

import com.td.models.CommitModel;
import com.td.models.RepositoryModel;
import com.td.processor.CommitProcessor;
import com.td.processor.RepositoryProcessor;
import com.td.readers.InMemoryReader;
import com.td.readers.MongoRepositoryReader;
import com.td.writers.MongoCommitWriter;
import com.td.writers.MongoRepositoryWriter;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.job.flow.support.SimpleFlow;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.file.transform.LineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.List;

@Configuration
@EnableBatchProcessing
public class TDBatch {

    private static final int CHUNK_SIZE = 10;

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Autowired
    private MongoRepositoryWriter mongoRepositoryWriter;

    @Autowired
    private MongoCommitWriter mongoCommitWriter;

    @Autowired
    private CommitProcessor commitProcessor;

    @Autowired
    private RepositoryProcessor repositoryProcessor;

    @Autowired
    private MongoRepositoryReader mongoRepositoryReader;

    @Autowired
    MongoTemplate mongoTemplate;

    @Bean
    public Job repositoryJob() {

        Flow flow = new FlowBuilder<SimpleFlow>("flow").start(cloneRepositoriesStep()).next(readCommitMetadataStep())
                .build();

        return jobBuilderFactory.get("repositoryJob").incrementer(new RunIdIncrementer()).start(flow).end().build();
    }

    @Bean
    public Step cloneRepositoriesStep() {
        return stepBuilderFactory.get("cloneRepositoriesStep").<RepositoryModel, RepositoryModel>chunk(1)
                .reader(csvFileReader()).processor(repositoryProcessor).writer(mongoRepositoryWriter)
                .taskExecutor(taskExecutor()).build();
    }

    @Bean
    public Step readCommitMetadataStep() {
        return stepBuilderFactory.get("readCommitMetadataStep").<RepositoryModel, List<CommitModel>>chunk(1)
                .reader(mongoRepositoryReader).processor(commitProcessor).writer(mongoCommitWriter)
                .taskExecutor(taskExecutor()).throttleLimit(10).build();
    }

    @Bean
    public TaskExecutor taskExecutor() {
        return new SimpleAsyncTaskExecutor("spring_batch");
    }

    @Bean
    ItemReader<RepositoryModel> csvFileReader() {
        FlatFileItemReader<RepositoryModel> csvReader = new FlatFileItemReader<>();
        csvReader.setResource(new ClassPathResource("repositories.csv"));
        csvReader.setLinesToSkip(1);

        LineMapper<RepositoryModel> repoLineMapper = createRepoLineMapper();
        csvReader.setLineMapper(repoLineMapper);

        return csvReader;
    }

    private LineMapper<RepositoryModel> createRepoLineMapper() {
        DefaultLineMapper<RepositoryModel> repoLineMapper = new DefaultLineMapper<>();

        LineTokenizer repoLineTokenizer = createRepoLineTokenizer();
        repoLineMapper.setLineTokenizer(repoLineTokenizer);

        FieldSetMapper<RepositoryModel> repoInfoMapper = createRepoInfoMapper();
        repoLineMapper.setFieldSetMapper(repoInfoMapper);

        return repoLineMapper;
    }

    private LineTokenizer createRepoLineTokenizer() {
        DelimitedLineTokenizer repoLineTokenizer = new DelimitedLineTokenizer();
        repoLineTokenizer.setDelimiter(",");
        repoLineTokenizer.setNames(new String[] { "id", "author", "name", "uri", "issueTracker", "buildCommand" });
        return repoLineTokenizer;
    }

    private FieldSetMapper<RepositoryModel> createRepoInfoMapper() {
        BeanWrapperFieldSetMapper<RepositoryModel> repoInfoMapper = new BeanWrapperFieldSetMapper<>();
        repoInfoMapper.setTargetType(RepositoryModel.class);
        return repoInfoMapper;
    }

}
