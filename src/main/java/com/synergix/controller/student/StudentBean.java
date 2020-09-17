package com.synergix.controller.student;

import com.synergix.model.Student;
import com.synergix.repository.Student.StudentRepo;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Conversation;
import javax.enterprise.context.ConversationScoped;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

@Named
@ConversationScoped
public class StudentBean implements Serializable {

    private static final int INIT_PAGE = 1;
    private static final int PAGE_SIZE = 5;
    private final static String MANAGER_PAGE = "showManagerPage";
    private final static String DETAIL_PAGE = "showDetailPage";
    public final static int MINIMUM_LENGTH_NAME = 2;
    public final static String EMAIL_REGEX = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    public static final String PHONE_REGEX = "^0\\d{9}$";

    private String navigateStudentPage;
    private int page = INIT_PAGE;
    private int pageSize = PAGE_SIZE;
    private int pageCount;
    private List<Integer> selectedStudentList = new ArrayList<>();
    private Map<Integer, Boolean> selectedStudentMap = new HashMap<>();
    private List<Student> students = new ArrayList<>();
    private Student tempStudent;

    @Inject
    private Conversation conversation;

    @Inject
    private StudentRepo studentRepo;

    @PostConstruct
    public void initNavigator() {
        this.navigateStudentPage = MANAGER_PAGE;
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("editStudent", null);
    }

    public String getManagerPage() {
        return MANAGER_PAGE;
    }

    public String getDetailPage() {
        return DETAIL_PAGE;
    }

    public String getNavigateStudentPage() {
        return navigateStudentPage;
    }

    public void setNavigateStudentPage(String navigateStudentPage) {
        this.navigateStudentPage = navigateStudentPage;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public Student getTempStudent() {
        return tempStudent;
    }

    public void setTempStudent(Student tempStudent) {
        this.tempStudent = tempStudent;
    }

    public int getPageSize() {
        return pageSize;
    }

    public int getPageCount() {
        this.pageCount = (int) Math.ceil(this.count() / (double) pageSize);
        return pageCount;
    }

    public void setPageCount(int pageCount) {
        this.pageCount = pageCount;
    }

    public Map<Integer, Boolean> getSelectedStudentMap() {
        return selectedStudentMap;
    }

    public void setSelectedStudentMap(Map<Integer, Boolean> selectedStudentMap) {
        this.selectedStudentMap = selectedStudentMap;
    }

    public List<Student> getStudents() {
        return students;
    }

    public void setStudents(List<Student> students) {
        this.students = students;
    }

    public List<Integer> getSelectedStudentList() {
        this.selectedStudentList = this.getSelectedStudentMap().entrySet().stream()
                .filter(Map.Entry::getValue)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
        return selectedStudentList;
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
        this.endConversation();
        this.initConversation();
        this.students = studentRepo.getAllByPage(page, pageSize);
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

    public void validateName(FacesContext facesContext, UIComponent component, Object value) throws ValidatorException {
        String sName = value.toString();
        if (sName.length() < MINIMUM_LENGTH_NAME) {
            FacesMessage facesMessage = new FacesMessage("Minimum name length is x. Please enter again!");
            facesMessage.setSeverity(FacesMessage.SEVERITY_ERROR);
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("newStudent", null);
            throw new ValidatorException(facesMessage);
        }
    }

    public void validateEmail(FacesContext context, UIComponent component, Object value) throws ValidatorException {
        String email = value.toString();
        if (!email.isEmpty() && !email.matches(EMAIL_REGEX)) {
            FacesMessage facesMessage = new FacesMessage("  Invalid email! Please enter email again!");
            facesMessage.setSeverity(FacesMessage.SEVERITY_ERROR);
            throw new ValidatorException(facesMessage);
        }
    }

    public void validatePhone(FacesContext context, UIComponent component, Object value) throws ValidatorException {
        String phone = value.toString();
        if (!phone.isEmpty() && !phone.matches(PHONE_REGEX)) {
            FacesMessage facesMessage = new FacesMessage("Phone length is 10, starts with 0. Please enter again!");
            facesMessage.setSeverity(FacesMessage.SEVERITY_ERROR);
            throw new ValidatorException(facesMessage);
        }
    }

    public void create() {
        tempStudent = new Student();
        this.getAllByPage();
        students.add(tempStudent);
    }

    public void cancelAdd() {
        this.getAllByPage();
    }

    public void save(Student student) {
        if (student != null) {
            studentRepo.save(student);
            FacesMessage savedNotificationMsg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Save new student successfully", null);
            FacesContext.getCurrentInstance().addMessage("studentsList", savedNotificationMsg);
        }
        this.cancelAdd();
    }

    public void deleteSelectedStudent() {
        for (Integer studentId : this.getSelectedStudentList()) {
            studentRepo.delete(studentId);
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

    public void moveToDetailPage(Student student) {
        Map<String, Object> sessionMap = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
        sessionMap.put("editStudent", student);
        this.navigateStudentPage = DETAIL_PAGE;
    }

    public void cancelEdit() {
        Map<String, Object> sessionMap = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
        sessionMap.put("editStudent", null);
    }

    public void update(Student student) {
        if (student != null) {
            studentRepo.update(student);
            FacesMessage updatedNotificationMsg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Saved at " + new Date(), null);
            FacesContext.getCurrentInstance().addMessage("studentDetail", updatedNotificationMsg);
        }
    }
}
