package com.example.models.items;

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

    protected Item(ItemID id, String name, String description, boolean placeable, boolean usable) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.placeable = placeable;
        this.usable = usable;

    }

    public ItemID id() {
        return id;
    }

    public String name() {
        return name;
    }

    public String description() {
        return description;
    }

    public boolean placeable() {
        return placeable;
    }

    public boolean usable() {
        return usable;
    }

    /**
     * Executes the item action if one is defined.
     *
     * @param state current game state
     * @return {@code true} when the action succeeds
     */
    public boolean use(GameState state) {
        return false;
    }
}
