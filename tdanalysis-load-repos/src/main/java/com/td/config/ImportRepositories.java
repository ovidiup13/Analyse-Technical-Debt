package com.td.config;

import com.td.models.RepositoryModel;
import com.td.processor.CommitProcessor;
import com.td.processor.RepositoryProcessor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
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
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.mongodb.core.MongoTemplate;

@Configuration
@EnableBatchProcessing
public class ImportRepositories {

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Autowired
    MongoTemplate mongoTemplate;

    @Bean
    public Job repositoryJob() {
        return jobBuilderFactory.get("repositoryJob").incrementer(new RunIdIncrementer()).flow(step1()).end().build();
    }

    @Bean
    public Step step1() {
        return stepBuilderFactory.get("step1")
                .<RepositoryModel, RepositoryModel>chunk(10)
                .reader(csvFileReader())
//                .processor(commitProcessor())
                .processor(repositoryProcessor())
                .writer(writer())
                .build();
    }

    @Bean
    public RepositoryProcessor repositoryProcessor() {
        return new RepositoryProcessor();
    }

    @Bean
    public CommitProcessor commitProcessor() {
        return new CommitProcessor();
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
