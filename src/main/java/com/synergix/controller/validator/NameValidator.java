package com.synergix.controller.validator;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

@FacesValidator
public class NameValidator implements Validator {
    public final static int MINIMUM_LENGTH = 2;

    @Override
    public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
        String sName = value.toString();
        if (sName.length() < MINIMUM_LENGTH) {
            FacesMessage facesMessage = new FacesMessage("  Minimum length is 2. Please enter again!");
            facesMessage.setSeverity(FacesMessage.SEVERITY_ERROR);
            throw new ValidatorException(facesMessage);
        }
    }
}
