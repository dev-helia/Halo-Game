package view;

import model.core.Player;
import model.core.Room;
import model.elements.Item;
import model.obstacle.GameObstacle;

import java.util.List;

/**
 * 控制台视图实现类，负责将游戏状态打印到终端
 */
public class ConsoleView implements View {

  @Override
  public void displayMainMenu() {
    System.out.println("===================================");
    System.out.println("🎮 Welcome to AlignQuest Adventure!");
    System.out.println("Commands: N, S, E, W, T item, D item, U item, X target, A answer, I, L, Q");
    System.out.println("===================================");
  }

  @Override
  public void renderGame(Player player, Room room) {
    System.out.println("\n== You are in: " + room.getName() + " ==");

    // 显示障碍 or 正常描述
    GameObstacle obs = room.getObstacle();
    if (obs != null && obs.isActive()) {
      System.out.println("⚠️ Obstacle: " + obs.getDescription());
    }

    // 展示房间物品
    List<Item> items = room.getItems();
    if (!items.isEmpty()) {
      System.out.print("🧸 Items here: ");
      for (Item i : items) {
        System.out.print(i.getName() + " ");
      }
      System.out.println();
    }

    // 展示玩家状态
    System.out.println("💖 Health: " + player.getHealth());
    System.out.println("🎖 Score: " + player.getScore());
  }

  @Override
  public void showMessage(String message) {
    System.out.println(message);
  }
}
