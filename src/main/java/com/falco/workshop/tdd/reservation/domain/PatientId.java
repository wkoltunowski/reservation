package com.falco.workshop.tdd.reservation.domain;

public class PatientId {
    private final String patient;

    public PatientId(String patient) {
        this.patient = patient;
    }

    public String getPatient() {
        return patient;
    }
}
