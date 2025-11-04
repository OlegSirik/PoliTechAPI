package ru.pt.api.dto.errors;

public class ValidationError {

    private String code;
    private String reason;
    private String path;

    public ValidationError(String code, String reason, String path) {
        this.code = code;
        this.reason = reason;
        this.path = path;
    }

    public ValidationError() {
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
