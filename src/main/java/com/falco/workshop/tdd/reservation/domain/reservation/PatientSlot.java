package com.falco.workshop.tdd.reservation.domain.reservation;

import com.falco.workshop.tdd.reservation.domain.slots.FreeSlot;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class PatientSlot {
    private final FreeSlot freeSlot;
    private final PatientId patient;

    private PatientSlot(PatientId patient, FreeSlot freeSlot) {
        this.patient = patient;
        this.freeSlot = freeSlot;
    }

    public PatientId patient() {
        return patient;
    }

    public FreeSlot slot() {
        return this.freeSlot;
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

    public static PatientSlot patientSlot(PatientId patientId, FreeSlot freeSlot) {
        return new PatientSlot(patientId, freeSlot);
    }
}
