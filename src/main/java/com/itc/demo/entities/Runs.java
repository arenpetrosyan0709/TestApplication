package com.itc.demo.entities;

import org.springframework.data.annotation.Id;

import java.time.OffsetDateTime;

public class Runs {
    @Id
    private Long id;
    private Long userId;
    private double start_latitude;
    private double start_longitude;
    private OffsetDateTime start_datetime;
    private double finish_latitude;
    private double finish_longitude;
    private OffsetDateTime finish_datetime;
    private Long distance;

    public Runs(Long id, Long userId, double start_latitude, double start_longitude, OffsetDateTime start_datetime, double finish_latitude, double finish_longitude, OffsetDateTime finish_datetime, Long distance) {
        this.id = id;
        this.userId = userId;
        this.start_latitude = start_latitude;
        this.start_longitude = start_longitude;
        this.start_datetime = start_datetime;
        this.finish_latitude = finish_latitude;
        this.finish_longitude = finish_longitude;
        this.finish_datetime = finish_datetime;
        this.distance = distance;
    }
}
