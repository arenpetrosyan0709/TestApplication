package com.itc.demo.services;

import com.itc.demo.entities.Run;
import com.itc.demo.repository.RunnerRepository;
import com.itc.demo.repository.RunsRepository;
import com.itc.demo.utils.HaversineAlgorithm;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RunsService {

    private final RunnerRepository runnerRepository;
    private final RunsRepository runsRepository;
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    public RunsService(RunnerRepository runnerRepository, RunsRepository runsRepository) {
        this.runnerRepository = runnerRepository;
        this.runsRepository = runsRepository; }

    public String runStarted (String userId, double startLatitude, double startLongitude, String startDatetime) {
        boolean runnerExists = runnerRepository.existsById(userId);
        if (runnerExists) {
            if (userId.equals("") || startLatitude == 0.0 || startLongitude == 0.0 || startDatetime.equals("")) {
                return "Empty fields are not allowed.";
            }
            if (userId.equals("null") || startDatetime.equals("null")) {
                return "Empty fields are not allowed.";
            }
            boolean isDatetimeCorrect = checkDatetime(startDatetime);
            Run run;
            if (isDatetimeCorrect) {
                run = new Run(userId, startLatitude, startLongitude, startDatetime);
                runsRepository.save(run);
            } else {
                return "Incorrect DateTime format.";
            }
            return "Success. Run with id " + run.getId() + " has been created.";
        } else {
            return "Runner with id " + userId + " does not exist";
        }
    }

    public String runFinished (String runId, String userId, double finishLatitude, double finishLongitude, String finishDatetime, Long distance) {
        boolean exists = runsRepository.existsById(runId);
        if (exists) {
            if (runId.equals("") || userId.equals("") || finishLatitude == 0.0 || finishLongitude == 0.0 || finishDatetime.equals("")) {
                return "Empty fields are not allowed.";
            }
            if (runId.equals("null") || userId.equals("null") || finishDatetime.equals("null")) {
                return "Empty fields are not allowed.";
            }
            boolean isDatetimeCorrect = checkDatetime(finishDatetime);

            Run run = getRun(runId);
            if (!run.isFinished()) {
                if (isDatetimeCorrect) {
                    run.setFinishDatetime(finishDatetime);
                } else {
                    return "Incorrect DateTime format.";
                }

                LocalDateTime startDateTime = LocalDateTime.parse(run.getStartDatetime(), formatter);
                LocalDateTime finishDateTime = LocalDateTime.parse(finishDatetime, formatter);
                if (finishDateTime.isBefore(startDateTime) || finishDateTime.getYear()!=startDateTime.getYear()) {
                    return "Finish DateTime is not correct.";
                }
                run.setUserId(userId);
                run.setFinishLatitude(finishLatitude);
                run.setFinishLongitude(finishLongitude);
                run.setFinished();

                if (distance != 0) {
                    run.setDistance(distance);
                } else {
                    Long calculatedDistance = HaversineAlgorithm.distanceInM(run.getStartLatitude(), run.getStartLongitude(), finishLatitude, finishLongitude);
                    run.setDistance(calculatedDistance);
                }
                long time = calculateTimeInS(startDateTime, finishDateTime);
                Long averageSpeed = distance/time;
                run.setAverageSpeed(averageSpeed);
                runsRepository.save(run);
                return "Success. Run with id " + run.getId() + " has been updated.";
            } else {
                return "This run already has been finished.";
            }
        } else {
            return "Run with id " + runId + " was not found.";
        }
    }

    public Object getAllRunsOfRunner(String userId) {
        boolean runnerExists = runnerRepository.existsById(userId);
        if (runnerExists) {
            List<Run> allRuns = runsRepository.findAll();
            return allRuns.stream().filter(it -> it.getUserId().equals(userId)).collect(Collectors.toList());
        } else {
            return "Runner with id " + userId + " does not exist";
        }
    }

    public Object getAllRunsOfRunnerFromTo(String userId, String fromDatetime, String toDatetime) {
        boolean runnerExists = runnerRepository.existsById(userId);
        if (runnerExists) {
            List<Run> allRuns = runsRepository.findAll();
            List<Run> userRuns = allRuns.stream().filter(it -> it.getUserId().equals(userId)).collect(Collectors.toList());
            List<Run> from = userRuns.stream().filter(it ->
                            LocalDateTime.parse(it.getStartDatetime(), formatter).isAfter(LocalDateTime.parse(fromDatetime, formatter)))
                            .collect(Collectors.toList());
            List<Run> fromTo = from.stream().filter(it ->
                            LocalDateTime.parse(it.getFinishDatetime(), formatter).isBefore(LocalDateTime.parse(toDatetime, formatter)))
                            .collect(Collectors.toList());
            return fromTo;
        } else {
            return "Runner with id " + userId + " does not exist";
        }
    }

    public Object getAllRunsOfRunnerFrom(String userId, String fromDatetime) {
        boolean runnerExists = runnerRepository.existsById(userId);
        if (runnerExists) {
            List<Run> allRuns = runsRepository.findAll();
            List<Run> userRuns = allRuns.stream().filter(it -> it.getUserId().equals(userId)).collect(Collectors.toList());
            List<Run> from = userRuns.stream().filter(it ->
                            LocalDateTime.parse(it.getStartDatetime(), formatter).isAfter(LocalDateTime.parse(fromDatetime, formatter)))
                    .collect(Collectors.toList());
            return from;
        } else {
            return "Runner with id " + userId + " does not exist";
        }
    }

    public Object getAllRunsOfRunnerTo(String userId, String toDatetime) {
        boolean runnerExists = runnerRepository.existsById(userId);
        if (runnerExists) {
            List<Run> allRuns = runsRepository.findAll();
            List<Run> userRuns = allRuns.stream().filter(it -> it.getUserId().equals(userId)).collect(Collectors.toList());
            List<Run> to = userRuns.stream().filter(it ->
                            LocalDateTime.parse(it.getFinishDatetime(), formatter).isBefore(LocalDateTime.parse(toDatetime, formatter)))
                    .collect(Collectors.toList());
            return to;
        } else {
            return "Runner with id " + userId + " does not exist";
        }
    }

    private long calculateTimeInS(LocalDateTime startDateTime, LocalDateTime finishDateTime) {
        long result = 0;
        long startDays = startDateTime.getDayOfYear();
        long finishDays = finishDateTime.getDayOfYear();
        long difDays = finishDays - startDays;

        long startHours = startDateTime.getHour();
        long finishHours = finishDateTime.getHour();
        long difHours = finishHours < startHours ? (finishHours + 24 - startHours) :  (finishHours - startHours);

        long startMins = startDateTime.getMinute();
        long finishMins = finishDateTime.getMinute();
        long difMins = finishMins < startHours ? (finishMins + 60 - startMins) :  (finishMins - startMins);;

        long startSecs = startDateTime.getSecond();
        long finishSecs = finishDateTime.getSecond();
        long difSecs = finishSecs < startSecs ? (finishSecs + 60 - startSecs) :  (finishSecs - startSecs);;

        result += difDays * 24 * 60 * 60;
        result += difHours * 60 * 60;
        result += difMins * 60;
        result += difSecs;
        return result;
    }

    private Run getRun(String runId)  {
        Iterable<Run> runners = runsRepository.findAllById(Collections.singleton(runId));
        return runners.iterator().next();
    }

    private boolean checkDatetime (String datetime) {
        try {
            LocalDateTime dateTime = LocalDateTime.parse(datetime, formatter);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

}
