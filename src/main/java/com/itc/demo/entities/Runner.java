package com.itc.demo.entities;

import org.springframework.data.annotation.Id;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

public class Runner {
    static final OffsetDateTime MIN_BIRTH_DAY;
    static final OffsetDateTime MAX_BIRTH_DAY;

    static {
        MIN_BIRTH_DAY = OffsetDateTime.now().minusYears(118);
        MAX_BIRTH_DAY =  OffsetDateTime.now().minusYears(18);
    }

    @Id
    private Long id;

    private String first_name;
    private String last_name;
    private String birth_date;
    private String sex;

    public Runner(String first_name, String last_name, String birth_date, String sex) {
        this.first_name = first_name;
        this.last_name = last_name;
        this.birth_date = birth_date;
        this.sex = sex;
    }

    public Runner() {

    }

    public Long getId() {
        return id;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getBirth_date() {
        return birth_date;
    }

    public void setBirth_date(String birth_date) {
        OffsetDateTime dateTime = OffsetDateTime.parse(birth_date) ;
        if (dateTime.isAfter(MIN_BIRTH_DAY) && dateTime.isBefore(MAX_BIRTH_DAY)) {
            this.birth_date = dateTime.toString();
        } else {

        }
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        if (sex.toLowerCase().equals("m") || sex.toLowerCase().equals("f")) {
            this.sex = sex;
        } else {

        }
    }
    public void StartRun(Long userId, double start_latitude, double start_longitude, OffsetDateTime start_datetime) {

    }

    public void FinishRun(Long userId, double finish_latitude, double finish_longitude, OffsetDateTime finish_datetime) {

    }

    public void FinishRun(Long userId, double finish_latitude, double finish_longitude, OffsetDateTime finish_datetime, Long distance) {


    }
    public List<Runs> getAllRuns(Long userId) {
        List runs = new ArrayList();
        return runs;
    }

    public List<Runs> getSortedRunsByTime(Long userId, OffsetDateTime from_datetime, OffsetDateTime to_datetime) {
        List runs = new ArrayList();
        return runs;
    }

}
