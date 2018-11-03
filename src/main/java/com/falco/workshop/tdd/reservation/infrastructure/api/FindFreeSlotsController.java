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
package com.falco.workshop.tdd.reservation.infrastructure.api;


import com.falco.workshop.tdd.reservation.application.FindFreeSlotsService;
import com.falco.workshop.tdd.reservation.domain.DateInterval;
import com.falco.workshop.tdd.reservation.domain.Slot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDateTime;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@Controller
@RequestMapping("/findFreeSlots")
public class FindFreeSlotsController {
    @Autowired
    private FindFreeSlotsService findFreeSlotsService;


    @RequestMapping(method = GET)
    @ResponseBody
    public List<SlotJS> findFreeSlots() {
        LocalDateTime startFrom = LocalDateTime.now();
        return findFreeSlotsService
                .findFreeSlots(DateInterval.parse(startFrom, startFrom.plusDays(1))).stream()
                .map(this::toJson).collect(toList());
    }

    private SlotJS toJson(Slot slot) {
        return new SlotJS(slot.id().id().toString(), slot.interval().start().toString(), slot.interval().end().toString());
    }

    private class SlotJS {
        private final String scheduleId;
        private final String start;
        private final String end;

        public SlotJS(String scheduleId, String start, String end) {
            this.scheduleId = scheduleId;
            this.start = start;
            this.end = end;
        }

        public String getScheduleId() {
            return scheduleId;
        }

        public String getStart() {
            return start;
        }

        public String getEnd() {
            return end;
        }
    }
}
