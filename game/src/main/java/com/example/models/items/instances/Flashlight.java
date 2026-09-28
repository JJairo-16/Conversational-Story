package com.example.models.items.instances;

import com.example.models.items.Item;
import com.example.models.items.ItemID;

/**
 * Flashlight used to search dark rooms.
 */
public final class Flashlight extends Item {
    /**
     * Creates the flashlight definition.
     */
    public Flashlight() {
        super(ItemID.FLASHLIGHT, "Llanterna", "Permet registrar zones fosques, com la cuina sense llum.", true,
                true);
    }
}
