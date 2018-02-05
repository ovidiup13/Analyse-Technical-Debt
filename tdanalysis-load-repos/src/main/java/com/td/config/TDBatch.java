package com.td.config;

import com.td.models.RepositoryModel;
import com.td.processor.CommitProcessor;
import com.td.processor.RepositoryProcessor;
import com.td.readers.InMemoryReader;
import com.td.writers.InMemoryWriter;
import com.td.writers.NoOpWriter;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.job.flow.support.SimpleFlow;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.data.MongoItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.file.transform.LineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.mongodb.core.MongoTemplate;

@Configuration
@EnableBatchProcessing
public class TDBatch {

    private static final int CHUNK_SIZE = 10;

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Autowired
    private NoOpWriter noOpWriter;

    @Autowired
    private InMemoryWriter inMemoryWriter;

    @Autowired
    private InMemoryReader inMemoryReader;

    @Autowired
    private CommitProcessor commitProcessor;

    @Autowired
    private RepositoryProcessor repositoryProcessor;

    @Autowired
    MongoTemplate mongoTemplate;

    @Bean
    public Job repositoryJob() {

        Flow flow = new FlowBuilder<SimpleFlow>("flow")
                .start(cloneRepositories())
                .next(readCommitMetadata())
                .build();

        return jobBuilderFactory
                .get("repositoryJob")
                .incrementer(new RunIdIncrementer())
                .start(flow)
                .end()
                .build();
    }

    @Bean
    public Step cloneRepositories() {
        return stepBuilderFactory
                .get("cloneRepositories")
                .<RepositoryModel, RepositoryModel>chunk(CHUNK_SIZE)
                .reader(csvFileReader())
                .processor(repositoryProcessor)
                .writer(inMemoryWriter)
                .build();
    }

    @Bean
    public Step readCommitMetadata() {
        return stepBuilderFactory
                .get("readCommitMetadata")
                .<RepositoryModel, RepositoryModel>chunk(CHUNK_SIZE)
                .reader(inMemoryReader)
                .processor(commitProcessor)
                .writer(noOpWriter)
                .build();
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

    @Bean
    public MongoItemWriter<RepositoryModel> writer() {
        MongoItemWriter<RepositoryModel> writer = new MongoItemWriter<>();
        writer.setTemplate(mongoTemplate);
        writer.setCollection("repos");
        return writer;
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
        repoLineTokenizer.setNames(new String[]{"id", "author", "name", "uri"});
        return repoLineTokenizer;
    }

    private FieldSetMapper<RepositoryModel> createRepoInfoMapper() {
        BeanWrapperFieldSetMapper<RepositoryModel> repoInfoMapper = new BeanWrapperFieldSetMapper<>();
        repoInfoMapper.setTargetType(RepositoryModel.class);
        return repoInfoMapper;
    }


}
