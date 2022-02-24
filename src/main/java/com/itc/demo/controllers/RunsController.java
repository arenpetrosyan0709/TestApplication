package com.itc.demo.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.itc.demo.services.RunsService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/application/runs")
public class RunsController {
    private final RunsService runsServise;

    public RunsController(RunsService runsServise) {
        this.runsServise = runsServise;
    }
    @GetMapping(value = "/test")
    public Object test() {
        return "TEST OK: ";
    }

    @PostMapping (value = "/start")
    public String startRun (@RequestBody JsonNode body) {
        return runsServise.runStarted(body.get("userId").asText(), body.get("startLatitude").asDouble(), body.get("startLongitude").asDouble(), body.get("startDatetime").asText());
    }

    @PostMapping (value = "/finish")
    public String finishRun (@RequestBody JsonNode body) {
        return runsServise.runFinished(body.get("runId").asText(), body.get("userId").asText(), body.get("finishLatitude").asDouble(), body.get("finishLongitude").asDouble(), body.get("finishDatetime").asText(), body.get("distance").asLong());
    }

    @GetMapping (value = "/getallruns")
    public Object getUserRuns(@RequestParam (value = "userId") String userId) {
        return runsServise.getAllRunsOfRunner(userId);
    }

    @GetMapping (value = "/getallrunsfromto")
    public Object getUserRunsFromTo(@RequestParam (value = "userId") String userId,
                                    @RequestParam (value = "fromDatetime") String fromDatetime,
                                    @RequestParam (value = "toDatetime") String toDatetime) {
        return runsServise.getAllRunsOfRunnerFromTo(userId, fromDatetime, toDatetime) ;
    }

    @GetMapping (value = "/getallrunsfrom")
    public Object getUserRunsFrom(@RequestParam (value = "userId") String userId,
                                    @RequestParam (value = "fromDatetime") String fromDatetime)  {
        return runsServise.getAllRunsOfRunnerFrom(userId, fromDatetime);
    }

    @GetMapping (value = "/getallrunsto")
    public Object getUserRunsTo(@RequestParam (value = "userId") String userId,
                                  @RequestParam (value = "toDatetime") String toDatetime)  {
        return runsServise.getAllRunsOfRunnerTo(userId, toDatetime);
    }

}

