package model.core;

import com.google.gson.*;

import model.elements.*;
import model.obstacle.GameObstacle;
import model.obstacle.Monster;
import model.obstacle.Puzzle;
import utils.fileutil.JsonUtils;
import utils.roomparser.RoomsParser;

import java.io.*;
import java.util.*;

import static utils.elementparser.FixtureParser.parseFixtures;
import static utils.elementparser.ItemParser.parseItems;
import static utils.obstacleparser.MonsterParser.parseMonsters;
import static utils.obstacleparser.PuzzleParser.parsePuzzles;


/**
 * WorldEngine: Responsible for loading JSON data,
 * generating game maps,
 * and providing world status.
 */
public class WorldEngine implements Serializable {
  // fields and the default constructor
  private Map<Integer, Room> worldMap; // Whole room map: Room number -> Room object

  /**
   * Constructor (no seed default build)
   * Initialize the world map object:
   * key is the room number and the value is the Room object.
   */
  public WorldEngine() {
    this.worldMap = new HashMap<>();
  }

  /**
   * Main function to generate worlds from JSON files (map + elements).
   * Read the file → parse the room → parse items / fixtures / puzzles → stuff back to the room.
   *
   * @param jsonFilePath the file path of the target json file
   * @throws IOException input and output exception
   */
  public void generateWorld(String jsonFilePath) throws IOException {
    // get the root object
    JsonObject root = JsonUtils.safeParseJson(jsonFilePath);
    // get the wordMap
    RoomsParser.parseRooms(root, worldMap);

    List<Item> globalItems = new ArrayList<>();
    List<Fixture> globalFixtures = new ArrayList<>();

    parseItems(root, worldMap, globalItems);
    parseFixtures(root, worldMap, globalFixtures);
    // parse room obstacles
    parseMonsters(root, worldMap);
    parsePuzzles(root, worldMap);

  }

  // ==== printer ====

  /**
   * Print the current world map for simple smoke testing.
   */
  public void printWorldMap() {
    System.out.println("=== Game World Map ===");

    for (Room room : worldMap.values()) {
      System.out.println("Room Number: " + room.getRoomNumber());
      System.out.println("Room Name: " + room.getName());
      System.out.println("Room Description: " + room.getRoomDescription());

      // exits
      System.out.println("Room Exits:");
      System.out.println("  → N: " + room.getExit("N"));
      System.out.println("  → S: " + room.getExit("S"));
      System.out.println("  → E: " + room.getExit("E"));
      System.out.println("  → W: " + room.getExit("W"));

      // items
      System.out.println("Items:");
      if (room.getItems() == null) {
        System.out.println("  - null");
      } else if (room.getItems().isEmpty()) {
        System.out.println("  - (empty)");
      } else {
        for (Item item : room.getItems()) {
          System.out.println("  - " + item.getName());
        }
      }

      // fixtures
      System.out.println("Fixtures:");
      if (room.getFixtures() == null) {
        System.out.println("  - null");
      } else if (room.getFixtures().isEmpty()) {
        System.out.println("  - (empty)");
      } else {
        for (Fixture fixture : room.getFixtures()) {
          System.out.println("  - " + fixture.getName());
        }
      }

      // obstacle
      GameObstacle obs = room.getObstacle();
      if (obs == null) {
        System.out.println("Obstacle: null");
      } else if (obs instanceof Puzzle) {
        System.out.println("Puzzle: " + obs.getName());
      } else if (obs instanceof Monster) {
        System.out.println("Monster: " + obs.getName());
      }

      System.out.println("--------------------------------------------------\n");
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

  // ==== game state ====

  /**
   * Save state boolean.
   *
   * @param filePath the file path
   * @param player   the player
   * @return boolean
   */
  public boolean saveState(String filePath, Player player) {
    try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(filePath))) {
      out.writeObject(this);
      out.writeObject(player);
      return true;
    } catch (IOException e) {
      e.printStackTrace();
      return false;
    }
  }


  /**
   * Restore state boolean.
   *
   * @param filePath  the file path
   * @param playerRef the player ref
   * @return boolean
   */
  public boolean restoreState(String filePath, Player playerRef) {
    try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(filePath))) {
      WorldEngine loadedWorld = (WorldEngine) in.readObject();
      Player loadedPlayer = (Player) in.readObject();

      this.worldMap = loadedWorld.worldMap;

      if (playerRef != null && loadedPlayer != null) {
        playerRef.copyFrom(loadedPlayer);
      } else {
        return false;
      }

      return true;
    } catch (IOException | ClassNotFoundException e) {
      e.printStackTrace();
      return false;
    }
  }
}
