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
import com.falco.workshop.tdd.reservation.domain.slots.FreeSlot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@Controller
@RequestMapping("/freeslots")
public class FreeSlotsController {
    @Autowired
    private FindFreeSlotsService findFreeSlotsService;


    @RequestMapping(method = GET)
    @ResponseBody
    public List<FreeSlotJS> findFreeSlots() {
        LocalDateTime startFrom = LocalDateTime.now();
        List<FreeSlotJS> slots = new LinkedList<>();
        LocalDateTime maxDate = startFrom.plusDays(45);
        while (slots.size() < 50 && startFrom.isBefore(maxDate)) {
            slots.addAll(findForDay(startFrom));
            startFrom = startFrom.plusDays(1);
        }
        return slots;
    }

    private List<FreeSlotJS> findForDay(LocalDateTime startFrom) {
        return findFreeSlotsService
                .findFreeSlots(DateInterval.fromTo(startFrom, startFrom.plusDays(1))).stream().limit(50).map(FreeSlotJS::new)
                .collect(toList());
    }
}

class FreeSlotJS {
    private Long scheduleId;
    private LocalDateTime start;
    private LocalDateTime end;

    FreeSlotJS() {
    }

    FreeSlotJS(FreeSlot freeSlot) {
        this.scheduleId = freeSlot.id().id();
        this.start = freeSlot.interval().start();
        this.end = freeSlot.interval().end();
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

