package com.falco.workshop.tdd.reservation.application;

import com.falco.workshop.tdd.reservation.domain.FreeSlotRepository;
import com.falco.workshop.tdd.reservation.domain.Slot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SlotReservationService {
    private final FreeSlotRepository freeSlotRepository;

    @Autowired
    public SlotReservationService(FreeSlotRepository freeSlotRepository) {
        this.freeSlotRepository = freeSlotRepository;
    }

    public void reserveSlot(Slot slot) {
        Slot oldSlot = freeSlotRepository.findById(slot.id(), slot.interval());
        freeSlotRepository.delete(oldSlot);
        freeSlotRepository.saveAll(oldSlot.splitBy(slot));
    }
}
