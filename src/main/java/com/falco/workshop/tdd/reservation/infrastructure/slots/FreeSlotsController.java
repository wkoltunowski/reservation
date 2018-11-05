/*
 * Copyright 2012-2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.falco.workshop.tdd.reservation.infrastructure.slots;


import com.falco.workshop.tdd.reservation.application.FindFreeSlotsService;
import com.falco.workshop.tdd.reservation.domain.DateInterval;
import com.falco.workshop.tdd.reservation.domain.slots.VisitSlot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.stream.IntStream;

import static com.falco.workshop.tdd.reservation.domain.DateInterval.fromTo;
import static java.time.Duration.ofDays;
import static java.time.LocalDateTime.now;
import static java.time.temporal.ChronoUnit.DAYS;
import static java.util.stream.Collectors.toList;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@Controller
@RequestMapping("/freeslots")
public class FreeSlotsController {
    @Autowired
    private FindFreeSlotsService freeSlotsService;


    @RequestMapping(method = GET)
    @ResponseBody
    public List<FreeSlotJS> freeslots() {
        return toFreeSlotJS(findMaxNSlots(fromTo(now(), ofDays(45)), 50));
    }

    private List<VisitSlot> findMaxNSlots(DateInterval interval, int maxSlotsCount) {
        LocalDate startDay = interval.start().toLocalDate();
        return IntStream.range(1, (int) DAYS.between(interval.start(), interval.end()))
                .mapToObj(day -> findForDay(startDay.plusDays(day), maxSlotsCount))
                .flatMap(Collection::stream)
                .limit(maxSlotsCount)
                .collect(toList());
    }

    private List<VisitSlot> findForDay(LocalDate date, int nSlotsCount) {
        return freeSlotsService.findFreeSlots(fromTo(date.atStartOfDay(), date.atStartOfDay().plusDays(1))).stream().limit(nSlotsCount).collect(toList());
    }

    private List<FreeSlotJS> toFreeSlotJS(List<VisitSlot> maxNSlots) {
        return maxNSlots.stream().map(FreeSlotJS::new).collect(toList());
    }
}

class FreeSlotJS {
    private Long scheduleId;
    private LocalDateTime start;
    private LocalDateTime end;

    FreeSlotJS() {
    }

    FreeSlotJS(VisitSlot scheduleSlot) {
        this.scheduleId = scheduleSlot.id().id();
        this.start = scheduleSlot.interval().start();
        this.end = scheduleSlot.interval().end();
    }

    public Long getScheduleId() {
        return scheduleId;
    }

    public LocalDateTime getStart() {
        return start;
    }

    public LocalDateTime getEnd() {
        return end;
    }
}

