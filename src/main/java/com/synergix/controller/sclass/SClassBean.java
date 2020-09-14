package com.synergix.controller.sclass;

import com.synergix.model.SClass;
import com.synergix.model.Student;
import com.synergix.repository.SClass.SClassRepo;
import com.synergix.repository.Student.StudentRepo;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Conversation;
import javax.enterprise.context.ConversationScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@Named(value = "sClassBean")
@ConversationScoped
public class SClassBean implements Serializable {

    @Inject
    private Conversation conversation;

    @Inject
    private SClassRepo sClassRepo;

    @Inject
    private StudentRepo studentRepo;

    private static final int INIT_PAGE = 1;
    private static final int PAGE_SIZE = 5;
    private int page = INIT_PAGE;
    private int pageSize = PAGE_SIZE;
    private int pageCount;
    private String navigateSClassPage;
    private final static String MANAGER_PAGE = "showManagerPage";
    private final static String DETAIL_PAGE = "showDetailPage";
    private List<Integer> selectedSClassList = new ArrayList<>();
    private Map<Integer, Boolean> selectedSClassMap = new HashMap<>();
    private Map<Integer, Boolean> selectedStudentMap = new HashMap<>();
    private List<Integer> studentInClassList = new ArrayList<>();
    private StringBuilder deleteExceptionMessage;

    @PostConstruct
    public void initNavigator() {
        this.navigateSClassPage = MANAGER_PAGE;
    }

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

    public String getManagerPage() {
        return MANAGER_PAGE;
    }

    public String getDetailPage() {
        return DETAIL_PAGE;
    }

    public String getNavigateSClassPage() {
        return navigateSClassPage;
    }

    public void setNavigateSClassPage(String navigateSClassPage) {
        this.navigateSClassPage = navigateSClassPage;
    }

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

    private List<SClass> classes = new ArrayList<>();

    public List<SClass> getClasses() {
        return classes;
    }

    public void setClasses(List<SClass> classes) {
        this.classes = classes;
    }

    public StringBuilder getDeleteExceptionMessage() {
        return deleteExceptionMessage;
    }

    public void setDeleteExceptionMessage(String s) {
        this.deleteExceptionMessage.append(s);
    }

    public List<Integer> getSelectedSClassList() {
        this.selectedSClassList = this.getSelectedSClassMap().entrySet().stream()
                .filter(Map.Entry::getValue)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
        return selectedSClassList;
    }

    public List<Integer> getStudentInClassList() {
        this.studentInClassList = this.getSelectedStudentMap().entrySet()
                .stream()
                .filter(Map.Entry::getValue)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
        return studentInClassList;
    }

    public void setStudentInClassList(List<Integer> studentInClassList) {
        this.studentInClassList = studentInClassList;
    }

    public Map<Integer, Boolean> getSelectedSClassMap() {
        return selectedSClassMap;
    }

    public void setSelectedSClassMap(Map<Integer, Boolean> selectedSClassMap) {
        this.selectedSClassMap = selectedSClassMap;
    }

    public Map<Integer, Boolean> getSelectedStudentMap() {
        return selectedStudentMap;
    }

    public void setSelectedStudentMap(Map<Integer, Boolean> selectedStudentMap) {
        this.selectedStudentMap = selectedStudentMap;
    }

    public List<SClass> getAll() {
        return sClassRepo.getAll();
    }

    public void getAllByPage() {
        this.classes = sClassRepo.getAllByPage(page, pageSize);
    }

    public void create() {
        SClass newSClass = new SClass();
        Map<String, Object> sessionMap = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
        sessionMap.put("newSClass", newSClass);
        this.getAllByPage();
        classes.add(newSClass);
    }

    private SClass sClass = null;

    public void getSClassMentor(Integer mentorId) {
        Map<String, Object> sessionMap = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
        try {
            sessionMap.put("mentorName", studentRepo.getById(mentorId).getsName());
        } catch (SQLException throwables) {
            FacesContext.getCurrentInstance().addMessage("sClassDetail",
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Cannot find student ID:" + mentorId, null));
        }
    }

    public void save(SClass sClass) {
        if (sClass != null) {
            sClassRepo.save(sClass);
            FacesMessage facesMessage = new FacesMessage(FacesMessage.SEVERITY_INFO, "Save new class successfully", null);
            FacesContext.getCurrentInstance().addMessage("message", facesMessage);
        }
        this.cancelAdd();
    }

    public void update(SClass sClass) {
        if (sClass != null) {
            sClassRepo.update(sClass);
            FacesContext.getCurrentInstance().addMessage("sClassDetail", new FacesMessage(FacesMessage.SEVERITY_INFO, "Saved at " + new Date(), null));
        }
    }

    public void cancelEdit() {
        Map<String, Object> sessionMap = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
        sessionMap.put("editSClass", null);
    }

    public void cancelAdd() {
        Map<String, Object> sessionMap = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
        sessionMap.put("newSClass", null);
        this.getAllByPage();
    }

    public int countClassSize(Integer classId) {
        return sClassRepo.countClassSize(classId);
    }

    public int count() {
        return sClassRepo.count();
    }

    public void next() {
        if (this.getPage() >= this.getPageCount()) return;
        else this.page++;
        this.cancelAdd();
        this.getAllByPage();
        this.selectedSClassMap.clear();
    }

    public void previous() {
        if (this.getPage() <= 1) return;
        else this.page--;
        this.cancelAdd();
        this.getAllByPage();
        this.selectedSClassMap.clear();
    }

    public void moveToDetailPage(SClass sClass) {
        Integer mentorId = sClassRepo.getSClassMentorId(sClass.getId());
        Map<String, Object> sessionMap = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
        if (mentorId == null) {
            FacesContext.getCurrentInstance().addMessage("sClassDetail", new FacesMessage(FacesMessage.SEVERITY_INFO, "Class does not have mentor yet", null));
            sessionMap.put("mentorName", null);
        } else {
            try {
                sessionMap.put("mentorName", studentRepo.getById(mentorId).getsName());
            } catch (SQLException e) {
                FacesContext.getCurrentInstance().addMessage("sClassDetail",
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Cannot find student ID:" + mentorId, null));
            }
        }
        sessionMap.put("editSClass", sClass);
        sessionMap.put("mentorId", mentorId);
        this.navigateSClassPage = DETAIL_PAGE;
    }

    public void updateSClassMentor(Integer sClassId, Integer studentId) {
        List<Integer> studentIdList = new ArrayList<>();
        studentIdList = sClassRepo.getStudentsByClassId(sClassId);
        if (studentIdList.contains(studentId)) {
            sClassRepo.setSClassMentor(sClassId, studentId);
            FacesContext.getCurrentInstance().addMessage("sClassDetail", new FacesMessage(FacesMessage.SEVERITY_INFO, "Update mentor at " + new Date(), null));
            try {
                FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("mentorName", studentRepo.getById(studentId).getsName());
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        } else {
            FacesContext.getCurrentInstance().addMessage("sClassDetail",
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Not found studen ID " + studentId + " in class", null));
        }
    }

    public void deleteSelectedSClass() {
        List<Integer> cannotDeleteSClassId = new ArrayList<>();
        for (Integer sClassId : this.getSelectedSClassList()) {
            try {
                sClassRepo.delete(sClassId);
            } catch (SQLException e) {
                cannotDeleteSClassId.add(sClassId);
            }
        }
        if (!cannotDeleteSClassId.isEmpty()) {
            this.setDeleteExceptionMessage("Cannot delete Class ID: ");
            for (int i = 0; i < cannotDeleteSClassId.size(); i++) {
                if (i == cannotDeleteSClassId.size() - 1) this.setDeleteExceptionMessage(String.valueOf(i));
                else this.setDeleteExceptionMessage(i + ", ");
            }
        }
        this.getAllByPage();
        this.selectedSClassMap.clear();
    }

    public void selectAll() {
        for (SClass sClass : this.getClasses()) {
            selectedSClassMap.put(sClass.getId(), true);
        }
    }

    public void unselectAll() {
        for (SClass sClass : this.getClasses()) {
            selectedSClassMap.put(sClass.getId(), false);
        }
    }

    public void selectAllStudents() {
        for (Integer studentId : this.getStudentInClassList()) {
            selectedStudentMap.put(studentId, true);
        }
    }

    public void unselectAllStudents() {
        for (Integer studentId : this.getStudentInClassList()) {
            selectedStudentMap.put(studentId, false);
        }
    }

    public List<Student> getSClassStudentsList(Integer sClassId) {
        List<Integer> studentsIdList = new ArrayList<>();
        List<Student> studentList = new ArrayList<>();
        studentsIdList = sClassRepo.getStudentsByClassId(sClassId);
        for (Integer studentId : studentsIdList) {
            try {
                studentList.add(studentRepo.getById(studentId));
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        }
        return studentList;
    }
}
