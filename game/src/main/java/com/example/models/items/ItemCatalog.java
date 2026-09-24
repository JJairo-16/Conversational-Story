package com.example.models.items;

import java.util.EnumMap;
import java.util.Map;

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
        add(new Item(ItemID.DONUT, "Dònut", "Distracció temporal per a en Malien.", true, true, null));
        add(new Item(ItemID.ASTRONAUT_SUIT, "Vestit d'astronauta", "Equip necessari per sortir de la nau.", false,
                true, null));
        add(new Item(ItemID.REPAIR_TOOL, "Eina especial", "Eina necessària per reparar els propulsors.", false,
                true, null));
        add(new Item(ItemID.CREW_CARD, "Tarja de tripulant", "Tarja personal per obrir portes.", false, true, null));
        add(new Item(ItemID.SNORKEL, "Snorkel", "Permet veure sota l'aigua al bany inundat.", false, true, null));
        add(new Item(ItemID.FLASHLIGHT, "Llanterna", "Permet registrar zones fosques, com la cuina sense llum.",
                false, true, null));
        add(new Item(ItemID.SMALL_KEY, "Clau petita", "Obre l'armari del vestit especial al vestuari.", false,
                true, null));
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
