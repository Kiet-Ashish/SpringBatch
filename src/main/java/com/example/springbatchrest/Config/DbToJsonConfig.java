package com.example.springbatchrest.Config;

import com.example.springbatchrest.Entity.Todo;
import com.example.springbatchrest.Repository.TodoPageRepository;
import com.example.springbatchrest.Repository.TodoRepository;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.item.json.JacksonJsonObjectMarshaller;
import org.springframework.batch.item.json.JsonFileItemWriter;
import org.springframework.batch.item.json.builder.JsonFileItemWriterBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.PlatformTransactionManager;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;

@Configuration
public class DbToJsonConfig {
    private JobRepository jobRepository;
    private PlatformTransactionManager transactionManager;
    private TodoPageRepository repository;
    private TodoProcessor processor;

    public DbToJsonConfig(JobRepository jobRepository, PlatformTransactionManager transactionManager,TodoPageRepository repository, TodoProcessor processor){
        this.jobRepository = jobRepository;
        this.transactionManager = transactionManager;
        this.repository = repository;
        this.processor = processor;
    }
    // Json to Db job....
    @Bean(name = "dbToJsonJob")
    public Job jsonToDb(){
        return new JobBuilder("jsonToDbJob", jobRepository)
                .start(jsonStep())
                .build();
    }
    // Json to Db job....
    @Bean(name = "dbToJsonStep")
    public Step jsonStep(){
        return new StepBuilder("dbToJsonStep", jobRepository)
                .<Todo, Todo>chunk(50, transactionManager)
                .reader(reader())
                .processor(processor)
                .writer(writer())
                .build();
    }

    @Bean(name = "dbToJsonReader")
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

    @Bean(name = "dbToJsonWriter")
    public JsonFileItemWriter<Todo> writer(){
        return new JsonFileItemWriterBuilder<Todo>()
                .resource(new FileSystemResource(new File("Files/Todo.json")))
                .jsonObjectMarshaller(new JacksonJsonObjectMarshaller<Todo>())
                .name("todoJsonFileWriter")
                .build();
    }
}
