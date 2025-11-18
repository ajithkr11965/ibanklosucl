package com.sib.ibanklosucl.exception;

public enum ValidationError {
    ERRO01("All applicants should have either Same Program or None.Also At least  One person must have  program  other than None"),
    ERRO02("At least one program other than 'NONE' should exist."),
    ERRO03("For  %s ,the Requested Loan Amount should be in range : %s "),
    ERRO04("Tenor should be in range : %s"),
    ERRO05("Loan amount must be <= to Vehicle Amount"),

    ERRO06("For Non-Foir, Tenor must be less than or equal to %s and Loan Amount must be %s or below "),
    ERRO07("For Non-Foir, Bureau score  must be greater than or equal to %s  "),
    ERRO08("For selecting fixed type, the loan tenor should be greater than or equal to %s months."),
    ERRO09("Surrogate Program should be chosen only once!!."),
    ERRO10("Surrogate Program should be chosen only once!!."),
    ERRO11("%s not completed for %s"),
    ERRO12("For NRI's if Income(Program) is considered Non-Foir cant be selected!! "),
    ERRO13("The Program of Guarantor Should be NONE "),
     ERRO14("For  %s ,the Eligible Loan Amount - %s  should be in range : %s "),
    ERRO15("Insurance Premium  amount must be <  Vehicle & Loan Amount"),
    COM001("%s"),
    KYCO01("Age Eligibilty Check Failed for %s");


    private final String message;

    ValidationError(String message) {
        this.message = message;
    }

    public String getMessage(Object... args) {
        if (args.length > 0) {
            return String.format(message, args);
        } else {
            return message;
        }
    }
}

