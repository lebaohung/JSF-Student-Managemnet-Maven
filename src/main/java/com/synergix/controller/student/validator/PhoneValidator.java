package com.synergix.controller.student.validator;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

@FacesValidator
public class PhoneValidator implements Validator {
    public static final String PHONE_REGEX = "^0\\d{9}$";

    @Override
    public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
        String phone = value.toString();
        if (!phone.isEmpty() && !phone.matches(PHONE_REGEX)) {
                FacesMessage facesMessage = new FacesMessage("  Phone length is 10, starts with 0. Please enter again!");
                facesMessage.setSeverity(FacesMessage.SEVERITY_ERROR);
                throw new ValidatorException(facesMessage);
        }
    }
}
