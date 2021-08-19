package com.neueda.assignment.urlshrinker.model;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Set;
import java.util.function.Predicate;

import static org.assertj.core.api.Assertions.assertThat;

public class BeanValidationTest<T> {

    private Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    public Predicate<ConstraintViolation<T>> violationForFieldWithMessage(String fieldName, String expectedMessage) {
        return (ConstraintViolation<T> violation) ->
                violation.getPropertyPath().toString().equals(fieldName) && violation.getMessage().equals(expectedMessage);

    }

    public Predicate<ConstraintViolation<T>> violationForField(String fieldName) {
        return (ConstraintViolation<T> violation) -> violation.getPropertyPath().toString().equals(fieldName);
    }

    public void verifyViolationForFieldWithMessage(String fieldName, String expectedMessage, T bean) {
        Set<ConstraintViolation<T>> results = this.validator.validate(bean);
        assertThat(results).anyMatch(violationForFieldWithMessage(fieldName, expectedMessage));
    }

    public void verifyNoViolationFor(T bean) {
        Set<ConstraintViolation<T>> results = this.validator.validate(bean);
        assertThat(results).isEmpty();
    }

    public void verifyNoViolationForField(String fieldName, T bean) {
        Set<ConstraintViolation<T>> results = this.validator.validate(bean);
        assertThat(results).noneMatch(violationForField(fieldName));
    }

}
