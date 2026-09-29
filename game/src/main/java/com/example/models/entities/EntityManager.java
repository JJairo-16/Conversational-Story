package com.example.models.entities;

import com.example.models.game.GameFlag;
import com.example.models.game.GameState;
import com.example.models.items.ItemID;

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
        checkMalienEncounter(state);
        crewMember.takeTurn(state);
        checkMalienEncounter(state);
    }

    /**
     * Checks whether Malien meets the player or an awake crew member.
     *
     * @param state current game state
     * @return {@code true} when Malien meets the player and ends the game
     */
    public boolean checkMalienEncounter(GameState state) {
        if (malien.getCurrentRoom() == null || malien.isDistracted()) {
            return false;
        }

        boolean meetsPlayer = malien.getCurrentRoom() == player.getCurrentRoom();
        boolean meetsAwakeCrew = crewMember.isAlive()
                && !crewMember.isAsleep()
                && malien.getCurrentRoom() == crewMember.getCurrentRoom();

        if (!meetsPlayer && !meetsAwakeCrew) {
            return false;
        }

        if (meetsPlayer && state.getInventory().has(ItemID.DONUT)) {
            state.getInventory().remove(ItemID.DONUT);
            malien.distract(EntityRules.Malien.DONUT_DISTRACTION_TURNS);
            state.addEventMessage("En Malien et troba, però llances automàticament el dònut. Queda atordit durant "
                    + EntityRules.Malien.DONUT_DISTRACTION_TURNS + " torns.");
            return false;
        }

        if (meetsAwakeCrew) {
            crewMember.die();
            state.addFlag(GameFlag.CREW_MEMBER_DIES);
            state.addEventMessage("En Malien troba el tripulant i el deixa fora de combat.");
        }

        if (meetsPlayer) {
            state.markGameOver();
            state.addFlag(GameFlag.PLAYER_DIE);
            state.addEventMessage("En Malien t'ha trobat. La missió s'ha acabat.");
            return true;
        }

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
