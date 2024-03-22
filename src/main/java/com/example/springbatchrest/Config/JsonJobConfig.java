package com.example.springbatchrest.Config;

import com.example.springbatchrest.Entity.Todo;
import com.example.springbatchrest.Repository.TodoRepository;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.file.FlatFileParseException;
import org.springframework.batch.item.json.JacksonJsonObjectReader;
import org.springframework.batch.item.json.JsonItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class JsonJobConfig {
    // put all configuration here.
    private JobRepository jobRepository;
    private PlatformTransactionManager transactionManager;
    private TodoProcessor processor;
    private final TodoRepository repository;
    @Autowired
    public JsonJobConfig(JobRepository jobRepository, PlatformTransactionManager transactionManager, TodoProcessor processor, TodoRepository repository){
        this.jobRepository = jobRepository;
        this.transactionManager = transactionManager;
        this.processor = processor;
        this.repository = repository;
    }

    @Bean(value = "jsonJob")
    public Job jsonJob(){
        return new JobBuilder("JSON-TO-DATABASE", jobRepository)
                .start(jsonStep())
                .build();
    }

    @Bean
    public Step jsonStep(){
        return new StepBuilder("firstStep", jobRepository)
                .<Todo, Todo>chunk(100, transactionManager)
                .reader(reader())
                .processor(processor)
                .writer(writer())
                .faultTolerant()
                .skip(Throwable.class)
                .skipLimit(10)
                .build();
    }

    // Json item reader....
    @Bean(name = "jsonReader")
    public JsonItemReader<Todo> reader(){
        JsonItemReader<Todo> reader = new JsonItemReader<>();
        reader.setResource(new FileSystemResource("src/main/resources/todos.json"));
        reader.setJsonObjectReader(
                new JacksonJsonObjectReader<>(Todo.class)
        );
        reader.setName("jsonReader");
        return reader;
    }

    // Database item writer....

    @Bean(name = "jsonWriter")
    public RepositoryItemWriter<Todo> writer(){
        RepositoryItemWriter<Todo> writer = new RepositoryItemWriter<>();
        writer.setRepository(repository);
        writer.setMethodName("save");
        return writer;
    }

    // task executor
    @Bean(name = "jsonExecutor")
    public TaskExecutor executor(){
        SimpleAsyncTaskExecutor executor = new SimpleAsyncTaskExecutor();
        executor.setConcurrencyLimit(30);
        return executor;
    }
}
