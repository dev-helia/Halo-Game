package view;

import model.core.Player;
import model.core.Room;
import model.elements.Item;
import model.obstacle.GameObstacle;

import java.util.List;

/**
 * ConsoleView is a text-based view that outputs game information to the terminal.
 * It implements the View interface for consistent interaction with the controller.
 */
public class ConsoleView implements View {

  /**
   * Displays the main welcome banner and available commands.
   */
  @Override
  public void displayMainMenu() {
    System.out.println("===================================");
    System.out.println("🎮 Welcome to AlignQuest Adventure!");
    System.out.println("Commands: N, S, E, W, T item, D item, U item, X target, A answer, I, L, Q");
    System.out.println("===================================");
  }

  /**
   * Renders the current game state to the console including:
   * - Room name
   * - Any active obstacle (puzzle/monster)
   * - Items present in the room
   * - Player health and score
   *
   * @param player the current player
   * @param room   the current room the player is in
   */
  @Override
  public void renderGame(Player player, Room room) {
    System.out.println("\n== You are in: " + room.getName() + " ==");

    // Show active obstacle description (if any)
    GameObstacle obs = room.getObstacle();
    if (obs != null && obs.isActive()) {
      System.out.println("⚠️ Obstacle: " + obs.getDescription());
    }

    // Show list of items in the room
    List<Item> items = room.getItems();
    if (!items.isEmpty()) {
      System.out.print("🧸 Items here: ");
      for (Item i : items) {
        System.out.print(i.getName() + " ");
      }
      System.out.println();
    }

    // Show player stats
    System.out.println("💖 Health: " + player.getHealth());
    System.out.println("🎖 Score: " + player.getScore());
  }

  /**
   * Outputs a single message line to the console.
   *
   * @param message the message to show (e.g., "You picked up the ticket.")
   */
  @Override
  public void showMessage(String message) {
    System.out.println(message);
  }
}
