package com.itc.demo.entities;

import org.springframework.data.annotation.Id;


public class Run {
    @Id
    private String id;
    private String userId;
    private double start_latitude;
    private double start_longitude;
    private String start_datetime;
    private double finish_latitude;
    private double finish_longitude;
    private String finish_datetime;
    private Long distance;
    private boolean finished;

    public Run(String id,
                String userId,
                double startLatitude,
                double startLongitude,
                String startDatetime,
                double finishLatitude,
                double finishLongitude,
                String finishDatetime,
                Long distance
                ) {
        this.id = id;
        this.userId = userId;
        this.start_latitude = startLatitude;
        this.start_longitude = startLongitude;
        this.start_datetime = startDatetime;
        this.finish_latitude = finishLatitude;
        this.finish_longitude = finishLongitude;
        this.finish_datetime = finishDatetime;
        this.distance = distance;
        this.finished = finish_datetime != null && !finish_datetime.equals("") && !finish_datetime.equals("null");
    }

    public Run(){
    }

    public Run(String userId, double startLatitude, double startLongitude, String startDatetime) {
        this.userId = userId;
        this.start_latitude = startLatitude;
        this.start_longitude = startLongitude;
        this.start_datetime = startDatetime;
    }

    public String getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public double getStartLatitude() {
        return start_latitude;
    }

    public void setStartLatitude(double start_latitude) {
        this.start_latitude = start_latitude;
    }

    public double getStartLongitude() {
        return start_longitude;
    }

    public void setStartLongitude(double start_longitude) {
        this.start_longitude = start_longitude;
    }

    public String getStartDatetime() {
        return start_datetime;
    }

    public void setStartDatetime(String start_datetime) {
        this.start_datetime = start_datetime;
    }

    public double getFinishLatitude() {
        return finish_latitude;
    }

    public void setFinishLatitude(double finish_latitude) {
        this.finish_latitude = finish_latitude;
    }

    public double getFinishLongitude() {
        return finish_longitude;
    }

    public void setFinishLongitude(double finish_longitude) {
        this.finish_longitude = finish_longitude;
    }

    public String getFinishDatetime() {
        return finish_datetime;
    }

    public void setFinishDatetime(String finish_datetime) {
        this.finish_datetime = finish_datetime;
    }

    public Long getDistance() {
        return distance;
    }

    public void setDistance(Long distance) {
        this.distance = distance;
    }

    public boolean isFinished() {
        return finished;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }
}
