package com.itc.demo.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.itc.demo.services.RunnerService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/application/runner")
public class RunnerController {
    private final RunnerService runnerService;

    public RunnerController(RunnerService runnerService) {
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
    public Object getRunner(@RequestParam (value = "userId") String userId)  {
        return runnerService.getRunner(userId);
    }

    @GetMapping (value = "/getrunners")
    public Object getRunners()  {
        return runnerService.getAllRunners();
    }

    @PostMapping (value = "/editrunner")
    public String editRunner (@RequestBody() JsonNode body) {
        return runnerService.edit(body.get("userId").asText(), body.get("firstName").asText(), body.get("lastName").asText(), body.get("birthDate").asText(), body.get("sex").asText());
    }

    @GetMapping (value = "/deleterunner")
    public String deleteRunner(@RequestParam (value = "userId") String userId) {
        return runnerService.delete(userId);
    }

    @GetMapping (value = "/getrunnerstat")
    public Object getUserRunsFrom(@RequestParam (value = "userId") String userId)  {
        return runnerService.getRunnerStat(userId);
    }

    @GetMapping (value = "/getrunnerstatfromto")
    public Object getUserRunsFromTo(@RequestParam (value = "userId") String userId,
                                    @RequestParam (value = "fromDatetime") String fromDatetime,
                                    @RequestParam (value = "toDatetime") String toDatetime) {
        return runnerService.getRunnerStatFromTo(userId, fromDatetime, toDatetime);
    }

    @GetMapping (value = "/getrunnerstatfrom")
    public Object getUserRunsFrom(@RequestParam (value = "userId") String userId,
                                  @RequestParam (value = "fromDatetime") String fromDatetime)  {
        return runnerService.getRunnerStatFrom(userId, fromDatetime);
    }

    @GetMapping (value = "/getrunnerstatto")
    public Object getUserRunsTo(@RequestParam (value = "userId") String userId,
                                @RequestParam (value = "toDatetime") String toDatetime)  {
        return runnerService.getRunnerStatTo(userId, toDatetime);
    }

}

