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
  private Player player;


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

  // ==== getter&setter ====

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

  /**
   * Sets player.
   *
   * @param player the player
   */
  public void setPlayer(Player player) {
    this.player = player;
  }

  /**
   * Gets player.
   *
   * @return the player
   */
  public Player getPlayer() {
    return this.player;
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

  public void printWorldMap() {
  }
}
