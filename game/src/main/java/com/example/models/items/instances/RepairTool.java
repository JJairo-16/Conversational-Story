package com.example.models.items.instances;

import com.example.models.items.Item;
import com.example.models.items.ItemID;

/**
 * Special tool required to repair the propulsors.
 */
public final class RepairTool extends Item {
    /**
     * Creates the repair tool definition.
     */
    public RepairTool() {
        super(ItemID.REPAIR_TOOL, "Eina especial", "Eina necessària per reparar els propulsors.", false, true);
    }
}
