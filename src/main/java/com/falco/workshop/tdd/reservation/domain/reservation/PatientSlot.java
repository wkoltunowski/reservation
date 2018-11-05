package com.falco.workshop.tdd.reservation.domain.reservation;

import com.falco.workshop.tdd.reservation.domain.slots.Slot;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class PatientSlot {
    private final Slot slot;
    private final PatientId patient;

    private PatientSlot(PatientId patient, Slot slot) {
        this.patient = patient;
        this.slot = slot;
    }

    public PatientId patient() {
        return patient;
    }

    public Slot slot() {
        return this.slot;
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

    public static PatientSlot patientSlot(PatientId patientId, Slot slot) {
        return new PatientSlot(patientId, slot);
    }
}
