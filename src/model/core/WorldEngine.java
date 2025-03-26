package model.core;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

import model.elements.*;
import model.obstacle.*;
import utils.JsonUtils;
import utils.RoomsParser;

import java.io.*;
import java.util.*;


/**
 * WorldEngine: Responsible for loading JSON data,
 * generating game maps,
 * and providing world status.
 */
public class WorldEngine implements Serializable {
  // fields and the default constructor
  private Map<Integer, Room> worldMap; // Whole room map: Room number -> Room object
  private Map<String, Item> globalItems;
  private Map<String, Fixture> globalFixtures;
  private Map<String, Puzzle> globalPuzzles;
  private Map<String, Monster> globalMonsters;

  /**
   * Constructor (no seed default build)
   * Initialize the world map object:
   * key is the room number and the value is the Room object.
   */
  public WorldEngine() {
    this.worldMap = new HashMap<>();
    this.globalItems = new HashMap<>();
    this.globalFixtures = new HashMap<>();
    this.globalPuzzles = new HashMap<>();
    this.globalMonsters = new HashMap<>();
  }

  /**
   * Main function to generate worlds from JSON files (map + elements).
   * Read the file → parse the room → parse items / fixtures / puzzles → stuff back to the room.
   *
   * @param jsonFilePath the file path of the target json file
   * @throws IOException input and output exception
   */
  public void generateWorld(String jsonFilePath) throws IOException {
    JsonObject root = JsonUtils.safeParseJson(jsonFilePath);
    RoomsParser.parseRooms(root, worldMap);
  }

  /**
   * Print the current world map for simple smoke testing.
   */
  public void printWorldMap() {
    System.out.println("=== Game World Map ===");
    // print rooms
    for (Map.Entry<Integer, Room> entry : worldMap.entrySet()) {
      Room room = entry.getValue();
      System.out.println("Room " + room.getRoomNumber() + ": " + room.getName());
      System.out.println("Description " + room.getRoomDescription());
      System.out.println("  Exits -> N: " + room.getExit("N") + ", S: " + room.getExit("S") + ", E: " + room.getExit("E") + ", W: " + room.getExit("W"));

      // items and fixtures in the current room
      if (room.getItems() != null && !room.getItems().isEmpty()) {
        System.out.println("  Items: ");
        for (Item item : room.getItems()) {
          System.out.println("    - " + item.getName());
        }
      }

      if (room.getFixtures() != null && !room.getFixtures().isEmpty()) {
        System.out.println("  Fixtures: ");
        for (Fixture fixture : room.getFixtures()) {
          System.out.println("    - " + fixture.getName());
        }
      }

      System.out.println();
    }
  }

  // ==== getter ====

  /**
   * Room getter.
   *
   * @param roomNumber the room number
   * @return the room
   */
  public Room getRoom(int roomNumber) {
    return worldMap.get(roomNumber);
  }

  /**
   * worldMap getter.
   *
   * @return the world map
   */
  public Map<Integer, Room> getWorldMap() {
    return worldMap;
  }

  public boolean saveState(String filePath, Player player) {
    try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(filePath))) {
      out.writeObject(this);
      out.writeObject(player);
      return true;
    } catch (IOException e) {
      return false;
    }
  }

  public boolean restoreState(String filePath, Player playerRef) {
    try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(filePath))) {
      WorldEngine loadedWorld = (WorldEngine) in.readObject();
      Player loadedPlayer = (Player) in.readObject();

      this.worldMap = loadedWorld.worldMap;
      playerRef.copyFrom(loadedPlayer);
      return true;
    } catch (IOException | ClassNotFoundException e) {
      return false;
    }
  }
}
