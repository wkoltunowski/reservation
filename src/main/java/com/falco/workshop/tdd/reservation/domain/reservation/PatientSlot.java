package com.falco.workshop.tdd.reservation.domain.reservation;

import com.falco.workshop.tdd.reservation.domain.DateInterval;
import com.falco.workshop.tdd.reservation.domain.schedule.ScheduleId;
import com.falco.workshop.tdd.reservation.domain.slots.VisitSlot;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import static com.falco.workshop.tdd.reservation.domain.slots.VisitSlot.visitSlot;

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
        return "PatientSlot[patientId:" + patient.id() + ", " + "visitSlot:" + visitSlot + "]";
    }

    public static PatientSlot patientSlot(PatientId patientId, VisitSlot visitSlot) {
        return new PatientSlot(patientId, visitSlot);
    }
    public static PatientSlot patientSlot(PatientId patientId, ScheduleId scheduleId,DateInterval interval) {
        return new PatientSlot(patientId, visitSlot(scheduleId,interval));
    }
}
