package com.example.models.generation;

import com.example.models.entities.CrewMember.WakeResultType;
import com.example.models.entities.EntityManager;
import com.example.models.items.ItemID;
import com.example.models.map.RoomID;
import com.example.models.map.ShipMap;

/**
 * Prefabricated game routes used by the seeded game generator.
 */
public enum GameRoute {
    /** Route where the crew card has fallen into the flooded bathroom drain. */
    DRAIN_CARD("Tarja al desguas") {
        @Override
        public void setup(ShipMap shipMap, EntityManager entityManager) {
            placeCommonProgressionItems(shipMap);
            shipMap.getRoom(RoomID.BEDROOM).addItem(ItemID.FLASHLIGHT);
            shipMap.getRoom(RoomID.LIVING_ROOM).addItem(ItemID.SMALL_KEY);
            shipMap.getRoom(RoomID.BATHROOM).addItem(ItemID.CREW_CARD);
            shipMap.getRoom(RoomID.OFFICES).addItem(ItemID.SNORKEL);
            entityManager.getCrewMember().moveTo(RoomID.BEDROOM);
            entityManager.getCrewMember().setWakeBehavior(WakeResultType.TRUE_HINT,
                    "El tripulant desperta sobresaltat: \"La tarja va caure al desguas dels banys. Necessites alguna cosa per mirar sota l'aigua.\"");
        }
    },

    /** Route where the crew member gives the crew card after being woken up. */
    CREW_CARD("Tarja del tripulant") {
        @Override
        public void setup(ShipMap shipMap, EntityManager entityManager) {
            placeCommonProgressionItems(shipMap);
            shipMap.getRoom(RoomID.BATHROOM).addItem(ItemID.SMALL_KEY);
            shipMap.getRoom(RoomID.BEDROOM).addItem(ItemID.SNORKEL);
            shipMap.getRoom(RoomID.KITCHEN).addItem(ItemID.FLASHLIGHT);
            entityManager.getCrewMember().moveTo(RoomID.BEDROOM);
            entityManager.getCrewMember().setWakeBehavior(WakeResultType.START_WANDERING,
                    "El tripulant desperta, et dona la seva tarja i marxa a revisar la nau.");
        }
    },

    /** Route where the crew card is hidden in the office desk. */
    OFFICE_CARD("Tarja a oficines") {
        @Override
        public void setup(ShipMap shipMap, EntityManager entityManager) {
            placeCommonProgressionItems(shipMap);
            shipMap.getRoom(RoomID.BEDROOM).addItem(ItemID.SMALL_KEY);
            shipMap.getRoom(RoomID.LIVING_ROOM).addItem(ItemID.SNORKEL);
            shipMap.getRoom(RoomID.OFFICES).addItem(ItemID.CREW_CARD);
            shipMap.getRoom(RoomID.WORKSHOPS).addItem(ItemID.FLASHLIGHT);
            entityManager.getCrewMember().moveTo(RoomID.BEDROOM);
            entityManager.getCrewMember().setWakeBehavior(WakeResultType.FALSE_HINT,
                    "El tripulant desperta confos i insisteix que la tarja es als banys, pero no sembla gaire segur.");
        }
    };

    private final String displayName;

    GameRoute(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Applies the route setup to the generated game objects.
     *
     * @param shipMap       ship map to populate
     * @param entityManager entities to position
     */
    public abstract void setup(ShipMap shipMap, EntityManager entityManager);

    /**
     * Returns a player-readable route name.
     *
     * @return display name
     */
    public String getDisplayName() {
        return displayName;
    }

    private static void placeCommonProgressionItems(ShipMap shipMap) {
        shipMap.getRoom(RoomID.WORKSHOPS).addItem(ItemID.REPAIR_TOOL);
        shipMap.getRoom(RoomID.KITCHEN).addItem(ItemID.DONUT);
    }
}
