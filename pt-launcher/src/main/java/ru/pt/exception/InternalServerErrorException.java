package ru.pt.exception;

import ru.pt.domain.error.ErrorModel;

public class InternalServerErrorException extends RuntimeException {
    private final ErrorModel errorModel;

    public InternalServerErrorException(ErrorModel errorModel) {
        super(errorModel.getMessage());
        this.errorModel = errorModel;
    }

    public InternalServerErrorException(String message) {
        super(message);
        this.errorModel = new ErrorModel(500, message);
    }

    public InternalServerErrorException(String message, Throwable cause) {
        super(message, cause);
        this.errorModel = new ErrorModel(500, message);
    }

    public ErrorModel getErrorModel() {
        return errorModel;
    }
}
