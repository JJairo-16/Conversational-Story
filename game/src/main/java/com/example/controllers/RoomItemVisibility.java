package com.example.controllers;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.example.models.game.GameState;
import com.example.models.items.Item;
import com.example.models.items.ItemID;
import com.example.models.map.RoomID;

/**
 * Shared visibility rules for items shown and collected from the current room.
 *
 * <p>
 * Some items exist in a room before they are visible to the player. For
 * example, the kitchen donut requires the flashlight to reveal it, the flooded
 * bathroom card requires the snorkel inspection route, and items found through
 * a room action stay hidden until that action is used.
 */
final class RoomItemVisibility {
    private static final Comparator<ItemID> ITEM_ID_SORTER = Comparator.comparing(ItemID::name);

    /**
     * Prevents instantiation.
     */
    private RoomItemVisibility() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * Returns the items currently visible in the player's room.
     *
     * @param state current game state
     * @return sorted visible item identifiers
     */
    static List<ItemID> getVisibleItems(GameState state) {
        return state.getCurrentRoom()
                .getItems()
                .stream()
                .filter(item -> isVisible(state, item))
                .sorted(ITEM_ID_SORTER)
                .toList();
    }

    /**
     * Builds room detail lines about hidden and visible objects.
     *
     * @param state current game state
     * @return item-related room detail lines
     */
    static List<String> describeCurrentRoomItems(GameState state) {
        List<String> details = new ArrayList<>();

        addHiddenItemDetails(state, details);
        addVisibleItemDetails(state, details);

        return details;
    }

    /**
     * Adds room-specific hidden item hints to the detail list.
     *
     * @param state   current game state
     * @param details mutable detail list to append to
     */
    private static void addHiddenItemDetails(GameState state, List<String> details) {
        RoomID roomId = state.getCurrentRoom().getId();

        if (roomId == RoomID.KITCHEN) {
            addKitchenItemDetails(state, details);
        } else if (roomId == RoomID.BATHROOM) {
            addBathroomItemDetails(state, details);
        }
    }

    /**
     * Adds kitchen hints for the hidden donut reveal flow.
     *
     * @param state   current game state
     * @param details mutable detail list to append to
     */
    private static void addKitchenItemDetails(GameState state, List<String> details) {
        if (state.getInventory().has(ItemID.DONUT)) {
            return;
        }

        if (state.isKitchenDonutRevealed()) {
            details.add("Amb la llanterna veus un dònut al fons del rebost.");
        } else if (state.getInventory().has(ItemID.FLASHLIGHT)) {
            details.add("La cuina és fosca. Podries usar la llanterna per revisar el rebost.");
        } else {
            details.add("El rebost queda massa fosc per veure si hi ha alguna cosa útil.");
        }
    }

    /**
     * Adds bathroom hints for the flooded drain card reveal flow.
     *
     * @param state   current game state
     * @param details mutable detail list to append to
     */
    private static void addBathroomItemDetails(GameState state, List<String> details) {
        if (!state.getCurrentRoom().hasItem(ItemID.CREW_CARD)) {
            return;
        }

        if (state.isFloodedDrainRevealed()) {
            details.add("Amb l'snorkel distingeixes una tarja sota l'aigua.");
        } else if (state.getInventory().has(ItemID.SNORKEL)) {
            details.add("Sota l'aigua sembla haver-hi alguna cosa. Podries usar l'snorkel per veure-ho bé.");
        } else {
            details.add("Sota l'aigua sembla haver-hi alguna cosa, però necessites l'snorkel per veure-ho bé.");
        }
    }

    /**
     * Adds a summary line with the currently visible items.
     *
     * @param state   current game state
     * @param details mutable detail list to append to
     */
    private static void addVisibleItemDetails(GameState state, List<String> details) {
        List<String> visibleItemNames = getVisibleItems(state).stream()
                .map(item -> formatItemName(state, item))
                .toList();

        if (!visibleItemNames.isEmpty()) {
            details.add("Objectes visibles: " + String.join(", ", visibleItemNames) + ".");
        }
    }

    /**
     * Resolves an item identifier into a display name.
     *
     * @param state  current game state
     * @param itemId item identifier
     * @return catalog display name, or the enum name as fallback
     */
    static String formatItemName(GameState state, ItemID itemId) {
        Item item = state.getItemCatalog().getItem(itemId);
        return item == null ? itemId.name() : item.name();
    }

    /**
     * Applies special visibility requirements for room items.
     *
     * @param state current game state
     * @param item  item to inspect
     * @return {@code true} when the player can see the item
     */
    private static boolean isVisible(GameState state, ItemID item) {
        RoomID roomId = state.getCurrentRoom().getId();

        if (roomId == RoomID.KITCHEN && item == ItemID.DONUT) {
            return state.isKitchenDonutRevealed();
        }

        if (roomId == RoomID.BATHROOM && item == ItemID.CREW_CARD) {
            return state.isFloodedDrainRevealed();
        }

        return !RoomActionItemRules.isActionOwnedItem(state, item);
    }
}
