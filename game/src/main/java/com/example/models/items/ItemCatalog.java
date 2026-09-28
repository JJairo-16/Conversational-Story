package com.example.models.items;

import java.util.EnumMap;
import java.util.Map;

import com.example.models.items.instances.AstronautSuit;
import com.example.models.items.instances.CrewCard;
import com.example.models.items.instances.Donut;
import com.example.models.items.instances.Flashlight;
import com.example.models.items.instances.RepairTool;
import com.example.models.items.instances.SmallKey;
import com.example.models.items.instances.Snorkel;

/**
 * Catalog of all available item definitions.
 */
public final class ItemCatalog {
    private final Map<ItemID, Item> items = new EnumMap<>(ItemID.class);

    /**
     * Creates the default item catalog.
     */
    public ItemCatalog() {
        createItems();
    }

    /**
     * Returns an item definition by identifier.
     *
     * @param id item identifier
     * @return item definition, or {@code null} if it is not registered
     */
    public Item getItem(ItemID id) {
        return items.get(id);
    }

    /**
     * Registers the known item definitions.
     */
    private void createItems() {
        add(new Donut());
        add(new AstronautSuit());
        add(new RepairTool());
        add(new CrewCard());
        add(new Snorkel());
        add(new Flashlight());
        add(new SmallKey());
    }

    /**
     * Registers an item definition.
     *
     * @param item item definition
     */
    private void add(Item item) {
        items.put(item.id(), item);
    }
}
