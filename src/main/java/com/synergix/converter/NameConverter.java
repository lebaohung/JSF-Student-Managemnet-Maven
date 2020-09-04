package com.synergix.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

@FacesConverter(value = "nameConverter")
public class NameConverter implements Converter {
    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        value = value.toLowerCase();
        StringBuilder value2 = new StringBuilder();
        Character ch = ' ';
        for (int i = 0; i < value.length(); i ++) {
            if (ch == ' ' && value.charAt(i) != ' ') {
                value2.append(Character.toUpperCase(value.charAt(i)));
            } else {
                value2.append(value.charAt(i));
            }
            ch = value.charAt(i);
        }
        return value2.toString();
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        return null;
    }
}
