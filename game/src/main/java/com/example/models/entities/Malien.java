package com.example.models.entities;

import com.example.models.map.RoomID;
import com.example.models.game.GameState;
import com.example.models.generation.GameRandom;

/**
 * Hostile entity that threatens the player.
 */
public final class Malien extends Entity implements TurnActor {
    private int distractedTurns;

    /**
     * Creates Malien.
     *
     * @param currentRoom initial room identifier
     */
    public Malien(RoomID currentRoom) {
        super("malien", "Malien", currentRoom);
    }

    /**
     * Indicates whether Malien is currently distracted.
     *
     * @return {@code true} while distraction turns remain
     */
    public boolean isDistracted() {
        return distractedTurns > 0;
    }

    /**
     * Returns how many turns Malien will remain distracted.
     *
     * @return remaining distraction turns
     */
    public int getDistractedTurns() {
        return distractedTurns;
    }

    /**
     * Distracts Malien for a number of turns.
     *
     * @param turns distraction duration; negative values are treated as zero
     */
    public void distract(int turns) {
        distractedTurns = Math.max(0, turns);
    }

    /**
     * Reduces the remaining distraction duration by one turn.
     */
    public void reduceDistraction() {
        if (distractedTurns > 0) {
            distractedTurns--;
        }
    }

    /**
     * Executes Malien's turn.
     *
     * <p>
     * Program Malien's room-to-room movement in this method, after the spawn
     * block.
     *
     * @param state current game state
     */
    @Override
    public void takeTurn(GameState state) {
        if (isDistracted()) {
            reduceDistraction();
            return;
        }

        boolean spawned = (getCurrentRoom() != null);
        if (!spawned && state.getWorldTurns() < EntityRules.Malien.WORLD_TURNS_BEFORE_SPAWN) {
            return;
        }

        if (!spawned) {
            moveTo(selectSpawnRoom(state));
            return;
        }

        if (state.getRandom().chance(EntityRules.Malien.SKIP_MOVEMENT_CHANCE)) {
            return;
        }

        moveTo(selectMovementTarget(state));
    }

    /**
     * Selects the room where Malien appears for the first time.
     *
     * @param state current game state
     * @return allowed spawn room
     */
    private RoomID selectSpawnRoom(GameState state) {
        RoomID target = randomRoom(state);

        while (isAwakeCrewRoom(state, target)) {
            target = randomRoom(state);
        }

        return target;
    }

    /**
     * Selects Malien's next movement target.
     *
     * <p>
     * Targets occupied by the player or by an awake crew member can be rerolled
     * according to the configured chance.
     *
     * @param state current game state
     * @return selected movement target
     */
    private RoomID selectMovementTarget(GameState state) {
        RoomID target = randomRoom(state);
        GameRandom random = state.getRandom();

        if (isOccupiedTarget(state, target) && random.chance(EntityRules.Malien.OCCUPIED_TARGET_REROLL_CHANCE)) {
            target = randomRoom(state);
        }

        return target;
    }

    /**
     * Selects a random room that non-player entities are allowed to occupy.
     *
     * @param state current game state
     * @return allowed room identifier
     */
    private RoomID randomRoom(GameState state) {
        RoomID target = state.getRandom().getRandomIndex(RoomID.values());
        while (!EntityRules.Shared.isNpcRoomAllowed(target)) {
            target = state.getRandom().getRandomIndex(RoomID.values());
        }

        return target;
    }

    /**
     * Indicates whether a target contains the player or an awake crew member.
     *
     * @param state  current game state
     * @param target target room
     * @return {@code true} when the room is occupied by a relevant target
     */
    private boolean isOccupiedTarget(GameState state, RoomID target) {
        return target == state.getEntityManager().getPlayer().getCurrentRoom()
                || isAwakeCrewRoom(state, target);
    }

    /**
     * Indicates whether an awake crew member occupies a room.
     *
     * @param state  current game state
     * @param target target room
     * @return {@code true} when the living, awake crew member is in the room
     */
    private boolean isAwakeCrewRoom(GameState state, RoomID target) {
        CrewMember crewMember = state.getEntityManager().getCrewMember();
        return crewMember.isAlive() && !crewMember.isAsleep() && target == crewMember.getCurrentRoom();
    }
}
