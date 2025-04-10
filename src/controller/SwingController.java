package controller;

import model.IModel;
import model.core.Room;
import model.elements.Fixture;
import model.elements.Item;
import model.obstacle.GameObstacle;
import model.obstacle.Monster;
import view.Features;
import view.SwingView;

import javax.swing.*;
import java.util.List;

/**
 * SwingController implements Features for handling GUI interactions.
 */
public class SwingController extends AbstractController implements Features {

  private final IModel model;
  private final SwingView view;

  /**
   * Constructs a swing controller
   * @param model the game model.
   * @param view the game view.
   */
  public SwingController(IModel model, SwingView view) {
    super(model);
    this.model = model;
    this.view = view;
    this.view.addFeatures(this);
    updateUI();
  }

  /**
   * Updates the view with current room, inventory, and status.
   */
  private void updateUI() {
    Room room = model.getCurrentRoom();
    view.renderRoom(room);
    List<Item> items = model.getInventory();
    String[] names = items.stream().map(Item::getName).toArray(String[]::new);
    view.updateInventory(names);
    view.updateStatusBar(
            model.getHealthStatus().toString(),
            (int) model.getHealth(),
            model.getPlayerRank().toString(),
            (int) model.getScore()
    );
  }

  /**
   * Returns the current room for rendering.
   *
   * @return the current Room
   */
  // This method is needed by SwingView to fetch the current room
  public Room getCurrentRoom() {
    return model.getCurrentRoom();
  }

  /**
   * Moves the player north.
   */
  @Override public void moveNorth() { move("N"); }

  /**
   * Moves the player south.
   */
  @Override public void moveSouth() { move("S"); }

  /**
   * Moves the player east.
   */
  @Override public void moveEast()  { move("E"); }

  /**
   * Moves the player west.
   */
  @Override public void moveWest()  { move("W"); }

  /**
   * Moves the player in a specified direction and handles encounters.
   *
   * @param dir the direction (N, S, E, W)
   */
  @Override
  public void move(String dir) {
    boolean moved = model.movePlayer(dir);
    if (moved) {
      Room current = model.getCurrentRoom();
      GameObstacle obs = current.getObstacle();

      if (obs instanceof Monster m && m.isActive() && m.canAttack()) {
        String fullMsg = m.getEffects() + "\n\n" + m.getAttackMessage();
        JOptionPane.showMessageDialog(null, fullMsg, m.getName(), JOptionPane.WARNING_MESSAGE);
        m.attack(model.getPlayer());
        view.showMessage(m.getAttackMessage());
        view.showMessage("Player takes -" + m.getDamage() + " damage.");

        if (model.getHealth() <= 0) {
          view.showMessage("You have fallen asleep. Game Over.");
          view.showGameOver(model.getHealthStatus().toString(), model.getPlayerRank().toString());
          return;
        }
      }

      view.showMessage("You move " + getDirectionName(dir) + ".");
      updateUI();
    } else {
      view.showMessage("You can't move that way.");
    }
  }

  /**
   * Returns the full name of a direction.
   *
   * @param dir short direction
   * @return full name
   */
  private String getDirectionName(String dir) {
    return switch (dir) {
      case "N" -> "North";
      case "S" -> "South";
      case "E" -> "East";
      case "W" -> "West";
      default -> dir;
    };
  }

  /**
   * Handles taking an item from the room.
   *
   * @param itemName the item name
   */
  @Override public void takeItem(String itemName) {
    boolean success = model.pickItem(itemName);
    view.showMessage(success ? "You picked up: " + itemName : "You can't take that.");
    updateUI();
  }

  /**
   * Handles dropping an item from inventory.
   *
   * @param itemName the item name
   */
  @Override public void dropItem(String itemName) {
    boolean success = model.dropItem(itemName);
    view.showMessage(success ? "You dropped: " + itemName : "You don't have that item.");
    updateUI();
  }

  /**
   * Uses an item, potentially solving puzzles or defeating monsters.
   *
   * @param itemName the item name
   */
  @Override public void useItem(String itemName) {
    String result = handleUse(itemName);
    JOptionPane.showMessageDialog(null, result, "" +
            "" +
            "" +
            " Item", JOptionPane.INFORMATION_MESSAGE);
    updateUI();
  }

  /**
   * Examines an item, fixture, or obstacle by name.
   *
   * @param name name of the object to examine
   */
  @Override public void examine(String name) {
    Room room = model.getCurrentRoom();

    for (Item i : model.getInventory()) {
      if (i.getName().equalsIgnoreCase(name)) {
        JOptionPane.showMessageDialog(view, i.getDescription(),
                "Examining: " + i.getName(), JOptionPane.INFORMATION_MESSAGE);
        return;
      }
    }

    Item item = room.getItem(name);
    if (item != null) {
      JOptionPane.showMessageDialog(view, item.getDescription(),
              "Examining: " + item.getName(), JOptionPane.INFORMATION_MESSAGE);
      return;
    }

    Fixture fixture = room.getFixture(name);
    if (fixture != null) {
      view.showPopupWithImage(fixture.getName());
      JOptionPane.showMessageDialog(view, fixture.getDescription(),
              "Examining: " + fixture.getName(), JOptionPane.INFORMATION_MESSAGE);
      return;
    }

    GameObstacle obs = room.getObstacle();
    if (obs != null && obs.getName().equalsIgnoreCase(name)) {
      JOptionPane.showMessageDialog(view,
              obs.getCurrentDescription(),
              "Examining: " + obs.getName(),
              JOptionPane.INFORMATION_MESSAGE);
      return;
    }

    view.showMessage("You see nothing interesting about that.");
  }

  /**
   * Submits an answer for a puzzle.
   *
   * @param answer user's answer
   */
  @Override public void answer(String answer) {
    boolean correct = model.answerPuzzle(answer);
    String result = correct ? "Puzzle solved!" : "That didn't work.";
    JOptionPane.showMessageDialog(null, result, "Answer Result", JOptionPane.INFORMATION_MESSAGE);
    updateUI();
  }

  /**
   * Re-renders the current room.
   */
  @Override public void look() {
    view.renderRoom(model.getCurrentRoom());
  }

  /**
   * Displays the player’s current inventory.
   */
  @Override public void showInventory() {
    String[] items = model.getInventory().stream().map(Item::getName).toArray(String[]::new);
    view.showInventory(items);
  }

  /**
   * Prompts for a save file name and saves game state.
   */
  @Override public void saveGame() {
    String name = view.promptForSaveFile();
    if (name != null && !name.isBlank()) {
      boolean success = model.saveGame("resources/saves/" + name + ".json");
      view.showMessage(success ? "Game saved." : "Save failed.");
      showPlayerSummary("Game Saved");
    }
  }

  /**
   * Prompts for a saved file and loads game state.
   */
  @Override public void restoreGame() {
    String filePath = view.promptForRestoreFile();
    if (filePath != null) {
      boolean ok = model.loadGame(filePath);
      if (ok) {
        view.showMessage("Game restored.");
        updateUI();
        showPlayerSummary("Game Restored");
      } else {
        view.showMessage("Restore failed.");
      }
    }
  }

  /**
   * Quits the game with a confirmation and shows summary.
   */
  @Override public void quitGame() {
    if (view.promptYesNo("Quit the game?")) {
      String health = model.getHealthStatus().toString();
      String rank = model.getPlayerRank().toString();
      String summary = String.format("Thanks for playing!\nFinal Health: %s\nFinal Rank: %s", health, rank);
      JOptionPane.showMessageDialog(null, summary, "Game Summary", JOptionPane.INFORMATION_MESSAGE);
      System.exit(0);
    }
  }

  /**
   * Shows a summary of the player's current status.
   *
   * @param title dialog title
   */
  private void showPlayerSummary(String title) {
    String health = model.getHealthStatus().toString();
    String rank = model.getPlayerRank().toString();
    String summary = String.format("Current Health: %s\nCurrent Rank: %s", health, rank);
    JOptionPane.showMessageDialog(null, summary, title, JOptionPane.INFORMATION_MESSAGE);
  }

  /**
   * Displays the About screen.
   */
  @Override public void showAbout() {
    view.showAbout();
  }
}
