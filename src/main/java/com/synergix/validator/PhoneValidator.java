package com.synergix.validator;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

@FacesValidator
public class PhoneValidator implements Validator {

    @Override
    public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
        String phone = value.toString();
        if (phone.length() != 0) {
            if (!phone.startsWith("0")) {
                FacesMessage facesMessage = new FacesMessage("  Phone starts with 0. Please enter again!");
                facesMessage.setSeverity(FacesMessage.SEVERITY_ERROR);
                throw new ValidatorException(facesMessage);
            }
            if (phone.length() != 10) {
                FacesMessage facesMessage = new FacesMessage("  Phone length is 10. Please enter again!");
                facesMessage.setSeverity(FacesMessage.SEVERITY_ERROR);
                throw new ValidatorException(facesMessage);
            }
        }
    }
}
