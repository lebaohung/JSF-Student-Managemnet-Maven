package com.synergix.service;

import com.synergix.model.Student;
import com.synergix.repository.Student.StudentRepo;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;

@Named
@RequestScoped
public class StudentService {

    @Inject
    private StudentRepo studentRepo;

    private String message;

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public List<Student> getStudentsList() {
        return studentRepo.getAll();
    }

    public void saveStudent(Student student) {
        studentRepo.save(student);
        this.setMessage(studentRepo.getMessage());
    }

    public void updateEditedStudent(Student student) {
        studentRepo.update(student);
        this.setMessage(studentRepo.getMessage());
    }

    public void deleteStudent(Integer studentId) {
        studentRepo.delete(studentId);
    }
}
