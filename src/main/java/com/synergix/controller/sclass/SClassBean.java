package com.synergix.controller.sclass;

import com.synergix.controller.IBean;
import com.synergix.controller.IPaging;
import com.synergix.model.SClass;
import com.synergix.model.Student;
import com.synergix.repository.SClass.SClassRepo;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.Conversation;
import javax.enterprise.context.ConversationScoped;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Named(value = "sClassBean")
@ConversationScoped
public class SClassBean implements Serializable, IBean<SClass>, IPaging<SClass> {

    @Inject
    private Conversation conversation;

    @Inject
    private SClassRepo sClassRepo;

    private static final int INIT_PAGE = 1;
    private static final int PAGE_SIZE = 5;
    private int page = INIT_PAGE;
    private int pageSize = PAGE_SIZE;
    private int pageCount;
    private List<Student> studentInClassList = new ArrayList<>();

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

    public void initConversation() {
        if (conversation.isTransient()) {
            conversation.begin();
        }
    }

    @Override
    public String moveToListPage() {
        this.cancelAdd();
        this.cancelEdit();
        this.closeConversation();
        this.initConversation();
        return "/views/sclass/listSClass";
    }

    @Override
    public List<SClass> getAll() {
        return sClassRepo.getAll();
    }

    @Override
    public List<SClass> getAllByPage() {
        return sClassRepo.getAllByPage(page, pageSize);
    }

    @Override
    public void create() {
        Map<String, Object> sessionMap = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
        sessionMap.put("newSClass", new SClass());
    }

    private SClass sClass = null;

    @Override
    public void getEdit(Integer sClassId) {
        sClass = sClassRepo.getById(sClassId);
        Map<String, Object> sessionMap = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
        sessionMap.put("editSClass", sClass);
    }

    @Override
    public void save(SClass sClass) {
        sClassRepo.save(sClass);
        this.cancelAdd();
    }

    @Override
    public void update(SClass sClass) {
        sClassRepo.update(sClass);
        this.cancelEdit();
    }

    @Override
    public void delete(Integer sclassId) {
        sClassRepo.delete(sclassId);
    }

    @Override
    public void cancelEdit() {
        Map<String, Object> sessionMap = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
        sessionMap.put("editSClass", null);
    }

    @Override
    public void cancelAdd() {
        Map<String, Object> sessionMap = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
        sessionMap.put("newSClass", null);
    }

    public int countClassSize(Integer classId) {
        return sClassRepo.countClassSize(classId);
    }

    public int count() {
        return sClassRepo.count();
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

    public String moveToDetailPage(Integer sClassId) {
        this.setStudentInClassList(sClassRepo.getStudentsByClassId(sClassId));
        return "/views/sclass/classDetail";
    }

    public void closeConversation() {
        if (!conversation.isTransient()) conversation.end();
    }
}
