package com.falco.workshop.tdd.reservation.domain.reservation;

import com.falco.workshop.tdd.reservation.domain.slots.VisitSlot;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class PatientSlot {
    private final PatientId patient;
    private final VisitSlot visitSlot;

    private PatientSlot(PatientId patient, VisitSlot visitSlot) {
        this.patient = patient;
        this.visitSlot = visitSlot;
    }

    public PatientId patient() {
        return patient;
    }

    public VisitSlot slot() {
        return this.visitSlot;
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

    public static PatientSlot patientSlot(PatientId patientId, VisitSlot visitSlot) {
        return new PatientSlot(patientId, visitSlot);
    }
}
