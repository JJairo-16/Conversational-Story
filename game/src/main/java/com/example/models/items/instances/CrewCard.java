package com.example.models.items.instances;

import com.example.models.items.Item;
import com.example.models.items.ItemID;

/**
 * Crew card used to open restricted doors.
 */
public final class CrewCard extends Item {
    /**
     * Creates the crew card definition.
     */
    public CrewCard() {
        super(ItemID.CREW_CARD, "Tarja de tripulant", "Tarja personal per obrir portes.", false, true);
    }
}
