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
 * 控制器类，负责接收用户输入，调用模型逻辑，并通过视图展示结果
 */
public class GameController {
  private final Player player;
  private final WorldEngine world;
  private final View view;
  private final Scanner scanner;

  public GameController(Player player, WorldEngine world, View view, Readable inputSource) {
    this.player = player;
    this.world = world;
    this.view = view;
    this.scanner = new Scanner(inputSource);
  }

  /**
   * 游戏主循环
   */
  public void startGame() throws IOException {
    view.displayMainMenu();

    while (true) {
      Room currentRoom = player.getCurrentRoom();
      view.showMessage("Health Status: " + player.getHealthStatus());
      view.renderGame(player, currentRoom);

      // 如果房间里有怪物 & 还活跃 → 自动攻击
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

      // 获取玩家输入
      view.showMessage("\nEnter command: ");
      if (!scanner.hasNextLine()) break;
      String line = scanner.nextLine().trim();
      if (line.equalsIgnoreCase("Q")) {
        world.saveState("savegame.json", player);
        view.showMessage("Game saved before quitting.");
        break;
      }

      // 解析命令
      String[] parts = line.split(" ", 2);
      String cmd = parts[0].toUpperCase();
      String arg = parts.length > 1 ? parts[1] : null;

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
          if (item != null) {
            view.showMessage(item.getDescription());
          } else {
            view.showMessage("You see nothing interesting about that.");
          }

          Fixture fix = currentRoom.getFixture(arg);
          if (fix != null) {
            view.showMessage(fix.getDescription());
          } else {
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

    view.showMessage("Thanks for playing! Goodbye~");
    view.showMessage("Final Score: " + player.getScore());
    view.showMessage("Your Rank: " + player.getRank());
  }
}

