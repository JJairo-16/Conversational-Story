package com.example.utils.ui.helpers;

import com.example.models.entities.CrewMember;
import com.example.models.entities.Entity;
import com.example.models.entities.Malien;
import com.example.models.entities.Player;
import com.example.models.game.GameState;
import com.example.models.map.Room;
import com.example.models.map.RoomID;
import com.example.utils.ui.Prettier;

/**
 * Prints entity-focused summaries for console screens.
 */
public final class EntityDisplayer {
    private EntityDisplayer() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * Prints map and state information for an entity.
     *
     * <p>
     * Common entity data is printed first, then type-specific state is added for
     * the player, Malien or the crew member.
     *
     * @param entity entity to inspect
     * @param state  current game state
     */
    public static void printEntityMapInfo(Entity entity, GameState state) {
        Prettier.printTitle(formatEntityName(entity));
        System.out.printf("Posició: %s%n", formatEntityPosition(state, entity));

        if (entity instanceof Player player) {
            printPlayerMapInfo(player);
        } else if (entity instanceof Malien malien) {
            printMalienMapInfo(malien);
        } else if (entity instanceof CrewMember crewMember) {
            printCrewMemberMapInfo(crewMember);
        }
    }

    private static String formatEntityPosition(GameState state, Entity entity) {
        if (entity instanceof Malien && entity.getCurrentRoom() == null) {
            return "No detectat";
        }

        return formatRoomName(state, entity.getCurrentRoom());
    }

    /**
     * Prints player-specific map information.
     *
     * @param player player to inspect
     */
    private static void printPlayerMapInfo(Player player) {
        System.out.printf("Vestit d'astronauta: %s%n", formatBoolean(player.isAstronautSuitEquipped()));
    }

    /**
     * Prints Malien-specific map information.
     *
     * @param malien Malien to inspect
     */
    private static void printMalienMapInfo(Malien malien) {
        System.out.printf("Distracció activa: %s%n", formatBoolean(malien.isDistracted()));
        System.out.printf("Torns de distracció restants: %d%n", malien.getDistractedTurns());
    }

    /**
     * Prints crew member-specific map information.
     *
     * @param crewMember crew member to inspect
     */
    private static void printCrewMemberMapInfo(CrewMember crewMember) {
        System.out.printf("Viu: %s%n", formatBoolean(crewMember.isAlive()));
        System.out.printf("Estat: %s%n", formatCrewMemberState(crewMember));
    }

    /**
     * Resolves a room identifier into its display name.
     *
     * @param state current game state
     * @param id    room identifier
     * @return room name, or a fallback label when the room is unknown
     */
    public static String formatRoomName(GameState state, RoomID id) {
        Room room = state.getRoom(id);
        return room == null ? "Sala desconeguda" : room.getName();
    }

    /**
     * Formats a boolean state for console output.
     *
     * @param value state value
     * @return localized state label
     */
    private static String formatBoolean(boolean value) {
        return value ? "Sí" : "No";
    }

    /**
     * Formats an entity name with role details when needed.
     *
     * <p>
     * The player is annotated so the map makes clear which tracked entity is under
     * direct player control.
     *
     * @param entity entity to format
     * @return readable entity name
     */
    public static String formatEntityName(Entity entity) {
        return entity.getName() + (entity instanceof Player ? " (Jugador)" : "");
    }

    /**
     * Formats the crew member's current activity as a single state.
     *
     * @param crewMember crew member to inspect
     * @return readable activity state
     */
    private static String formatCrewMemberState(CrewMember crewMember) {
        if (crewMember.isAsleep()) {
            return "Dormint";
        }

        if (crewMember.isWandering()) {
            return "Deambulant";
        }

        return "Despert";
    }
}
