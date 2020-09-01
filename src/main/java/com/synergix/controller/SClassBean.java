package com.synergix.controller;

import com.synergix.model.SClass;
import com.synergix.repository.SClass.SClassRepo;

import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;
import java.util.Map;

@Named(value = "sClassBean")
@RequestScoped
public class SClassBean implements IBean<SClass> {

    private boolean isAdd;

    public boolean isAdd() {
        return isAdd;
    }

    public void setAdd(boolean add) {
        isAdd = add;
    }

    @Inject
    private SClassRepo sClassRepo;

    @Override
    public String moveToListPage() {
        this.cancelEdit();
        return "/views/sclass/listSClass";
    }

    @Override
    public List<SClass> getAll() {
        return sClassRepo.getAll();
    }

    @Override
    public void create() {
        Map<String, Object> sessionMap = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
        sessionMap.put("newSClass", new SClass(null));
        this.setAdd(true);
    }

    @Override
    public void getEdit(Integer sClassId) {
        Map<String, Object> sessionMap = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
        sessionMap.put("editSClass", sClassRepo.getById(sClassId));
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
        this.setAdd(false);
    }

    public int countClassSize(Integer classId) {
        return sClassRepo.countClassSize(classId);
    }
}
