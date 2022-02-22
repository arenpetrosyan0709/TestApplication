package com.itc.demo.entities;

import org.springframework.data.annotation.Id;

public class Runner {

    @Id
    private String id;
    private String first_name;
    private String last_name;
    private String birth_date;
    private String sex;

    public Runner(String firstName, String lastName, String birthDate, String sex) {
        this.first_name = firstName;
        this.last_name = lastName;
        this.birth_date = birthDate;
        this.sex = sex;
    }

    public Runner() {
    }

    public String getId() {
        return id;
    }

    public String getFirstName() {
        return first_name;
    }

    public void setFirstName(String first_name) {
        this.first_name = first_name;
    }

    public String getLastName() {
        return last_name;
    }

    public void setLastName(String last_name) {
        this.last_name = last_name;
    }

    public String getBirthDate() {
        return birth_date;
    }

    public void setBirthDate(String birth_date) {
        this.birth_date = birth_date;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

}
