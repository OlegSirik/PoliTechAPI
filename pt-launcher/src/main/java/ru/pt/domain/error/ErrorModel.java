package ru.pt.domain.error;

import java.util.List;

public class ErrorModel {
    private int code;
    private String message;
    private List<ErrorDetail> errors;

    public ErrorModel() {
    }

    public ErrorModel(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public ErrorModel(int code, String message, List<ErrorDetail> errors) {
        this.code = code;
        this.message = message;
        this.errors = errors;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<ErrorDetail> getErrors() {
        return errors;
    }

    public void setErrors(List<ErrorDetail> errors) {
        this.errors = errors;
    }

    public static class ErrorDetail {
        private String domain;
        private String reason;
        private String message;
        private String field;

        public ErrorDetail() {
        }

        public ErrorDetail(String domain, String reason, String message, String field) {
            this.domain = domain;
            this.reason = reason;
            this.message = message;
            this.field = field;
        }

        public String getDomain() {
            return domain;
        }

        public void setDomain(String domain) {
            this.domain = domain;
        }

        public String getReason() {
            return reason;
        }

        public void setReason(String reason) {
            this.reason = reason;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getField() {
            return field;
        }

        public void setField(String field) {
            this.field = field;
        }
    }
}
