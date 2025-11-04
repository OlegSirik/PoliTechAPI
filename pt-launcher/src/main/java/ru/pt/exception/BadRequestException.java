package ru.pt.exception;

import ru.pt.domain.error.ErrorModel;

public class BadRequestException extends RuntimeException {
    private final ErrorModel errorModel;

    public BadRequestException(ErrorModel errorModel) {
        super(errorModel.getMessage());
        this.errorModel = errorModel;
    }

    public BadRequestException(String message) {
        super(message);
        this.errorModel = new ErrorModel(400, message);
    }

    public BadRequestException(String message, Throwable cause) {
        super(message, cause);
        this.errorModel = new ErrorModel(400, message);
    }

    public ErrorModel getErrorModel() {
        return errorModel;
    }
}
