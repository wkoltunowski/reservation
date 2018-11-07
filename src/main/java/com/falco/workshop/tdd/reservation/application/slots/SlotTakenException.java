package com.falco.workshop.tdd.reservation.application.slots;

public class SlotTakenException extends RuntimeException {
    public SlotTakenException() {
        super("Slot already taken!");
    }
}
