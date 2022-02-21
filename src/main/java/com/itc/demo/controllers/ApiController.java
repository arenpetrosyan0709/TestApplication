package com.itc.demo.controllers;

import com.itc.demo.services.RunnerService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ApiController {

    private final RunnerService runnerService;

    public ApiController(RunnerService runnerService) {
        this.runnerService = runnerService;
    }

    @GetMapping
    public void addRunner(@RequestParam (value = "first_name") String first_name,
                          @RequestParam (value = "last_name") String last_name,
                          @RequestParam (value = "birth_date") String birth_date,
                          @RequestParam (value = "sex") String sex) {
        runnerService.add(first_name, last_name, birth_date, sex);
    }
}

