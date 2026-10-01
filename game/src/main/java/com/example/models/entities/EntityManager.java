package com.example.models.entities;

import java.util.ArrayList;
import java.util.List;

import com.example.models.game.GameFlag;
import com.example.models.game.GameState;
import com.example.models.items.ItemID;
import com.example.models.map.RoomID;

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
            boolean crewWasWithPlayer = meetsAwakeCrew;
            RoomID escapeRoom = selectEscapeRoom(state);
            
            state.getInventory().remove(ItemID.DONUT);
            malien.distract(EntityRules.Malien.DONUT_DISTRACTION_TURNS);
            
            player.moveTo(escapeRoom);
            if (crewWasWithPlayer) {
                crewMember.moveTo(escapeRoom);
            }
            
            state.addEventMessage("En Malien et troba, però llances automàticament el dònut. Queda atordit durant "
                    + EntityRules.Malien.DONUT_DISTRACTION_TURNS + " torns i aprofiteu per fugir.");
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
     * Selects a room where the player can flee after distracting Malien.
     *
     * @param state current game state
     * @return escape room identifier
     */
    private RoomID selectEscapeRoom(GameState state) {
        List<RoomID> candidates = new ArrayList<>();

        for (RoomID roomId : state.getCurrentRoom().getConnections().values()) {
            if (isEscapeRoomAllowed(state, roomId)) {
                candidates.add(roomId);
            }
        }

        if (candidates.isEmpty()) {
            for (RoomID roomId : RoomID.values()) {
                if (isEscapeRoomAllowed(state, roomId)) {
                    candidates.add(roomId);
                }
            }
        }

        return state.getRandom().getRandomIndex(candidates);
    }

    /**
     * Indicates whether a room can be used as a forced escape destination.
     *
     * @param state  current game state
     * @param roomId candidate room
     * @return {@code true} when the room is safe enough for forced movement
     */
    private boolean isEscapeRoomAllowed(GameState state, RoomID roomId) {
        return roomId != null
                && roomId != malien.getCurrentRoom()
                && !state.isRoomLockedForPlayer(roomId)
                && EntityRules.Shared.isNpcRoomAllowed(roomId);
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
