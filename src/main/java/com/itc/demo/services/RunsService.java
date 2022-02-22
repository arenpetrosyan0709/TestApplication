package com.itc.demo.services;

import com.itc.demo.entities.Run;
import com.itc.demo.repository.RunsRepository;
import com.itc.demo.utils.HaversineAlgorithm;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;

@Service
public class RunsService {

    private final RunsRepository runsRepository;
    private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    public RunsService(RunsRepository runsRepository) {
        this.runsRepository = runsRepository; }

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
        return "Success. Run with id " + run.getId() + " has been created";
    }

    public String runFinished (String runId, String userId, double finishLatitude, double finishLongitude, String finishDatetime, Long distance) {
        if (runId.equals("") || userId.equals("") || finishLatitude==0.0 || finishLongitude==0.0 || finishDatetime.equals("")) {
            return "Empty fields are not allowed";
        }
        if (runId.equals("null") || userId.equals("null") || finishDatetime.equals("null")) {
            return "Empty fields are not allowed";
        }
        boolean isDatetimeCorrect = checkDatetime(finishDatetime);

        Run run = getRun(runId);
        if (isDatetimeCorrect) {
            run.setFinishDatetime(finishDatetime);
        } else {
            return "Incorrect DateTime format";
        }

        run.setUserId(userId);
        run.setFinishLatitude(finishLatitude);
        run.setFinishLongitude(finishLongitude);

        if (distance!=0) {
           run.setDistance(distance);
        } else {
            Long calculatedDistance = HaversineAlgorithm.distanceInM(run.getStartLatitude(), run.getStartLongitude(), finishLatitude, finishLongitude);
            run.setDistance(calculatedDistance);
        }
        runsRepository.save(run);
        return "Success. Run with id " + run.getId() + " has been updated";
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
