package com.synergix.controller.student;

import com.synergix.controller.IBean;
import com.synergix.controller.IPaging;
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
public class StudentBean implements IBean<Student>, IPaging<Student> {

    private static final int INIT_PAGE = 1;
    private static final int PAGE_SIZE = 5;

    private int page = INIT_PAGE;
    private int pageSize = PAGE_SIZE;
    private int pageCount;

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getPageCount() {
        this.pageCount = (int) Math.ceil( this.count() / (double) pageSize );
        return pageCount;
    }

    public void setPageCount(int pageCount) {
        this.pageCount = pageCount;
    }

    @Inject
    private StudentRepo studentRepo;

    @Override
    public String moveToListPage() {
        this.cancelAdd();
        this.cancelEdit();
        return "/views/student/listStudent";
    }

    @Override
    public List<Student> getAll() {
        return studentRepo.getAll();
    }


    @Override
    public List<Student> getAllByPage() {
        return studentRepo.getAllByPage(page, pageSize);
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

    public int count() {
        return studentRepo.count();
    }

    @Override
    public void next() {
        if (this.getPage() >= this.getPageCount()) return;
        else this.page++;
    }

    @Override
    public void previous() {
        if (this.getPage() <= 1) return;
        else this.page--;
    }
}
