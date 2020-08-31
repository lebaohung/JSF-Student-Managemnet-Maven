package com.synergix.model;

import java.io.Serializable;

public class Student implements Serializable {

    private int id;
    private String sName;
    private String email;
    private String phone;
    private int sClassId;

    public Student() {
    }

    public Student(String sName, String email, String phone, int sClassId) {
        this.sName = sName;
        this.email = email;
        this.phone = phone;
        this.sClassId = sClassId;
    }

    public Student(int id, String sName, String email, String phone, int sClassId) {
        this.id = id;
        this.sName = sName;
        this.email = email;
        this.phone = phone;
        this.sClassId = sClassId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getsName() {
        return sName;
    }

    public void setsName(String sName) {
        this.sName = sName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getsClassId() {
        return sClassId;
    }

    public void setsClassId(int sClassId) {
        this.sClassId = sClassId;
    }
}
