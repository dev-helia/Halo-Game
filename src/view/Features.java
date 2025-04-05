package view;

/**
 * This interface represents all the user actions that the GUI can trigger in the game.
 * The controller will implement this interface to define what each action actually does.
 * This follows the MVC pattern where the view delegates behavior to the controller.
 */
public interface Features {

  /**
   * Move the player to the north.
   */
  void moveNorth();

  /**
   * Move the player to the south.
   */
  void moveSouth();

  /**
   * Move the player to the east.
   */
  void moveEast();

  /**
   * Move the player to the west.
   */
  void moveWest();

  /**
   * Pick up an item by name.
   * @param itemName The name of the item to take.
   */
  void takeItem(String itemName);

  /**
   * Drop an item by name.
   * @param itemName The name of the item to drop.
   */
  void dropItem(String itemName);

  /**
   * Use an item from the inventory.
   * @param itemName The name of the item to use.
   */
  void useItem(String itemName);

  /**
   * Examine an item or fixture by name.
   * @param name The name of the object to examine.
   */
  void examine(String name);

  /**
   * Answer a puzzle with a specific answer.
   * @param answer The user's answer input.
   */
  void answer(String answer);

  /**
   * Refresh the current room view (reprint or redisplay).
   */
  void look();

  /**
   * Show the player’s current inventory in the GUI.
   */
  void showInventory();

  /**
   * Save the current game state (prompt user for save file name).
   */
  void saveGame();

  /**
   * Restore a previously saved game (let user choose file).
   */
  void restoreGame();

  /**
   * Quit the game. Can show confirmation dialog.
   */
  void quitGame();

  /**
   * Show the "About" popup screen with game info.
   */
  void showAbout();

  /**
   * General-purpose move command using direction string (used in GUI).
   * @param direction N, S, E, or W
   */
  default void move(String direction) {
    switch (direction.toUpperCase()) {
      case "N" -> moveNorth();
      case "S" -> moveSouth();
      case "E" -> moveEast();
      case "W" -> moveWest();
      default -> throw new IllegalArgumentException("Invalid direction: " + direction);
    }
  }
}
