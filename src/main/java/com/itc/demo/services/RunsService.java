package com.itc.demo.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itc.demo.entities.Run;
import com.itc.demo.entities.Runner;
import com.itc.demo.repository.RunsRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;

@Service
public class RunsService {

    private final RunsRepository runsRepository;
    private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    public RunsService(RunsRepository runsRepository) {this.runsRepository = runsRepository; }

    public String runStarted (String userId, double startLatitude, double startLongitude, String startDatetime) {
        if (userId.equals("") || startLatitude==0.0 || startLongitude==0.0 || startDatetime.equals("")) {
            return "Empty fields are not allowed";
        }
        if (userId.equals("null") || startDatetime.equals("null")) {
            return "Empty fields are not allowed";
        }
        boolean isDatetimeCorrect = checkDatetime(startDatetime);
        Run run;
        if (isDatetimeCorrect) {
            run = new Run(userId, startLatitude, startLongitude, startDatetime);
            runsRepository.save(run);
        } else {
            return "Incorrect DateTime format";
        }
        return "Success. Run with id" + run.getId() + "has been created";
    }

    private Run getRun(String runId)  {
        Iterable<Run> runners = runsRepository.findAllById(Collections.singleton(runId));
        Run run = runners.iterator().next();
        return run;
    }


    private boolean checkDatetime (String datetime) {
        try {
            LocalDate dateTime = LocalDate.parse(datetime, formatter);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

}
