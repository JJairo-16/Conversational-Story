package com.example.models.items.instances;

import com.example.models.actions.ActionResult;
import com.example.models.game.GameState;
import com.example.models.items.Item;
import com.example.models.items.ItemID;

/**
 * Snorkel used to inspect flooded areas.
 */
public final class Snorkel extends Item {
    /**
     * Creates the snorkel definition.
     */
    public Snorkel() {
        super(ItemID.SNORKEL, "Snorkel", "Permet veure sota l'aigua al bany inundat.", false, true);
    }

    /**
     * Uses the same flooded-drain route as the room interaction.
     *
     * @param state current game state
     * @return flooded-drain inspection result
     */
    @Override
    public ActionResult use(GameState state) {
        return state.inspectFloodedDrainWithSnorkel();
    }
}
