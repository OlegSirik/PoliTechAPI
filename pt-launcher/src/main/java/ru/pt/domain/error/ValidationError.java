package ru.pt.domain.error;

import java.util.List;

public class ValidationError {

    private String validator;   
    private String errorText;

    public ValidationError(String validator, String errorText) {
        this.validator = validator;
        this.errorText = errorText;
    }

    public String getValidator() { return validator; }
    public void setValidator(String validator) { this.validator = validator; }
    public String getErrorText() { return errorText; }
    public void setErrorText(String errorText) { this.errorText = errorText; }
}