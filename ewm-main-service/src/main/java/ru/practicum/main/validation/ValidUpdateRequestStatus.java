package ru.practicum.main.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = UpdateRequestStatusValidator.class)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidUpdateRequestStatus {
    String message() default "Обновление статуса заявки на участие в событии возможно на один из перечисленных " +
            "статусов: [CONFIRMED, REJECTED].";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
