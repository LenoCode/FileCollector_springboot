package com.leno.example.validators;


import com.leno.example.exceptions.validationExceptions.ObjectNotValidException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class ObjectValidator {

    /**
     * Factory object
     */
    private final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    /**
     * Creates a validator object
     */
    private final Validator validator = factory.getValidator();


    /**
     * Check json payload before going to controller and if json not valid it will throw exception
     * @param testRecord
     * @param <T>
     * @throws ObjectNotValidException
     */
    public <T> void validate(T testRecord,Class<?>[] groups) throws ObjectNotValidException {
        Set<ConstraintViolation<T>> constraintViolations =  validator.validate(testRecord,groups);
        if( !constraintViolations.isEmpty()){

            var errorMessages =  constraintViolations.stream().
                    map(ConstraintViolation::getMessage).collect(Collectors.toSet()); // STREAM OF VALLIDATION AND MAP TO GET ALL MESSAGE AND PUT THEM IN COLLECTION SET

            throw new ObjectNotValidException(errorMessages);
        }
    }
}
