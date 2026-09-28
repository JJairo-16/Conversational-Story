package com.example.models.entities;

import com.example.models.game.GameState;
import com.example.models.generation.GameRoute;
import com.example.models.items.ItemID;
import com.example.models.map.RoomID;

/**
 * Crew member NPC that can sleep, wander and die.
 *
 * <p>
 * The crew member starts alive and asleep. Waking and wandering state are used
 * by room interactions and by the map information screen.
 */
public final class CrewMember extends Entity implements TurnActor {
    private boolean asleep = true;
    private boolean wandering;
    private boolean alive = true;
    private WakeResult wakeResult = new WakeResult(WakeResultType.TRUE_HINT, "");

    /**
     * Creates the crew member.
     *
     * @param currentRoom initial room identifier
     */
    public CrewMember(RoomID currentRoom) {
        super("crew-member", "Tripulant", currentRoom);
    }

    /**
     * Indicates whether the crew member is asleep.
     *
     * @return {@code true} when asleep
     */
    public boolean isAsleep() {
        return asleep;
    }

    /**
     * Wakes the crew member when still asleep.
     *
     * @return wake result
     */
    private CrewInteractionResult wakeUp(GameState state) {
        if (!asleep) {
            return talk(state);
        }

        asleep = false;
        if (wakeResult.type() == WakeResultType.START_WANDERING) {
            startWandering();
        }

        applyWakeResult(state);
        return new CrewInteractionResult(CrewInteractionResultType.WOKEN_UP, wakeResult.message());
    }

    /**
     * Defines what happens when this crew member is woken in the selected route.
     *
     * @param type    wake outcome
     * @param message message shown to the player
     */
    public void setWakeBehavior(WakeResultType type, String message) {
        wakeResult = new WakeResult(type, message == null ? "" : message);
    }

    /**
     * Handles player interaction with this crew member.
     *
     * @param state current game state
     * @return interaction result and message
     */
    public CrewInteractionResult interact(GameState state) {
        if (!alive) {
            return new CrewInteractionResult(CrewInteractionResultType.DEAD, "El tripulant no pot respondre.");
        }

        if (asleep) {
            return wakeUp(state);
        }

        return talk(state);
    }

    private CrewInteractionResult talk(GameState state) {
        if (state.getRoute() == GameRoute.DRAIN_CARD) {
            return new CrewInteractionResult(CrewInteractionResultType.TALKED,
                    "El tripulant insisteix: \"Busca al bany inundat. Si pots mirar sota l'aigua, trobaras la tarja.\"");
        }

        if (state.getRoute() == GameRoute.CREW_CARD) {
            return new CrewInteractionResult(CrewInteractionResultType.TALKED,
                    "El tripulant diu: \"Ja tens la meva tarja. Ves amb compte amb en Malien.\"");
        }

        if (state.getRoute() == GameRoute.OFFICE_CARD) {
            return new CrewInteractionResult(CrewInteractionResultType.TALKED,
                    "El tripulant dubta: \"Potser era al bany... o potser la vaig veure a oficines.\"");
        }

        return new CrewInteractionResult(CrewInteractionResultType.TALKED,
                "El tripulant no recorda res més que et pugui ajudar.");
    }

    private void applyWakeResult(GameState state) {
        if (wakeResult.type() == WakeResultType.START_WANDERING && state.getRoute() == GameRoute.CREW_CARD
                && !state.getInventory().has(ItemID.CREW_CARD)) {
            state.getInventory().add(ItemID.CREW_CARD);
        }
    }

    /**
     * Indicates whether the crew member is wandering.
     *
     * @return {@code true} when wandering
     */
    public boolean isWandering() {
        return wandering;
    }

    /**
     * Indicates whether the crew member is still alive.
     *
     * @return {@code true} when alive
     */
    public boolean isAlive() {
        return alive;
    }

    /**
     * Marks the crew member as dead and stops any wandering behavior.
     */
    public void die() {
        alive = false;
        wandering = false;
    }

    /**
     * Enables crew member wandering.
     */
    public void startWandering() {
        wandering = true;
    }

    /**
     * Stops crew member wandering.
     */
    public void stopWandering() {
        wandering = false;
    }

    /**
     * Executes the crew member's turn.
     *
     * <p>
     * Scaffold: sleeping crew members do nothing. Future wandering and hint logic
     * belongs here.
     *
     * @param state current game state
     */
    @Override
    public void takeTurn(com.example.models.game.GameState state) {
        if (asleep || !wandering) {
            return;
        }
    }

    /**
     * Structured result produced after trying to wake the crew member.
     */
    public record WakeResult(WakeResultType type, String message) {
    }

    /**
     * Structured result produced after interacting with the crew member.
     */
    public record CrewInteractionResult(CrewInteractionResultType type, String message) {
    }

    /**
     * Possible structured outcomes of crew interaction.
     */
    public enum CrewInteractionResultType {
        /** The crew member was woken up by this interaction. */
        WOKEN_UP,

        /** The crew member was already awake and answered. */
        TALKED,

        /** The crew member was already awake. */
        ALREADY_AWAKE,

        /** The player is not in the same room as the crew member. */
        NOT_IN_ROOM,

        /** The crew member is dead. */
        DEAD
    }

    /**
     * Possible structured outcomes of waking the crew member.
     */
    public enum WakeResultType {
        /** The crew member gives a useful hint. */
        TRUE_HINT,

        /** The crew member gives a false hint. */
        FALSE_HINT,

        /** The crew member starts wandering around the ship. */
        START_WANDERING,

        /** The crew member eats a donut. */
        EAT_DONUT,

        /** The player is not in the same room as the crew member. */
        NOT_IN_ROOM,

        /** The crew member was already awake. */
        ALREADY_AWAKE
    }
}
