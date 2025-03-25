package model.core;

import model.elements.Fixture;
import model.elements.Item;
import model.obstacle.GameObstacle;

import java.util.*;

/**
 * Represents a room in the adventure game.
 * A room contains:
 * - A unique room number and name
 * - A set of directional exits ("N", "S", "E", "W")
 * - Optional items, fixtures, and an obstacle (puzzle or monster)
 * - Raw field strings to be parsed later
 */

public class Room {
  private final int roomNumber;
  private final String name;
  private String roomDescription;


  // Direction → Target room number
  private final Map<String, Integer> exits;

  private final List<Item> items;
  private final List<Fixture> fixtures;

  private GameObstacle obstacle;

  // Used to store raw string fields before parsing
  private final Map<String, String> rawFields;

  /**
   * Constructs a new Room with room number and name.
   *
   * @param roomNumber the unique room number
   * @param name       the name of the room
   */
  public Room(int roomNumber, String name) {
    this.roomNumber = roomNumber;
    this.name = name;
    this.exits = new HashMap<>();
    this.items = new ArrayList<>();
    this.fixtures = new ArrayList<>();
    this.obstacle = null;
    this.rawFields = new HashMap<>();
  }

  // ------------------------------------------
  // Exits
  // ------------------------------------------

  /**
   * Sets the exit in the given direction to point to a room number.
   *
   * @param direction        direction string (e.g., "N", "S", "E", "W")
   * @param targetRoomNumber room number to link to
   */
  public void setExit(String direction, int targetRoomNumber) {
    exits.put(direction.toUpperCase(), targetRoomNumber);
  }

  /**
   * Returns the target room number for the given direction.
   * Returns 0 if there is no path.
   *
   * @param direction direction string
   * @return room number or 0
   */
  public int getExit(String direction) {
    return exits.getOrDefault(direction.toUpperCase(), 0);
  }

  public Map<String, Integer> getExits() {
    return exits;
  }

  // ------------------------------------------
  // Items
  // ------------------------------------------

  public void addItem(Item item) {
    items.add(item);
  }

  public Item removeItem(String itemName) {
    Iterator<Item> it = items.iterator();
    while (it.hasNext()) {
      Item i = it.next();
      if (i.getName().equalsIgnoreCase(itemName)) {
        it.remove();
        return i;
      }
    }
    return null;
  }

  public Item getItem(String itemName) {
    for (Item i : items) {
      if (i.getName().equalsIgnoreCase(itemName)) {
        return i;
      }
    }
    return null;
  }

  public List<Item> getItems() {
    return items;
  }

  // ------------------------------------------
  // Fixtures
  // ------------------------------------------

  public void addFixture(Fixture f) {
    fixtures.add(f);
  }

  public Fixture getFixture(String name) {
    for (Fixture f : fixtures) {
      if (f.getName().equalsIgnoreCase(name)) {
        return f;
      }
    }
    return null;
  }

  public List<Fixture> getFixtures() {
    return fixtures;
  }

  // ------------------------------------------
  // Obstacles
  // ------------------------------------------

  public void setObstacle(GameObstacle obs) {
    this.obstacle = obs;
  }

  public boolean hasObstacle() {
    return obstacle != null && obstacle.isActive();
  }

  public void deactivateObstacle() {
    if (obstacle != null) {
      obstacle.deactivate();
    }
  }

  public GameObstacle getObstacle() {
    return obstacle;
  }

  // ------------------------------------------
  // Raw Fields (for JSON string lists)
  // ------------------------------------------

  /**
   * Stores raw string fields from JSON like "Pen, Notebook"
   *
   * @param key   the field name (e.g., "items", "fixtures")
   * @param value the raw comma-separated string
   */
  public void setRawField(String key, String value) {
    rawFields.put(key.toLowerCase(), value);
  }

  /**
   * Returns the raw string for a field (e.g., "Pen, Notebook")
   *
   * @param key the field name
   * @return the raw string or null
   */
  public String getRawField(String key) {
    return rawFields.get(key.toLowerCase());
  }

  // ------------------------------------------
  // Info(getter and toString)
  // ------------------------------------------

  public int getRoomNumber() {
    return roomNumber;
  }

  public String getName() {
    return name;
  }

  public String getRoomDescription() {
    return roomDescription;

  }
  @Override
  public String toString() {
    return "Room " + roomNumber + ": " + name;
  }

  public void setDescription(String description) {
    this.roomDescription = description;
  }
}