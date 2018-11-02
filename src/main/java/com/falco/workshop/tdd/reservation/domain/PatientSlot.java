package com.falco.workshop.tdd.reservation.domain;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.persistence.Embeddable;

@Embeddable
public class PatientSlot {
    private Slot slot;
    private PatientId patient;

    public PatientSlot() {
    }

    private PatientSlot(PatientId patient, Slot slot) {
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

    public static PatientSlot reservationDetails(PatientId patientId, Slot slot) {
        return new PatientSlot(patientId, slot);
    }

    public Slot slot() {
        return this.slot;
    }
}
