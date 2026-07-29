package ru.practicum.main.dto.error;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiError {
    private List<Object> errors; // список стектрейсов (StackTraceElement) или описания ошибок (String)
    private String message;
    private String reason;
    private HttpStatus status;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;
}
