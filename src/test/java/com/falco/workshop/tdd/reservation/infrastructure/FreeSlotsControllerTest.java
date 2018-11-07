package com.falco.workshop.tdd.reservation.infrastructure;

import com.falco.workshop.tdd.reservation.application.slots.FindFreeSlotsService;
import com.falco.workshop.tdd.reservation.domain.DateInterval;
import com.falco.workshop.tdd.reservation.domain.slots.VisitSlot;
import com.falco.workshop.tdd.reservation.infrastructure.slots.FreeSlotsController;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.stream.Collectors;

import static com.falco.workshop.tdd.reservation.domain.DateInterval.fromTo;
import static com.falco.workshop.tdd.reservation.domain.schedule.ScheduleId.scheduleId;
import static com.falco.workshop.tdd.reservation.domain.slots.VisitSlot.visitSlot;
import static java.time.LocalDateTime.now;
import static java.util.Arrays.stream;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@WebMvcTest(FreeSlotsController.class)
public class FreeSlotsControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private FindFreeSlotsService findFreeSlotsService;

    @Test
    public void shouldFindFreeSlots() throws Exception {
        String nowDay = now().toLocalDate().toString();
        givenVisitSlots(
                visitSlot(scheduleId(1), fromTo(nowDay + " 12:00-12:15")),
                visitSlot(scheduleId(2), fromTo(nowDay + " 12:30-12:45"))

        );

        mockMvc.perform(get("/freeslots"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$[0].scheduleId", is(1)))
                .andExpect(jsonPath("$[0].start", is(nowDay + "T12:00:00")))
                .andExpect(jsonPath("$[0].end", is(nowDay + "T12:15:00")))
                .andExpect(jsonPath("$[1].scheduleId", is(2)))
                .andExpect(jsonPath("$[1].start", is(nowDay + "T12:30:00")))
                .andExpect(jsonPath("$[1].end", is(nowDay + "T12:45:00")))
                .andExpect(jsonPath("$", hasSize(2)));


    }

    private void givenVisitSlots(VisitSlot... slots) {
        when(findFreeSlotsService.findFreeSlots(any(DateInterval.class), anyInt())).then(
                i -> stream(slots)
                        .filter(s -> s.interval().intersects(i.getArgument(0)))
                        .limit((Integer) i.getArgument(1))
                        .collect(Collectors.toList()));
    }
}
