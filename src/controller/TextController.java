package controller;

import model.IModel;
import model.core.Room;
import model.obstacle.GameObstacle;
import model.obstacle.Monster;
import view.View;
import utils.fileutil.PathUtils;

import java.io.IOException;
import java.util.Scanner;

/**
 * Handles text-based gameplay using the shared model and view interfaces.
 */
public class TextController extends AbstractController {
  private final View view;
  private final Scanner scanner;

  private static final String SAVE_KEY = "SAVE";
  private static final String QUIT_KEY = "Q";

  /**
   * Constructs a GameController for text-based gameplay.
   *
   * @param model the game model
   * @param view the view interface
   * @param inputSource the input stream for reading commands
   */
  public TextController(IModel model, View view, Readable inputSource) {
    super(model);
    this.view = view;
    this.scanner = new Scanner(inputSource);
  }

  /**
   * Starts the main game loop after player has been initialized.
   */
  public void startGame() throws IOException {
    gameLoop();
  }

  /**
   * Main gameplay loop, handles commands and updates game state.
   */
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

  /**
   * Moves the player and re-renders the room if successful.
   */
  private void move(String dir) {
    boolean moved = model.movePlayer(dir);
    view.showMessage(moved ? "You move " + directionFull(dir) : "You can't move that way.");
    if (moved) view.renderGame(model.getPlayer(), model.getCurrentRoom());
  }

  /**
   * Returns full direction name from shorthand.
   */
  private String directionFull(String dir) {
    return switch (dir) {
      case "N" -> "North";
      case "S" -> "South";
      case "E" -> "East";
      case "W" -> "West";
      default -> dir;
    };
  }

  /**
   * Handles item pickup.
   */
  private void takeItem(String itemName) {
    if (itemName == null) {
      view.showMessage("Take what?");
      return;
    }
    boolean success = model.pickItem(itemName);
    view.showMessage(success ? "You took: " + itemName : "Item not found or too heavy.");
  }

  /**
   * Handles item drop.
   */
  private void dropItem(String itemName) {
    if (itemName == null) {
      view.showMessage("Drop what?");
      return;
    }
    boolean success = model.dropItem(itemName);
    view.showMessage(success ? "You dropped: " + itemName : "You don't have that.");
  }

  /**
   * Handles item usage.
   */
  private void useItem(String itemName) {
    view.showMessage(handleUse(itemName));
  }

  /**
   * Prompts for save file name and saves game.
   */
  private void saveGame() {
    view.showMessage("Enter filename (without .json):");
    String fileName = scanner.nextLine().trim();
    String fullPath = PathUtils.getSavePath(fileName);
    boolean saved = model.saveGame(fullPath);
    view.showMessage(saved ? "Saved to: " + fileName : "Save failed.");
  }
}
