package com.ptit.controller;

import com.ptit.controller.sclass.SClassBean;
import com.ptit.controller.student.StudentBean;

import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.Locale;

@Named
@SessionScoped
public class HomeBean implements Serializable {

    @Inject
    private StudentBean studentBean;

    @Inject
    private SClassBean sClassBean;

    private static final String SHOW_STUDENTS_MANAGEMENT = "showStudentsManagement";
    private static final String SHOW_CLASSES_MANAGEMENT = "showClassesManagement";
    private String navigateHomePage;

    public String getShowStudentsManagement() {
        return SHOW_STUDENTS_MANAGEMENT;
    }

    public String getShowClassesManagement() {
        return SHOW_CLASSES_MANAGEMENT;
    }

    public String getNavigateHomePage() {
        return navigateHomePage;
    }

    public void setNavigateHomePage(String navigateHomePage) {
        this.navigateHomePage = navigateHomePage;
    }

    private Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();

    public Locale getLocale() {
        return locale;
    }

    public String getLanguage() {
        return locale.getLanguage();
    }

    public void changeLanguage(String language) {
        locale = new Locale(language);
        FacesContext.getCurrentInstance().getViewRoot().setLocale(new Locale(language));
    }

    public void showStudentsManagement() {
        this.navigateHomePage = SHOW_STUDENTS_MANAGEMENT;
    }

    public void showClassesManagement() {
        this.navigateHomePage = SHOW_CLASSES_MANAGEMENT;
    }

    public void backToHomePage() {
        this.navigateHomePage = null;
    }
}
