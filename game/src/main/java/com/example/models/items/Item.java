package com.example.models.items;

import com.example.models.actions.ActionResult;
import com.example.models.game.GameState;

/**
 * Describes an item available in the escape room.
 */
public abstract class Item {
    protected final ItemID id;
    protected final String name;
    protected final String description;
    protected final boolean placeable;
    protected final boolean usable; 

    /**
     * Creates an item definition.
     *
     * @param id          stable item identifier
     * @param name        display name
     * @param description short description
     * @param placeable   whether the player can place the item in a room
     * @param usable      whether the inventory menu should offer a use command
     */
    protected Item(ItemID id, String name, String description, boolean placeable, boolean usable) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.placeable = placeable;
        this.usable = usable;

    }

    /**
     * Returns the stable item identifier.
     *
     * @return item identifier
     */
    public ItemID id() {
        return id;
    }

    /**
     * Returns the item display name.
     *
     * @return display name
     */
    public String name() {
        return name;
    }

    /**
     * Returns the item description.
     *
     * @return description
     */
    public String description() {
        return description;
    }

    /**
     * Indicates whether the item can be placed in a room.
     *
     * @return {@code true} when placeable
     */
    public boolean placeable() {
        return placeable;
    }

    /**
     * Indicates whether the item can be used from inventory.
     *
     * @return {@code true} when usable
     */
    public boolean usable() {
        return usable;
    }

    /**
     * Executes the item action if one is defined.
     *
     * @param state current game state
     * @return item use result
     */
    public ActionResult use(GameState state) {
        return ActionResult.failure("Ara mateix no pots utilitzar aquest objecte.");
    }
}
