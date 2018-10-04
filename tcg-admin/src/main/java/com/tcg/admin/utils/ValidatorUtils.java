package com.tcg.admin.utils;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.validation.groups.Default;
/**
 * @author christian.f
 * Validate Model with the hibernate-validator annotations
 * @NotNull
 * **/
public final class ValidatorUtils {
	
	private ValidatorUtils() {}
	
	public static final <T> Set<ConstraintViolation<T>> validate(T toCheck) {
	    //Generic Validator
	    ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
	    Validator validator = validatorFactory.getValidator();

	    return validator.validate(toCheck, Default.class);
	}

}
