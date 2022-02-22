package com.itc.demo.entities;

import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;

public class Runs {
    @Id
    private Long id;
    private Long userId;
    private double start_latitude;
    private double start_longitude;
    private LocalDateTime start_datetime;
    private double finish_latitude;
    private double finish_longitude;
    private LocalDateTime finish_datetime;
    private Long distance;

    public Runs(Long id,
                Long userId,
                double startLatitude,
                double startLongitude,
                LocalDateTime startDatetime,
                double finishLatitude,
                double finishLongitude,
                LocalDateTime finishDatetime,
                Long distance) {
        this.id = id;
        this.userId = userId;
        this.start_latitude = startLatitude;
        this.start_longitude = startLongitude;
        this.start_datetime = startDatetime;
        this.finish_latitude = finishLatitude;
        this.finish_longitude = finishLongitude;
        this.finish_datetime = finishDatetime;
        this.distance = distance;
    }


}
