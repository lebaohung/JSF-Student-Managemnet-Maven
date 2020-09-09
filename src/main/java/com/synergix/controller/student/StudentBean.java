package com.synergix.controller.student;

import com.synergix.controller.IBean;
import com.synergix.controller.IPaging;
import com.synergix.model.Student;
import com.synergix.repository.Student.StudentRepo;

import javax.enterprise.context.Conversation;
import javax.enterprise.context.ConversationScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Named
@ConversationScoped
public class StudentBean implements Serializable, IBean<Student>, IPaging<Student> {

    private static final int INIT_PAGE = 1;
    private static final int PAGE_SIZE = 5;

    private int page = INIT_PAGE;
    private int pageSize = PAGE_SIZE;
    private int pageCount;
    private List<Integer> selectStudentList = new ArrayList<>();
    private Map<Integer, Boolean> selectStudentMap = new HashMap<>();

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
        this.pageCount = (int) Math.ceil(this.count() / (double) pageSize);
        return pageCount;
    }

    public void setPageCount(int pageCount) {
        this.pageCount = pageCount;
    }

    public List<Integer> getSelectStudentList() {
        return this.selectStudentMap.entrySet().stream()
                .filter(Map.Entry::getValue)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    public void setSelectStudentList(List<Integer> selectStudentList) {
        this.selectStudentList = selectStudentList;
    }

    public Map<Integer, Boolean> getSelectStudentMap() {
        return selectStudentMap;
    }

    public void setSelectStudentMap(Map<Integer, Boolean> selectStudentMap) {
        this.selectStudentMap = selectStudentMap;
    }

    public void deleteSelectStudents() {
        for (Integer studentId : this.getSelectStudentList()) {
            studentRepo.delete(studentId);
        }
        this.selectStudentMap.clear();
    }

    @Inject
    private Conversation conversation;

    @Inject
    private StudentRepo studentRepo;

    public void initConversation() {
        try {
            conversation.begin();
        } catch (Exception e) {
            System.out.println("Warning! Long-running conversation running!");
        }
    }

    public void endConversation() {
        try {
            conversation.end();
        } catch (Exception e) {
            System.out.println("Warning! Transient conversation cannot end!");
        }
    }

    @Override
    public String moveToListPage() {
        this.cancelAdd();
        this.cancelEdit();
        this.endConversation();
        this.initConversation();
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
        sessionMap.put("newStudent", new Student());
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
        if (student != null) {
            studentRepo.save(student);
        }
        this.cancelAdd();
    }

    @Override
    public void cancelAdd() {
        Map<String, Object> sessionMap = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
        sessionMap.put("newStudent", null);
    }

    @Override
    public void update(Student student) {
        if (student != null) {
            studentRepo.update(student);
        }
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
