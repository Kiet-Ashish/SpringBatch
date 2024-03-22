package com.example.springbatchrest.Service;

import com.example.springbatchrest.Entity.Todo;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class JobService {

    private final JobLauncher jobLauncher;
    private final Job csvJob;
    private final Job jsonJob;
    private final Job restJob;
    private final Job dbToCsv;
    private final Job dbToJsonJob;
    public JobService(JobLauncher jobLauncher,@Qualifier(value = "csvJob") Job csvJob, @Qualifier(value = "jsonJob") Job jsonJob, @Qualifier(value = "restJob") Job restJob, @Qualifier(value = "dbToCsvJob") Job dbToCsv, @Qualifier(value = "dbToJsonJob") Job dbToJsonJob){
        this.jobLauncher = jobLauncher;
        this.csvJob = csvJob;
        this.jsonJob = jsonJob;
        this.restJob = restJob;
        this.dbToCsv = dbToCsv;
        this.dbToJsonJob = dbToJsonJob;
    }

    @Async
    public void startCsvToDB(){
        JobParameters parameters = new JobParametersBuilder()
                .addLong("startTime", System.currentTimeMillis())
                .toJobParameters();

        try {
            jobLauncher.run(csvJob, parameters);
        }
        catch (JobExecutionAlreadyRunningException
               | JobRestartException
               | JobInstanceAlreadyCompleteException
               | JobParametersInvalidException e){
            e.printStackTrace();
        }
    }
    @Async
    public void startJsonToDB(){
        JobParameters parameters = new JobParametersBuilder()
                .addLong("startTime", System.currentTimeMillis())
                .toJobParameters();

        try {
            jobLauncher.run(jsonJob, parameters);
        }
        catch (JobExecutionAlreadyRunningException
               | JobRestartException
               | JobInstanceAlreadyCompleteException
               | JobParametersInvalidException e){
            e.printStackTrace();
        }
    }
    @Async
    public void startRestToDB(){
        JobParameters parameters = new JobParametersBuilder()
                .addLong("startTime", System.currentTimeMillis())
                .toJobParameters();

        try {
            jobLauncher.run(restJob, parameters);
        }
        catch (JobExecutionAlreadyRunningException
               | JobRestartException
               | JobInstanceAlreadyCompleteException
               | JobParametersInvalidException e){
            e.printStackTrace();
        }
    }


    @Async
    public void startDbToCsv(){
        JobParameters parameters = new JobParametersBuilder()
                .addLong("startTime", System.currentTimeMillis())
                .toJobParameters();

        try {
            jobLauncher.run(dbToCsv, parameters);
        }
        catch (JobExecutionAlreadyRunningException
               | JobRestartException
               | JobInstanceAlreadyCompleteException
               | JobParametersInvalidException e){
            e.printStackTrace();
        }
    }
    @Async
    public void startDbToJson(){
        JobParameters parameters = new JobParametersBuilder()
                .addLong("startTime", System.currentTimeMillis())
                .toJobParameters();

        try {
            jobLauncher.run(dbToJsonJob, parameters);
        }
        catch (JobExecutionAlreadyRunningException
               | JobRestartException
               | JobInstanceAlreadyCompleteException
               | JobParametersInvalidException e){
            e.printStackTrace();
        }
    }
}
