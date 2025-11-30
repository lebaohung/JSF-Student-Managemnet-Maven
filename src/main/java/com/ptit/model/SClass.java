package com.ptit.model;

import java.io.Serializable;

public class SClass implements Serializable {

    private Integer id;
    private String name;
    private Student monitor;

    public SClass() {
    }

    public SClass(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public SClass(Integer id, String name, Student monitor) {
        this.id = id;
        this.name = name;
        this.monitor = monitor;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Student getMonitor() {
        return monitor;
    }

    public void setMonitor(Student monitor) {
        this.monitor = monitor;
    }
}
