package com.sib.ibanklosucl.model.mssf;

public enum EmploymentType {
    SALARIED(1),
    SELF_EMPLOYED(2),
    NO_INCOME_SOURCE(3);

    private final int value;

    EmploymentType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}


