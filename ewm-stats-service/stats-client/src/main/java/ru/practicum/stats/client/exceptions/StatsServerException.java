package ru.practicum.stats.client.exceptions;

import java.util.List;

public class StatsServerException extends RuntimeException {
    List<String> errors;

    public StatsServerException(String message) {
        super(message);
    }

    public StatsServerException(String message, List<String> errors) {
        super(message);
        this.errors = errors;
    }
}
