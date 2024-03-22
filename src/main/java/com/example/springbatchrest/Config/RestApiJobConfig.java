package com.example.springbatchrest.Config;

import com.example.springbatchrest.Entity.Todo;
import com.example.springbatchrest.Service.JobService;
import com.example.springbatchrest.Service.RestJobService;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.adapter.ItemReaderAdapter;
import org.springframework.batch.item.adapter.ItemWriterAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.Iterator;
import java.util.List;

@Configuration
public class RestApiJobConfig {
    public final JobRepository jobRepository;
    public final PlatformTransactionManager transactionManager;

    public final RestJobService restJobService;
    public final TodoProcessor processor;

    public RestApiJobConfig(JobRepository jobRepository, PlatformTransactionManager transactionManager, RestJobService restJobService, TodoProcessor processor){
        this.jobRepository = jobRepository;
        this.processor = processor;
        this.transactionManager = transactionManager;
        this.restJobService = restJobService;
    }
    @Bean(name = "restJob")
    public Job restJob(){
        return new JobBuilder("restJob", jobRepository)
                .start(step())
                .build();
    }
    @Bean(name = "restStep")
    public Step step(){
        return new StepBuilder("restStep", jobRepository)
                .<Todo, Todo> chunk(50, transactionManager)
                .reader(todoItemReaderAdapter())
                .processor(processor)
                .writer(writer())
                .build();
    }
    @Bean(name = "restReader")
    public ItemReaderAdapter<Todo> todoItemReaderAdapter(){
        ItemReaderAdapter<Todo> readerAdapter = new ItemReaderAdapter<>();
        readerAdapter.setTargetObject(restJobService);
        readerAdapter.setTargetMethod("oneAtTime");
        return readerAdapter;
    }
    @Bean(name = "restWriter")
    public ItemWriter<Todo> writer(){
        return new ItemWriter<Todo>() {
            @Override
            public void write(Chunk<? extends Todo> chunk) throws Exception {
                chunk.forEach(System.out::println);
            }
        };
    }
}
