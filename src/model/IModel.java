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

  /**
   * Generate world.
   *
   * @param jsonFilePath the json file path
   * @throws IOException the io exception
   */
// ===== World Setup =====
  void generateWorld(String jsonFilePath) throws IOException;

  /**
   * Initialize player.
   *
   * @param name the name
   */
// ===== Player Initialization =====
  void initializePlayer(String name);

  /**
   * Move player boolean.
   *
   * @param direction the direction
   * @return the boolean
   */
// ===== Player Actions =====
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

  /**
   * Save game boolean.
   *
   * @param filePath the file path
   * @return the boolean
   */
// ===== State Save/Load =====
  boolean saveGame(String filePath);

  /**
   * Load game boolean.
   *
   * @param filePath the file path
   * @return the boolean
   */
  boolean loadGame(String filePath);

  /**
   * Gets player.
   *
   * @return the player
   */
// ===== Game Status Query =====
  Player getPlayer();

  /**
   * Gets current room.
   *
   * @return the current room
   */
  Room getCurrentRoom();

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

  /**
   * Gets player reference.
   *
   * @return the player reference
   */
// ===== Obstacle / Entity Access (for monster attack etc.) =====
  Player getPlayerReference();  // for Monster.attack(player)
}
