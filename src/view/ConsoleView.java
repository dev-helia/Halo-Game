package view;

import model.core.Player;
import model.core.Room;
import model.core.HealthStatus;
import model.elements.Item;
import model.elements.Fixture;
import model.obstacle.GameObstacle;
import model.obstacle.Monster;

import java.io.PrintWriter;
import java.util.List;

/**
 * ConsoleView is a text-based view that outputs game information to the terminal.
 * It implements the View interface for consistent interaction with the controller.
 */
public class ConsoleView implements View {

  private final PrintWriter out;

  public ConsoleView(PrintWriter batchOutput) {
    this.out = batchOutput;
  }

  public ConsoleView() {
    this(new PrintWriter(System.out, true));
  }

  /**
   * Displays the main welcome banner and available commands.
   */
  @Override
  public void displayMainMenu() {
    out.println("===================================");
    out.println("Welcome to the Adventure Game!");
    out.println();
    out.println("Commands:");
    out.println("  (N)orth, (S)outh, (E)ast or (W)est to move");
    out.println("  (T)ake <item> to take an item");
    out.println("  (D)rop <item> to drop an item");
    out.println("  (U)se <item> to use an item");
    out.println("  (X)amine <object> to inspect something");
    out.println("  (A)nswer <text> to solve a puzzle");
    out.println("  (I)nventory to view your items");
    out.println("  (L)ook to re-describe your location");
    out.println("  (Q)uit to save and exit");
    out.println("===================================");
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
    out.println("\n==============================");
    out.println("You are standing in: " + room.getName());
    out.println(room.getRoomDescription());

    // Show obstacles
    GameObstacle obs = room.getObstacle();
    if (obs != null && obs.isActive()) {
      if (obs instanceof Monster) {
        out.println("A monster " + obs.getName() + " growls at you! You cannot get past!");
        out.println(((Monster) obs).getAttackMessage());
      } else {
        out.println("Puzzle: " + obs.getDescription());
      }
    }

    // Show items
    List<Item> items = room.getItems();
    if (!items.isEmpty()) {
      out.print("Items you see here: ");
      for (Item i : items) {
        out.print(i.getName() + " ");
      }
      out.println();
    }

    // Show fixtures
    List<Fixture> fixtures = room.getFixtures();
    if (!fixtures.isEmpty()) {
      out.print("🪑 Fixtures here: ");
      for (Fixture f : fixtures) {
        out.print(f.getName() + " ");
      }
      out.println();
    }

    // Show health status
    out.println("Health: " + player.getHealth() + " | Status: " + player.getHealthStatus());
    out.println("Score: " + player.getScore());
    out.println("==============================\n");
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
