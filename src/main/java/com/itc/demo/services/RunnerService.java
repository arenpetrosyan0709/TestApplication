package com.itc.demo.services;

import com.itc.demo.entities.Runner;
import com.itc.demo.repository.RunnerRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

@Service
public class RunnerService {
    static final LocalDate MIN_BIRTH_DAY;
    static final LocalDate MAX_BIRTH_DAY;
    static final DateTimeFormatter dFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    static final String [] symbols = {"!","@","#","$","%","^","*","(",")","№",",",".","_","+","-","=","|","/","<",">","?",":",";", "1", "2", "3", "4", "5", "6", "7", "8", "9", "0"};

    static {
        MIN_BIRTH_DAY = LocalDate.now().minusYears(118);
        MAX_BIRTH_DAY =  LocalDate.now().minusYears(18);
    }

    private static RunnerRepository runnerRepository;

    public RunnerService(RunnerRepository runnerRepository) {
        RunnerService.runnerRepository = runnerRepository;
    }

    public static String add(String firstName, String lastName, String birthDate, String sex) {
        String correctFirstName;
        String correctLastName;
        String correctBirthDate;
        String correctSex;

        if (firstName==null || lastName==null || birthDate==null || sex==null) {
            return "Empty fields are not allowed.";
        }
        if (firstName.equals("") || lastName.equals("") || birthDate.equals("") || sex.equals("")) {
            return "Empty fields are not allowed.";
        }
        if (checkInputName(firstName)) {
            correctFirstName = firstName;
        } else {
            return "Incorrect First Name.";
        }
        if (checkInputName(lastName)) {
            correctLastName = lastName;
        } else {
            return "Incorrect Last Name.";
        }
        if (checkInputBirthDate(birthDate)) {
            correctBirthDate = birthDate;
        } else {
            return "Incorrect Birth Date.";
        }

        if (sex.equalsIgnoreCase("m") || sex.equalsIgnoreCase("male")) {
            correctSex = "male";
        } else if (sex.equalsIgnoreCase("f") || sex.equalsIgnoreCase("female")) {
            correctSex = "female";
        } else {
            return "Incorrect Sex.";
        }

        Runner runner = new Runner(correctFirstName, correctLastName, correctBirthDate, correctSex);
        runnerRepository.save(runner);
        return "Success. Runner with id " + runner.getId() + " has been created.";
    }

    public static Object getRunner (String userId) {
        boolean exists = runnerRepository.existsById(userId);
        if (exists) {
            Iterable<Runner> runners = runnerRepository.findAllById(Collections.singleton(userId));
            Runner runner = runners.iterator().next();
            return createRunnerData(runner);

        } else {
            return "User with id " + userId + " was not found.";
        }
    }

    public static Object getAllRunners()  {
        List<HashMap<String, Object>> result = new ArrayList<>();
        Iterable<Runner> runners = runnerRepository.findAll();
        for (Runner runner : runners) {
            result.add(createRunnerData(runner));
        }
        if (!result.isEmpty()) {
            return result;
        } else {
            return "There are no runners in database yet.";
        }
    }

    public static String delete(String userId) {
        boolean exists = runnerRepository.existsById(userId);
        if (exists) {
            Iterable<Runner> runners = runnerRepository.findAllById(Collections.singleton(userId));
            Runner runner = runners.iterator().next();
            runnerRepository.delete(runner);
            return "Success. Runner " + runner.getFirstName() + " " + runner.getLastName() + " has been deleted.";
        } else {
            return "User with id " + userId + " was not found";
        }
    }

    public static String edit(String userId, String firstName, String lastName, String birthDate, String sex) {
        boolean exists = runnerRepository.existsById(userId);
        if (exists) {
            boolean firstNameToEdit = false;
            boolean lastNameToEdit = false;
            boolean birthDateToEdit = false;
            boolean sexToEdit = false;

            Iterable<Runner> runners = runnerRepository.findAllById(Collections.singleton(userId));
            Runner runner = runners.iterator().next();

            String correctFirstName = runner.getFirstName();
            String correctLastName = runner.getLastName();
            String correctBirthDate = runner.getBirthDate();
            String correctSex = runner.getSex();

            if ((firstName != "") && (firstName != null) && !firstName.equals("null")) {
                if (checkInputName(firstName)) {
                    correctFirstName = firstName;
                    firstNameToEdit = true;
                } else {
                    return "Incorrect First Name.";
                }
            }
            if ((lastName != "") && (lastName != null) && !lastName.equals("null")) {
                if (checkInputName(lastName)) {
                    correctLastName = lastName;
                    lastNameToEdit = true;
                } else {
                    return "Incorrect Last Name.";
                }
            }
            if ((birthDate != "") && (birthDate != null) && !birthDate.equals("null")) {
                if (checkInputBirthDate(birthDate)) {
                    correctBirthDate = birthDate;
                    birthDateToEdit = true;
                } else {
                    return "Incorrect Birth Date.";
                }
            }

            if ((sex != "") && (sex != null) && !sex.equals("null")) {
                if (sex.equalsIgnoreCase("m") || sex.equalsIgnoreCase("male")) {
                    correctSex = "male";
                    sexToEdit = true;
                } else if (sex.equalsIgnoreCase("f") || sex.equalsIgnoreCase("female")) {
                    correctSex = "female";
                    sexToEdit = true;
                } else {
                    return "Incorrect Sex.";
                }
            }

            if (firstNameToEdit) {
                runner.setFirstName(correctFirstName);
            }
            if (lastNameToEdit) {
                runner.setLastName(correctLastName);
            }
            if (birthDateToEdit) {
                runner.setBirthDate(correctBirthDate);
            }
            if (sexToEdit) {
                runner.setSex(correctSex);
            }
            runnerRepository.save(runner);
            return "Success. Runner with id " + runner.getId() + " has been updated.";
        } else {
            return "User with id " + userId + " was not found";
        }
    }

    public static Object getRunnerStat(String userId) {
        boolean exists = runnerRepository.existsById(userId);
        Object result;
        if (exists) {
            Iterable<Runner> runners = runnerRepository.findAllById(Collections.singleton(userId));
            Runner runner = runners.iterator().next();
            Object allRunsOfRunner = RunsService.getAllRunsOfRunner(userId);
            result = createStatInfo(allRunsOfRunner, runner);
        } else {
            return "User with id " + userId + " was not found.";
        }
        return result;
    }

    public static Object getRunnerStatFromTo(String userId, String fromDatetime, String toDatetime) {
        boolean exists = runnerRepository.existsById(userId);
        Object result;
        if (exists) {
            Iterable<Runner> runners = runnerRepository.findAllById(Collections.singleton(userId));
            Runner runner = runners.iterator().next();
            Object allRunsOfRunner = RunsService.getAllRunsOfRunnerFromTo(userId, fromDatetime, toDatetime);
            result = createStatInfo(allRunsOfRunner, runner);
        } else {
            return "User with id " + userId + " was not found.";
        }
        return result;
    }

    public static Object getRunnerStatFrom(String userId, String fromDatetime) {
        boolean exists = runnerRepository.existsById(userId);
        Object result;
        if (exists) {
            Iterable<Runner> runners = runnerRepository.findAllById(Collections.singleton(userId));
            Runner runner = runners.iterator().next();
            Object allRunsOfRunner = RunsService.getAllRunsOfRunnerFrom(userId, fromDatetime);
            result = createStatInfo(allRunsOfRunner, runner);
        } else {
            return "User with id " + userId + " was not found.";
        }
        return result;
    }

    public static Object getRunnerStatTo(String userId, String toDatetime) {
        boolean exists = runnerRepository.existsById(userId);
        Object result;
        if (exists) {
            Iterable<Runner> runners = runnerRepository.findAllById(Collections.singleton(userId));
            Runner runner = runners.iterator().next();
            Object allRunsOfRunner = RunsService.getAllRunsOfRunnerTo(userId, toDatetime);
            result = createStatInfo(allRunsOfRunner, runner);
        } else {
            return "User with id " + userId + " was not found.";
        }
        return result;
    }

    private static Object createStatInfo(Object allRunsOfRunner, Runner runner) {
        HashMap<String, Object> result = new HashMap<>();
        if (allRunsOfRunner instanceof List) {
            if ( ((List<?>) allRunsOfRunner).get(0) instanceof HashMap) {
                List<HashMap> runs = (List<HashMap>) allRunsOfRunner;
                long numberOfRuns = runs.size();
                long fullDistance = 0;
                double avSpeed;
                double sumOfSpeeds = 0.0;
                String fullName = runner.getFirstName() + " " + runner.getLastName();
                for (HashMap run : runs) {
                    fullDistance += (long) run.get("distance");
                    sumOfSpeeds += (double) run.get("averageSpeed");
                }
                avSpeed = sumOfSpeeds / numberOfRuns;
                result.put("userId", runner.getId());
                result.put("fullName", fullName);
                result.put("numberOfRuns", numberOfRuns);
                result.put("fullDistance", fullDistance);
                result.put("averageSpeed", avSpeed);
            }
        } else if (allRunsOfRunner instanceof String) {
            return allRunsOfRunner;
        }
        return result;
    }

    private static boolean checkInputName (String name) {
        if (name != null ) {
            int symbolCounter = 0;
            for (String symbol : symbols) {
                if (name.contains(symbol)) {
                    symbolCounter++;
                }
            }
            return symbolCounter == 0;
        } else {
            return false;
        }
    }

    private static HashMap<String, Object> createRunnerData(Runner temp) {
        HashMap<String, Object> runnerData = new HashMap<>();
        runnerData.put("id", temp.getId());
        runnerData.put("firstName", temp.getFirstName());
        runnerData.put("lastName", temp.getLastName());
        runnerData.put("birthDate", temp.getBirthDate());
        runnerData.put("sex", temp.getSex());
        return runnerData;
    }

    private static boolean checkInputBirthDate (String birthDate) {
        LocalDate dateTime = LocalDate.parse(birthDate, dFormatter);
        return (dateTime.isAfter(MIN_BIRTH_DAY) && dateTime.isBefore(MAX_BIRTH_DAY));
    }
}
