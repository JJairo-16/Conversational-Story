package com.example.controllers;

import com.example.models.actions.RoomAction;
import com.example.models.game.GameState;
import com.example.models.items.ItemID;
import com.example.models.map.Room;
import com.example.models.map.RoomID;

/**
 * Defines which room items are obtained through room actions instead of generic
 * visible collection.
 */
final class RoomActionItemRules {
    private RoomActionItemRules() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * Indicates whether an item is owned by a room action in the current room.
     *
     * @param state current game state
     * @param item  item to inspect
     * @return {@code true} when the item must be obtained through a room action
     */
    static boolean isActionOwnedItem(GameState state, ItemID item) {
        Room room = state.getCurrentRoom();
        RoomID roomId = room.getId();

        return switch (roomId) {
            case BEDROOM -> ownsItem(room, RoomAction.CHECK_LOCKER, item,
                    ItemID.FLASHLIGHT, ItemID.SNORKEL, ItemID.SMALL_KEY);
            case LIVING_ROOM -> ownsItem(room, RoomAction.SEARCH_TABLE, item,
                    ItemID.SMALL_KEY, ItemID.SNORKEL, ItemID.FLASHLIGHT);
            case OFFICES -> ownsItem(room, RoomAction.OPEN_DESK, item,
                    ItemID.CREW_CARD, ItemID.SNORKEL);
            case WORKSHOPS -> ownsItem(room, RoomAction.SEARCH_TOOLS, item,
                    ItemID.REPAIR_TOOL, ItemID.FLASHLIGHT);
            default -> false;
        };
    }

    /**
     * Indicates whether a room action has any item it can currently obtain.
     *
     * @param state  current game state
     * @param action room action to inspect
     * @return {@code true} when the action owns at least one present item
     */
    static boolean actionHasOwnedItem(GameState state, RoomAction action) {
        Room room = state.getCurrentRoom();

        return switch (action) {
            case CHECK_LOCKER -> hasAnyItem(room, ItemID.FLASHLIGHT, ItemID.SNORKEL, ItemID.SMALL_KEY);
            case SEARCH_TABLE -> hasAnyItem(room, ItemID.SMALL_KEY, ItemID.SNORKEL, ItemID.FLASHLIGHT);
            case OPEN_DESK -> hasAnyItem(room, ItemID.CREW_CARD, ItemID.SNORKEL);
            case SEARCH_TOOLS -> hasAnyItem(room, ItemID.REPAIR_TOOL, ItemID.FLASHLIGHT);
            default -> false;
        };
    }

    private static boolean ownsItem(Room room, RoomAction action, ItemID item, ItemID... actionItems) {
        if (!room.hasAction(action) || !room.hasItem(item)) {
            return false;
        }

        for (ItemID actionItem : actionItems) {
            if (item == actionItem) {
                return true;
            }
        }

        return false;
    }

    private static boolean hasAnyItem(Room room, ItemID... items) {
        for (ItemID item : items) {
            if (room.hasItem(item)) {
                return true;
            }
        }

        return false;
    }
}
