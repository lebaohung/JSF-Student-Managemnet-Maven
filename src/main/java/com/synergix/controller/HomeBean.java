package com.synergix.controller;

import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import java.io.Serializable;
import java.util.Locale;

@Named
@SessionScoped
public class HomeBean implements Serializable {

    private final static String SHOW_STUDENTS_MANAGEMENT = "showStudentsManagement";
    private final static String SHOW_CLASSES_MANAGEMENT = "showClassesManagement";
    private String navigateToPage;

    public String getShowStudentsManagement() {
        return SHOW_STUDENTS_MANAGEMENT;
    }

    public String getShowClassesManagement() {
        return SHOW_CLASSES_MANAGEMENT;
    }

    public String getNavigateToPage() {
        return navigateToPage;
    }

    public void setNavigateToPage(String navigateToPage) {
        this.navigateToPage = navigateToPage;
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
        this.navigateToPage = SHOW_STUDENTS_MANAGEMENT;
    }

    public void showClassesManagement() {
        this.navigateToPage = SHOW_CLASSES_MANAGEMENT;
    }

    public void backToHomePage() {
        this.navigateToPage = null;
    }
}
