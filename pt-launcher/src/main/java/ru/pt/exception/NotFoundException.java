package ru.pt.exception;

import ru.pt.domain.error.ErrorModel;

public class NotFoundException extends RuntimeException {
    private final ErrorModel errorModel;

    public NotFoundException(ErrorModel errorModel) {
        super(errorModel.getMessage());
        this.errorModel = errorModel;
    }

    public NotFoundException(String message) {
        super(message);
        this.errorModel = new ErrorModel(404, message);
    }

    public NotFoundException(String message, Throwable cause) {
        super(message, cause);
        this.errorModel = new ErrorModel(404, message);
    }

    public ErrorModel getErrorModel() {
        return errorModel;
    }
}
