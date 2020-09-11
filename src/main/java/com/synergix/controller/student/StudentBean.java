package com.synergix.controller.student;

import com.synergix.model.Student;
import com.synergix.repository.Student.StudentRepo;

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
public class StudentBean implements Serializable {

    private static final int INIT_PAGE = 1;
    private static final int PAGE_SIZE = 5;

    private int page = INIT_PAGE;
    private int pageSize = PAGE_SIZE;
    private int pageCount;
    private List<Integer> selectedStudentList = new ArrayList<>();
    private Map<Integer, Boolean> selectedStudentMap = new HashMap<>();
    private StringBuilder deleteExceptionMessage;

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getPageSize() {
        return pageSize;
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

    @Inject
    private Conversation conversation;

    @Inject
    private StudentRepo studentRepo;

    public void initConversation() {
        try {
            conversation.begin();
        } catch (IllegalStateException e) {
            System.out.println("Warning! Long-running conversation running!");
        }
    }

    public void endConversation() {
        try {
            conversation.end();
        } catch (IllegalStateException e) {
            System.out.println("Warning! Transient conversation cannot end!");
        }
    }

    public String moveToListPage() {
        this.cancelAdd();
        this.cancelEdit();
        this.endConversation();
        this.initConversation();
        conversation.setTimeout(36000000);
        return "/views/student/listStudent";
    }

    public void test() {
        System.out.print("map: ");
        System.out.println(this.getSelectedStudentMap());
    }

    public List<Student> getAll() {
        return studentRepo.getAll();
    }

    private List<Student> students = new ArrayList<>();

    public List<Student> getStudents() {
        return students;
    }

    public void setStudents(List<Student> students) {
        this.students = students;
    }

    public void getAllByPage() {
        this.students = studentRepo.getAllByPage(page, pageSize);
    }

    public void create() {
        Student newStudent = new Student();
        Map<String, Object> sessionMap = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
        sessionMap.put("newStudent", newStudent);
        this.getAllByPage();
        students.add(newStudent);
    }

    public void getEdit(Integer studentId) {
        Map<String, Object> sessionMap = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
        sessionMap.put("editStudent", studentRepo.getById(studentId));
    }

    public void cancelEdit() {
        Map<String, Object> sessionMap = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
        sessionMap.put("editStudent", null);
    }

    public void save(Student student) {
        if (student != null) {
            studentRepo.save(student);
        }
        this.cancelAdd();
    }

    public void cancelAdd() {
        Map<String, Object> sessionMap = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
        sessionMap.put("newStudent", null);
        this.getAllByPage();
    }

    public void update(Student student) {
        if (student != null) {
            studentRepo.update(student);
        }
        this.cancelEdit();
    }

    public void delete(Integer studentId) {
        return;
    }

    public int count() {
        return studentRepo.count();
    }

    public void next() {
        if (this.getPage() >= this.getPageCount()) return;
        else this.page++;
        this.cancelAdd();
        this.getAllByPage();
        this.selectedStudentMap.clear();
    }

    public void previous() {
        if (this.getPage() <= 1) return;
        else this.page--;
        this.cancelAdd();
        this.getAllByPage();
        this.selectedStudentMap.clear();
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
        this.getAllByPage();
        this.selectedStudentMap.clear();
    }

    public void selectAll() {
        for (Student student : this.getStudents()) {
            selectedStudentMap.put(student.getId(), true);
        }
    }

    public void unselectAll() {
        for (Student student : this.getStudents()) {
            selectedStudentMap.replace(student.getId(), false);
        }
    }
}
