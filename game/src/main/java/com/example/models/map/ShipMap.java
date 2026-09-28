package com.example.models.map;

import java.util.EnumMap;
import java.util.Map;

import com.example.models.actions.RoomAction;

/**
 * Builds and owns the ship room graph.
 *
 * <p>
 * This class creates rooms and their connections. Initial item placement belongs
 * to the game generator so that each game can use a seed.
 */
public final class ShipMap {
    private final Map<RoomID, Room> rooms = new EnumMap<>(RoomID.class);

    /**
     * Creates the default ship map.
     */
    public ShipMap() {
        createRooms();
        connectRooms();
    }

    /**
     * Returns the starting room for the game.
     *
     * @return starting room
     */
    public Room getStartingRoom() {
        return getRoom(RoomID.BEDROOM);
    }

    /**
     * Returns a room by identifier.
     *
     * @param id room identifier
     * @return matching room
     */
    public Room getRoom(RoomID id) {
        return rooms.get(id);
    }

    /**
     * Returns the room reached from another room in a given direction.
     *
     * @param from      current room
     * @param direction movement direction
     * @return destination room, or {@code null} when there is no exit
     */
    public Room getConnectedRoom(Room from, Direction direction) {
        RoomID destinationId = from.getConnection(direction);
        return destinationId == null ? null : getRoom(destinationId);
    }

    /**
     * Creates every room object before any connection is added.
     */
    private void createRooms() {
        add(new Room(RoomID.WORKSHOPS, "Tallers", "Zona on es guarden eines i peces de reparació."));
        add(new Room(RoomID.COSTUME, "Vestuari", "Sala amb equipament per sortir a l'exterior."));
        add(new Room(RoomID.KITCHEN, "Cuina",
                "Espai de menjar i subministraments. La llum no funciona i els racons queden a les fosques."));
        add(new Room(RoomID.BATHROOM, "Banys", "Zona inundada de banys de la tripulació."));
        add(new Room(RoomID.BEDROOM, "Dormitori", "Zona d'hibernació i descans de la tripulació."));
        add(new Room(RoomID.OFFICES, "Oficines", "Zona central de treball de la nau."));
        add(new Room(RoomID.LIVING_ROOM, "Menjador", "Sala comuna on menja la tripulació."));
        add(new Room(RoomID.EXIT_ROOM, "Sala sortida exterior", "Accés per sortir de la nau."));
        add(new Room(RoomID.COMMAND, "Comandament", "Sala de control de la nau."));

        addRoomActions();
    }

    private void addRoomActions() {
        getRoom(RoomID.BEDROOM).addAction(RoomAction.CHECK_LOCKER);

        getRoom(RoomID.BATHROOM).addAction(RoomAction.INSPECT_FLOODED_DRAIN);

        getRoom(RoomID.LIVING_ROOM).addAction(RoomAction.SEARCH_TABLE);

        getRoom(RoomID.OFFICES).addAction(RoomAction.OPEN_DESK);

        getRoom(RoomID.COSTUME).addAction(RoomAction.OPEN_SUIT_LOCKER);

        getRoom(RoomID.WORKSHOPS).addAction(RoomAction.SEARCH_TOOLS);

        getRoom(RoomID.EXIT_ROOM).addAction(RoomAction.INSPECT_ENGINES);
        getRoom(RoomID.EXIT_ROOM).addAction(RoomAction.REPAIR_ENGINES);

        getRoom(RoomID.COMMAND).addAction(RoomAction.START_ENGINES);
    }

    /**
     * Defines the ship layout using bidirectional room connections.
     */
    private void connectRooms() {
        connect(RoomID.BEDROOM, Direction.NORTH, RoomID.BATHROOM);
        connect(RoomID.BEDROOM, Direction.WEST, RoomID.LIVING_ROOM);
        connect(RoomID.BATHROOM, Direction.WEST, RoomID.OFFICES);
        connect(RoomID.OFFICES, Direction.NORTH, RoomID.WORKSHOPS);
        connect(RoomID.OFFICES, Direction.WEST, RoomID.COSTUME);
        connect(RoomID.OFFICES, Direction.SOUTH, RoomID.COMMAND);
        connect(RoomID.COSTUME, Direction.SOUTH, RoomID.KITCHEN);
        connect(RoomID.KITCHEN, Direction.EAST, RoomID.LIVING_ROOM);
        connect(RoomID.LIVING_ROOM, Direction.NORTH, RoomID.COMMAND);
        connect(RoomID.LIVING_ROOM, Direction.SOUTH, RoomID.EXIT_ROOM);
    }

    /**
     * Registers a room so it can be found later by its identifier.
     *
     * @param room room to register
     */
    private void add(Room room) {
        rooms.put(room.getId(), room);
    }

    /**
     * Connects two rooms in both directions.
     *
     * @param from      origin room identifier
     * @param direction direction from the origin room
     * @param to        destination room identifier
     */
    private void connect(RoomID from, Direction direction, RoomID to) {
        getRoom(from).connect(direction, to);
        getRoom(to).connect(direction.opposite(), from);
    }
}
