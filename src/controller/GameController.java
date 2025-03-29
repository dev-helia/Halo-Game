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
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;

/**
 * The type Game controller.
 */
public class GameController {
  private Player player;
  private final WorldEngine world;
  private final View view;
  private final Scanner scanner;
  // File path
  private static final String MAP_FOLDER =
          Paths.get(System.getProperty("user.dir"), "src", "resources", "maps").toString();

  private static final String SAVE_FOLDER =
          Paths.get(System.getProperty("user.dir"), "src", "resources", "saves").toString();

  // Direction keys
  private static final String NORTH_KEY = "N";
  private static final String SOUTH_KEY = "S";
  private static final String EAST_KEY = "E";
  private static final String WEST_KEY = "W"; //

  // Item interaction
  private static final String TAKE_KEY = "T";
  private static final String DROP_KEY = "D";
  private static final String USE_KEY = "U";

  // Info & puzzle
  private static final String INVENTORY_KEY = "I";
  private static final String LOOK_KEY = "L";
  private static final String EXAMINE_KEY = "X";
  private static final String ANSWER_KEY = "A";

  // System
  private static final String SAVE_KEY = "SAVE";
  private static final String RESTORE_KEY = "RESTORE";
  private static final String NEW_KEY = "NEW";
  private static final String QUIT_KEY = "Q";
  private static final String HELP_KEY = "HELP";

  // Ensure save folder exists
  static {
    try {
      Files.createDirectories(Paths.get(SAVE_FOLDER));
    } catch (IOException e) {
      System.err.println("Failed to create save directory: " + e.getMessage());
    }
  }
  /**
   * Instantiates a new Game controller.
   *
   * @param world       the world
   * @param view        the view
   * @param inputSource the input source
   */
  public GameController(WorldEngine world, View view, Readable inputSource) {
    this.world = world;
    this.view = view;
    this.scanner = new Scanner(inputSource);
    this.player = null; // we’ll set it later after name is input
  }

  /**
   * Start game.
   *
   * @throws IOException the io exception
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
          view.showMessage("Map selection failed. Exiting...");
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
          view.showMessage("Save selection failed. Exiting...");
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
      view.showMessage("Type a command ('HELP' for help, 'Q' to quit):");

      if (!scanner.hasNextLine()) {
        view.showMessage("Input stream closed. Ending game.");
        break;
      }
      String line = scanner.nextLine().trim();

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
   * Picks a .json file from the given folder path by prompting the user.
   *
   * @param folderPath   the directory path where .json files are stored
   * @param promptTitle  the title message shown before listing files
   * @param promptInput  the prompt asking user to choose a file number
   * @return the full path of the selected .json file, or null if no file is selected
   */
  private String pickJsonFile(String folderPath, String promptTitle, String promptInput) {
    // file folder
    File folder = new File(folderPath);
    // filter
    File[] files = folder.listFiles((dir, name) -> name.endsWith(".json"));

    // no json files in current path
    if (files == null || files.length == 0) {
      view.showMessage("No .json files found in: " + folderPath);
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
          view.showMessage("Invalid number. Try again:");
        }
      } catch (NumberFormatException e) {
        view.showMessage("Please enter a number.");
      }
    }
  }

  /**
   * Allows the user to select a game map from available .json files.
   *
   * @return the path to the selected map file, or null if selection fails
   */
  private String selectMap() {
    return pickJsonFile(MAP_FOLDER, "🧭 Available maps:", "Choose a map number:");
  }

  /**
   * Allows the user to select a saved game state from available .json files.
   *
   * @return the path to the selected save file, or null if selection fails
   */
  private String selectSave() {
    return pickJsonFile(SAVE_FOLDER, "💾 Available saves:", "Choose a save number:");
  }

  /**
   * Check the quit command input.
   *
   * @param cmd the user's command input
   * @return true if it is a quit command
   */
  private boolean isQuitCommand(String cmd) {
    return cmd.equals(QUIT_KEY);
  }

  /**
   * Handling the quit command loop.
   *
   * @return true if the user wants to quit
   */
  private boolean handleQuitCommand() {
    view.showMessage("Do you want to save before quitting? (Y/N)");
    while (true) {
      String response = scanner.nextLine().trim().toUpperCase();
      switch (response) {
        case "Y" -> {
          saveGameState();
          return true; // quit
        }
        case "N" -> {
          view.showMessage("Okay~ No save. 🌙✨");
          return true; // quit
        }
        default -> view.showMessage("Please enter 'Y' or 'N'.");
      }
    }
  }

  /**
   * Handling save the current game state.
   */
  private void saveGameState() {
    view.showMessage("Enter file name to save (without .json):");
    String fileName = scanner.nextLine().trim();
    if (!fileName.endsWith(".json")) {
      fileName += ".json";
    }
    String fullPath = SAVE_FOLDER + File.separator + fileName;
    boolean success = world.saveState(fullPath, player);
    view.showMessage(success ? "Game saved to " + fileName : " Save failed.");
  }

  /**
   * Handing user's input commands.
   *
   * @param cmd  current command.
   * @param arg the input string
   */
  private void handleCommand(String cmd, String arg) {
    switch (cmd) {
      case NORTH_KEY, SOUTH_KEY, EAST_KEY, WEST_KEY -> handleMove(cmd);
      case TAKE_KEY -> handleTake(arg);
      case DROP_KEY -> handleDrop(arg);
      case USE_KEY -> handleUse(arg);
      case INVENTORY_KEY -> handleInventory();
      case LOOK_KEY -> handleLook();
      case EXAMINE_KEY -> handleExamine(arg);
      case ANSWER_KEY -> handleAnswer(arg);
      case SAVE_KEY -> handleSave();
      case RESTORE_KEY -> handleRestore();
      case HELP_KEY -> handleHelp();
      default -> view.showMessage("Unknown command: " + cmd);
    }
  }

  /**
   * Handling move command.
   *
   * @param cmd the move command
   */
  private void handleMove(String cmd) {
    boolean moved = player.move(cmd, world.getWorldMap());
    if (moved) {
      view.showMessage("You move " + directionToFull(cmd) + ".");
      Room current = player.getCurrentRoom();
      view.renderGame(player, current);
    } else {
      view.showMessage("You can't move that way.");
    }
  }

  /**
   * Handling complete the move command.
   *
   * @param dir the move command
   * @return the full name of the direction (North, West, South, East)
   */
  private String directionToFull(String dir) {
    return switch (dir) {
      case "N" -> "North";
      case "S" -> "South";
      case "E" -> "East";
      case "W" -> "West";
      default -> dir;
    };
  }

  /**
   * Handling take the item.
   *
   * @param arg the input string
   */
  private void handleTake(String arg) {
    if (arg == null) {
      view.showMessage("What do you want to take?");
      return;
    }
    boolean success = player.pickItem(arg);
    view.showMessage(success ? "You picked up " + arg + "." : "You can't take that.");
  }

  /**
   * Handling dropping the item.
   *
   * @param arg the input string
   */
  private void handleDrop(String arg) {
    if (arg == null) {
      view.showMessage("What do you want to drop?");
      return;
    }
    boolean success = player.dropItem(arg);
    view.showMessage(success ? "You dropped " + arg + "." : "You don't have that item.");
  }

  /**
   * Handling use the item.
   *
   * @param arg
   */
  private void handleUse(String arg) {
    if (arg == null) {
      view.showMessage("Use what?");
      return;
    }
    String result = player.useItem(arg);
    view.showMessage(result);
  }

  /**
   * Handling check the inventory.
   */
  private void handleInventory() {
    List<Item> inventory = player.getInventory();
    if (inventory.isEmpty()) {
      view.showMessage("Your inventory is empty.");
      return;
    }

    view.showMessage("Your Inventory:");
    for (Item i : inventory) {
      view.showMessage(" - " + i.getName() + " (uses left: " + i.getUsesRemaining() + ")");
    }
  }

  /**
   * Handling look around the room.
   */
  private void handleLook() {
    Room current = player.getCurrentRoom();
    view.renderGame(player, current);
  }

  /**
   * Handling examine command.
   *
   * @param arg the input string
   */
  private void handleExamine(String arg) {
    if (arg == null) {
      view.showMessage("Examine what?");
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

  /**
   * Handling answer the puzzle.
   *
   * @param arg input string
   */
  private void handleAnswer(String arg) {
    if (arg == null) {
      view.showMessage("Answer what?");
      return;
    }

    Room room = player.getCurrentRoom();
    boolean solved = player.answerCorrect(arg, room);
    if (solved) {
      //player.addScore(10); // 示例值
    }
    view.showMessage(solved ? "🎉 Puzzle solved!" : "That didn't work.");
  }

  /**
   * Private method for save the game.
   */
  private void handleSave() {
    view.showMessage("Do you want to save the game? (Y/N)");
    while (true) {
      String answer = scanner.nextLine().trim().toUpperCase();
      switch (answer) {
        case "Y" -> {
          saveGameState();
          return;
        }
        case "N" -> {
          view.showMessage("No save. Let's keep going~ 🌈");
          return;
        }
        default -> view.showMessage("Please enter 'Y' or 'N'.");
      }
    }
  }

  /**
   * Private method for restoring the game.
   */
  private void handleRestore() {
    String savePath = selectSave();
    if (savePath == null) {
      view.showMessage("No valid save file selected.");
      return;
    }

    Player temp = new Player("TEMP", new Room(0, "TEMP", "Placeholder"));
    boolean success = world.restoreState(savePath, temp);
    if (success) {
      this.player = temp;
      view.showMessage("Game restored!");
    } else {
      view.showMessage("Restore failed.");
    }
  }

  /**
   * Help command.
   */
  private void handleHelp() {
    view.showMessage("\nMovement Commands\n---------------------");

    // Movement
    view.showMessage("  - " + NORTH_KEY + " / " + SOUTH_KEY + " / " + EAST_KEY + " / " + WEST_KEY + " : Move North/South/East/West");

    // Item actions
    view.showMessage("\nItem Actions\n---------------------");
    view.showMessage("  - " + TAKE_KEY + " <item>    : Take an item");
    view.showMessage("  - " + DROP_KEY + " <item>    : Drop an item");
    view.showMessage("  - " + USE_KEY + " <item>     : Use an item");

    // Info & puzzle
    view.showMessage("\nPuzzle & Info\n---------------------");
    view.showMessage("  - " + INVENTORY_KEY + "           : Check inventory");
    view.showMessage("  - " + LOOK_KEY + "               : Look around the room");
    view.showMessage("  - " + EXAMINE_KEY + " <name>     : Examine an item or fixture");
    view.showMessage("  - " + ANSWER_KEY + " <answer>    : Answer a puzzle");

    // System
    view.showMessage("\nSystem Commands\n---------------------");
    view.showMessage("  - " + SAVE_KEY + "               : Save your progress");
    view.showMessage("  - " + RESTORE_KEY + "            : Restore a saved game");
    view.showMessage("  - " + QUIT_KEY + "               : Quit the game");
    view.showMessage("  - " + HELP_KEY + "               : Show this help menu again");
  }
}

