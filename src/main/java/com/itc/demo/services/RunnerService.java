package com.itc.demo.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itc.demo.entities.Runner;
import com.itc.demo.repository.RunnerRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class RunnerService {
    static final LocalDate MIN_BIRTH_DAY;
    static final LocalDate MAX_BIRTH_DAY;
    static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    static final String [] symbols = {"!","@","#","$","%","^","*","(",")","№",",",".","_","+","-","=","|","/","<",">","?",":",";", "1", "2", "3", "4", "5", "6", "7", "8", "9", "0"};

    static {
        MIN_BIRTH_DAY = LocalDate.now().minusYears(118);
        MAX_BIRTH_DAY =  LocalDate.now().minusYears(18);
    }
    private final RunnerRepository runnerRepository;

    public RunnerService(RunnerRepository runnerRepository) {
        this.runnerRepository = runnerRepository;
    }


    public String add(String firstName, String lastName, String birthDate, String sex) {
        String correctFirstName;
        String correctLastName;
        String correctBirthDate;
        String correctSex;

        if (firstName==null || lastName==null || birthDate==null || sex==null) {
            return "Empty fields are not allowed";
        }
        if (firstName.equals("") || lastName.equals("") || birthDate.equals("") || sex.equals("")) {
            return "Empty fields are not allowed";
        }
        if (checkInputName(firstName)) {
            correctFirstName = firstName;
        } else {
            return "Incorrect First Name";
        }
        if (checkInputName(lastName)) {
            correctLastName = lastName;
        } else {
            return "Incorrect Last Name";
        }
        if (checkInputBirthDate(birthDate)) {
            correctBirthDate = birthDate;
        } else {
            return "Incorrect Birth Date";
        }

        if (sex.equalsIgnoreCase("m") || sex.equalsIgnoreCase("male")) {
            correctSex = "male";
        } else if (sex.equalsIgnoreCase("f") || sex.equalsIgnoreCase("female")) {
            correctSex = "female";
        } else {
            return "Incorrect Sex";
        }

        Runner runner = new Runner(correctFirstName, correctLastName, correctBirthDate, correctSex);
        runnerRepository.save(runner);
        return "Success. Runner with id " + runner.getId() + " has been created";
    }

    public String getRunner (String userId) throws JsonProcessingException {
        Iterable<Runner> runners = runnerRepository.findAllById(Collections.singleton(userId));
        Runner runner = runners.iterator().next();
        ObjectMapper mapper = new ObjectMapper();
        String result = mapper.writeValueAsString(runner);
        return result;
    }

    public List<Object> getAllRunners() throws JsonProcessingException {
        List result = new ArrayList();
        Iterable<Runner> runners = runnerRepository.findAll();
        for (Object runner : runners) {
            ObjectMapper mapper = new ObjectMapper();
            String runnerData = mapper.writeValueAsString(runner);
            result.add(runnerData);
        }
        return result;
    }

    public String edit(String userId, String firstName, String lastName, String birthDate, String sex) {
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
                return "Incorrect First Name";
            }
        }
        if ((lastName != "") && (lastName != null) && !lastName.equals("null")) {
            if (checkInputName(lastName)) {
                correctLastName = lastName;
                lastNameToEdit = true;
            } else {
                return "Incorrect Last Name";
            }
        }
        if ((birthDate !="") && (birthDate != null) && !birthDate.equals("null")) {
            if (checkInputBirthDate(birthDate)) {
                correctBirthDate = birthDate;
                birthDateToEdit = true;
            } else {
                return "Incorrect Birth Date";
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
                return "Incorrect Sex";
            }
        }

        if (firstNameToEdit) { runner.setFirstName(correctFirstName); }
        if (lastNameToEdit) { runner.setLastName(correctLastName); }
        if (birthDateToEdit) { runner.setBirthDate(correctBirthDate); }
        if (sexToEdit) { runner.setSex(correctSex); }
        runnerRepository.save(runner);
        return "Success. Runner with id " + runner.getId() + " has been updated";
    }

    private boolean checkInputName (String name) {
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

    private boolean checkInputBirthDate (String birthDate) {
        LocalDate dateTime = LocalDate.parse(birthDate, formatter);
        return (dateTime.isAfter(MIN_BIRTH_DAY) && dateTime.isBefore(MAX_BIRTH_DAY));
    }
}
