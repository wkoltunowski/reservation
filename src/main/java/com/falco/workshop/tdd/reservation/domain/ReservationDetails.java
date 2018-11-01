package com.falco.workshop.tdd.reservation.domain;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class ReservationDetails {
    private final Slot slot;
    private final PatientId patient;

    public ReservationDetails(PatientId patient, Slot slot) {
        this.patient = patient;
        this.slot = slot;
    }

    @Override
    public boolean equals(Object o) {
        return EqualsBuilder.reflectionEquals(this, o);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

    public static ReservationDetails reservationDetails(PatientId patientId, Slot slot) {
        return new ReservationDetails(patientId, slot);
    }

    public Slot slot() {
        return this.slot;
    }
}
