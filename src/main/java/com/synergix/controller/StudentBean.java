package com.synergix.controller;

import com.synergix.model.Student;
import com.synergix.repository.Student.StudentRepo;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Named
@RequestScoped
public class StudentBean {

    private Student student = null;

    private List<Student> studentList = new ArrayList<>();

    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Inject
    private StudentRepo studentRepo;

    public StudentRepo getStudentRepo() {
        return studentRepo;
    }

    public void setStudentRepo(StudentRepo studentRepo) {
        this.studentRepo = studentRepo;
    }

    public String moveToListPage() {
        this.cancelEdit();
        studentList = studentRepo.getAll();
        Map<String, Object> sessionMap = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
        sessionMap.put("studentList", studentList);
        return "/views/student/listStudent";
    }

    public void create() {
        Student newStudent = new Student(null, null, null, 0);
        studentList.add(newStudent);
        Map<String, Object> sessionMap = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
        sessionMap.put("newStudent", newStudent);
//        return "addStudent.xhtml";
    }

    public void getById(Integer studentId) {
        Map<String, Object> sessionMap = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
        student = studentRepo.getById(studentId);
        sessionMap.put("editStudent", student);
    }

    public void cancelEdit() {
        Map<String, Object> sessionMap = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
        student = null;
        sessionMap.put("editStudent", student);
    }

    public void save(Student student) {
        studentRepo.save(student);
        this.message = "Add new Student successfully";
    }

    public void update(Student student) {
        studentRepo.update(student);
        this.cancelEdit();
        this.message = "Edit Student ID " + student.getId() + " successfully";
    }

    public void delete(Integer studentId) {
        studentRepo.delete(studentId);
    }
}
