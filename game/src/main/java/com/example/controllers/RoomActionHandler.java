package com.example.controllers;

import java.util.ArrayList;
import java.util.List;

import com.example.models.actions.ActionResult;
import com.example.models.actions.RoomAction;
import com.example.models.entities.CrewMember.CrewInteractionResult;
import com.example.models.game.GameState;
import com.example.models.items.ItemID;

/**
 * Resolves actions selected from a room interaction menu.
 *
 * <p>
 * This handler owns room-level interaction rules. Item-use behavior remains in
 * the item classes, and shared reveal logic lives on {@link GameState} when both
 * a room action and an item use need the same path.
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
            case CHECK_LOCKER:
                return checkLocker(state);
            case COLLECT_VISIBLE_ITEMS:
                return collectVisibleItems(state);
            case INSPECT_FLOODED_DRAIN:
                return inspectFloodedDrain(state);
            case WAKE_CREW:
                return interactWithCrew(state);
            case TALK_CREW:
                return interactWithCrew(state);
            case SEARCH_TABLE:
                return searchTable(state);
            case OPEN_DESK:
                return openDesk(state);
            case OPEN_SUIT_LOCKER:
                return openSuitLocker(state);
            case SEARCH_TOOLS:
                return searchTools(state);
            case INSPECT_ENGINES:
                return inspectEngines(state);
            case REPAIR_ENGINES:
                return repairEngines(state);
            case START_ENGINES:
                return startEngines(state);
            default:
                return ActionResult.success("");
        }
    }

    private static ActionResult checkLocker(GameState state) {
        if (takeItemIfPresent(state, ItemID.FLASHLIGHT)) {
            return ActionResult.success("Revises la taquilla i hi trobes una llanterna.");
        }

        if (takeItemIfPresent(state, ItemID.SNORKEL)) {
            return ActionResult.success("Revises la taquilla i hi trobes un snorkel.");
        }

        if (takeItemIfPresent(state, ItemID.SMALL_KEY)) {
            return ActionResult.success("Revises la taquilla i hi trobes una clau petita.");
        }

        return ActionResult.success("Revises la taquilla, però no hi queda res útil.");
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

    private static ActionResult inspectFloodedDrain(GameState state) {
        return state.inspectFloodedDrainWithSnorkel();
    }

    private static ActionResult interactWithCrew(GameState state) {
        CrewInteractionResult result = state.interactWithCrewMember();
        return switch (result.type()) {
            case NOT_IN_ROOM, DEAD -> ActionResult.failure(result.message());
            case WOKEN_UP, TALKED, ALREADY_AWAKE -> ActionResult.success(result.message());
        };
    }

    private static ActionResult searchTable(GameState state) {
        if (takeItemIfPresent(state, ItemID.SMALL_KEY)) {
            return ActionResult.success("Registres la taula i hi trobes una clau petita.");
        }

        if (takeItemIfPresent(state, ItemID.SNORKEL)) {
            return ActionResult.success("Registres la taula i hi trobes un snorkel.");
        }

        return ActionResult.success("Registres la taula, però no hi queda res útil.");
    }

    private static ActionResult openDesk(GameState state) {
        if (takeItemIfPresent(state, ItemID.CREW_CARD)) {
            return ActionResult.success("Obres l'escriptori i hi trobes la tarja de tripulant.");
        }

        if (takeItemIfPresent(state, ItemID.SNORKEL)) {
            return ActionResult.success("Obres l'escriptori i hi trobes un snorkel.");
        }

        return ActionResult.success("Obres l'escriptori, però no hi trobes res que et serveixi.");
    }

    private static ActionResult openSuitLocker(GameState state) {
        if (!state.getInventory().has(ItemID.SMALL_KEY)) {
            return ActionResult.failure("L'armari del vestit està tancat. Necessites una clau petita.");
        }

        if (state.getInventory().has(ItemID.ASTRONAUT_SUIT)) {
            return ActionResult.success("L'armari ja està obert i ja tens el vestit d'astronauta.");
        }

        state.getInventory().add(ItemID.ASTRONAUT_SUIT);
        return ActionResult.success("Obres l'armari amb la clau petita i obtens el vestit d'astronauta.");
    }

    private static ActionResult searchTools(GameState state) {
        if (takeItemIfPresent(state, ItemID.REPAIR_TOOL)) {
            return ActionResult.success("Registres el taller i hi trobes l'eina especial de reparació.");
        }

        if (takeItemIfPresent(state, ItemID.FLASHLIGHT)) {
            return ActionResult.success("Registres el taller i hi trobes una llanterna.");
        }

        return ActionResult.success("Registres les eines, però no hi queda res important.");
    }

    private static ActionResult inspectEngines(GameState state) {
        state.inspectEngines();
        return ActionResult.success("Inspecciones els motors: necessiten el vestit d'astronauta i l'eina especial.");
    }

    private static ActionResult repairEngines(GameState state) {
        if (!state.areEnginesInspected()) {
            return ActionResult.failure("Abans de reparar, has d'inspeccionar els motors.");
        }

        if (!state.getEntityManager().getPlayer().isAstronautSuitEquipped()) {
            return ActionResult.failure("No pots treballar a l'exterior sense equipar-te el vestit d'astronauta.");
        }

        if (!state.getInventory().has(ItemID.REPAIR_TOOL)) {
            return ActionResult.failure("Necessites l'eina especial per reparar els motors.");
        }

        state.repairEngines();
        return ActionResult.success("Repares els motors amb l'eina especial.");
    }

    private static ActionResult startEngines(GameState state) {
        if (!state.areEnginesRepaired()) {
            return ActionResult.failure("Els motors encara no estan reparats.");
        }

        return ActionResult.success("Engegues els motors. La nau torna a estar operativa.");
    }

    /**
     * Takes an item from the current room if it is present.
     *
     * @param state current game state
     * @param item  item to take
     * @return {@code true} when the item moved to inventory
     */
    private static boolean takeItemIfPresent(GameState state, ItemID item) {
        return state.getCurrentRoom().hasItem(item) && state.takeItem(item);
    }
}
