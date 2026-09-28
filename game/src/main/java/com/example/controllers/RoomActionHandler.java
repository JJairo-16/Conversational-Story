package com.example.controllers;

import java.util.ArrayList;
import java.util.List;

import com.example.models.actions.ActionResult;
import com.example.models.actions.RoomAction;
import com.example.models.entities.CrewMember.CrewInteractionResult;
import com.example.models.game.GameFlag;
import com.example.models.game.GameState;
import com.example.models.items.ItemID;

/**
 * Resolves actions selected from a room interaction menu.
 *
 * <p>
 * This handler only coordinates the menu action with the model. Concrete entity
 * behavior belongs to the entity itself, and full room puzzle logic should live
 * outside this dispatcher when those puzzles are implemented.
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
            case COLLECT_VISIBLE_ITEMS:
                return collectVisibleItems(state);
            case WAKE_CREW:
                return interactWithCrew(state);
            case TALK_CREW:
                return interactWithCrew(state);
            case SEARCH_DARK_PANTRY:
                return searchDarkPantry(state);
            case PREPARE_BAIT_IN_DARK:
                return prepareBaitInDark(state);
            case OPEN_SUIT_LOCKER:
                return openSuitLocker(state);
            default:
                return ActionResult.success("");
        }
    }

    private static ActionResult interactWithCrew(GameState state) {
        CrewInteractionResult result = state.interactWithCrewMember();
        return switch (result.type()) {
            case NOT_IN_ROOM, DEAD -> ActionResult.failure(result.message());
            case WOKEN_UP, TALKED, ALREADY_AWAKE -> ActionResult.success(result.message());
        };
    }

    private static ActionResult collectVisibleItems(GameState state) {
        List<ItemID> visibleItems = new ArrayList<>(RoomItemVisibility.getVisibleItems(state));
        if (visibleItems.isEmpty()) {
            return ActionResult.failure("No hi ha cap objecte visible per recollir.");
        }

        List<String> collectedNames = new ArrayList<>();

        for (ItemID itemId : visibleItems) {
            if (state.takeItem(itemId)) {
                collectedNames.add(RoomItemVisibility.formatItemName(state, itemId));
            }
        }

        if (collectedNames.isEmpty()) {
            return ActionResult.failure("No has pogut recollir cap objecte.");
        }

        return ActionResult.success("Has recollit: " + String.join(", ", collectedNames) + ".");
    }

    private static ActionResult searchDarkPantry(GameState state) {
        if (!state.getInventory().has(ItemID.FLASHLIGHT)) {
            return ActionResult.failure("La cuina està massa fosca. Necessites una llanterna per registrar el rebost.");
        }

        if (state.getInventory().has(ItemID.DONUT)) {
            return ActionResult.success("Ja has agafat el dònut útil del rebost.");
        }

        if (state.getCurrentRoom().hasItem(ItemID.DONUT)) {
            state.takeItem(ItemID.DONUT);
        } else {
            state.getInventory().add(ItemID.DONUT);
        }

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
