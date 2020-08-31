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

    private boolean isAdd;

    public boolean isAdd() {
        return isAdd;
    }

    public void setAdd(boolean add) {
        isAdd = add;
    }

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
        return "/views/student/listStudent";
    }

    public List<Student> getAll() {
        return studentRepo.getAll();
    }

    public void create() {
        this.setAdd(true);
        Map<String, Object> sessionMap = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
        sessionMap.put("newStudent", new Student("acac", "asdasd", "asdasd", 0));
    }

    public void getById(Integer studentId) {
        Map<String, Object> sessionMap = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
        sessionMap.put("editStudent", studentRepo.getById(studentId));
    }

    public void cancelEdit() {
        Map<String, Object> sessionMap = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
        sessionMap.put("editStudent", null);
    }

    public void save(Student student) {
        studentRepo.save(student);
        this.cancelEdit();
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
