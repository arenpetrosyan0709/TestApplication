package com.itc.demo.services;

import com.itc.demo.entities.Run;
import com.itc.demo.entities.Runner;
import com.itc.demo.repository.RunnerRepository;
import com.itc.demo.repository.RunsRepository;
import com.itc.demo.utils.HaversineAlgorithm;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

@Service
public class RunsService {

    private static RunnerRepository runnerRepository;
    private static RunsRepository runsRepository;

    private static final DateTimeFormatter dtFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    public RunsService(RunnerRepository runnerRepository, RunsRepository runsRepository, RunnerService runnerService) {
        this.runnerRepository = runnerRepository;
        this.runsRepository = runsRepository;
    }

    public static String runStarted (String userId, double startLatitude, double startLongitude, String startDatetime) {
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

    public static String runFinished (String runId, String userId, double finishLatitude, double finishLongitude, String finishDatetime, Long distance) {
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

    public static Object getAllRunsOfRunner(String userId) {
        boolean runnerExists = runnerRepository.existsById(userId);
        List<HashMap> result = new ArrayList();
        if (runnerExists) {
            Iterable<Run> runs = runsRepository.findAll();
            for (Run run : runs) {
                result.add(createRunData(run));
            }
            if (!result.isEmpty()) {
                return result;
            } else {
                Iterable<Runner> runners = runnerRepository.findAllById(Collections.singleton(userId));
                Runner runner = runners.iterator().next();
                return "Runs for Runner " + runner.getFirstName() + " " + runner.getLastName() + " does not exist yet";
            }
        } else {
            return "Runner with id " + userId + " does not exist";
        }
    }

    public static Object getAllRunsOfRunnerFromTo(String userId, String fromDatetime, String toDatetime) {
        boolean runnerExists = runnerRepository.existsById(userId);
        List<HashMap> result = new ArrayList();
        if (runnerExists) {
            Iterable<Run> runs = runsRepository.findAll();
            for (Run temp : runs) {
                LocalDateTime start;
                LocalDateTime finish;
                try {
                    start = LocalDateTime.parse(fromDatetime, dtFormatter);
                    finish = LocalDateTime.parse(toDatetime, dtFormatter);
                } catch (DateTimeParseException e) {
                    return "Incorrect Datetime format";
                }
                if (temp.getUserId().equals(userId)
                        && LocalDateTime.parse(temp.getStartDatetime(), dtFormatter).isAfter(start)
                        && LocalDateTime.parse(temp.getFinishDatetime(), dtFormatter).isBefore(finish)) {
                    result.add(createRunData(temp));
                }
            }
            if (!result.isEmpty()) {
                return result;
            } else {
                Iterable<Runner> runners = runnerRepository.findAllById(Collections.singleton(userId));
                Runner runner = runners.iterator().next();
                return "Runs for Runner " + runner.getFirstName() + " " + runner.getLastName() + " in period from " + fromDatetime + " to " + toDatetime + " does not exist";
            }
        } else {
            return "Runner with id " + userId + " does not exist";
        }
    }

    public static Object getAllRunsOfRunnerFrom(String userId, String fromDatetime) {
        boolean runnerExists = runnerRepository.existsById(userId);
        List<HashMap> result = new ArrayList();
        if (runnerExists) {
            Iterable<Run> runs = runsRepository.findAll();
            for (Run temp : runs) {
                LocalDateTime start;
                try {
                    start = LocalDateTime.parse(fromDatetime, dtFormatter);
                } catch (DateTimeParseException e) {
                    return "Incorrect Datetime format";
                }
                if (temp.getUserId().equals(userId)
                        && LocalDateTime.parse(temp.getStartDatetime(), dtFormatter).isAfter(start)) {
                    result.add(createRunData(temp));
                }
            }
            if (!result.isEmpty()) {
                return result;
            } else {
                Iterable<Runner> runners = runnerRepository.findAllById(Collections.singleton(userId));
                Runner runner = runners.iterator().next();
                return "Runs for Runner " + runner.getFirstName() + " " + runner.getLastName() + " after " + fromDatetime + " does not exist";
            }
        } else {
            return "Runner with id " + userId + " does not exist";
        }}

    public static Object getAllRunsOfRunnerTo(String userId, String toDatetime) {
        boolean runnerExists = runnerRepository.existsById(userId);
        List<HashMap> result = new ArrayList();
        if (runnerExists) {
            Iterable<Run> runs = runsRepository.findAll();
            for (Run temp : runs) {
                LocalDateTime finish;
                try {
                    finish = LocalDateTime.parse(toDatetime, dtFormatter);
                } catch (DateTimeParseException e) {
                    return "Incorrect Datetime format";
                }
                if (temp.getUserId().equals(userId)
                        && LocalDateTime.parse(temp.getFinishDatetime(), dtFormatter).isBefore(finish)) {
                    result.add(createRunData(temp));
                }
            }
            if (!result.isEmpty()) {
                return result;
            } else {
                Iterable<Runner> runners = runnerRepository.findAllById(Collections.singleton(userId));
                Runner runner = runners.iterator().next();
                return "Runs for Runner " + runner.getFirstName() + " " + runner.getLastName() + " before " + toDatetime + " does not exist";
            }
        } else {
            return "Runner with id " + userId + " does not exist";
        }
    }

    private static long calculateTimeInS(LocalDateTime startDateTime, LocalDateTime finishDateTime) {
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

    private static Run getRun(String runId)  {
        Iterable<Run> runners = runsRepository.findAllById(Collections.singleton(runId));
        return runners.iterator().next();
    }

    private static HashMap<String, Object> createRunData(Run temp) {
        HashMap<String, Object> runData = new HashMap<>();
        runData.put("id", temp.getId());
        runData.put("userId", temp.getUserId());
        runData.put("startDatetime", temp.getStartDatetime());
        runData.put("startLatitude", temp.getStartLatitude());
        runData.put("startLongitude", temp.getStartLongitude());
        runData.put("finishDatetime", temp.getFinishDatetime());
        runData.put("finishLatitude", temp.getFinishLatitude());
        runData.put("finishLongitude", temp.getFinishLongitude());
        runData.put("distance", temp.getDistance());
        runData.put("averageSpeed", temp.getAverageSpeed());
        return runData;
    }

    private static boolean checkDatetime(String datetime) {
        try {
            LocalDateTime dateTime = LocalDateTime.parse(datetime, dtFormatter);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

}
