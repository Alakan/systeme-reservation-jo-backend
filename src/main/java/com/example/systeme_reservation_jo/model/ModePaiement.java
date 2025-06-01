package com.example.systeme_reservation_jo.model;

public enum ModePaiement {
    CARTE(0),
    PAYPAL(1),
    VIREMENT(2);

    private final int value;

    ModePaiement(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
