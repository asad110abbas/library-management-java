package com.portfolio.library.service;

/** A request that is well-formed but violates a library rule (e.g. loan limit reached). */
public class BusinessRuleException extends RuntimeException {
    public BusinessRuleException(String message) { super(message); }
}
