package com.synergix.controller;

import com.synergix.model.Student;
import com.synergix.repository.Student.StudentRepo;

import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;
import java.util.Map;

@Named
@RequestScoped
public class StudentBean implements IBean<Student>{

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

    public List<Student> getAll() {
        return studentRepo.getAll();
    }

    public String create() {
        Student newStudent = new Student();
        Map<String, Object> sessionMap = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
        sessionMap.put("newStudent", newStudent);
        return "addStudent.xhtml";
    }

    public String getById(Integer studentId) {
                Student editStudent = null;
        Map<String, Object> sessionMap = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
        editStudent = studentRepo.getById(studentId);
        sessionMap.put("editStudent", editStudent);
        return "editStudent.xhtml";
    }

    public void save(Student student) {
        studentRepo.save(student);
        this.message = "Add new Student successfully";
    }

    public void update(Student student) {
        studentRepo.update(student);
        this.message = "Edit Student ID " + student.getId() + " successfully";
    }

    public void delete(Integer studentId) {
        studentRepo.delete(studentId);
    }
}
