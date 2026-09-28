package com.example.models.entities;

import com.example.models.game.GameState;

/**
 * Owns the game entities and coordinates entity-level events.
 * 
 * @param player     player entity
 * @param malien     hostile entity
 * @param crewMember single crew member NPC
 */
public record EntityManager(Player player, Malien malien, CrewMember crewMember) {

    /**
     * Returns the player entity.
     *
     * @return player
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * Returns Malien.
     *
     * @return Malien
     */
    public Malien getMalien() {
        return malien;
    }

    /**
     * Returns the crew member.
     *
     * @return crew member
     */
    public CrewMember getCrewMember() {
        return crewMember;
    }

    /**
     * Executes non-player entity turns.
     *
     * @param state current game state
     */
    public void takeEntityTurns(GameState state) {
        malien.takeTurn(state);
        crewMember.takeTurn(state);
    }

    /**
     * Checks whether Malien meets the player.
     *
     * @param state current game state
     * @return {@code true} when the encounter ends the game
     */
    public boolean checkMalienEncounter(GameState state) {
        return false;
    }

    /**
     * Interacts with the crew member if the action is valid.
     *
     * @return interaction result
     */
    public CrewMember.CrewInteractionResult interactWithCrewMember(GameState state) {
        if (player.getCurrentRoom() != crewMember.getCurrentRoom()) {
            return new CrewMember.CrewInteractionResult(CrewMember.CrewInteractionResultType.NOT_IN_ROOM,
                    "No hi ha cap tripulant en aquesta sala.");
        }

        return crewMember.interact(state);
    }
}
