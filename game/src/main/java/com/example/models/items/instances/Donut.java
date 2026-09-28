package com.example.models.items.instances;

import com.example.models.items.Item;
import com.example.models.items.ItemID;

/**
 * Donut used to distract Malien.
 */
public final class Donut extends Item {
    /**
     * Creates the donut definition.
     */
    public Donut() {
        super(ItemID.DONUT, "Dònut", "Distracció temporal per a en Malien.", true, true);
    }
}
