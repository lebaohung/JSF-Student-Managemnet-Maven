package com.synergix.controller.sclass;

import com.synergix.model.SClass;
import com.synergix.model.Student;
import com.synergix.repository.SClass.SClassRepo;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Conversation;
import javax.enterprise.context.ConversationScoped;
import javax.faces.application.FacesMessage;
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

@Named(value = "sClassBean")
@ConversationScoped
public class SClassBean implements Serializable {

    @Inject
    private Conversation conversation;

    @Inject
    private SClassRepo sClassRepo;

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
    private List<Student> studentInClassList = new ArrayList<>();
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

    public List<Student> getStudentInClassList() {
        return studentInClassList;
    }

    public void setStudentInClassList(List<Student> studentInClassList) {
        this.studentInClassList = studentInClassList;
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

    public Map<Integer, Boolean> getSelectedSClassMap() {
        return selectedSClassMap;
    }

    public void setSelectedSClassMap(Map<Integer, Boolean> selectedSClassMap) {
        this.selectedSClassMap = selectedSClassMap;
    }

    public String moveToListPage() {
        this.cancelAdd();
        this.cancelEdit();
        this.endConversation();
        this.initConversation();
        return "/views/sclass/listSClass";
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

    public void getEdit(Integer sClassId) {
        Map<String, Object> sessionMap = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
        sessionMap.put("editSClass", sClassRepo.getById(sClassId));
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
        }
        this.cancelEdit();
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

    public void moveToDetailPage(SClass sClass, Integer id) {
        Map<String, Object> sessionMap = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
        sessionMap.put("editSClass", sClass);
        sessionMap.put("id", id);
        this.navigateSClassPage = DETAIL_PAGE;
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
                if (i == cannotDeleteSClassId.size() -1) this.setDeleteExceptionMessage(String.valueOf(i));
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
}
