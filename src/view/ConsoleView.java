package view;

import model.core.Player;
import model.core.Room;
import model.core.HealthStatus;
import model.elements.Item;
import model.elements.Fixture;
import model.obstacle.GameObstacle;
import model.obstacle.Monster;

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
    System.out.println("🎮 Welcome to the Adventure Game!");
    System.out.println("To begin, enter your name.");
    System.out.println();
    System.out.println("Commands:");
    System.out.println("  (N)orth, (S)outh, (E)ast or (W)est to move");
    System.out.println("  (T)ake <item> to take an item");
    System.out.println("  (D)rop <item> to drop an item");
    System.out.println("  (U)se <item> to use an item");
    System.out.println("  (X)amine <object> to inspect something");
    System.out.println("  (A)nswer <text> to solve a puzzle");
    System.out.println("  (I)nventory to view your items");
    System.out.println("  (L)ook to re-describe your location");
    System.out.println("  (Q)uit to save and exit");
    System.out.println("===================================");
  }

  /**
   * Renders the current game state to the console including:
   * - Room name, description
   * - Any active obstacle (puzzle/monster)
   * - Items and fixtures present in the room
   * - Player health, score, and status
   *
   * @param player the current player
   * @param room   the current room the player is in
   */
  @Override
  public void renderGame(Player player, Room room) {
    System.out.println("\n==============================");
    System.out.println("📍 You are standing in: " + room.getName());
    System.out.println(room.getRoomDescription());

    // Show obstacles
    GameObstacle obs = room.getObstacle();
    if (obs != null && obs.isActive()) {
      if (obs instanceof Monster) {
        System.out.println("👹 A monster " + obs.getName() + " growls at you! You cannot get past!");
        System.out.println(((Monster) obs).getAttackMessage());
      } else {
        System.out.println("🧩 Puzzle: " + obs.getDescription());
      }
    }

    // Show items
    List<Item> items = room.getItems();
    if (!items.isEmpty()) {
      System.out.print("🧸 Items you see here: ");
      for (Item i : items) {
        System.out.print(i.getName() + " ");
      }
      System.out.println();
    }

    // Show fixtures
    List<Fixture> fixtures = room.getFixtures();
    if (!fixtures.isEmpty()) {
      System.out.print("🪑 Fixtures here: ");
      for (Fixture f : fixtures) {
        System.out.print(f.getName() + " ");
      }
      System.out.println();
    }

    // Show health status
    System.out.println("💖 Health: " + player.getHealth() + " | Status: " + player.getHealthStatus());
    System.out.println("🎖 Score: " + player.getScore());
    System.out.println("==============================");
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
