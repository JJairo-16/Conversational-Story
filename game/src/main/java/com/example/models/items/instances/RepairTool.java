package com.example.models.items.instances;

import com.example.models.actions.ActionResult;
import com.example.models.game.GameState;
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
        super(ItemID.REPAIR_TOOL, "Eina especial", "Eina necessària per reparar els propulsors.", true, true);
    }

    @Override
    public ActionResult use(GameState state) {
        // Future action hook: if the player is in the exterior exit room, repair
        // the engines here; otherwise show that there is nothing to repair here.
        return ActionResult.failure("Aquí no hi ha res que puguis reparar amb l'eina especial.");
    }
}
