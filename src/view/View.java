package view;

import model.core.Player;
import model.core.Room;

/**
 * View interface defines the standard methods used by any game view (text, GUI, etc.).
 * Implementations are responsible for displaying the game state and player messages.
 */
public interface View {
  /**
   * Displays the main menu or intro banner for the game.
   */
  void displayMainMenu();

  /**
   * Renders the current room, showing details like:
   * - Room name
   * - Obstacles (if any)
   * - Items present
   * - Player health and score
   * @param player the player whose state is shown
   * @param room   the room the player is currently in
   */
  void renderGame(Player player, Room room);

/**
 * Displays a message to the user (e.g., command feedback, errors).
 * @param message the message to display
 */
  void showMessage(String message);
}
