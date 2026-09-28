package com.example.models.items.instances;

import com.example.models.actions.ActionResult;
import com.example.models.game.GameState;
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
        super(ItemID.SMALL_KEY, "Clau petita", "Obre l'armari del vestit especial al vestuari.", true, true);
    }

    @Override
    public ActionResult use(GameState state) {
        // Future action hook: if the player is in the costume room, delegate to
        // OPEN_SUIT_LOCKER; otherwise show that this key does not fit anything here.
        return ActionResult.failure("Aquesta clau no encaixa amb res d'aquesta sala.");
    }
}
