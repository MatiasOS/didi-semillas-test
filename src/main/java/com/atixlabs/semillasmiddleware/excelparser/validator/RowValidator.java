package com.atixlabs.semillasmiddleware.excelparser.validator;

import java.util.List;
import java.util.stream.Collectors;

public class RowValidator<T> extends Validator<T> {

    private final List<Validator<T>> validators;

    public RowValidator(List<Validator<T>> validators) {
        this.validators = validators;
    }

    @Override
    public List<String> validate(T info) {
        return validators.stream().flatMap(
                validator -> validator.validate(info).stream())
                .collect(Collectors.toList());
    }
}
