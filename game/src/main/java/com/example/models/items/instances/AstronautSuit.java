package com.example.models.items.instances;

import com.example.models.items.Item;
import com.example.models.items.ItemID;

/**
 * Astronaut suit required to survive outside the ship.
 */
public final class AstronautSuit extends Item {
    /**
     * Creates the astronaut suit definition.
     */
    public AstronautSuit() {
        super(ItemID.ASTRONAUT_SUIT, "Vestit d'astronauta", "Equip necessari per sortir de la nau.", false, true);
    }
}
