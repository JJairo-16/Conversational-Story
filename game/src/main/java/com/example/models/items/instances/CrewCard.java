package com.example.models.items.instances;

import com.example.models.actions.ActionResult;
import com.example.models.game.GameState;
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

    @Override
    public ActionResult use(GameState state) {
        // Future action hook: use the card against locked doors; otherwise show
        // that the card only works near a secured access.
        return ActionResult.failure("La tarja només és útil davant d'un accés bloquejat.");
    }
}
