package ru.pt.exception;

import ru.pt.domain.error.ErrorModel;

public class ServiceUnavailableException extends RuntimeException {
    private final ErrorModel errorModel;

    public ServiceUnavailableException(ErrorModel errorModel) {
        super(errorModel.getMessage());
        this.errorModel = errorModel;
    }

    public ServiceUnavailableException(String message) {
        super(message);
        this.errorModel = new ErrorModel(503, message);
    }

    public ServiceUnavailableException(String message, Throwable cause) {
        super(message, cause);
        this.errorModel = new ErrorModel(503, message);
    }

    public ErrorModel getErrorModel() {
        return errorModel;
    }
}
