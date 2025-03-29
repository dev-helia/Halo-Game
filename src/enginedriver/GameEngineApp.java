package enginedriver;

import controller.GameController;
import model.core.WorldEngine;
import view.ConsoleView;
import view.View;

import java.io.IOException;
import java.io.InputStreamReader;

/**
 * GameEngineApp is the required entry point for the game engine.
 * It sets up the model (WorldEngine), controller, and view.
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
    // Load the world data from the JSON file
    WorldEngine world = new WorldEngine();
    // Create the view
    View view = new ConsoleView(); // 输出会写到 output 上
    // Create the controller
    this.controller = new GameController(world, view, source); // remove Player param
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
