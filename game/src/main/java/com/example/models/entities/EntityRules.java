package com.example.models.entities;

import java.util.Collections;
import java.util.Set;

import com.example.models.map.RoomID;

/**
 * Numeric and shared rules used by entity behavior.
 */
public final class EntityRules {
    private EntityRules() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * Rules shared by non-player entities.
     */
    public static final class Shared {
        private static final Set<RoomID> NPC_BANNED_ROOMS = Set.of(RoomID.COMMAND);

        private Shared() {
            throw new UnsupportedOperationException("Utility class");
        }

        /**
         * Returns rooms that non-player entities must not target.
         *
         * @return read-only banned room set
         */
        public static Set<RoomID> npcBannedRooms() {
            return Collections.unmodifiableSet(NPC_BANNED_ROOMS);
        }

        /**
         * Indicates whether a non-player entity can target a room.
         *
         * @param roomId room identifier
         * @return {@code true} when the room is allowed
         */
        public static boolean isNpcRoomAllowed(RoomID roomId) {
            return roomId != null && !NPC_BANNED_ROOMS.contains(roomId);
        }
    }

    /**
     * Rules specific to Malien.
     */
    public static final class Malien {
        /** Number of world turns before Malien appears on the map. */
        public static final int WORLD_TURNS_BEFORE_SPAWN = 3;

        /** Number of Malien turns distracted by one donut. */
        public static final int DONUT_DISTRACTION_TURNS = 3;

        /** Forced distraction duration for lower-risk encounters. */
        public static final int FORCED_DISTRACTION_TURNS_LOW_RISK = 2;

        /** Forced distraction duration for higher-risk encounters. */
        public static final int FORCED_DISTRACTION_TURNS_HIGH_RISK = 1;

        /** Chance to reroll a target occupied by the player or an awake crew member. */
        public static final int OCCUPIED_TARGET_REROLL_CHANCE = 40;

        /** Chance that Malien skips movement during its turn. */
        public static final int SKIP_MOVEMENT_CHANCE = 15;

        private Malien() {
            throw new UnsupportedOperationException("Utility class");
        }
    }

    /**
     * Rules specific to the crew member.
     */
    public static final class CrewMember {
        /** Chance to reroll when the crew member targets Malien's room. */
        public static final int MALIEN_TARGET_REROLL_CHANCE = 65;

        /** Chance that the crew member skips movement during its turn. */
        public static final int SKIP_MOVEMENT_CHANCE = 10;

        private CrewMember() {
            throw new UnsupportedOperationException("Utility class");
        }
    }
}
