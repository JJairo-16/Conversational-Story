package com.example.controllers;

import com.example.models.actions.ActionResult;
import com.example.models.actions.RoomAction;
import com.example.models.game.GameFlag;
import com.example.models.game.GameState;
import com.example.models.items.ItemID;

/**
 * Resolves actions selected from a room interaction menu.
 *
 * <p>
 * Room actions are responsible for deciding whether inventory objects matter in
 * that room. For example, the snorkel should not solve anything when used
 * directly from the inventory; the flooded-drain room action should inspect the
 * inventory and decide whether the player can see under the water.
 */
public final class RoomActionHandler {
    /**
     * Prevents instantiation.
     */
    private RoomActionHandler() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * Executes a room-specific action against the current game state.
     *
     * @param action room action selected by the player
     * @param state  current game state to inspect and mutate
     * @return result message for the UI
     */
    public static ActionResult handle(RoomAction action, GameState state) {
        switch (action) {
            case SEARCH_DARK_PANTRY:
                return searchDarkPantry(state);
            case PREPARE_BAIT_IN_DARK:
                return prepareBaitInDark(state);
            case OPEN_SUIT_LOCKER:
                return openSuitLocker(state);
            default:
                break;
        }

        return ActionResult.success("");
    }

    private static ActionResult searchDarkPantry(GameState state) {
        if (!state.getInventory().has(ItemID.FLASHLIGHT)) {
            return ActionResult.failure("La cuina està massa fosca. Necessites una llanterna per registrar el rebost.");
        }

        if (state.getInventory().has(ItemID.DONUT)) {
            return ActionResult.success("Ja has agafat el dònut útil del rebost.");
        }

        state.getInventory().add(ItemID.DONUT);
        return ActionResult.success("Amb la llanterna trobes un dònut que pot servir d'esquer.");
    }

    private static ActionResult prepareBaitInDark(GameState state) {
        if (!state.getInventory().has(ItemID.FLASHLIGHT)) {
            return ActionResult.failure("No pots preparar res amb seguretat: la cuina està a les fosques.");
        }

        if (!state.getInventory().has(ItemID.DONUT)) {
            return ActionResult.failure("Necessites trobar alguna cosa que pugui servir d'esquer.");
        }

        state.addFlag(GameFlag.MALIEN_DISTRACTED);
        state.getEntityManager().getMalien().distract(3);
        return ActionResult.success("Prepares el dònut com a esquer. En Malien quedarà distret uns torns.");
    }

    private static ActionResult openSuitLocker(GameState state) {
        if (!state.getInventory().has(ItemID.SMALL_KEY)) {
            return ActionResult.failure("L'armari del vestit especial està tancat. Necessites una clau petita.");
        }

        if (state.getInventory().has(ItemID.ASTRONAUT_SUIT)) {
            return ActionResult.success("L'armari ja està obert i ja portes el vestit especial.");
        }

        state.getInventory().add(ItemID.ASTRONAUT_SUIT);
        return ActionResult.success("Obres l'armari amb la clau petita i obtens el vestit especial.");
    }
}
