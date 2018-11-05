package com.falco.workshop.tdd.reservation.infrastructure.schedule;

import com.falco.workshop.tdd.reservation.domain.DailyDoctorSchedule;
import com.falco.workshop.tdd.reservation.domain.ScheduleId;
import com.falco.workshop.tdd.reservation.domain.TimeInterval;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.Duration;
import java.util.Optional;

import static com.falco.workshop.tdd.reservation.domain.DailyDoctorSchedule.dailyDoctorSchedule;

@Entity
public class ScheduleJS {
    @Id
    @GeneratedValue(generator = "auto")
    private Long id;
    private String visitDuration;
    private String workingHours;

    ScheduleJS() {
    }

    public ScheduleJS(DailyDoctorSchedule schedule) {
        this.id = Optional.ofNullable(schedule.id()).map(ScheduleId::id).orElse(null);
        this.visitDuration = schedule.visitDuration().toString();
        this.workingHours = schedule.workingHours().toString();
    }

    public Long getId() {
        return id;
    }

    public String getVisitDuration() {
        return visitDuration;
    }

    public String getWorkingHours() {
        return workingHours;
    }

    public DailyDoctorSchedule toSchedule() {
        return dailyDoctorSchedule(Optional.ofNullable(id).map(ScheduleId::new).orElse(null), TimeInterval.parse(workingHours), Duration.parse(visitDuration));
    }
}
