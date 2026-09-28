package com.example.models.items.instances;

import com.example.models.items.Item;
import com.example.models.items.ItemID;

/**
 * Small key used to open the suit locker.
 */
public final class SmallKey extends Item {
    /**
     * Creates the small key definition.
     */
    public SmallKey() {
        super(ItemID.SMALL_KEY, "Clau petita", "Obre l'armari del vestit especial al vestuari.", false, true);
    }
}
