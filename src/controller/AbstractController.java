package controller;

import model.core.Player;
import model.core.Room;
import model.core.WorldEngine;
import model.elements.Fixture;
import model.elements.Item;
import model.obstacle.GameObstacle;
import model.obstacle.Monster;
import model.obstacle.Puzzle;

import java.util.List;

/**
 * AbstractController defines common command handling logic shared between
 * text and GUI-based controllers (e.g., use, examine, answer).
 */
public abstract class AbstractController {
  protected final WorldEngine world;
  protected Player player;

  public AbstractController(WorldEngine world, Player player) {
    this.world = world;
    this.player = player;
  }

  public void setPlayer(Player player) {
    this.player = player;
  }

  /**
   * Handle the logic for using an item in the current room.
   * Includes solving puzzles, defeating monsters, and default item use.
   *
   * @param itemName the item to use
   * @return message describing what happened
   */
  public String handleUse(String itemName) {
    if (itemName == null || itemName.isEmpty()) {
      return "Use what?";
    }

    Room currentRoom = player.getCurrentRoom();
    GameObstacle obstacle = currentRoom.getObstacle();

    // Use against monster
    if (obstacle instanceof Monster monster && monster.isActive()
            && monster.isDefeatedByItem(itemName)) {
      monster.deactivate();
      player.updateScore(monster.getValue());
      unblockRoomExits(currentRoom);

      String effect = player.useItem(itemName);  // consume and show when_used
      return "You used " + itemName + " and defeated the monster!\n" + effect;
    }

    // Use to solve puzzle
    if (obstacle instanceof Puzzle puzzle && puzzle.isActive()
            && puzzle.isSolved(itemName)) {
      puzzle.deactivate();
      player.updateScore(puzzle.getValue());
      unblockRoomExits(currentRoom);

      // Actually use the item (decrement use, show effect)
      String effect = player.useItem(itemName);

      return "You used " + itemName + " and solved the puzzle!\n" + effect;
    }

    // Fallback: use item normally
    return player.useItem(itemName);
  }

  /**
   * Handle examine command logic for items and fixtures.
   *
   * @param name the object name to examine
   * @return description or feedback string
   */
  public String handleExamine(String name) {
    if (name == null || name.isEmpty()) {
      return "Examine what?";
    }

    Room room = player.getCurrentRoom();
    Item item = room.getItem(name);
    if (item != null) {
      return item.getDescription();
    }

    Fixture fixture = room.getFixture(name);
    if (fixture != null) {
      return fixture.getDescription();
    }

    return "You see nothing interesting about that.";
  }

  /**
   * Handle answering a puzzle with a string input.
   *
   * @param answer the player's input
   * @return result message
   */
  public String handleAnswer(String answer) {
    if (answer == null || answer.isEmpty()) {
      return "Answer what?";
    }

    Room room = player.getCurrentRoom();
    boolean solved = player.answerCorrect(answer, room);

    if (solved) {
      unblockRoomExits(room);  // <-- this is the key fix
      return "Puzzle solved!";
    }

    return "That didn't work.";
  }

  /**
   * Return a formatted list of the player's current inventory.
   *
   * @return inventory description
   */
  public String showInventoryString() {
    List<Item> inventory = player.getInventory();
    if (inventory.isEmpty()) {
      return "Your inventory is empty.";
    }

    StringBuilder sb = new StringBuilder("Inventory:\n");
    for (Item item : inventory) {
      sb.append(" - ").append(item.getName())
              .append(" (uses left: ").append(item.getUsesRemaining()).append(")\n");
    }
    return sb.toString();
  }

  /**
   * Helper method to unblock negative exits in the current room.
   * This is used after solving puzzles or defeating monsters.
   *
   * @param room the current room
   */
  private void unblockRoomExits(Room room) {
    for (String dir : List.of("N", "S", "E", "W")) {
      int exit = room.getExit(dir);
      if (exit < 0) {
        room.setExit(dir, -exit);
      }
    }
  }

  /**
   * Get the current room.
   *
   * @return the room
   */
  public Room renderGame() {
    return player.getCurrentRoom();
  }

  public Player getPlayer() {
    return player;
  }

  public WorldEngine getWorld() {
    return world;
  }
}
