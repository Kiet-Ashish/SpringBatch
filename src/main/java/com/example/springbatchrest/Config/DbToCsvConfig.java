package com.example.springbatchrest.Config;

import com.example.springbatchrest.Entity.Todo;
import com.example.springbatchrest.Repository.TodoPageRepository;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.item.file.FlatFileHeaderCallback;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.PlatformTransactionManager;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.Collections;
import java.util.HashMap;

@Configuration
public class DbToCsvConfig {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final TodoPageRepository repository;
    private final TodoProcessor processor;
    @Autowired
    public DbToCsvConfig(JobRepository jobRepository, PlatformTransactionManager transactionManager, TodoPageRepository repository, TodoProcessor processor){
        this.jobRepository = jobRepository;
        this.transactionManager = transactionManager;
        this.repository = repository;
        this.processor = processor;
    }
    @Bean(name = "dbToCsvJob")
    public Job dbJob(){
        return new JobBuilder("DbToCsv", jobRepository)
                .start(step())
                .build();
    }

    @Bean(name = "dbToCsvStep")
    public Step step(){
        return new StepBuilder("DbToCsvStep", jobRepository)
                .<Todo, Todo>chunk(100,  transactionManager)
                .reader(reader())
                .processor(processor)
                .writer(writer())
                .build();
    }
    @Bean(name = "DbToCsvReader")
    public RepositoryItemReader<Todo> reader(){
        Long maxId = 302L;
        RepositoryItemReader<Todo> reader = new RepositoryItemReader<>();
        reader.setRepository(repository);
        reader.setMethodName("findByIdLessThan");
        reader.setArguments(Collections.singletonList(maxId));
        reader.setPageSize(100);
        HashMap<String, Sort.Direction> sorts = new HashMap<>();
        sorts.put("id", Sort.Direction.ASC);
        reader.setSort(sorts);
        return reader;
    }

    @Bean(name = "DbToCsvWriter")
    public FlatFileItemWriter<Todo> writer(){
        FlatFileItemWriter<Todo> writer = new FlatFileItemWriter<>();
        writer.setResource(new FileSystemResource(new File("Files/data.csv")));
        FlatFileHeaderCallback  fileHeaderCallback = new FlatFileHeaderCallback() {
            @Override
            public void writeHeader(Writer writer) throws IOException {
                writer.write("user id,id,title,completed");
            }
        };
        BeanWrapperFieldExtractor<Todo> fieldExtractor = new BeanWrapperFieldExtractor<>();
        fieldExtractor.setNames(new String[] {"userId","id","title", "completed"});
        fieldExtractor.afterPropertiesSet();
        DelimitedLineAggregator<Todo> lineAggregator = new DelimitedLineAggregator<>();
        lineAggregator.setDelimiter(",");
        lineAggregator.setFieldExtractor(fieldExtractor);
        writer.setLineAggregator(lineAggregator);
        writer.setHeaderCallback(fileHeaderCallback);
        return writer;
    }
}
