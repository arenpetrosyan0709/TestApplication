package com.itc.demo.services;

import com.itc.demo.entities.Runner;
import com.itc.demo.repository.RunnerRepository;
import org.springframework.stereotype.Service;

@Service
public class RunnerService {
    private final RunnerRepository runnerRepository;

    public RunnerService(RunnerRepository runnerRepository) {
        this.runnerRepository = runnerRepository;
    }

    public void add(String first_name, String last_name, String birth_date, String sex) {
        Runner runner = new Runner(first_name, last_name, birth_date, sex);
        runnerRepository.save(runner);

    }


}
