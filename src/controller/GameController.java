package controller;

import model.core.Player;
import model.core.WorldEngine;
import model.core.Room;
import model.elements.Item;
import model.elements.Fixture;
import model.obstacle.GameObstacle;
import model.obstacle.Monster;
import view.View;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

/**
 * GameController is responsible for handling the main game loop:
 * - Accepting player input
 * - Interacting with the model (World, Player, Rooms)
 * - Displaying output via the View
 *  - Call : WorldEngine, Player, View
 *  - Use : Scanner
 */
public class GameController {
  private Player player;
  private final WorldEngine world;
  private final View view;
  private final Scanner scanner;
  private static final String MAP_FOLDER = "src/resources/maps";
  private static final String SAVE_FOLDER = "src/resources/saves";
  // 🌐 Direction keys
  private static final String NORTH_KEY = "N";
  private static final String SOUTH_KEY = "S";
  private static final String EAST_KEY = "E";
  private static final String WEST_KEY = "W"; //
  private static final Set<String> MOVE_KEYS = Set.of(NORTH_KEY, SOUTH_KEY, EAST_KEY, WEST_KEY);

  // 🎒 Item interaction
  private static final String TAKE_KEY = "T";
  private static final String DROP_KEY = "D";
  private static final String USE_KEY = "U";

  // 🕵️ Info & puzzle
  private static final String INVENTORY_KEY = "I";
  private static final String LOOK_KEY = "L";
  private static final String EXAMINE_KEY = "X";
  private static final String ANSWER_KEY = "A";

  // 💾 System
  private static final String SAVE_KEY = "SAVE";
  private static final String RESTORE_KEY = "RESTORE";
  private static final String NEW_KEY = "NEW";
  private static final String QUIT_KEY = "Q";
  private static final String HELP_KEY = "HELP";


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
    this.scanner = new Scanner(inputSource); //TODO 什么意思
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
    // New game (select map) or resume game
    view.showMessage("Type " + NEW_KEY +" start a new game or "+ RESTORE_KEY + " to load saves:");
    while (true) {
      String input = scanner.nextLine().trim().toUpperCase();

      // start a new game
      if (input.equals(NEW_KEY)) {
        // List the map + select
        String mapPath = selectMap();
        if (mapPath == null) {
          view.showMessage("🚨 Map selection failed. Exiting...");
          return;
        }
        world.generateWorld(mapPath);
        view.showMessage("Map loaded successfully!");
        // Ask player name
        view.showMessage("Enter your name:");
        String name = scanner.nextLine().trim();
        Room startingRoom = world.getRoom(1);
        this.player = new Player(name, startingRoom);
        break;
      }

      // restore the game
      else if (input.equals(RESTORE_KEY)) {
        // List saves + selection
        String savePath = selectSave();
        if (savePath == null) {
          view.showMessage("🚨 Save selection failed. Exiting...");
          return;
        }
        this.player = new Player("TEMP", new Room(0, "TEMP", "This is a temporary room."));
        boolean restored = world.restoreState(savePath, player);
        if (restored) {
          view.showMessage("Game restored successfully!");
          break;
        }
      } else {
        view.showMessage("Invalid input. Please enter 'NEW' or 'RESTORE'.");
      }
    }

    // Main game loop
    while (true) {
      // current room info
      Room currentRoom = player.getCurrentRoom();
      view.showMessage("Health Status: " + player.getHealthStatus());

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
      view.showMessage("👉 Type a command ('HELP' for help, 'Q' to quit):");

      if (!scanner.hasNextLine()) {
        view.showMessage("🛑 Input stream closed. Ending game.");
        break;
      }
      String line = scanner.nextLine().trim();

      // TODO 好像没有看见加分的逻辑
      // TODO 而且可以单纯输入a, 应该要clamping一下
      // enter input
      // cmd(T/D/U) + optional arg(Ticket)
      String[] parts = line.split(" ", 2);
      String cmd = parts[0].toUpperCase();
      String arg = parts.length > 1 ? parts[1] : null;

      if (isQuitCommand(cmd)) {
        if (handleQuitCommand()) break;
        continue;
      }

      handleCommand(cmd, arg);
    }
    // Game over summary
    view.showMessage("Thanks for playing! Goodbye~");
    view.showMessage("Final Score: " + player.getScore());
    view.showMessage("Your Rank: " + player.getRank());
  }

  /**
   * A generic file picker from folder.
   * 1. List current json files.
   * 2. Allow the user to simply choose a number to enter the game.
   * The index starts from 1.
   * TODO path的不同的系统的解析
   *
   * @param folderPath  folder to list files from (e.g., "src/resources", "src/resources/saves")
   * @param promptTitle what to display as title (e.g., "🧭 Available maps:")
   * @param promptInput what to display as prompt (e.g., "Choose a map number:")
   * @return full path of selected file, or null if none selected
   */
  private String pickJsonFile(String folderPath, String promptTitle, String promptInput) {
    // file folder
    File folder = new File(folderPath);
    // filter
    File[] files = folder.listFiles((dir, name) -> name.endsWith(".json"));

    // no json files in current path
    if (files == null || files.length == 0) {
      view.showMessage("⚠️ No .json files found in: " + folderPath);
      return null;
    }

    // print current files with order
    view.showMessage(promptTitle);
    for (int i = 0; i < files.length; i++) {
      view.showMessage((i + 1) + ". " + files[i].getName());
    }
    view.showMessage(promptInput);

    // user input
    while (true) {
      String input = scanner.nextLine().trim();
      try {
        int choice = Integer.parseInt(input);
        if (choice >= 1 && choice <= files.length) {
          return files[choice - 1].getPath();
        } else {
          view.showMessage("⚠️ Invalid number. Try again:");
        }
      } catch (NumberFormatException e) {
        view.showMessage("⚠️ Please enter a number.");
      }
    }
  }

  private String selectMap() {
    return pickJsonFile(MAP_FOLDER, "🧭 Available maps:", "Choose a map number:");
  }

  private String selectSave() {
    return pickJsonFile(SAVE_FOLDER, "💾 Available saves:", "Choose a save number:");
  }

  private boolean isQuitCommand(String cmd) {
    return cmd.equals(QUIT_KEY);
  }

  /**
   * Handles the "Q" command: asks whether to save and confirms quitting.
   *
   * @return true if player wants to quit, false if they cancel
   */
  private boolean handleQuitCommand() {
    view.showMessage("Do you want to save before quitting? (Y/N)");
    while (true) {
      String response = scanner.nextLine().trim().toUpperCase();
      switch (response) {
        case "Y" -> {
          view.showMessage("Enter file name to save (without .json):");
          String fileName = scanner.nextLine().trim();
          if (!fileName.endsWith(".json")) {
            fileName += ".json";
          }
          String fullPath = SAVE_FOLDER + "/" + fileName;
          boolean success = world.saveState(fullPath, player);
          view.showMessage(success ? "✅ Game saved to " + fileName : "❌ Save failed.");
          return true; // quit
        }
        case "N" -> {
          view.showMessage("Okay~ No save. 🌙✨");
          return true; // quit
        }
        default -> view.showMessage("❓ Please enter 'Y' or 'N'.");
      }
    }
  }

  private void handleCommand(String cmd, String arg) {
    switch (cmd) {
      case "N", "S", "E", "W" -> handleMove(cmd);
      case "T" -> handleTake(arg);
      case "D" -> handleDrop(arg);
      case "U" -> handleUse(arg);
      case "I" -> handleInventory();
      case "L" -> handleLook();
      case "X" -> handleExamine(arg);
      case "A" -> handleAnswer(arg);
      case "SAVE" -> handleSave();
      case "RESTORE" -> handleRestore();
      case "HELP" -> handleHelp();
      default -> view.showMessage("Unknown command: " + cmd);
    }
  }

  private void handleMove(String cmd) {
    boolean moved = player.move(cmd, world.getWorldMap());
    if (moved) {
      view.showMessage("You move " + directionToFull(cmd) + ".");
      Room current = player.getCurrentRoom();
      view.renderGame(player, current);
    } else {
      view.showMessage("🚧 You can't move that way.");
    }
  }

  private String directionToFull(String dir) {
    return switch (dir) {
      case "N" -> "North";
      case "S" -> "South";
      case "E" -> "East";
      case "W" -> "West";
      default -> dir;
    };
  }

  private void handleTake(String arg) {
    if (arg == null) {
      view.showMessage("❓ What do you want to take?");
      return;
    }
    boolean success = player.pickItem(arg);
    view.showMessage(success ? "🎒 You picked up " + arg + "." : "🚫 You can't take that.");
  }

  private void handleDrop(String arg) {
    if (arg == null) {
      view.showMessage("❓ What do you want to drop?");
      return;
    }
    boolean success = player.dropItem(arg);
    view.showMessage(success ? "🗑️ You dropped " + arg + "." : "🚫 You don't have that item.");
  }

  private void handleUse(String arg) {
    if (arg == null) {
      view.showMessage("❓ Use what?");
      return;
    }
    String result = player.useItem(arg);
    view.showMessage(result);
  }

  private void handleInventory() {
    List<Item> inventory = player.getInventory();
    if (inventory.isEmpty()) {
      view.showMessage("👜 Your inventory is empty.");
      return;
    }

    view.showMessage("🎒 Your Inventory:");
    for (Item i : inventory) {
      view.showMessage(" - " + i.getName() + " (uses left: " + i.getUsesRemaining() + ")");
    }
  }

  private void handleLook() {
    Room current = player.getCurrentRoom();
    view.renderGame(player, current);
  }

  private void handleExamine(String arg) {
    if (arg == null) {
      view.showMessage("❓ Examine what?");
      return;
    }

    Room room = player.getCurrentRoom();
    boolean found = false;

    Item item = room.getItem(arg);
    if (item != null) {
      view.showMessage("🔍 " + item.getDescription());
      found = true;
    }

    Fixture fixture = room.getFixture(arg);
    if (fixture != null) {
      view.showMessage("🛋️ " + fixture.getDescription());
      found = true;
    }

    if (!found) {
      view.showMessage("🤷 You see nothing interesting about that.");
    }
  }

  private void handleAnswer(String arg) {
    if (arg == null) {
      view.showMessage("❓ Answer what?");
      return;
    }

    Room room = player.getCurrentRoom();
    boolean solved = player.answerCorrect(arg, room);
    if (solved) {
      //player.addScore(10); // 示例值
    }
    view.showMessage(solved ? "🎉 Puzzle solved!" : "❌ That didn't work.");
  }

  private void handleSave() {
    view.showMessage("Do you want to save the game? (Y/N)");
    while (true) {
      String answer = scanner.nextLine().trim().toUpperCase();
      switch (answer) {
        case "Y" -> {
          view.showMessage("Enter file name to save (without .json):");
          String fileName = scanner.nextLine().trim();
          if (!fileName.endsWith(".json")) {
            fileName += ".json";
          }
          String path = SAVE_FOLDER + "/" + fileName;
          boolean success = world.saveState(path, player);
          view.showMessage(success ? "✅ Game saved to " + fileName : "❌ Save failed.");
          return;
        }
        case "N" -> {
          view.showMessage("No save. Let's keep going~ 🌈");
          return;
        }
        default -> view.showMessage("❓ Please enter 'Y' or 'N'.");
      }
    }
  }

  private void handleRestore() {
    String savePath = selectSave();
    if (savePath == null) {
      view.showMessage("🚨 No valid save file selected.");
      return;
    }

    Player temp = new Player("TEMP", new Room(0, "TEMP", "Placeholder"));
    boolean success = world.restoreState(savePath, temp);
    if (success) {
      this.player = temp;
      view.showMessage("✅ Game restored!");
    } else {
      view.showMessage("❌ Restore failed.");
    }
  }

  private void handleHelp() {
    view.showMessage("\n🎮 Movement Commands\n---------------------");

    // Movement
    view.showMessage("  - " + NORTH_KEY + " / " + SOUTH_KEY + " / " + EAST_KEY + " / " + WEST_KEY + " : Move North/South/East/West");

    // Item actions
    view.showMessage("\n🧸 Item Actions\n---------------------");
    view.showMessage("  - " + TAKE_KEY + " <item>    : Take an item");
    view.showMessage("  - " + DROP_KEY + " <item>    : Drop an item");
    view.showMessage("  - " + USE_KEY + " <item>     : Use an item");

    // Info & puzzle
    view.showMessage("\n🧩 Puzzle & Info\n---------------------");
    view.showMessage("  - " + INVENTORY_KEY + "           : Check inventory");
    view.showMessage("  - " + LOOK_KEY + "               : Look around the room");
    view.showMessage("  - " + EXAMINE_KEY + " <name>     : Examine an item or fixture");
    view.showMessage("  - " + ANSWER_KEY + " <answer>    : Answer a puzzle");

    // System
    view.showMessage("\n💾 System Commands\n---------------------");
    view.showMessage("  - " + SAVE_KEY + "               : Save your progress");
    view.showMessage("  - " + RESTORE_KEY + "            : Restore a saved game");
    view.showMessage("  - " + QUIT_KEY + "               : Quit the game");
    view.showMessage("  - " + HELP_KEY + "               : Show this help menu again");
  }

}

