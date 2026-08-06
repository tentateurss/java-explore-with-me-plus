package ru.practicum.main.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import ru.practicum.main.enums.RequestStatus;

public class UpdateRequestStatusValidator implements ConstraintValidator<ValidUpdateRequestStatus, RequestStatus> {
    @Override
    public boolean isValid(RequestStatus status, ConstraintValidatorContext cxt) {
       return status.equals(RequestStatus.CONFIRMED) || status.equals(RequestStatus.REJECTED);
    }
}
