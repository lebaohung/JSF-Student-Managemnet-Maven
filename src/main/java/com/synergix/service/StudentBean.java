package com.synergix.service;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.List;

@Named
@RequestScoped
public class StudentBean implements Serializable {

    private int id;
    private String sname;
    private String email;
    private String phone;

    private String message;

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    @Inject
    private StudentService studentService;

    public StudentBean() {
    }

    public StudentBean(int id, String sname, String email, String phone) {
        this.id = id;
        this.sname = sname;
        this.email = email;
        this.phone = phone;
    }

    public StudentBean(String sname, String email, String phone) {
        this.sname = sname;
        this.email = email;
        this.phone = phone;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSname() {
        return sname;
    }

    public void setSname(String sname) {
        this.sname = sname;
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

    public List<StudentBean> getStudentsList() {
        return studentService.getStudentsList();
    }

    public void saveStudent(StudentBean studentBean) {
        studentService.saveStudent(studentBean);
    }
}
