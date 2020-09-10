package com.synergix.controller.student;

import com.synergix.controller.IBean;
import com.synergix.controller.IPaging;
import com.synergix.model.Student;
import com.synergix.repository.Student.StudentRepo;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Conversation;
import javax.enterprise.context.ConversationScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.sql.SQLException;
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
    private static final String SHOW_STUDENTS_LIST = "showStudentsList";
    private static final String SHOW_EDIT_STUDENT = "showEditStudent";
    private static final String SHOW_ADD_STUDENT = "showAddStudent";

    private int page = INIT_PAGE;
    private int pageSize = PAGE_SIZE;
    private int pageCount;
    private List<Integer> selectedStudentList = new ArrayList<>();
    private Map<Integer, Boolean> selectedStudentMap = new HashMap<>();
    private StringBuilder deleteExceptionMessage;
    private Map<String, Boolean> navigationMap = new HashMap<>();

    public String getShowStudentsList() {
        return SHOW_STUDENTS_LIST;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public StringBuilder getDeleteExceptionMessage() {
        return deleteExceptionMessage;
    }

    public void setDeleteExceptionMessage(String s) {
        this.deleteExceptionMessage.append(s);
    }

    public int getPageCount() {
        this.pageCount = (int) Math.ceil(this.count() / (double) pageSize);
        return pageCount;
    }

    public void setPageCount(int pageCount) {
        this.pageCount = pageCount;
    }

    public List<Integer> getSelectedStudentList() {
        this.selectedStudentList = this.getSelectedStudentMap().entrySet().stream()
                .filter(Map.Entry::getValue)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
        return selectedStudentList;
    }

    public Map<Integer, Boolean> getSelectedStudentMap() {
        return selectedStudentMap;
    }

    public void setSelectedStudentMap(Map<Integer, Boolean> selectedStudentMap) {
        this.selectedStudentMap = selectedStudentMap;
    }

    public Map<String, Boolean> getNavigationMap() {
        return navigationMap;
    }

    public void setNavigationMap(Map<String, Boolean> navigationMap) {
        this.navigationMap = navigationMap;
    }

    @Inject
    private Conversation conversation;

    @Inject
    private StudentRepo studentRepo;

    @PostConstruct
    public void setNavigationRule() {
        navigationMap.put(SHOW_STUDENTS_LIST, false);
        navigationMap.put(SHOW_EDIT_STUDENT, false);
        navigationMap.put(SHOW_ADD_STUDENT, false);
    }

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
        conversation.setTimeout(36000000);
        return "/views/student/listStudent";
    }

    public void showManagement() {
        this.cancelAdd();
        this.cancelEdit();
        this.endConversation();
        this.initConversation();
        conversation.setTimeout(36000000);
        this.navigationMap.replace(SHOW_STUDENTS_LIST, !this.navigationMap.get(SHOW_STUDENTS_LIST));
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
        return;
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

    public void deleteSelectedStudent() {
        List<Integer> cannotDeleteStudentId = new ArrayList<>();
        for (Integer studentId : this.getSelectedStudentList()) {
            try {
                studentRepo.delete(studentId);
            } catch (SQLException e) {
                cannotDeleteStudentId.add(studentId);
            }
        }
        if (!cannotDeleteStudentId.isEmpty()) {
            this.setDeleteExceptionMessage("Cannot delete Student ID: ");
            for (int i = 0; i < cannotDeleteStudentId.size(); i++) {
                if (i == cannotDeleteStudentId.size() - 1) this.setDeleteExceptionMessage(String.valueOf(i));
                else this.setDeleteExceptionMessage(i + ", ");
            }
        }
        this.selectedStudentMap.clear();
    }
}
