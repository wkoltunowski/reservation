package com.falco.workshop.tdd.reservation.infrastructure;

import com.falco.workshop.tdd.reservation.domain.DailyDoctorSchedule;
import com.falco.workshop.tdd.reservation.domain.ScheduleId;
import com.falco.workshop.tdd.reservation.domain.TimeInterval;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.Duration;

import static com.falco.workshop.tdd.reservation.domain.DailyDoctorSchedule.dailyDoctorSchedule;

@Entity
public class Schedule {
    @Id
    @GeneratedValue(generator = "auto")
    private Long id;
    private String visitDuration;
    private String workingHours;

    Schedule() {
    }

    public Schedule(DailyDoctorSchedule schedule) {
        this.id = schedule.id().id();
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
        return dailyDoctorSchedule(new ScheduleId(id), TimeInterval.parse(workingHours), Duration.parse(visitDuration));
    }
}
