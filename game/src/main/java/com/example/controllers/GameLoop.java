package com.example.controllers;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.example.models.actions.ActionResult;
import com.example.models.actions.ActionType;
import com.example.models.actions.RoomAction;
import com.example.models.entities.Entity;
import com.example.models.entities.EntityManager;
import com.example.models.game.GameState;
import com.example.models.generation.GameGenerator;
import com.example.models.generation.GameRandom;
import com.example.models.items.Inventory;
import com.example.models.items.Item;
import com.example.models.items.ItemID;
import com.example.models.map.Direction;
import com.example.models.map.Room;
import com.example.models.map.RoomID;
import com.example.utils.ui.Cleaner;
import com.example.utils.input.Menu;
import com.example.utils.ui.Prettier;
import com.example.utils.ui.helpers.EntityDisplayer;
import com.example.utils.ui.helpers.GameMenuBuilder;

/**
 * Runs one game session.
 */
public class GameLoop {
    private static final List<ActionType> MAIN_MENU_OPTIONS = List.of(
            ActionType.NAVIGATION,
            ActionType.INVENTORY,
            ActionType.MAP,
            ActionType.INTERACTION,
            ActionType.I_HALL,
            ActionType.WAIT,
            ActionType.EXIT);

    private final Cleaner cleaner = new Cleaner();
    private static final Comparator<RoomAction> ROOM_ACTION_COMPARATOR = Comparator
            .comparing(RoomAction::getDisplayName);

    private static final Comparator<Item> ITEM_COMPARATOR = Comparator
            .comparing(Item::name);

    private static final int NORMAL_WAIT_MESSAGE_CHANCE = 60;

    private static final List<String> NORMAL_WAIT_MESSAGES = List.of(
            "No fas res.",
            "Et quedes quiet durant uns instants.",
            "Passa un torn.",
            "Respires fondo i esperes.",
            "Escoltes el soroll constant de la nau.",
            "Observes la sala amb calma.",
            "Et quedes atent al teu voltant.",
            "Esperes sense moure't.",
            "El temps passa lentament.",
            "Esperes uns segons i observes la sala.",
            "El torn passa sense cap canvi evident.",
            "Et quedes quiet, atent a qualsevol soroll estrany.");

    private static final List<String> SILLY_WAIT_MESSAGES = List.of(
            "T'esperes. Decisió valenta, si això compta com a decisió.",
            "Perds el temps amb una elegància discutible.",
            "Et quedes plantat com si esperessis instruccions del sostre.",
            "La nau cruix. Tu continues fent de moble.",
            "Un altre torn llençat al buit.",
            "No mous ni un dit. Productivitat: zero.",
            "El capità decideix practicar l'art de no fer res.",
            "Vago. Però com a mínim ets constant.",
            "Si esperar fos una feina, avui cobraries hores extra.",
            "El Malien podria estar planejant coses. Tu no.",
            "La tripulació imaginària jutja la teva mandra.",
            "Esperes amb una confiança que ningú ha demanat.",
            "No passa res visible, excepte la teva falta d'iniciativa.");

    /**
     * Executes the game loop.
     */
    public void run() {
        GameRandom random = new GameRandom(System.currentTimeMillis());
        GameState state = new GameGenerator(random).generate();
        IHall iHall = IHall.create(state.getRoute(), random);
        boolean running = true;

        while (running && !state.isGameOver()) {
            ActionType actionType = readMenuOption(state);
            running = handleMenuShell(actionType, state, iHall);
        }
    }

    /**
     * Builds the current room summary shown above the main action menu.
     *
     * @param state current game state
     * @return current room detail lines
     */
    private List<String> createCurrentPositionDetails(GameState state) {
        Room currentRoom = state.getCurrentRoom();
        List<String> details = new ArrayList<>();
        details.add("Posició actual: " + currentRoom.getName());
        details.add(currentRoom.getDescription());
        details.addAll(RoomItemVisibility.describeCurrentRoomItems(state));
        return details;
    }

    /**
     * Reads the selected top-level menu section.
     *
     * @param state current game state
     * @return selected action type
     */
    private ActionType readMenuOption(GameState state) {
        List<String> options = MAIN_MENU_OPTIONS.stream()
                .map(ActionType::getDisplayName)
                .toList();
        int option = Menu.getOption(options, "Accions", createCurrentPositionDetails(state));
        return MAIN_MENU_OPTIONS.get(option - 1);
    }

    /**
     * Dispatches a top-level menu section to its submenu handler.
     *
     * @param actionType selected top-level menu section
     * @param state      current game state
     * @return {@code false} only when the player chooses to exit the session
     */
    private boolean handleMenuShell(ActionType actionType, GameState state, IHall iHall) {
        switch (actionType) {
            case NAVIGATION -> handleNavigationMenu(state);
            case INVENTORY -> handleInventoryMenu(state);
            case MAP -> handleMapMenu(state);
            case INTERACTION -> {
                return handleInteractionMenu(state);
            }
            case I_HALL -> talkToIHall(iHall);
            case WAIT -> handleWaitMenu(state);
            case EXIT -> {
                cleaner.clear();
                Prettier.info("Sortint del joc...");
                return false;
            }
        }

        return true;
    }

    /**
     * Shows the navigation submenu and moves the player to a selected destination.
     *
     * <p>
     * The menu displays room names, but the selected option keeps the matching
     * direction so the map can still move through directional connections.
     *
     * @param state current game state
     */
    private void handleNavigationMenu(GameState state) {
        Room currentRoom = state.getCurrentRoom();
        List<Map.Entry<Direction, RoomID>> exits = new ArrayList<>(currentRoom.getConnections().entrySet());

        if (exits.isEmpty()) {
            Prettier.warn("No hi ha sortides disponibles.");
            Menu.pause();
            return;
        }

        List<String> options = GameMenuBuilder.createNavigationOptions(state, exits);

        int option = Menu.getOption(options, "Navegació");
        if (option == options.size()) {
            return;
        }

        Map.Entry<Direction, RoomID> selectedExit = exits.get(option - 1);
        Room destination = state.getRoom(selectedExit.getValue());

        if (destination == null) {
            Prettier.warn("Aquesta sortida encara no està configurada.");
            Menu.pause();
            return;
        }

        boolean success = state.move(selectedExit.getKey());

        if (success) {
            Prettier.info("Vas a: %s", destination.getName());
            state.advancePlayerTurn();
        } else if (state.isRoomLockedForPlayer(destination.getId())) {
            Prettier.warn("La sala de comandament està bloquejada. Necessites la tarja de tripulant.");
        } else {
            Prettier.warn("No pots anar a aquesta sala.");
        }

        Menu.pause();
    }

    /**
     * Handles the inventory submenu.
     *
     * <p>
     * The inventory menu lets the player inspect carried items. Selecting an item
     * opens its own detail menu when the item supports commands, or a read-only
     * detail screen otherwise.
     *
     * @param state current game state
     */
    private void handleInventoryMenu(GameState state) {
        boolean inspectingInventory = true;
        while (inspectingInventory) {
            List<Item> items = getInventoryItems(state);
            if (items.isEmpty()) {
                Prettier.warn("No portes cap objecte.");
                Menu.pause();
                return;
            }

            List<String> options = GameMenuBuilder.createInventoryMenuOptions(items);
            int option = Menu.getOption(options, "Inventari");
            if (option == options.size()) {
                inspectingInventory = false;
                continue;
            }

            Item selectedItem = items.get(option - 1);
            handleItemMenu(selectedItem, state);
        }
    }

    /**
     * Opens the submenu for a selected inventory item.
     *
     * @param item  selected item definition
     * @param state current game state
     */
    private void handleItemMenu(Item item, GameState state) {
        if (!hasItemCommands(item)) {
            displayItem(item);
            return;
        }

        boolean inspectingItem = true;
        while (inspectingItem && state.getInventory().has(item.id())) {
            List<String> options = GameMenuBuilder.createItemMenuOptions(item);
            int option = Menu.getOption(options, item.name(), createItemDetails(item));
            if (option == options.size()) {
                inspectingItem = false;
                continue;
            }

            inspectingItem = handleItemCommand(item, option, state);
        }
    }

    /**
     * Displays a read-only item details screen.
     *
     * @param item item definition to display
     */
    private void displayItem(Item item) {
        cleaner.clear();
        Prettier.printTitle(item.name());
        System.out.println(item.description());
        System.out.println();
        Menu.pause();
    }

    /**
     * Executes the selected item command.
     *
     * @param item   selected item definition
     * @param option selected option number
     * @param state  current game state
     * @return {@code true} to keep the item submenu open
     */
    private boolean handleItemCommand(Item item, int option, GameState state) {
        int currentOption = 1;

        if (item.usable()) {
            if (option == currentOption) {
                useInventoryItem(item, state);
                return true;
            }
            currentOption++;
        }

        if (item.placeable() && option == currentOption) {
            return !placeInventoryItem(item, state);
        }

        return true;
    }

    /**
     * Uses an inventory item and prints the result.
     *
     * @param item  item to use
     * @param state current game state
     */
    private void useInventoryItem(Item item, GameState state) {
        ActionResult result = state.useItem(item.id());
        if (result.success()) {
            Prettier.info(result.message());
        } else {
            Prettier.warn(result.message());
        }
        Menu.pause();
    }

    /**
     * Places an inventory item in the current room and prints the result.
     *
     * @param item  item to place
     * @param state current game state
     * @return {@code true} when the item was placed
     */
    private boolean placeInventoryItem(Item item, GameState state) {
        if (state.placeItem(item.id())) {
            Prettier.info("Has col·locat: %s", item.name());
            Menu.pause();
            return true;
        }

        Prettier.warn("No pots col·locar aquest objecte aquí.");
        Menu.pause();
        return false;
    }

    /**
     * Returns whether an item has at least one interactive command.
     *
     * @param item item definition to inspect
     * @return {@code true} when usable or placeable
     */
    private boolean hasItemCommands(Item item) {
        return item.usable() || item.placeable();
    }

    /**
     * Builds the detail lines shown in an item submenu.
     *
     * @param item item definition to describe
     * @return item detail lines
     */
    private List<String> createItemDetails(Item item) {
        return List.of(item.description());
    }

    /**
     * Resolves inventory identifiers into catalog definitions.
     *
     * @param state current game state
     * @return sorted item definitions currently carried by the player
     */
    private List<Item> getInventoryItems(GameState state) {
        Inventory inventory = state.getInventory();
        return inventory.getItems()
                .stream()
                .map((ItemID itemId) -> state.getItemCatalog().getItem(itemId))
                .filter(Objects::nonNull)
                .sorted(ITEM_COMPARATOR)
                .toList();
    }

    /**
     * Handles the map submenu.
     *
     * <p>
     * The map menu lets the player inspect every tracked entity without consuming
     * a turn. After showing one entity, control returns to the map menu until the
     * player chooses to go back.
     *
     * @param state current game state
     */
    private void handleMapMenu(GameState state) {
        List<Entity> entities = getMapEntities(state);
        List<String> options = GameMenuBuilder.createMapMenuOptions(entities);

        boolean inspectingMap = true;
        while (inspectingMap) {
            int option = Menu.getOption(options, "Mapa");
            if (option == options.size()) {
                inspectingMap = false;
                continue;
            }

            Entity selectedEntity = entities.get(option - 1);
            displayEntity(selectedEntity, state);
        }
    }

    /**
     * Returns the entities shown by the map submenu.
     *
     * @param state current game state
     * @return ordered entity list matching the map menu options
     */
    private List<Entity> getMapEntities(GameState state) {
        EntityManager manager = state.getEntityManager();
        return List.of(
                manager.getPlayer(),
                manager.getMalien(),
                manager.getCrewMember());
    }

    /**
     * Clears the screen and prints the selected entity's map details.
     *
     * @param entity entity selected in the map menu
     * @param state  current game state
     */
    private void displayEntity(Entity entity, GameState state) {
        cleaner.clear();
        EntityDisplayer.printEntityMapInfo(entity, state);
        System.out.println();
        Menu.pause();
    }

    /**
     * Shows room-specific actions and delegates the selected action execution.
     *
     * <p>
     * This method owns only the submenu flow: list actions, read the selected
     * option, call {@link RoomActionHandler#handle(RoomAction, GameState)}, and
     * print the returned result. The room action itself decides whether inventory
     * objects are relevant.
     *
     * @param state current game state
     * @return {@code false} only when a successful action ends the session
     */
    private boolean handleInteractionMenu(GameState state) {
        List<RoomAction> actions = getCurrentRoomActions(state);

        if (actions.isEmpty()) {
            Prettier.warn("No hi ha interaccions disponibles en aquesta sala.");
            Menu.pause();
            return true;
        }

        List<String> options = GameMenuBuilder.createInteractionMenu(actions);
        int option = Menu.getOption(options, "Interaccions");

        if (option == options.size()) {
            return true;
        }

        RoomAction selected = actions.get(option - 1);
        ActionResult result = RoomActionHandler.handle(selected, state);

        if (!result.message().isBlank()) {
            if (result.success()) {
                Prettier.info(result.message());
            } else {
                Prettier.warn(result.message());
            }
        }

        Menu.pause();
        if (selected != RoomAction.START_ENGINES)
            return true;

        return !result.success();
    }

    private void talkToIHall(IHall iHall) {
        cleaner.clear();
        Prettier.printTitle("iHall");
        Prettier.info(iHall.nextResponse());
        Menu.pause();
    }

    private List<RoomAction> getCurrentRoomActions(GameState state) {
        List<RoomAction> actions = new ArrayList<>(state.getCurrentRoom()
                .getActions()
                .stream()
                .filter(action -> action != RoomAction.COLLECT_VISIBLE_ITEMS)
                .filter(action -> action != RoomAction.TALK_CREW)
                .filter(action -> action != RoomAction.WAKE_CREW)
                .filter(action -> isRoomActionUseful(state, action))
                .toList());

        if (!RoomItemVisibility.getVisibleItems(state).isEmpty()) {
            actions.add(RoomAction.COLLECT_VISIBLE_ITEMS);
        }

        if (canInteractWithCrewMember(state)) {
            RoomAction crewAction = state.getEntityManager().getCrewMember().isAsleep()
                    ? RoomAction.WAKE_CREW
                    : RoomAction.TALK_CREW;
            actions.add(crewAction);
        }

        return actions.stream()
                .sorted(ROOM_ACTION_COMPARATOR)
                .toList();
    }

    /**
     * Indicates whether a fixed room action can still affect the current state.
     *
     * @param state  current game state
     * @param action room action to inspect
     * @return {@code true} when the action should be shown
     */
    private boolean isRoomActionUseful(GameState state, RoomAction action) {
        Room currentRoom = state.getCurrentRoom();
        return switch (action) {
            case CHECK_LOCKER -> hasAnyItem(currentRoom, ItemID.FLASHLIGHT, ItemID.SNORKEL, ItemID.SMALL_KEY);
            case INSPECT_FLOODED_DRAIN -> currentRoom.hasItem(ItemID.CREW_CARD) && !state.isFloodedDrainRevealed();
            case SEARCH_TABLE -> hasAnyItem(currentRoom, ItemID.SMALL_KEY, ItemID.SNORKEL, ItemID.FLASHLIGHT);
            case OPEN_DESK -> hasAnyItem(currentRoom, ItemID.CREW_CARD, ItemID.SNORKEL);
            case OPEN_SUIT_LOCKER -> !state.getInventory().has(ItemID.ASTRONAUT_SUIT);
            case SEARCH_TOOLS -> hasAnyItem(currentRoom, ItemID.REPAIR_TOOL, ItemID.FLASHLIGHT);
            case INSPECT_ENGINES -> !state.areEnginesInspected();
            case REPAIR_ENGINES -> !state.areEnginesRepaired();
            case START_ENGINES -> true;
            case COLLECT_VISIBLE_ITEMS, TALK_CREW, WAKE_CREW -> true;
        };
    }

    /**
     * Indicates whether a room contains at least one of the given items.
     *
     * @param room  room to inspect
     * @param items accepted item identifiers
     * @return {@code true} when any item is present
     */
    private boolean hasAnyItem(Room room, ItemID... items) {
        for (ItemID item : items) {
            if (room.hasItem(item)) {
                return true;
            }
        }

        return false;
    }

    private boolean canInteractWithCrewMember(GameState state) {
        var crewMember = state.getEntityManager().getCrewMember();
        return crewMember.isAlive() && crewMember.getCurrentRoom() == state.getCurrentRoom().getId();
    }

    /**
     * Handles the wait action.
     *
     * <p>
     * Waiting has one effect only: it consumes one player turn, exactly like a
     * successful movement would.
     *
     * @param state current game state
     */
    private void handleWaitMenu(GameState state) {
        state.advancePlayerTurn();
        Prettier.info(getWaitMessage(state));
        Menu.pause();
    }

    /**
     * Selects a random message for the wait action.
     *
     * @param state current game state
     * @return wait feedback message
     */
    private String getWaitMessage(GameState state) {
        GameRandom random = state.getRandom();

        List<String> messages = random.chance(NORMAL_WAIT_MESSAGE_CHANCE)
                ? NORMAL_WAIT_MESSAGES
                : SILLY_WAIT_MESSAGES;

        return random.getRandomIndex(messages);
    }
}
