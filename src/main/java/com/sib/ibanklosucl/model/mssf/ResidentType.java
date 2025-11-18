package com.sib.ibanklosucl.model.mssf;

public enum ResidentType {
    SELF_OWNED(1),
    FAMILY_OWNED(2),
    RENTED(3);

    private final int value;

    ResidentType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
