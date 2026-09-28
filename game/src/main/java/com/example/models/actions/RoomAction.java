package com.example.models.actions;

/**
 * Physical interactions available inside ship rooms.
 *
 * <p>
 * This enum intentionally excludes utility actions that belong to other menus,
 * such as checking the map or inspecting carried inventory objects.
 */
public enum RoomAction {
    /** Check the personal locker in the bedroom. */
    CHECK_LOCKER("Revisar taquilla"),

    /** Collect visible items in the current room. */
    COLLECT_VISIBLE_ITEMS("Recollir objectes visibles"),

    /** Inspect the flooded drain without taking anything directly. */
    INSPECT_FLOODED_DRAIN("Mirar sota l'aigua"),

    /** Wake the crew member if present and asleep. */
    WAKE_CREW("Despertar tripulant"),

    /** Talk to the crew member if present. */
    TALK_CREW("Parlar amb tripulant"),

    /** Search the dining table. */
    SEARCH_TABLE("Registrar taula"),

    /** Open the office desk. */
    OPEN_DESK("Obrir escriptori"),

    /** Open the suit locker. */
    OPEN_SUIT_LOCKER("Obrir armari del vestit"),

    /** Search the workshop tools. */
    SEARCH_TOOLS("Registrar eines"),

    /** Inspect the damaged engines. */
    INSPECT_ENGINES("Inspeccionar motors"),

    /** Repair the damaged engines. */
    REPAIR_ENGINES("Reparar motors"),

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
