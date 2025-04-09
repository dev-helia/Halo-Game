package controller;

import model.IModel;
import model.core.Player;
import model.core.Room;
import model.obstacle.GameObstacle;
import model.obstacle.Monster;
import view.View;
import utils.fileutil.PathUtils;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

/**
 * The GameController handles console-based gameplay using text input.
 * It reuses shared game logic from AbstractController for consistency with GUI controller.
 */
public class GameController extends AbstractController {
  private IModel model;
  private View view;
  private Scanner scanner;

  private static final String NEW_KEY = "NEW";
  private static final String RESTORE_KEY = "RESTORE";
  private static final String SAVE_KEY = "SAVE";
  private static final String QUIT_KEY = "Q";

  public GameController(IModel model, View view, Readable inputSource) {
    super(model);
    this.model = model; // 确保 model 是 IModel 类型
    this.view = view;
    this.scanner = new Scanner(inputSource);
  }

  public Player getPlayer() {
    return model.getPlayer();
  }

  public void startGame() throws IOException {
    view.displayMainMenu();
    view.showMessage("Enter NEW to start or RESTORE to load:");

    while (true) {
      String input = scanner.nextLine().trim().toUpperCase();

      if (input.equals(NEW_KEY)) {
        String mapPath = selectJson("maps", "Choose a map:");
        if (mapPath == null) return;
        model.generateWorld(mapPath);
        view.showMessage("Map loaded. Enter player name:");
        String name = scanner.nextLine().trim();
        model.initializePlayer(name);
        break;

      } else if (input.equals(RESTORE_KEY)) {
        String savePath = selectJson("saves", "Choose a save file:");
        if (savePath == null) return;
        boolean success = model.loadGame(savePath);
        view.showMessage(success ? "Game restored." : "Restore failed.");
        if (success) break;
      } else {
        view.showMessage("Invalid input. Try NEW or RESTORE.");
      }
    }

    gameLoop();
  }

  private void gameLoop() {
    while (true) {
      Room current = model.getCurrentRoom();
      view.renderGame(model.getPlayer(), current);
      view.showMessage("Health: " + model.getHealthStatus());

      if (!model.getPlayer().isAlive()) {
        view.showMessage("You have fallen. Game Over.");
        break;
      }

      GameObstacle obs = current.getObstacle();
      if (obs instanceof Monster m && m.isActive() && m.canAttack()) {
        m.attack(model.getPlayer());
        view.showMessage(m.getAttackMessage());
        view.showMessage("You take -" + m.getDamage() + " damage.");
      }

      view.showMessage("Enter command (N/S/E/W, T, D, U, I, X, A, LOOK, SAVE, Q):");
      if (!scanner.hasNextLine()) break;

      String[] parts = scanner.nextLine().trim().split(" ", 2);
      String cmd = parts[0].toUpperCase();
      String arg = parts.length > 1 ? parts[1] : null;

      switch (cmd) {
        case "N", "S", "E", "W" -> move(cmd);
        case "T" -> takeItem(arg);
        case "D" -> dropItem(arg);
        case "U" -> useItem(arg);
        case "I" -> view.showMessage(showInventoryString());
        case "X" -> view.showMessage(handleExamine(arg));
        case "A" -> view.showMessage(handleAnswer(arg));
        case "LOOK" -> view.renderGame(model.getPlayer(), model.getCurrentRoom());
        case SAVE_KEY -> saveGame();
        case QUIT_KEY -> {
          view.showMessage("Quitting. Thanks for playing!");
          return;
        }
        default -> view.showMessage("Unknown command: " + cmd);
      }
    }
  }

  private void move(String dir) {
    boolean moved = model.movePlayer(dir);
    view.showMessage(moved ? "You move " + directionFull(dir) : "You can't move that way.");
    if (moved) view.renderGame(model.getPlayer(), model.getCurrentRoom());
  }

  private String directionFull(String dir) {
    return switch (dir) {
      case "N" -> "North";
      case "S" -> "South";
      case "E" -> "East";
      case "W" -> "West";
      default -> dir;
    };
  }

  private void takeItem(String itemName) {
    if (itemName == null) {
      view.showMessage("Take what?");
      return;
    }
    boolean success = model.pickItem(itemName);
    view.showMessage(success ? "You took: " + itemName : "Item not found or too heavy.");
  }

  private void dropItem(String itemName) {
    if (itemName == null) {
      view.showMessage("Drop what?");
      return;
    }
    boolean success = model.dropItem(itemName);
    view.showMessage(success ? "You dropped: " + itemName : "You don't have that.");
  }

  private void useItem(String itemName) {
    view.showMessage(handleUse(itemName));
  }

  private void saveGame() {
    view.showMessage("Enter filename (without .json):");
    String fileName = scanner.nextLine().trim();
    String fullPath = PathUtils.getSavePath(fileName);
    boolean saved = model.saveGame(fullPath);
    view.showMessage(saved ? "Saved to: " + fileName : "Save failed.");
  }

  private String selectJson(String type, String prompt) {
    File folder = new File("resources/" + type);
    File[] files = folder.listFiles((dir, name) -> name.endsWith(".json"));

    if (files == null || files.length == 0) {
      view.showMessage("No files found in resources/" + type);
      return null;
    }

    view.showMessage(prompt);
    for (int i = 0; i < files.length; i++) {
      view.showMessage((i + 1) + ". " + files[i].getName());
    }

    while (true) {
      try {
        int choice = Integer.parseInt(scanner.nextLine().trim());
        if (choice >= 1 && choice <= files.length) {
          return files[choice - 1].getPath();
        }
      } catch (NumberFormatException ignored) {}
      view.showMessage("Invalid input. Enter a number between 1 and " + files.length);
    }
  }
}
