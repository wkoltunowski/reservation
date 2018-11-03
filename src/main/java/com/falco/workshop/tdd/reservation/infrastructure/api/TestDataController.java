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


import com.falco.workshop.tdd.reservation.application.DefineScheduleService;
import com.falco.workshop.tdd.reservation.domain.ScheduleId;
import com.falco.workshop.tdd.reservation.domain.TimeInterval;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import static com.falco.workshop.tdd.reservation.domain.DailyDoctorSchedule.dailyDoctorSchedule;
import static java.time.Duration.ofMinutes;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

@Controller
@RequestMapping("/testData")
public class TestDataController {
    @Autowired
    private DefineScheduleService defineScheduleService;


    @RequestMapping(method = PUT)
    @ResponseBody
    public void testData() {
        defineScheduleService.defineSchedule(dailyDoctorSchedule(new ScheduleId(1), TimeInterval.parse("08:00-16:00"), ofMinutes(15)));
    }

}
