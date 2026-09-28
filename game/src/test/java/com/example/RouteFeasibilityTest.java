package com.example;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.example.controllers.RoomActionHandler;
import com.example.models.actions.RoomAction;
import com.example.models.entities.CrewMember;
import com.example.models.entities.EntityManager;
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

        action(state, RoomAction.COLLECT_VISIBLE_ITEMS);
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
        move(state, Direction.WEST);
        action(state, RoomAction.COLLECT_VISIBLE_ITEMS);
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

    private static GameState createState(GameRoute route) {
        ShipMap shipMap = new ShipMap();
        EntityManager entityManager = new EntityManager(
                new Player(RoomID.BEDROOM),
                new Malien(RoomID.BEDROOM),
                new CrewMember(RoomID.BEDROOM));
        route.setup(shipMap, entityManager);
        return new GameState(shipMap, new ItemCatalog(), entityManager, new GameRandom(1), route);
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
    }

    private static void action(GameState state, RoomAction action) {
        var result = RoomActionHandler.handle(action, state);
        assertTrue(result.success(), () -> action + " failed: " + result.message());
    }

    private static void use(GameState state, ItemID item) {
        var result = state.useItem(item);
        assertTrue(result.success(), () -> "Using " + item + " failed: " + result.message());
    }

    private static void move(GameState state, Direction direction) {
        assertTrue(state.move(direction), () -> "Cannot move " + direction + " from " + state.getCurrentRoom().getId());
    }
}
