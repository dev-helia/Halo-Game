package controller;

import model.core.Player;
import model.core.WorldEngine;
import model.core.Room;
import model.elements.Item;
import model.elements.Fixture;
import model.obstacle.GameObstacle;
import model.obstacle.Monster;
import view.View;

import java.io.IOException;
import java.util.Scanner;

/**
 * GameController is responsible for handling the main game loop:
 * - Accepting player input
 * - Interacting with the model (World, Player, Rooms)
 * - Displaying output via the View
 */
public class GameController {
  private Player player;
  private final WorldEngine world;
  private final View view;
  private final Scanner scanner;

  /**
   * Constructor for GameController.
   *
   * @param world        The game world containing all rooms and elements.
   * @param view         The view used for output (e.g. console).
   * @param inputSource  Input source (BufferedReader/StringReader or System.in).
   */
  public GameController(WorldEngine world, View view, Readable inputSource) {
    this.world = world;
    this.view = view;
    this.scanner = new Scanner(inputSource);
    this.player = null; // we’ll set it later after name is input
  }


  /**
   * Starts the game loop, initializing the player and processing commands.
   *
   * @throws IOException if there is an input/output error
   */
  public void startGame() throws IOException {
    view.displayMainMenu();

    // Initialize player after asking for name
    view.showMessage("Enter your name:");
    String name = scanner.nextLine().trim();
    Room startingRoom = world.getRoom(1); // or world.getStartingRoom()
    Player player = new Player(name, startingRoom);
    this.player = player; // update the field

    // Main game loop
    while (true) {
      Room currentRoom = player.getCurrentRoom();
      view.showMessage("Health Status: " + player.getHealthStatus());
      view.renderGame(player, currentRoom);

      // Auto-monster attack logic.
      GameObstacle obs = currentRoom.getObstacle();
      if (obs instanceof Monster m && m.isActive() && m.canAttack()) {
        m.attack(player);
        view.showMessage(m.getAttackMessage());
        view.showMessage("Player takes -" + m.getDamage() + " damage!");
        if (player.getHealth() <= 0) {
          view.showMessage("You have fallen asleep... Game Over!");
          break;
        }
      }

      // Prompt for command
      view.showMessage("\nEnter command: ");
      if (!scanner.hasNextLine()) break;
      String line = scanner.nextLine().trim();
      // Quit command
      if (line.equalsIgnoreCase("Q")) {
        world.saveState("savegame.json", player);
        view.showMessage("Game saved before quitting.");
        break;
      }

      // Parse command and optional argument
      String[] parts = line.split(" ", 2);
      String cmd = parts[0].toUpperCase();
      String arg = parts.length > 1 ? parts[1] : null;

      // Process each command
      switch (cmd) {
        case "N", "S", "E", "W" -> {
          boolean moved = player.move(cmd, world.getWorldMap());
          if (!moved) view.showMessage("You can't move that way.");
        }
        case "T" -> {
          if (arg == null) {
            view.showMessage("What do you want to take?");
            break;
          }
          boolean success = player.pickItem(arg);
          view.showMessage(success ? "You picked up " + arg : "You can't take that.");
        }
        case "D" -> {
          if (arg == null) {
            view.showMessage("What do you want to drop?");
            break;
          }
          boolean success = player.dropItem(arg);
          view.showMessage(success ? "You dropped " + arg : "You don't have that item.");
        }
        case "U" -> {
          if (arg == null) {
            view.showMessage("Use what?");
            break;
          }
          view.showMessage(player.useItem(arg));
        }
        case "I" -> {
          view.showMessage("Your Inventory:");
          for (Item i : player.getInventory()) {
            view.showMessage(" - " + i.getName() + " (uses left: " + i.getUsesRemaining() + ")");
          }
        }
        case "L" -> view.renderGame(player, currentRoom);
        case "X" -> {
          if (arg == null) {
            view.showMessage("Examine what?");
            break;
          }
          Item item = currentRoom.getItem(arg);
          boolean found = false;
          if (item != null) {
            view.showMessage(item.getDescription());
            found = true;
          }

          Fixture fix = currentRoom.getFixture(arg);
          if (fix != null) {
            view.showMessage(fix.getDescription());
            found = true;
          }
          if (!found) {
            view.showMessage("You see nothing interesting about that.");
          }
        }

        case "A" -> {
          if (arg == null) {
            view.showMessage("Answer what?");
            break;
          }
          boolean solved = player.answerCorrect(arg, currentRoom);
          view.showMessage(solved ? "Puzzle solved!" : "That didn't work.");
        }
        default -> view.showMessage("Unknown command: " + cmd);

        case "SAVE" -> {
          boolean saved = world.saveState("savegame.json", player);
          view.showMessage(saved ? "Game saved." : "Failed to save game.");
        }
        case "RESTORE" -> {
          boolean loaded = world.restoreState("savegame.json", player);
          view.showMessage(loaded ? "Game restored." : "Failed to restore game.");
        }
      }
    }

    // Game over summary
    view.showMessage("Thanks for playing! Goodbye~");
    view.showMessage("Final Score: " + player.getScore());
    view.showMessage("Your Rank: " + player.getRank());
  }
}

