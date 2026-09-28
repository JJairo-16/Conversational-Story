package com.example.models.game;

import com.example.models.entities.EntityManager;
import com.example.models.generation.GameRandom;
import com.example.models.generation.GameRoute;
import com.example.models.items.Inventory;
import com.example.models.items.Item;
import com.example.models.items.ItemCatalog;
import com.example.models.items.ItemID;
import com.example.models.map.Direction;
import com.example.models.map.Room;
import com.example.models.map.RoomID;
import com.example.models.map.ShipMap;
import com.example.models.entities.CrewMember;

import java.util.EnumSet;
import java.util.Set;

/**
 * Mutable state of a generated game.
 */
public final class GameState {
    private final ShipMap shipMap;
    private final ItemCatalog itemCatalog;
    private final EntityManager entityManager;
    private final GameRandom random;
    private final GameRoute route;
    private final Set<GameFlag> flags = EnumSet.noneOf(GameFlag.class);
    private int playerTurns;
    private int worldTurns;
    private boolean gameOver;

    /**
     * Creates a game state.
     *
     * @param shipMap       ship map
     * @param itemCatalog   item catalog
     * @param entityManager entity manager
     * @param random        seeded random source
     */
    public GameState(ShipMap shipMap, ItemCatalog itemCatalog, EntityManager entityManager, GameRandom random,
            GameRoute route) {
        this.shipMap = shipMap;
        this.itemCatalog = itemCatalog;
        this.entityManager = entityManager;
        this.random = random;
        this.route = route;
    }

    /**
     * Returns the prefabricated route selected for this game.
     *
     * @return selected game route
     */
    public GameRoute getRoute() {
        return route;
    }

    /**
     * Adds a progress flag.
     *
     * @param flag flag to add
     */
    public void addFlag(GameFlag flag) {
        flags.add(flag);
    }

    /**
     * Indicates whether a progress flag is active.
     *
     * @param flag flag to inspect
     * @return {@code true} when active
     */
    public boolean hasFlag(GameFlag flag) {
        return flags.contains(flag);
    }

    /**
     * Returns a read-only snapshot of active flags.
     *
     * @return active flags
     */
    public Set<GameFlag> getFlags() {
        return Set.copyOf(flags);
    }

    /**
     * Returns the room currently occupied by the player.
     *
     * @return current room
     */
    public Room getCurrentRoom() {
        return shipMap.getRoom(entityManager.getPlayer().getCurrentRoom());
    }

    /**
     * Returns a room by identifier.
     *
     * @param id room identifier
     * @return matching room
     */
    public Room getRoom(RoomID id) {
        return shipMap.getRoom(id);
    }

    /**
     * Returns the player inventory.
     *
     * @return inventory
     */
    public Inventory getInventory() {
        return entityManager.getPlayer().getInventory();
    }

    /**
     * Returns the item catalog.
     *
     * @return item catalog
     */
    public ItemCatalog getItemCatalog() {
        return itemCatalog;
    }

    /**
     * Returns the entity manager.
     *
     * @return entity manager
     */
    public EntityManager getEntityManager() {
        return entityManager;
    }

    /**
     * Moves the player in a direction.
     *
     * @param direction movement direction
     * @return {@code true} when movement succeeds
     */
    public boolean move(Direction direction) {
        Room destination = shipMap.getConnectedRoom(getCurrentRoom(), direction);
        if (destination == null || isRoomLockedForPlayer(destination.getId())) {
            return false;
        }

        entityManager.getPlayer().moveTo(destination.getId());

        return true;
    }

    /**
     * Indicates whether the player lacks the requirement for a room.
     *
     * @param roomId destination room identifier
     * @return {@code true} when the room is currently locked
     */
    public boolean isRoomLockedForPlayer(RoomID roomId) {
        return roomId == RoomID.COMMAND && !getInventory().has(ItemID.CREW_CARD);
    }

    /**
     * Registers a player turn and advances the world every two player turns.
     */
    public void advancePlayerTurn() {
        playerTurns++;

        if (playerTurns % 2 == 0) {
            advanceWorldTurn();
        }
    }

    /**
     * Advances the world one turn.
     */
    public void advanceWorldTurn() {
        worldTurns++;
        entityManager.takeEntityTurns(this);
    }

    /**
     * Returns the number of player turns already processed.
     *
     * @return player turn count
     */
    public int getPlayerTurns() {
        return playerTurns;
    }

    /**
     * Returns the number of world turns already processed.
     *
     * @return world turn count
     */
    public int getWorldTurns() {
        return worldTurns;
    }

    /**
     * Takes an item from the current room.
     *
     * @param item item identifier
     * @return {@code true} when the item is taken
     */
    public boolean takeItem(ItemID item) {
        Room currentRoom = getCurrentRoom();
        if (!currentRoom.removeItem(item)) {
            return false;
        }

        getInventory().add(item);
        return true;
    }

    /**
     * Places an inventory item in the current room.
     *
     * @param item item identifier
     * @return {@code true} when the item is placed
     */
    public boolean placeItem(ItemID item) {
        Item definition = itemCatalog.getItem(item);
        if (definition == null || !definition.placeable() || !getInventory().has(item)) {
            return false;
        }

        getInventory().remove(item);
        return getCurrentRoom().addItem(item);
    }

    /**
     * Uses an inventory item.
     *
     * @param item item identifier
     * @return {@code true} when the item action succeeds
     */
    public boolean useItem(ItemID item) {
        Item definition = itemCatalog.getItem(item);
        return definition != null && definition.usable() && definition.use(this);
    }

    /**
     * Interacts with the crew member.
     *
     * @return interaction result
     */
    public CrewMember.CrewInteractionResult interactWithCrewMember() {
        return entityManager.interactWithCrewMember(this);
    }

    /**
     * Indicates whether the game is over.
     *
     * @return {@code true} when defeated
     */
    public boolean isGameOver() {
        return gameOver;
    }

    /**
     * Marks the game as over.
     */
    public void markGameOver() {
        gameOver = true;
    }

    public GameRandom getRandom() {
        return random;
    }
}
