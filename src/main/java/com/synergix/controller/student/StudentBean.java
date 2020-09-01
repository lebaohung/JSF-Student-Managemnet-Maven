package com.synergix.controller.student;

import com.synergix.controller.IBean;
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
public class StudentBean implements IBean<Student> {

    @Inject
    private StudentRepo studentRepo;

    @Override
    public String moveToListPage() {
        this.cancelEdit();
        return "/views/student/listStudent";
    }

    @Override
    public List<Student> getAll() {
        return studentRepo.getAll();
    }

    @Override
    public void create() {
        Map<String, Object> sessionMap = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
        sessionMap.put("newStudent", new Student(null, null, null, 0));
    }

    @Override
    public void getEdit(Integer studentId) {
        Map<String, Object> sessionMap = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
        sessionMap.put("editStudent", studentRepo.getById(studentId));
    }

    @Override
    public void cancelEdit() {
        Map<String, Object> sessionMap = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
        sessionMap.put("editStudent", null);
    }

    @Override
    public void save(Student student) {
        studentRepo.save(student);
        this.cancelAdd();
    }

    @Override
    public void cancelAdd() {
        Map<String, Object> sessionMap = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
        sessionMap.put("newStudent", null);
    }

    @Override
    public void update(Student student) {
        studentRepo.update(student);
        this.cancelEdit();
    }

    @Override
    public void delete(Integer studentId) {
        studentRepo.delete(studentId);
    }
}
