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
  void generateWorld(String jsonFilePath) throws IOException;

  // ===== Player Initialization =====
  void initializePlayer(String name);

  // ===== Player Actions =====
  boolean movePlayer(String direction);
  boolean pickItem(String itemName);  // from room -> inventory
  boolean dropItem(String itemName);  // from inventory -> room
  String useItem(String itemName);
  boolean answerPuzzle(String answer);

  // ===== State Save/Load =====
  boolean saveGame(String filePath);
  boolean loadGame(String filePath);

  // ===== Game Status Query =====
  Player getPlayer();
  Room getCurrentRoom();
  List<Item> getInventory();
  HealthStatus getHealthStatus();
  double getHealth();
  double getScore();
  PlayerRank getPlayerRank();

  // ===== Obstacle / Entity Access (for monster attack etc.) =====
  Player getPlayerReference();  // for Monster.attack(player)
}
