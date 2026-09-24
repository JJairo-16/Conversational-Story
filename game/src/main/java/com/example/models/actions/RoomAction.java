package com.example.models.actions;

/**
 * Room-specific actions prepared for future escape-room puzzles.
 */
public enum RoomAction {
    /** Search the cryosleep capsule area. */
    SEARCH_CAPSULE("Registrar càpsula"),

    /** Check the personal locker in the bedroom. */
    CHECK_LOCKER("Revisar taquilla"),

    /** Inspect the flooded drain without taking anything directly. */
    INSPECT_FLOODED_DRAIN("Mirar sota l'aigua"),

    /** Try to lower or redirect the bathroom water. */
    DRAIN_WATER("Drenar aigua"),

    /** Search the dining table. */
    SEARCH_TABLE("Registrar taula"),

    /** Talk to the crew member if present. */
    TALK_CREW("Parlar amb tripulant"),

    /** Search the dark pantry for food or bait. */
    SEARCH_DARK_PANTRY("Registrar rebost a les fosques"),

    /** Prepare bait for Malien in the dark kitchen. */
    PREPARE_BAIT_IN_DARK("Preparar esquer a la cuina"),

    /** Check the office terminal. */
    CHECK_TERMINAL("Consultar terminal"),

    /** Open the office desk. */
    OPEN_DESK("Obrir escriptori"),

    /** Open the suit locker. */
    OPEN_SUIT_LOCKER("Obrir armari del vestit"),

    /** Equip the astronaut suit. */
    EQUIP_SUIT("Equipar vestit"),

    /** Search the workshop tools. */
    SEARCH_TOOLS("Registrar eines"),

    /** Repair or calibrate the repair tool. */
    CALIBRATE_TOOL("Calibrar eina"),

    /** Inspect the damaged engines. */
    INSPECT_ENGINES("Inspeccionar motors"),

    /** Repair the damaged engines. */
    REPAIR_ENGINES("Reparar motors"),

    /** Check ship and entity positions. */
    CHECK_SHIP_MAP("Consultar mapa"),

    /** Start the engines after repairs. */
    START_ENGINES("Engegar motors");

    private final String displayName;
    RoomAction(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Returns the label shown in room interaction submenus.
     *
     * @return action label
     */
    public String getDisplayName() {
        return displayName;
    }

}
