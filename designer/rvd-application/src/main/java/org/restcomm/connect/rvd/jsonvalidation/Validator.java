package org.restcomm.connect.rvd.jsonvalidation;

import org.restcomm.connect.rvd.jsonvalidation.exceptions.ValidationFrameworkException;
import org.restcomm.connect.rvd.validation.ValidationResult;

public interface Validator {
    ValidationResult validate(String json) throws ValidationFrameworkException;
}
