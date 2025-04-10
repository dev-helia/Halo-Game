package model.core;

import model.elements.Fixture;
import model.elements.Item;
import model.obstacle.GameObstacle;

import java.io.Serializable;
import java.util.*;

/**
 * Represents a room in the adventure game.
 * A room contains:
 * - A unique room number and name
 * - A set of directional exits ("N", "S", "E", "W")
 * - Optional items, fixtures, and an obstacle (puzzle or monster)
 * - Raw field strings to be parsed later
 */
public class Room implements Serializable {
  private final int roomNumber;
  private final String name;
  private String roomDescription;


  // Direction → Target room number
  private final Map<String, Integer> exits;
  private List<Item> items;
  private List<Fixture> fixtures;
  private GameObstacle obstacle;
  // Used to store raw string fields before parsing
  private final Map<String, String> rawFields;

  private String picture;

  /**
   * Constructs a new Room with room number and name.
   *
   * @param roomNumber the unique room number
   * @param name       the name of the room
   */
  public Room(int roomNumber, String name, String roomDescription) {
    this.roomNumber = roomNumber;
    this.name = name;
    this.roomDescription = roomDescription;
    this.exits = new HashMap<>();
    this.items = new ArrayList<>();
    this.fixtures = new ArrayList<>();
    this.obstacle = null;
    this.rawFields = new HashMap<>();
    this.picture = picture;
  }

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

  /**
   * Adds an item.
   * @param item to be added.
   */
  public void addItem(Item item) {
    items.add(item);
  }

  /**
   * Removes an item.
   * @param itemName name of the item
   * @return the item removed.
   */
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

  /**
   * Gets the item.
   * @param itemName name of the item
   * @return the item.
   */
  public Item getItem(String itemName) {
    for (Item i : items) {
      if (i.getName().equalsIgnoreCase(itemName)) {
        return i;
      }
    }
    return null;
  }


  /**
   * Returns the list of items currently in the room.
   *
   * @return list of items
   */
  public List<Item> getItems() {
    return items;
  }

  /**
   * Sets the list of items in the room.
   *
   * @param items the new list of items
   */
  public void setItems(List<Item> items) {
    this.items = items;
  }


  // ------------------------------------------
  // Fixtures
  // ------------------------------------------

  /**
   * Adds a fixture to the room.
   *
   * @param f the fixture to add
   */
  public void addFixture(Fixture f) {
    fixtures.add(f);
  }

  /**
   * Retrieves a fixture by name from the room.
   *
   * @param name the name of the fixture to retrieve
   * @return the matching fixture, or null if not found
   */
  public Fixture getFixture(String name) {
    for (Fixture f : fixtures) {
      if (f.getName().equalsIgnoreCase(name)) {
        return f;
      }
    }
    return null;
  }

  /**
   * Returns the list of all fixtures in the room.
   *
   * @return list of fixtures
   */
  public List<Fixture> getFixtures() {
    return fixtures;
  }

  /**
   * Sets the list of fixtures in the room.
   *
   * @param fixtures the list of fixtures to set
   */
  public void setFixtures(List<Fixture> fixtures) {
    this.fixtures = fixtures;
  }

  // ------------------------------------------
  // Obstacles
  // ------------------------------------------

  /**
   * Sets the obstacle.
   *
   * @param obs the obstacle to place in the room
   */
  public void setObstacle(GameObstacle obs) {
    this.obstacle = obs;
  }

  /**
   * Checks if the room currently has an active obstacle.
   *
   * @return true if an active obstacle is present, false otherwise
   */
  public boolean hasObstacle() {
    return obstacle != null && obstacle.isActive();
  }

  /**
   * Deactivates the current obstacle, if one exists.
   */
  public void deactivateObstacle() {
    if (obstacle != null) {
      obstacle.deactivate();
    }
  }

  /**
   * Gets the obstacle in the room.
   *
   * @return the current obstacle, or null if none is set
   */
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


  /**
   * Returns the room's number.
   *
   * @return the room number
   */
  public int getRoomNumber() {
    return roomNumber;
  }

  /**
   * Returns the name of the room.
   *
   * @return the room name
   */
  public String getName() {
    return name;
  }

  /**
   * Returns the room's description.
   *
   * @return the room description
   */
  public String getRoomDescription() {
    return roomDescription;

  }

  /**
   * Returns a string representation of the room.
   *
   * @return formatted string with room number and name
   */
  @Override
  public String toString() {
    return "Room " + roomNumber + ": " + name;
  }

  /**
   * Set room descriptions.
   * @param description
   */
  public void setDescription(String description) {
    this.roomDescription = description;
  }

  /**
   * Sets the image filename associated with this room.
   *
   * @param picture the image file name
   */
  public void setPicture(String picture) {
    this.picture = picture;
  }

  /**
   * Gets the image filename for this room.
   *
   * @return the image file name
   */
  public String getPicture() {
    return picture;
  }
}