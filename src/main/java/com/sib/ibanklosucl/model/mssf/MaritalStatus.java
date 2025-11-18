package com.sib.ibanklosucl.model.mssf;

public enum MaritalStatus {
    SINGLE(1),
    MARRIED(2),
    DIVORCED(3);

    private final int value;

    MaritalStatus(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
