package com.example.models.generation;

import com.example.models.entities.CrewMember;
import com.example.models.entities.EntityManager;
import com.example.models.entities.Malien;
import com.example.models.entities.Player;
import com.example.models.game.GameState;
import com.example.models.items.ItemCatalog;
import com.example.models.map.RoomID;
import com.example.models.map.ShipMap;

/**
 * Creates a game state from a seeded generator.
 */
public final class GameGenerator {
    private final GameRandom random;

    /**
     * Creates a game generator.
     *
     * @param random seeded random source
     */
    public GameGenerator(GameRandom random) {
        this.random = random;
    }

    /**
     * Generates a complete game scaffold.
     *
     * @return generated game state
     */
    public GameState generate() {
        ShipMap shipMap = createShipMap();
        ItemCatalog itemCatalog = createItemCatalog();
        EntityManager entityManager = createEntityManager();
        GameRoute route = selectRoute();
        route.setup(shipMap, entityManager);
        GameState state = new GameState(shipMap, itemCatalog, entityManager, random, route);
        return state;
    }

    /**
     * Creates the ship map.
     *
     * @return ship map
     */
    private ShipMap createShipMap() {
        return new ShipMap();
    }

    /**
     * Creates the item catalog.
     *
     * @return item catalog
     */
    private ItemCatalog createItemCatalog() {
        return new ItemCatalog();
    }

    /**
     * Creates the starting entities.
     *
     * @return entity manager
     */
    private EntityManager createEntityManager() {
        Player player = new Player(RoomID.BEDROOM);
        Malien malien = new Malien(RoomID.BEDROOM);
        CrewMember crewMember = new CrewMember(RoomID.BEDROOM);
        return new EntityManager(player, malien, crewMember);
    }

    private GameRoute selectRoute() {
        GameRoute[] routes = GameRoute.values();
        return routes[random.nextInt(routes.length)];
    }
}
