package enginedriver;

import controller.GameController;
import model.core.Player;
import model.core.Room;
import model.core.WorldEngine;
import view.ConsoleView;
import view.View;

import java.io.IOException;

/**
 * GameEngineApp 是游戏的入口类，负责初始化模型、控制器和视图
 */
public class GameEngineApp {

  private final GameController controller;
  /**
   * 构造器，作业要求指定：提供数据文件路径、输入流、输出流
   */
  public GameEngineApp(String gameFileName, Readable source, Appendable output) throws IOException {
    // 1. 加载世界（模型）
    WorldEngine world = new WorldEngine();
    world.generateWorld(gameFileName);

    // 2. 创建玩家（默认起点是房间编号为 1）
    Room startingRoom = world.getRoom(1); // 或者你可以定义 getStartingRoom() 方法
    if (startingRoom == null) {
      throw new IllegalArgumentException("起始房间未找到！");
    }

    Player player = new Player("Hero", startingRoom);

    // 3. 构建视图（输出接口）
    View view = new ConsoleView(); // 输出会写到 output 上

    // 4. 创建控制器
    this.controller = new GameController(player, world, view, source);
  }

  /**
   * 启动游戏！
   */
  public void start() throws IOException {
    controller.startGame();
  }

  /**
   * 主函数（用于调试 & Smoke Test）
   */
  public static void main(String[] args) throws IOException {
    // 示例字符串输入流
    String s = "MyHero\nL\nT Ticket\nA 'Align'\nN\nN\nQ";
    java.io.BufferedReader input = new java.io.BufferedReader(new java.io.StringReader(s));

    GameEngineApp game = new GameEngineApp("src/resources/Museum_of_Planet_of_the_Apes.json", input, System.out);
    game.start();

    // 可切换为手动输入版本：
    // GameEngineApp game = new GameEngineApp("./resources/museum.json", new java.io.InputStreamReader(System.in), System.out);
    // game.start();
  }
}
