package com.synergix.controller;

import com.synergix.controller.sclass.SClassBean;
import com.synergix.controller.student.StudentBean;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Conversation;
import javax.enterprise.context.ConversationScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.Locale;

@Named
@ConversationScoped
public class HomeBean implements Serializable {

    @Inject
    private Conversation conversation;

    @Inject
    private StudentBean studentBean;

    @Inject
    private SClassBean sClassBean;

    @PostConstruct
    public void initConversation() {
        conversation.begin();
    }

    private final static String SHOW_STUDENTS_MANAGEMENT = "showStudentsManagement";
    private final static String SHOW_CLASSES_MANAGEMENT = "showClassesManagement";
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
        System.out.println("show student manager");
        studentBean.cancelAdd();
        studentBean.cancelEdit();
        studentBean.endConversation();
        studentBean.initConversation();
        studentBean.getAllByPage();
        this.navigateHomePage = SHOW_STUDENTS_MANAGEMENT;
    }

    public void showClassesManagement() {
        System.out.println("show class manager");
        sClassBean.cancelEdit();
        sClassBean.cancelAdd();
        sClassBean.endConversation();
        sClassBean.initConversation();
        sClassBean.getAllByPage();
        this.navigateHomePage = SHOW_CLASSES_MANAGEMENT;
    }

    public void backToHomePage() {
        this.navigateHomePage = null;
    }
}
