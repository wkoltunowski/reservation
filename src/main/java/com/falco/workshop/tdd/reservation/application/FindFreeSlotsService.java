package com.falco.workshop.tdd.reservation.application;

import com.falco.workshop.tdd.reservation.domain.DateInterval;
import com.falco.workshop.tdd.reservation.domain.FreeSlotRepository;
import com.falco.workshop.tdd.reservation.domain.Slot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class FindFreeSlotsService {
    private final FreeSlotRepository freeSlotRepository;

    @Autowired
    public FindFreeSlotsService(FreeSlotRepository freeSlotRepository) {
        this.freeSlotRepository = freeSlotRepository;
    }

    public List<Slot> findFreeSlots(DateInterval interval) {
        return this.freeSlotRepository.find(interval);
    }
}
