package controller;

import model.IModel;
import model.core.Room;
import model.elements.Fixture;
import model.elements.Item;
import model.obstacle.GameObstacle;
import model.obstacle.Monster;
import model.obstacle.Puzzle;

import java.util.List;

/**
 * AbstractController defines common command handling logic shared between
 * text and GUI-based controllers (e.g., use, examine, answer).
 * It now depends on the IModel interface for better abstraction.
 */
public abstract class AbstractController {
  protected final IModel model;

  /**
   * Constructs the controller with a given model.
   * @param model the game model implementing IModel
   */
  public AbstractController(IModel model) {
    this.model = model;
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

    Room currentRoom = model.getCurrentRoom();
    GameObstacle obstacle = currentRoom.getObstacle();

    // Use against monster
    if (obstacle instanceof Monster monster && monster.isActive()
            && monster.isDefeatedByItem(itemName)) {
      monster.deactivate();
      model.getPlayer().updateScore(monster.getValue());
      unblockRoomExits(currentRoom);

      String effect = model.useItem(itemName);
      return "You used " + itemName + " and defeated the monster!\n" + effect;
    }

    // Use to solve puzzle
    if (obstacle instanceof Puzzle puzzle && puzzle.isActive()
            && puzzle.isSolved(itemName)) {
      puzzle.deactivate();
      model.getPlayer().updateScore(puzzle.getValue());
      unblockRoomExits(currentRoom);


      String effect = model.useItem(itemName);
      return "You used " + itemName + " and solved the puzzle!\n" + effect;
    }

    // fallback
    return model.useItem(itemName);
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

    Room room = model.getCurrentRoom();
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

    boolean solved = model.answerPuzzle(answer);
    if (solved) {
      unblockRoomExits(model.getCurrentRoom());
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
    List<Item> inventory = model.getInventory();
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
    return model.getCurrentRoom();
  }

  /**
   * Gets the game model.
   *
   * @return the IModel implementation
   */
  public IModel getModel() {
    return model;
  }
}
