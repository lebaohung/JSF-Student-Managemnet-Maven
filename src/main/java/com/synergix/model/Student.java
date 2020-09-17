package com.synergix.model;

import java.io.Serializable;
import java.util.Date;

public class Student implements Serializable {

    private Integer id;
    private String sName;
    private String email;
    private String phone;
    private Date birthday;

    public Student() {
    }

    public Student(String sName, String email, String phone) {
        this.sName = sName;
        this.email = email;
        this.phone = phone;
    }

    public Student(Integer id, String sName, String email, String phone, Date birthday) {
        this.id = id;
        this.sName = sName;
        this.email = email;
        this.phone = phone;
        this.birthday = birthday;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
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

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }
}
