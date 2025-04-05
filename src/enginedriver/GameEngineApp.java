package enginedriver;

import controller.GameController;
import model.GameModel; // ✅ 使用 GameModel 替换 WorldEngine
import model.IModel;
import view.ConsoleView;
import view.View;

import java.io.IOException;
import java.io.InputStreamReader;

/**
 * GameEngineApp is the required entry point for the game engine.
 * It sets up the model (GameModel), controller, and view.
 * It supports both interactive and automated (smoke test) input sources.
 */
public class GameEngineApp {
  private final GameController controller;

  /**
   * Constructor required by assignment spec.
   * @param source       Input source (BufferedReader/StringReader/System.in).
   * @throws IOException if loading world fails
   */
  public GameEngineApp(Readable source) throws IOException {
    //改为使用封装后的 GameModel
    IModel model = new GameModel();

    // 创建视图
    View view = new ConsoleView();

    // 创建控制器，传入封装好的 model
    this.controller = new GameController(model, view, source);
  }

  /**
   * Starts the game session by invoking the controller.
   *
   * @throws IOException if game start fails
   */
  public void start() throws IOException {
    controller.startGame();
  }

  /**
   * Main method to perform a smoke test or enable manual gameplay.
   *
   * @param args Command-line arguments (not used)
   * @throws IOException if setup fails
   */
  public static void main(String[] args) throws IOException {
    GameEngineApp game = new GameEngineApp(new InputStreamReader(System.in));
    game.start();
  }
}
