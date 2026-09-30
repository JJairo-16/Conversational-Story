package com.example;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.example.controllers.RoomActionHandler;
import com.example.models.actions.RoomAction;
import com.example.models.entities.CrewMember;
import com.example.models.entities.EntityManager;
import com.example.models.entities.EntityRules;
import com.example.models.entities.Malien;
import com.example.models.entities.Player;
import com.example.models.game.GameState;
import com.example.models.generation.GameRandom;
import com.example.models.generation.GameRoute;
import com.example.models.items.ItemCatalog;
import com.example.models.items.ItemID;
import com.example.models.map.Direction;
import com.example.models.map.RoomID;
import com.example.models.map.ShipMap;

/**
 * Verifies that each prefabricated route can reach the engine startup.
 */
final class RouteFeasibilityTest {
    @Test
    void drainCardRouteCanBeCompleted() {
        GameState state = createState(GameRoute.DRAIN_CARD);

        action(state, RoomAction.CHECK_LOCKER);
        move(state, Direction.WEST);
        action(state, RoomAction.SEARCH_TABLE);
        move(state, Direction.EAST);
        move(state, Direction.NORTH);
        move(state, Direction.WEST);
        action(state, RoomAction.OPEN_DESK);
        move(state, Direction.EAST);
        action(state, RoomAction.INSPECT_FLOODED_DRAIN);
        action(state, RoomAction.COLLECT_VISIBLE_ITEMS);
        move(state, Direction.WEST);
        move(state, Direction.WEST);

        completeFromCostume(state);
    }

    @Test
    void crewCardRouteCanBeCompleted() {
        GameState state = createState(GameRoute.CREW_CARD);

        action(state, RoomAction.WAKE_CREW);
        move(state, Direction.NORTH);
        action(state, RoomAction.COLLECT_VISIBLE_ITEMS);
        move(state, Direction.SOUTH);
        move(state, Direction.WEST);
        action(state, RoomAction.SEARCH_TABLE);
        assertTrue(state.getInventory().has(ItemID.FLASHLIGHT), "Search table must provide the flashlight");
        move(state, Direction.WEST);
        use(state, ItemID.FLASHLIGHT);
        action(state, RoomAction.COLLECT_VISIBLE_ITEMS);
        move(state, Direction.NORTH);

        completeFromCostume(state);
    }

    @Test
    void officeCardRouteCanBeCompleted() {
        GameState state = createState(GameRoute.OFFICE_CARD);

        action(state, RoomAction.CHECK_LOCKER);
        move(state, Direction.WEST);
        action(state, RoomAction.SEARCH_TABLE);
        move(state, Direction.EAST);
        move(state, Direction.NORTH);
        move(state, Direction.WEST);
        action(state, RoomAction.OPEN_DESK);
        move(state, Direction.NORTH);
        action(state, RoomAction.SEARCH_TOOLS);
        move(state, Direction.SOUTH);
        move(state, Direction.WEST);

        completeFromCostume(state);
    }

    @Test
    void flashlightIsNeverPlacedInKitchen() {
        for (GameRoute route : GameRoute.values()) {
            GameState state = createState(route);

            assertFalse(state.getRoom(RoomID.KITCHEN).hasItem(ItemID.FLASHLIGHT),
                    () -> route + " must not place the flashlight in the kitchen");
        }
    }

    @Test
    void actionOwnedItemsAreNotCollectedAsVisibleItems() {
        GameState state = createState(GameRoute.DRAIN_CARD);

        failure(state, RoomAction.COLLECT_VISIBLE_ITEMS);

        assertFalse(state.getInventory().has(ItemID.FLASHLIGHT),
                "Locker items must not be collected as visible room items");

        action(state, RoomAction.CHECK_LOCKER);

        assertTrue(state.getInventory().has(ItemID.FLASHLIGHT),
                "Locker action must still provide its item");
    }

    @Test
    void deskItemsAreHiddenFromVisibleCollection() {
        GameState state = createState(GameRoute.OFFICE_CARD);

        move(state, Direction.NORTH);
        move(state, Direction.WEST);

        failure(state, RoomAction.COLLECT_VISIBLE_ITEMS);

        assertFalse(state.getInventory().has(ItemID.CREW_CARD),
                "Desk items must not be collected as visible room items");

        action(state, RoomAction.OPEN_DESK);

        assertTrue(state.getInventory().has(ItemID.CREW_CARD),
                "Desk action must still provide the card");
    }

    @Test
    void itemsWithoutAnotherActionCanStillBeCollectedAsVisibleItems() {
        GameState state = createState(GameRoute.CREW_CARD);

        move(state, Direction.NORTH);
        action(state, RoomAction.COLLECT_VISIBLE_ITEMS);

        assertTrue(state.getInventory().has(ItemID.SMALL_KEY),
                "Visible items with no room action owner must remain collectable");
    }

    @Test
    void snorkelCanInspectBathroomEvenWhenCardIsElsewhere() {
        GameState state = createState(GameRoute.CREW_CARD);

        action(state, RoomAction.CHECK_LOCKER);
        move(state, Direction.NORTH);
        action(state, RoomAction.INSPECT_FLOODED_DRAIN);

        assertTrue(state.isFloodedDrainRevealed(),
                "Using the snorkel in the bathroom must run the flooded drain inspection");
    }

    @Test
    void malienSpawnsAfterConfiguredWorldTurns() {
        GameState state = createState(GameRoute.DRAIN_CARD, new Malien(null));

        assertNull(state.getEntityManager().getMalien().getCurrentRoom(), "Malien must start hidden");

        for (int i = 1; i < EntityRules.Malien.WORLD_TURNS_BEFORE_SPAWN; i++) {
            state.advanceWorldTurn();
            assertNull(state.getEntityManager().getMalien().getCurrentRoom(),
                    "Malien must stay hidden before the configured spawn turn");
        }

        state.advanceWorldTurn();

        assertNotNull(state.getEntityManager().getMalien().getCurrentRoom(),
                "Malien must spawn on the configured world turn");
    }

    @Test
    void malienDoesNotSpawnOnAwakeCrewMember() {
        for (long seed = 1; seed <= 50; seed++) {
            GameState state = createState(GameRoute.DRAIN_CARD, new Malien(null), seed);
            action(state, RoomAction.WAKE_CREW);

            for (int i = 0; i < EntityRules.Malien.WORLD_TURNS_BEFORE_SPAWN; i++) {
                state.advanceWorldTurn();
            }

            assertTrue(state.getEntityManager().getCrewMember().isAlive(), "Crew member must still be alive");
            assertTrue(state.getEntityManager().getMalien().getCurrentRoom()
                    != state.getEntityManager().getCrewMember().getCurrentRoom(),
                    "Malien must not spawn on an awake crew member");
        }
    }

    @Test
    void malienEncounterAffectsPlayerAndAwakeCrewMember() {
        GameState state = createState(GameRoute.CREW_CARD, new Malien(RoomID.BEDROOM));

        action(state, RoomAction.WAKE_CREW);
        state.getEntityManager().checkMalienEncounter(state);

        assertFalse(state.getEntityManager().getCrewMember().isAlive(),
                "Malien must kill an awake crew member in the same room");
        assertTrue(state.isGameOver(), "Malien must end the game when it meets the player");
    }

    @Test
    void playerDonutAutomaticallyDistractsMalienOnEncounter() {
        GameState state = createState(GameRoute.CREW_CARD, new Malien(RoomID.BEDROOM));
        state.getInventory().add(ItemID.DONUT);

        action(state, RoomAction.WAKE_CREW);
        state.getEntityManager().checkMalienEncounter(state);

        assertFalse(state.isGameOver(), "Donut must prevent player death");
        assertTrue(state.getEntityManager().getCrewMember().isAlive(), "Donut must also protect the awake crew member");
        assertFalse(state.getInventory().has(ItemID.DONUT), "Automatic distraction must consume the donut");
        assertTrue(state.getEntityManager().getMalien().isDistracted(), "Automatic donut must distract Malien");
        assertFalse(state.consumeEventMessages().isEmpty(), "Automatic donut use must produce a message");
    }

    @Test
    void npcsRespectBannedRooms() {
        for (long seed = 1; seed <= 50; seed++) {
            GameState malienState = createState(GameRoute.DRAIN_CARD, new Malien(null), seed);
            for (int i = 0; i < EntityRules.Malien.WORLD_TURNS_BEFORE_SPAWN; i++) {
                malienState.advanceWorldTurn();
            }
            assertFalse(EntityRules.Shared.npcBannedRooms()
                    .contains(malienState.getEntityManager().getMalien().getCurrentRoom()),
                    "Malien must not spawn in banned NPC rooms");

            GameState crewState = createState(GameRoute.CREW_CARD, new Malien(null), seed);
            action(crewState, RoomAction.WAKE_CREW);
            crewState.getEntityManager().getCrewMember().takeTurn(crewState);
            assertFalse(EntityRules.Shared.npcBannedRooms()
                    .contains(crewState.getEntityManager().getCrewMember().getCurrentRoom()),
                    "Crew member must not move into banned NPC rooms");
        }
    }

    private static GameState createState(GameRoute route) {
        return createState(route, new Malien(RoomID.BEDROOM));
    }

    private static GameState createState(GameRoute route, Malien malien) {
        return createState(route, malien, 1);
    }

    private static GameState createState(GameRoute route, Malien malien, long seed) {
        ShipMap shipMap = new ShipMap();
        EntityManager entityManager = new EntityManager(
                new Player(RoomID.BEDROOM),
                malien,
                new CrewMember(RoomID.BEDROOM));
        route.setup(shipMap, entityManager);
        return new GameState(shipMap, new ItemCatalog(), entityManager, new GameRandom(seed), route);
    }

    private static void completeFromCostume(GameState state) {
        action(state, RoomAction.OPEN_SUIT_LOCKER);
        use(state, ItemID.ASTRONAUT_SUIT);
        move(state, Direction.EAST);
        move(state, Direction.NORTH);
        action(state, RoomAction.SEARCH_TOOLS);
        assertTrue(state.getInventory().has(ItemID.REPAIR_TOOL), "Route must provide the repair tool");
        move(state, Direction.SOUTH);
        move(state, Direction.SOUTH);
        move(state, Direction.SOUTH);
        move(state, Direction.SOUTH);
        action(state, RoomAction.INSPECT_ENGINES);
        action(state, RoomAction.REPAIR_ENGINES);
        move(state, Direction.NORTH);
        move(state, Direction.NORTH);
        action(state, RoomAction.START_ENGINES);
        assertTrue(state.isGameWon(), "Starting the engines must mark the game as won");
    }

    private static void action(GameState state, RoomAction action) {
        var result = RoomActionHandler.handle(action, state);
        assertTrue(result.success(), () -> action + " failed: " + result.message());
    }

    private static void failure(GameState state, RoomAction action) {
        var result = RoomActionHandler.handle(action, state);
        assertFalse(result.success(), () -> action + " should have failed");
    }

    private static void use(GameState state, ItemID item) {
        var result = state.useItem(item);
        assertTrue(result.success(), () -> "Using " + item + " failed: " + result.message());
    }

    private static void move(GameState state, Direction direction) {
        assertTrue(state.move(direction), () -> "Cannot move " + direction + " from " + state.getCurrentRoom().getId());
    }
}
