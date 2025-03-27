package enginedriver;

import controller.GameController;
import model.core.WorldEngine;
import view.ConsoleView;
import view.View;

import java.io.IOException;
import java.io.InputStreamReader;

/**
 * GameEngineApp is the required entry point for the game engine.
 * It sets up the model, controller, and view.
 */
public class GameEngineApp {
  private final GameController controller;

  /**
   * Constructor that wires up the game engine components.
   */
  public GameEngineApp(Readable source) {
    WorldEngine world = new WorldEngine();
    View view = new ConsoleView();
    this.controller = new GameController(world, view, source);
  }

  /**
   * Start the game (delegates to controller).
   */
  public void start() throws IOException {
    controller.startGame();
  }

  /**
   * Main method — entry point.
   */
  public static void main(String[] args) throws IOException {
    GameEngineApp game = new GameEngineApp(new InputStreamReader(System.in));
    game.start();
  }
}
