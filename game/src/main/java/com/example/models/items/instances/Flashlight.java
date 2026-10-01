package com.example.models.items.instances;

import com.example.models.actions.ActionResult;
import com.example.models.game.GameState;
import com.example.models.items.Item;
import com.example.models.items.ItemID;
import com.example.models.map.RoomID;

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

    /**
     * Reveals the kitchen donut when used in the kitchen.
     *
     * @param state current game state
     * @return reveal result, or a failure when used elsewhere
     */
    @Override
    public ActionResult use(GameState state) {
        if (state.getCurrentRoom().getId() != RoomID.KITCHEN) {
            return ActionResult.failure("La llanterna no revela res especial aquí.");
        }

        state.revealKitchenDonut();
        return ActionResult.success("Il·lumines el rebost i veus un dònut.");
    }
}
