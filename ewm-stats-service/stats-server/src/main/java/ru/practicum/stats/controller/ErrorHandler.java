package ru.practicum.stats.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
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

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handle(final MethodArgumentNotValidException e) {
        List<String> errors = new ArrayList<>();
        e.getBindingResult()
                .getAllErrors()
                .forEach(error -> errors.add(error.getDefaultMessage()));
        return new ErrorResponse(errors.toString());
    }


    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handle(final Throwable e) {
        log.error("stats-server: Непредвиденная ошибка сервера: {}.", e.getMessage());
        log.debug("stats-server: Детали ошибки: {}.", Arrays.toString(e.getStackTrace()));
        return new ErrorResponse(e.getMessage());
    }

}
