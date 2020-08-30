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

    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Inject
    private SClassRepo sClassRepo;

    @Override
    public List<SClass> getAll() {
        return sClassRepo.getAll();
    }

    @Override
    public String create() {
        SClass newSClass = new SClass();
        Map<String, Object> sessionMap = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
        sessionMap.put("newSClass", newSClass);
        return "addSClass.xhtml";
    }

    @Override
    public String getById(Integer sClassId) {
        SClass editSClass = null;
        Map<String, Object> sessionMap = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
        editSClass = sClassRepo.getById(sClassId);
        sessionMap.put("editSClass", editSClass);
        return "editSClass.xhtml";
    }

    @Override
    public void save(SClass sClass) {
        sClassRepo.save(sClass);
        this.message = "Add new Class successfully";
    }

    @Override
    public void update(SClass sClass) {
        sClassRepo.update(sClass);
        this.message = "Edit Class ID " + sClass.getId() + " successfully";
    }

    @Override
    public void delete(Integer sclassId) {
        sClassRepo.delete(sclassId);
    }
}
