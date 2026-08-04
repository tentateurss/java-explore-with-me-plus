package ru.practicum.stats.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.stats.dto.ErrorResponse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handle(final MethodArgumentNotValidException e) {
        List<String> errors = new ArrayList<>();

        e.getBindingResult()
                .getAllErrors()
                .forEach(error -> errors.add(error.getDefaultMessage()));

        return new ErrorResponse(errors.toString());
    }


    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handle(final MissingServletRequestParameterException e) {
        return new ErrorResponse(
                "Required parameter is missing: " + e.getParameterName()
        );
    }


    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handle(final IllegalArgumentException e) {
        return new ErrorResponse(e.getMessage());
    }


    @ExceptionHandler(Throwable.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handle(final Throwable e) {
        log.error("stats-server: Непредвиденная ошибка сервера: {}.", e.getMessage());
        log.debug("stats-server: Детали ошибки: {}.", Arrays.toString(e.getStackTrace()));

        return new ErrorResponse(e.getMessage());
    }
}