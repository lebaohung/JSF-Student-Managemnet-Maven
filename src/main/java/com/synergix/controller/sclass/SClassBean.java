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
    private List<Integer> selectedStudentIdList = new ArrayList<>();
    private List<Integer> studentsIdInClassList = new ArrayList<>();
    private List<Student> studentsInClassList = new ArrayList<>();
    private List<SClass> classes = new ArrayList<>();

    @PostConstruct
    public void initNavigator() {
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("newStudent", null);
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

    public List<SClass> getClasses() {
        return classes;
    }

    public void setClasses(List<SClass> classes) {
        this.classes = classes;
    }

    public List<Integer> getSelectedSClassList() {
        this.selectedSClassList = this.getSelectedSClassMap().entrySet()
                .stream()
                .filter(Map.Entry::getValue)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
        return selectedSClassList;
    }

    public List<Integer> getSelectedStudentIdList() {
        this.selectedStudentIdList = this.getSelectedStudentMap().entrySet()
                .stream()
                .filter(Map.Entry::getValue)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
        return selectedStudentIdList;
    }

    public List<Integer> getStudentsIdInClassList() {
        return studentsIdInClassList;
    }

    public void setStudentsIdInClassList(List<Integer> studentsIdInClassList) {
        this.studentsIdInClassList = studentsIdInClassList;
    }

    public void setSelectedStudentIdList(List<Integer> selectedStudentIdList) {
        this.selectedStudentIdList = selectedStudentIdList;
    }

    public List<Student> getStudentsInClassList() {
        return studentsInClassList;
    }

    public void setStudentsInClassList(List<Student> studentsInClassList) {
        this.studentsInClassList = studentsInClassList;
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

    public void createStudent() {
        Student student = new Student();
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("newStudent", student);
        this.studentsInClassList.add(student);
    }

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
            FacesContext.getCurrentInstance().addMessage("sclassList", facesMessage);
        }
        this.cancelAdd();
    }

    public void saveStudent(Integer sclassId, Integer studentId) {
        if (true) {
            sClassRepo.saveStudentIntoClass(sclassId, studentId);
            FacesMessage facesMessage = new FacesMessage(FacesMessage.SEVERITY_INFO, "Save new student into class successfully", null);
            FacesContext.getCurrentInstance().addMessage("studentInClass", facesMessage);
        } else {
            FacesMessage facesMessage = new FacesMessage(FacesMessage.SEVERITY_INFO, "Student ID " + studentId + "not in this class", null);
            FacesContext.getCurrentInstance().addMessage("studentInClass", facesMessage);
        }
        this.cancelAddStudent(sclassId);
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

    public void cancelAddStudent(Integer sclassId) {
        Map<String, Object> sessionMap = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
        sessionMap.put("newStudent", null);
        this.getSClassStudentsList(sclassId);
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
        this.studentsInClassList.clear();
        selectedStudentMap.clear();
        this.getSClassStudentsList(sClass.getId());
        this.navigateSClassPage = DETAIL_PAGE;
    }

    public void updateSClassMentor(Integer sClassId, Integer studentId) {
        List<Integer> studentIdList = new ArrayList<>();
        studentIdList = sClassRepo.getStudentsIdByClassId(sClassId);
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
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Not found student ID " + studentId + " in class", null));
        }
    }

    public void updateStudent(Integer sClassId, Integer studentId) {
        List<Integer> studentIdList = new ArrayList<>();
        List<Integer> studentIdInClassList = new ArrayList<>();
        studentIdList = studentRepo.getAllStudentsId();
        studentIdInClassList = sClassRepo.getStudentsIdByClassId(sClassId);
        if (studentIdInClassList.contains(studentId)) {
            FacesContext.getCurrentInstance().addMessage("studentInClass", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Student " + studentId + " existed in class", null));
        } else if (studentIdList.contains(studentId)) {
            sClassRepo.saveStudentIntoClass(sClassId, studentId);
            FacesContext.getCurrentInstance().addMessage("studentInClass", new FacesMessage(FacesMessage.SEVERITY_INFO, "Update student at " + new Date(), null));
        } else {
            FacesContext.getCurrentInstance().addMessage("studentInClass", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Not found student ID " + studentId + " in class", null));
        }
        this.cancelAddStudent(sClassId);
    }

    public void deleteSelectedSClass() {
        for (Integer sClassId : this.getSelectedSClassList()) {
            sClassRepo.delete(sClassId);
        }
        this.getAllByPage();
        this.selectedSClassMap.clear();
    }

    public void deleteSelectedStudent(SClass sClass) {
        for (Integer studentId : this.getSelectedStudentIdList()) {
            sClassRepo.deleteStudentInClass(sClass.getId(), studentId);
        }
        this.moveToDetailPage(sClass);
        this.selectedStudentMap.clear();
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
        for (Integer studentId : studentsIdInClassList) {
            selectedStudentMap.put(studentId, true);
        }
    }

    public void unselectAllStudents() {
        for (Integer studentId : studentsIdInClassList) {
            selectedStudentMap.put(studentId, false);
        }
    }

    public void getSClassStudentsList(Integer sClassId) {
        studentsInClassList.clear();
        studentsIdInClassList = sClassRepo.getStudentsIdByClassId(sClassId);
        for (Integer studentId : studentsIdInClassList) {
            try {
                studentsInClassList.add(studentRepo.getById(studentId));
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        }
    }
}
