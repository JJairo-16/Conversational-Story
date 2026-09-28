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
 */
final class RoomItemVisibility {
    private RoomItemVisibility() {
        throw new UnsupportedOperationException("Utility class");
    }

    static List<ItemID> getVisibleItems(GameState state) {
        return state.getCurrentRoom()
                .getItems()
                .stream()
                .filter(item -> isVisible(state, item))
                .sorted(Comparator.comparing(ItemID::name))
                .toList();
    }

    static List<String> describeCurrentRoomItems(GameState state) {
        List<String> details = new ArrayList<>();
        RoomID roomId = state.getCurrentRoom().getId();

        if (roomId == RoomID.KITCHEN && !state.getInventory().has(ItemID.DONUT)) {
            if (state.getInventory().has(ItemID.FLASHLIGHT)) {
                details.add("Amb la llanterna veus un dònut al fons del rebost.");
            } else {
                details.add("El rebost queda massa fosc per veure si hi ha alguna cosa útil.");
            }
        }

        if (roomId == RoomID.BATHROOM && state.getCurrentRoom().hasItem(ItemID.CREW_CARD)) {
            if (state.getInventory().has(ItemID.SNORKEL)) {
                details.add("Amb l'snorkel distingeixes una tarja sota l'aigua.");
            } else {
                details.add("Sota l'aigua sembla haver-hi alguna cosa, però necessites l'snorkel per veure-ho bé.");
            }
        }

        List<String> visibleItemNames = getVisibleItems(state)
                .stream()
                .map(item -> formatItemName(state, item))
                .toList();

        if (!visibleItemNames.isEmpty()) {
            details.add("Objectes visibles: " + String.join(", ", visibleItemNames) + ".");
        }

        return details;
    }

    static String formatItemName(GameState state, ItemID itemId) {
        Item item = state.getItemCatalog().getItem(itemId);
        return item == null ? itemId.name() : item.name();
    }

    private static boolean isVisible(GameState state, ItemID item) {
        RoomID roomId = state.getCurrentRoom().getId();

        if (roomId == RoomID.KITCHEN && item == ItemID.DONUT) {
            return state.getInventory().has(ItemID.FLASHLIGHT);
        }

        if (roomId == RoomID.BATHROOM && item == ItemID.CREW_CARD) {
            return state.getInventory().has(ItemID.SNORKEL);
        }

        return true;
    }
}
