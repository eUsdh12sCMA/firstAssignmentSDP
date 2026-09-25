package com.pcbuilder.validation;

/**
 * Thrown when build() is called on a configuration that breaks one of the
 * rules in ComputerBuildValidator. Unchecked on purpose - an invalid config
 * is a programming/config error, not something every caller needs to catch,
 * but the message always says exactly which rule failed and why.
 */
public class ComputerBuildValidationException extends RuntimeException {

    public ComputerBuildValidationException(String message) {
        super(message);
    }
}