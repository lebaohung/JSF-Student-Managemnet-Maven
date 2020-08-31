package com.synergix.validator;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

@FacesValidator
public class PhoneValidator implements Validator {
    public static final int ACCEPTABLE_PHONE_LENGTH = 10;
    public static final String PHONE_PREFIX  = "0";

    @Override
    public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
        String phone = value.toString();
        if (!phone.isEmpty()) {
            if (!phone.startsWith(PHONE_PREFIX)) {
                FacesMessage facesMessage = new FacesMessage("  Phone starts with 0. Please enter again!");
                facesMessage.setSeverity(FacesMessage.SEVERITY_ERROR);
                throw new ValidatorException(facesMessage);
            }
            if (phone.length() != ACCEPTABLE_PHONE_LENGTH) {
                FacesMessage facesMessage = new FacesMessage("  Phone length is 10. Please enter again!");
                facesMessage.setSeverity(FacesMessage.SEVERITY_ERROR);
                throw new ValidatorException(facesMessage);
            }
        }
    }
}
