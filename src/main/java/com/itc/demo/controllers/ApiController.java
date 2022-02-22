package com.itc.demo.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.itc.demo.services.RunnerService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/application/api")
public class ApiController {

    private final RunnerService runnerService;

    public ApiController(RunnerService runnerService) {
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
        String result = runnerService.add(first_name, last_name, birth_date, sex);
        return result;
    }
    @GetMapping (value = "/getrunner")
    public String getRunner(@RequestParam (value = "userId") String userId) throws JsonProcessingException {
        String result = runnerService.getRunner(userId);
        return result;
    }

    @GetMapping (value = "/getrunners")
    public Object getRunners() throws JsonProcessingException {
        Object result = runnerService.getAllRunners().toString();
        return result;
    }

    @PostMapping (value = "/editrunner")
    public String editRunner (@RequestBody() JsonNode body) {
        String result = runnerService.edit(body.get("userId").asText(), body.get("firstName").asText(), body.get("lastName").asText(), body.get("birthDate").asText(), body.get("sex").asText());
        return result;
    }
}

