package com.itc.demo.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itc.demo.entities.Run;
import com.itc.demo.repository.RunnerRepository;
import com.itc.demo.repository.RunsRepository;
import com.itc.demo.utils.HaversineAlgorithm;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class RunsService {

    private final RunnerRepository runnerRepository;
    private final RunsRepository runsRepository;
    private static final DateTimeFormatter dtFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
    private static final DateTimeFormatter dFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

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

                LocalDateTime startDateTime = LocalDateTime.parse(run.getStartDatetime(), dtFormatter);
                LocalDateTime finishDateTime = LocalDateTime.parse(finishDatetime, dtFormatter);
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
                double averageSpeed = (double) distance/ (double) time;
                double scale = Math.pow(10, 3);
                averageSpeed =  Math.ceil(averageSpeed * scale) / scale;
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

    public Object getAllRunsOfRunner(String userId) throws JsonProcessingException {
        boolean runnerExists = runnerRepository.existsById(userId);
        List<String> result = new ArrayList();
        if (runnerExists) {
            Iterable<Run> runs = runsRepository.findAll();
            for (Object run : runs) {
                ObjectMapper mapper = new ObjectMapper();
                String runData = mapper.writeValueAsString(run);
                result.add(runData);
            }
            return result;
        } else {
            return "Runner with id " + userId + " does not exist";
        }
    }

    public Object getAllRunsOfRunnerFromTo(String userId, String fromDatetime, String toDatetime) throws JsonProcessingException {
        boolean runnerExists = runnerRepository.existsById(userId);
        List<String> result = new ArrayList();
        LocalDate from = LocalDate.parse(fromDatetime, dFormatter);
        LocalDate to = LocalDate.parse(toDatetime, dFormatter);
        if (runnerExists) {
            Iterable<Run> runs = runsRepository.findAll();
            List<Run> runsOfUser = new ArrayList<>();
            while (runs.iterator().hasNext()) {
                if (runs.iterator().next().getUserId().equals(userId)) {
                    runsOfUser.add(runs.iterator().next());
                }
            }
            for (Run run : runsOfUser) {
                if (LocalDate.parse(run.getStartDatetime(), dtFormatter).isAfter(from) && LocalDate.parse(run.getFinishDatetime(), dtFormatter).isBefore(to)) {
                    ObjectMapper mapper = new ObjectMapper();
                    String runData = mapper.writeValueAsString(run);
                    result.add(runData);
                }
            }
            return result;
        } else {
            return "Runner with id " + userId + " does not exist";
        }
    }

    public Object getAllRunsOfRunnerFrom(String userId, String fromDatetime) throws JsonProcessingException {
        boolean runnerExists = runnerRepository.existsById(userId);
        List<String> result = new ArrayList();
        LocalDateTime from = LocalDateTime.parse(fromDatetime, dFormatter);
        if (runnerExists) {
            Iterable<Run> runs = runsRepository.findAll();
            while (runs.iterator().hasNext()) {
                if (runs.iterator().next().getUserId().equals(userId)
                        && LocalDateTime.parse(runs.iterator().next().getStartDatetime(), dtFormatter).isAfter(from)) {
                    ObjectMapper mapper = new ObjectMapper();
                    String runData = mapper.writeValueAsString(runs.iterator().next());
                    result.add(runData);
                }
            }
            return result;
        }
        return "Runner with id " + userId + " does not exist";

    }

    public Object getAllRunsOfRunnerTo(String userId, String toDatetime) throws JsonProcessingException {
        boolean runnerExists = runnerRepository.existsById(userId);
        List<String> result = new ArrayList();
        LocalDateTime to = LocalDateTime.parse(toDatetime, dFormatter);
        if (runnerExists) {
            Iterable<Run> runs = runsRepository.findAll();
            while (runs.iterator().hasNext()) {
                if (runs.iterator().next().getUserId().equals(userId)
                        && LocalDateTime.parse(runs.iterator().next().getFinishDatetime(), dtFormatter).isBefore(to)) {
                    ObjectMapper mapper = new ObjectMapper();
                    String runData = mapper.writeValueAsString(runs.iterator().next());
                    result.add(runData);
                }
            }
            return result;
        } else {
            return "Runner with id " + userId + " does not exist";
        }
    }

    private long calculateTimeInS( LocalDateTime startDateTime, LocalDateTime finishDateTime) {
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
            LocalDateTime dateTime = LocalDateTime.parse(datetime, dtFormatter);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

}
