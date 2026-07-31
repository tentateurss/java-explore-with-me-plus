package ru.practicum.main.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.main.dto.ApiError;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleNotFound(NotFoundException e) {
        log.warn("Not found: {}", e.getMessage());
        return new ApiError(HttpStatus.NOT_FOUND.getReasonPhrase(), "Object not found", e.getMessage());
    }

    @ExceptionHandler(ConflictException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleConflict(ConflictException e) {
        log.warn("Conflict: {}", e.getMessage());
        return new ApiError(HttpStatus.CONFLICT.getReasonPhrase(), "Conflict", e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleBadRequest(BadRequestException e) {
        log.warn("Bad request: {}", e.getMessage());
        return new ApiError(HttpStatus.BAD_REQUEST.getReasonPhrase(), "Invalid request data", e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleValidation(MethodArgumentNotValidException e) {
        log.warn("Validation error: {}", e.getMessage());
        return new ApiError(HttpStatus.BAD_REQUEST.getReasonPhrase(), "Invalid request data", e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiError handleGeneral(Exception e) {
        log.error("Unexpected error", e);
        return new ApiError(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), "Internal server error", e.getMessage());
    }
}