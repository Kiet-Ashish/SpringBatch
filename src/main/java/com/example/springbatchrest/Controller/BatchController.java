package com.example.springbatchrest.Controller;

import com.example.springbatchrest.Service.JobService;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/batch")
public class BatchController {

    private final JobService service;

    public BatchController(JobService service){
        this.service = service;
    }

    @GetMapping("/startCsv")
    public String startCsvToDB(){
        // job starting.....
        service.startCsvToDB();
        return "Csv Job Started....";
    }

    @GetMapping("/startJson")
    public String startJsonToDB(){
        // service layer
        service.startJsonToDB();
        return "Json Job Started";
    }

    @GetMapping("/startRest")
    public String startRestToDB(){
        service.startRestToDB();
        return "Rest Job started";
    }
    @GetMapping("/startDbToCsv")
    public String startDbToCsv(){
        service.startDbToCsv();
        return "Rest Job started";
    }

    @GetMapping("/startDbToJson")
    public String startDbToJson(){
        service.startDbToJson();
        return "Rest Job started...";
    }
}
