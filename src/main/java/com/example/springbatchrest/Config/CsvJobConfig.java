package com.example.springbatchrest.Config;

import com.example.springbatchrest.Entity.Organization;
import com.example.springbatchrest.Repository.OrganizationRepository;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileParseException;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class CsvJobConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final OrganizationRepository repository;

    @Autowired
    public CsvJobConfig(JobRepository jobRepository, PlatformTransactionManager transactionManager, OrganizationRepository repository){
        this.jobRepository = jobRepository;
        this.transactionManager = transactionManager;
        this.repository = repository;
    }

    @Bean(name = "csvJob")
    public Job job(){
        return new JobBuilder("CSV-TO-DATABASE", jobRepository)
                .start(csvStep())
                .build();
    }


    @Bean
    public Step csvStep(){
        return new StepBuilder("csv-database", jobRepository)
                .<Organization, Organization>chunk(10000, transactionManager)
                .reader(reader())
                .processor(processor())
                .writer(writer())
                .taskExecutor(executor())
                .faultTolerant()
                .skip(FlatFileParseException.class)
                .skip(Throwable.class).skipLimit(10)
                .build();

    }

    // Parallel Threads to make Batching fast
    @Bean(name = "csvToDbExecutor")
    public TaskExecutor executor(){
        SimpleAsyncTaskExecutor asyncTaskExecutor = new SimpleAsyncTaskExecutor();
        asyncTaskExecutor.setConcurrencyLimit(40);
        return asyncTaskExecutor;
    }

    // reader configuration
    @Bean
    public FlatFileItemReader<Organization> reader(){
        FlatFileItemReader<Organization> reader = new FlatFileItemReader<>();
        reader.setResource(new FileSystemResource("src/main/resources/Data.csv"));
        reader.setLineMapper(lineMapper());
        reader.setLinesToSkip(1);
        reader.setName("csvReader");
        return reader;
    }

    @Bean
    public LineMapper<Organization> lineMapper(){
        DefaultLineMapper<Organization> lineMapper = new DefaultLineMapper<>();

        DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
        lineTokenizer.setDelimiter(",");
        lineTokenizer.setStrict(false);
        lineTokenizer.setNames("id", "organizationId", "name", "website", "country", "description", "founded", "industry", "empCount");

        BeanWrapperFieldSetMapper<Organization> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
        fieldSetMapper.setTargetType(Organization.class);

        lineMapper.setLineTokenizer(lineTokenizer);
        lineMapper.setFieldSetMapper(fieldSetMapper);
        return lineMapper;
    }

    // writer Configuration
    @Bean
    public RepositoryItemWriter<Organization> writer(){
        RepositoryItemWriter<Organization> writer = new RepositoryItemWriter<>();
        writer.setRepository(repository);
        writer.setMethodName("save");
        return writer;
    }

    // processor Bean
    @Bean
    public OrganizationProcessor processor(){
        return new OrganizationProcessor();
    }
}
