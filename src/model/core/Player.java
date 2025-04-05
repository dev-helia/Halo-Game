package model.core;

import model.elements.Item;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Iterator;

import model.obstacle.Monster;
import model.obstacle.Puzzle;

/**
 * Representing a player in the game.
 * A player can:
 *  - Move between rooms, pick up and drop items, carry items (with max total weight of 13),
 *  - Take damage from monsters, solve puzzles and defeat monsters, use items with limited uses.
 *  Maintain a health status and end-game ranking based on total score.
 */
public class Player implements Serializable {

  private final String name;
  private int health;
  private Room currentRoom;
  private final List<Item> inventory;
  private double score;

  private static final int MAX_HEALTH = 100;
  private static final int MAX_WEIGHT = 13;

  /**
   * Constructor of a new player.
   * @param name         The player's name.
   * @param startingRoom The room where the player begins.
   */
  public Player(String name, Room startingRoom) {
    this.name = name;
    this.health = MAX_HEALTH;
    this.currentRoom = startingRoom;
    this.inventory = new ArrayList<>();
    this.score = 0;
  }

  /**
   * A copy of the player.
   * @param other the copy of the player.
   */
  public void copyFrom(Player other) {
    this.health = other.health;
    this.currentRoom = other.currentRoom;
    // update inventory content without reassigning the list
    this.inventory.clear();
    this.inventory.addAll(other.inventory);
    this.score = other.score;
  }

  /**
   * The player moves to the specified direction.
   * If there is a room in that direction and  no obstacles, update the room.
   * @param direction the direction (e.g. "N", "S", "E", "W")
   * @param roomMap   the map of roomNumber → Room
   * @return true if the move was successful
   */
  public boolean move(String direction, Map<Integer, Room> roomMap) {
    int nextRoomNumber = currentRoom.getExit(direction);
    // If no pathway
    if (nextRoomNumber <= 0) {
      return false;
    }

    Room nextRoom = roomMap.get(nextRoomNumber);
    if (nextRoom == null) {
      return false;
    }
    // Update the current room.
    this.currentRoom = nextRoom;
    return true;
  }

  /**
   * Gets the current room the player is in.
   * @return current room
   */
  public Room getCurrentRoom() {
    return currentRoom;
  }

  /**
   * Attempts to pick up an item from the current room by name.
   * Will only succeed if total weight does not exceed MAX_WEIGHT.
   * @param itemName name of the item
   * @return true if item picked up
   */
  public boolean pickItem(String itemName) {
    Item item = currentRoom.removeItem(itemName);
    if (item == null) return false;

    if (getTotalWeight() + item.getWeight() > MAX_WEIGHT) {
      // Return item to room if overweight
      currentRoom.addItem(item);
      System.out.printf("Item '%s' is too heavy to carry. Current: %.1f, Limit: %d\n",
              item.getName(), getTotalWeight(), MAX_WEIGHT);
      return false;
    }

    inventory.add(item);
    return true;
  }

  /**
   * Drops an item by name into the current room.
   * @param itemName the name of the item to drop
   * @return true if dropped successfully
   */
  public boolean dropItem(String itemName) {
    Iterator<Item> it = inventory.iterator();
    while (it.hasNext()) {
      Item i = it.next();
      if (i.getName().equalsIgnoreCase(itemName)) {
        it.remove();
        currentRoom.addItem(i);
        return true;
      }
    }
    return false;
  }

  /**
   * Uses an item by name (if usable).
   * @param itemName the item to use
   * @return the result message
   */
  public String useItem(String itemName) {
    for (Item i : inventory) {
      if (i.getName().equalsIgnoreCase(itemName)) {
        if (i.isUsable()) {
          return i.use(); // decrements uses and returns action text
        } else {
          return "You can't use that item anymore.";
        }
      }
    }
    return "Item not found in inventory.";
  }

  /**
   * Calculates the total weight the player is currently carrying.
   * @return total weight of inventory
   */
  public double getTotalWeight() {
    return inventory.stream().mapToDouble(Item::getWeight).sum();
  }

  /**
   * Returns the list of items currently held by the player.
   * @return inventory list
   */
  public List<Item> getInventory() {
    return inventory;
  }

  /**
   * Returns the player's current health.
   * @return health value
   */
  public double getHealth() {
    return health;
  }

  /**
   * Sets the player's health.
   * Value is capped between 0 and MAX_HEALTH.
   * @param health new health value
   */
  public void setHealth(int health) {
    this.health = Math.max(0, Math.min(health, MAX_HEALTH));
  }

  /**
   * Reduces player's health by the damage amount.
   * @param damage the amount of health lost
   */
  public void takeDamage(int damage) {
    setHealth(this.health - damage);
  }

  /**
   * Gets the player's health status based on health thresholds.
   * @return health status
   */
  public HealthStatus getHealthStatus() {
    return HealthStatus.fromHealth(this.health);
  }

  /**
   * Checks if the player is still active.
   * @return true if active, otherwise false.
   */
  public boolean isAlive() {
    return this.health > 0;
  }

  /**
   * Attempts to answer a puzzle in the given room.
   * If successful, deactivates the puzzle and adds score.
   * @param answer the user's answer
   * @param room   the current room
   * @return true if answer was correct
   */
  public boolean answerCorrect(String answer, Room room) {
    if (room.hasObstacle() && room.getObstacle() instanceof Puzzle puzzle) {
      if (puzzle.isSolved(answer)) {
        puzzle.deactivate();
        updateScore(puzzle.getValue());

        // Fix: If the puzzle affects the current room (not a different target room)
        if (puzzle.affectsTarget()) {
          int targetRoom = puzzle.getTargetRoomNumber();

          // Try unblocking any negative exits that point TO this room (the puzzle's target)
          for (Map.Entry<String, Integer> entry : room.getExits().entrySet()) {
            String dir = entry.getKey();
            int val = entry.getValue();
            if (val == -targetRoom) {
              room.setExit(dir, targetRoom);  // unblock the exit
              System.out.println("Unblocked exit " + dir + " to room " + targetRoom);
            }
          }
        }

        return true;
      }
    }
    return false;
  }

  /**
   * Attempt to defeat a monster obstacle in the current room using the given item.
   * If successful, the monster is deactivated and score is updated.
   *
   * @param itemName name of the item to use
   * @return true if the monster was defeated
   */
  public boolean defeatMonster(String itemName) {
    if (currentRoom.hasObstacle() && currentRoom.getObstacle() instanceof Monster monster) {
      if (monster.isDefeatedByItem(itemName)) {
        monster.deactivate();
        updateScore(monster.getValue());
        return true;
      }
    }
    return false;
  }

  /**
   * Updates the player's score.
   * @param points points to add
   */
  public void updateScore(int points) {
    this.score += points;
  }

  /**
   * Gets the player's total score.
   * @return current score
   */
  public double getScore() {
    return score;
  }

  /**
   * Gets the player's end-game rank based on score.
   * @return rank enum
   */
  public PlayerRank getRank() {
    return PlayerRank.fromScore((int) score);
  }

  /**
   * Gets the player's name.
   * @return name
   */
  public String getName() {
    return name;
  }
}

