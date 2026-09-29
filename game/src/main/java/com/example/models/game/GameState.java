package com.example.models.game;

import com.example.models.entities.EntityManager;
import com.example.models.actions.ActionResult;
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
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
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
    private final List<String> eventMessages = new ArrayList<>();
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
     * @param route         selected route
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
     * <p>
     * Flags are limited to puzzle progress not already represented by the route,
     * inventory, current room or entity state.
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
        entityManager.checkMalienEncounter(this);

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
        entityManager.checkMalienEncounter(this);
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
     * Reveals the kitchen donut after using the flashlight.
     */
    public void revealKitchenDonut() {
        addFlag(GameFlag.KITCHEN_DONUT_REVEALED);
    }

    /**
     * Indicates whether the kitchen donut has been revealed.
     *
     * @return {@code true} when visible
     */
    public boolean isKitchenDonutRevealed() {
        return hasFlag(GameFlag.KITCHEN_DONUT_REVEALED);
    }

    /**
     * Inspects the flooded bathroom with the snorkel.
     *
     * @return inspection result
     */
    public ActionResult inspectFloodedDrainWithSnorkel() {
        if (getCurrentRoom().getId() != RoomID.BATHROOM) {
            return ActionResult.failure("Aquí no hi ha prou aigua per fer servir l'snorkel.");
        }

        if (!getInventory().has(ItemID.SNORKEL)) {
            return ActionResult.failure("L'aigua és massa tèrbola. Necessites l'snorkel per mirar sota l'aigua.");
        }

        addFlag(GameFlag.FLOODED_DRAIN_REVEALED);

        if (getCurrentRoom().hasItem(ItemID.CREW_CARD)) {
            return ActionResult.success("Amb l'snorkel mires sota l'aigua i distingeixes la tarja de tripulant.");
        }

        return ActionResult.success("Mires sota l'aigua, però no hi trobes res important.");
    }

    /**
     * Indicates whether the flooded drain has been revealed with the snorkel.
     *
     * @return {@code true} when underwater items can be seen
     */
    public boolean isFloodedDrainRevealed() {
        return hasFlag(GameFlag.FLOODED_DRAIN_REVEALED);
    }

    /**
     * Marks the engines as inspected.
     */
    public void inspectEngines() {
        addFlag(GameFlag.ENGINES_INSPECTED);
    }

    /**
     * Indicates whether the engines have been inspected.
     *
     * @return {@code true} when inspected
     */
    public boolean areEnginesInspected() {
        return hasFlag(GameFlag.ENGINES_INSPECTED);
    }

    /**
     * Marks the engines as repaired.
     */
    public void repairEngines() {
        addFlag(GameFlag.ENGINES_REPAIRED);
    }

    /**
     * Indicates whether the engines have been repaired.
     *
     * @return {@code true} when repaired
     */
    public boolean areEnginesRepaired() {
        return hasFlag(GameFlag.ENGINES_REPAIRED);
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
     * @return item use result
     */
    public ActionResult useItem(ItemID item) {
        Item definition = itemCatalog.getItem(item);
        if (definition == null || !definition.usable()) {
            return ActionResult.failure("Ara mateix no pots utilitzar aquest objecte.");
        }

        return definition.use(this);
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
     * Indicates whether the mission has been completed successfully.
     *
     * @return {@code true} when the engines have been started
     */
    public boolean isGameWon() {
        return hasFlag(GameFlag.MISSION_COMPLETED);
    }

    /**
     * Marks the game as over.
     */
    public void markGameOver() {
        gameOver = true;
    }

    /**
     * Marks the mission as completed.
     */
    public void markGameWon() {
        addFlag(GameFlag.MISSION_COMPLETED);
    }

    /**
     * Adds a message produced by automatic game events.
     *
     * @param message event message
     */
    public void addEventMessage(String message) {
        if (message != null && !message.isBlank()) {
            eventMessages.add(message);
        }
    }

    /**
     * Returns and clears automatic event messages.
     *
     * @return pending event messages
     */
    public List<String> consumeEventMessages() {
        List<String> messages = List.copyOf(eventMessages);
        eventMessages.clear();
        return messages;
    }

    public GameRandom getRandom() {
        return random;
    }
}
