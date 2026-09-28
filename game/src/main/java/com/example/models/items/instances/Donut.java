package com.example.models.items.instances;

import com.example.models.actions.ActionResult;
import com.example.models.game.GameState;
import com.example.models.items.Item;
import com.example.models.items.ItemID;

/**
 * Donut used to distract Malien.
 */
public final class Donut extends Item {
    /** Number of Malien turns distracted by one donut. */
    private static final int MALIEN_DISTRACTION_TURNS = 3;

    /**
     * Creates the donut definition.
     */
    public Donut() {
        super(ItemID.DONUT, "Dònut", "Distracció temporal per a en Malien.", true, true);
    }

    /**
     * Distracts Malien and consumes the donut.
     *
     * @param state current game state
     * @return successful distraction result
     */
    @Override
    public ActionResult use(GameState state) {
        state.getEntityManager().getMalien().distract(MALIEN_DISTRACTION_TURNS);
        state.getInventory().remove(ItemID.DONUT);
        return ActionResult.success("Llances el dònut. En Malien queda distret durant "
                + MALIEN_DISTRACTION_TURNS + " torns.");
    }
}
