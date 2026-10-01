package com.example.models.actions;

/**
 * Top-level game menu actions.
 *
 * <p>
 * These values represent menu sections, not typed text commands. Each section is
 * prepared to host its own submenu when the game actions are implemented.
 */
public enum ActionType {
    /** Movement submenu. */
    NAVIGATION("Navegació"),

    /** Inventory submenu. */
    INVENTORY("Inventari"),

    /** Ship map and entity location submenu. */
    MAP("Mapa"),

    /** Room interaction submenu. */
    INTERACTION("Interactuar"),

    /** Conversation with the ship computer. */
    I_HALL("Parlar amb iHall"),

    /** Do nothing and advance the loop. */
    WAIT("Esperar"),

    /** Leave the current game session. */
    EXIT("Sortir");

    private final String displayName;

    ActionType(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Returns the label shown in the game menu.
     *
     * @return menu label
     */
    public String getDisplayName() {
        return displayName;
    }
}
