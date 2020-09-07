package com.synergix.validator;

import com.synergix.controller.student.StudentBean;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;
import javax.faces.view.facelets.Facelet;
import javax.inject.Inject;


@FacesValidator(value = "nameValidator", managed = true)
public class NameValidator implements Validator {
    public final static int MINIMUM_LENGTH = 2;

    @Inject
    private StudentBean studentBean;

    @Override
    public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
        String sName = value.toString();
        studentBean.test();
        if (sName.length() < MINIMUM_LENGTH) {
            FacesMessage facesMessage = new FacesMessage("  Minimum length is 2. Please enter again!");
            facesMessage.setSeverity(FacesMessage.SEVERITY_ERROR);
            throw new ValidatorException(facesMessage);
        }
    }
}
