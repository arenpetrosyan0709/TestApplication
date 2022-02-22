package com.itc.demo.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.itc.demo.services.RunnerService;
import com.itc.demo.services.RunsService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/application/api")
public class ApiController {
    private final RunsService runsServise;
    private final RunnerService runnerService;

    public ApiController(RunsService runsServise, RunnerService runnerService) {
        this.runsServise = runsServise;
        this.runnerService = runnerService;
    }
    @GetMapping(value = "/test")
    public Object test() {
        return "TEST OK: ";
    }

    @GetMapping (value = "/addrunner")
    public String addRunner(@RequestParam (value = "first_name") String first_name,
                          @RequestParam (value = "last_name") String last_name,
                          @RequestParam (value = "birth_date") String birth_date,
                          @RequestParam (value = "sex") String sex) {
        return runnerService.add(first_name, last_name, birth_date, sex);
    }
    @GetMapping (value = "/getrunner")
    public String getRunner(@RequestParam (value = "userId") String userId) throws JsonProcessingException {
        return runnerService.getRunner(userId);
    }

    @GetMapping (value = "/getrunners")
    public Object getRunners() throws JsonProcessingException {
        return runnerService.getAllRunners().toString();
    }

    @PostMapping (value = "/editrunner")
    public String editRunner (@RequestBody() JsonNode body) {
        return runnerService.edit(body.get("userId").asText(), body.get("firstName").asText(), body.get("lastName").asText(), body.get("birthDate").asText(), body.get("sex").asText());
    }

    @PostMapping (value = "/start")
    public String startRun (@RequestBody JsonNode body) {
        return runsServise.runStarted(body.get("userId").asText(), body.get("startLatitude").asDouble(), body.get("startLongitude").asDouble(), body.get("startDatetime").asText());
    }

    @PostMapping (value = "/finish")
    public String finishRun (@RequestBody JsonNode body) {
        return runsServise.runFinished(body.get("runId").asText(), body.get("userId").asText(), body.get("finishLatitude").asDouble(), body.get("finishLongitude").asDouble(), body.get("finishDatetime").asText(), body.get("distance").asLong());
    }
}

