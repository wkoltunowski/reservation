package com.falco.workshop.tdd.reservation.application;

import com.falco.workshop.tdd.reservation.domain.slots.FreeSlot;
import com.falco.workshop.tdd.reservation.domain.slots.FreeSlotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;

@Component
public class SlotReservationService {
    private final FreeSlotRepository freeSlotRepository;

    @Autowired
    public SlotReservationService(FreeSlotRepository freeSlotRepository) {
        this.freeSlotRepository = freeSlotRepository;
    }

    public void reserveSlot(FreeSlot freeSlot) {
        List<FreeSlot> oldFreeSlots = freeSlotRepository.findById(freeSlot.id(), freeSlot.interval());
        checkArgument(oldFreeSlots.size() == 1, "FreeSlot already taken!");
        FreeSlot oldFreeSlot = oldFreeSlots.get(0);
        checkArgument(oldFreeSlot.interval().encloses(freeSlot.interval()), "FreeSlot already taken!");
        freeSlotRepository.delete(oldFreeSlot);
        freeSlotRepository.saveAll(oldFreeSlot.splitBy(freeSlot));
    }
}
