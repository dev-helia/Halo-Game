package model;

import model.core.HealthStatus;
import model.core.Player;
import model.core.PlayerRank;
import model.core.Room;
import model.elements.Item;

import java.io.IOException;
import java.util.List;

/**
 * IModel interface:
 * High-level contract to interact with the game model (world + player).
 * Used by controller layer to control the game without knowing implementation.
 */
public interface IModel {

  // ===== World Setup =====

  /**
   * Generate world.
   *
   * @param jsonFilePath the json file path
   * @throws IOException the io exception
   */
  void generateWorld(String jsonFilePath) throws IOException;

  // ===== Player Initialization =====

  /**
   * Initialize player.
   *
   * @param name the name
   */
  void initializePlayer(String name);

  // ===== Player Actions =====

  /**
   * Move player boolean.
   *
   * @param direction the direction
   * @return the boolean
   */
  boolean movePlayer(String direction);

  /**
   * Pick item boolean.
   *
   * @param itemName the item name
   * @return the boolean
   */
  boolean pickItem(String itemName);  // from room -> inventory

  /**
   * Drop item boolean.
   *
   * @param itemName the item name
   * @return the boolean
   */
  boolean dropItem(String itemName);  // from inventory -> room

  /**
   * Use item string.
   *
   * @param itemName the item name
   * @return the string
   */
  String useItem(String itemName);

  /**
   * Answer puzzle boolean.
   *
   * @param answer the answer
   * @return the boolean
   */
  boolean answerPuzzle(String answer);

  // ===== State Save/Load =====

  /**
   * Save game boolean.
   *
   * @param filePath the file path
   * @return the boolean
   */
  boolean saveGame(String filePath);

  /**
   * Load game boolean.
   *
   * @param filePath the file path
   * @return the boolean
   */
  boolean loadGame(String filePath);

  // ===== Game Status Query =====

  /**
   * Gets player.
   *
   * @return the player
   */
  Player getPlayer();

  /**
   * Gets current room.
   *
   * @return the current room
   */
  Room getCurrentRoom();

  void setCurrentRoom(Room room);

  /**
   * Gets inventory.
   *
   * @return the inventory
   */
  List<Item> getInventory();

  /**
   * Gets health status.
   *
   * @return the health status
   */
  HealthStatus getHealthStatus();

  /**
   * Gets health.
   *
   * @return the health
   */
  double getHealth();

  /**
   * Gets score.
   *
   * @return the score
   */
  double getScore();

  /**
   * Gets player rank.
   *
   * @return the player rank
   */
  PlayerRank getPlayerRank();

  Player getPlayerReference();
}
