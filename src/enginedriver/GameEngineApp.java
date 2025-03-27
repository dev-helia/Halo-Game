package enginedriver;

import controller.GameController;
import model.core.Player;
import model.core.Room;
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
   *
   * @param gameFileName JSON file path containing the game world data.
   * @param source       Input source (BufferedReader/StringReader/System.in).
   * @param output       Output destination (e.g., System.out or file).
   * @throws IOException if loading world fails
   */
  public GameEngineApp(String gameFileName, Readable source, Appendable output) throws IOException {
    // Load the world data from the JSON file
    WorldEngine world = new WorldEngine();
    world.generateWorld(gameFileName);

    Room startingRoom = world.getRoom(1);
    if (startingRoom == null) {
      throw new IllegalArgumentException("Starting room cannot be found！");
    }

    Player player = new Player("Hero", startingRoom);

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

    //String s = "MyHero\nL\nT Ticket\nA 'Align'\nN\nN\nQ";
    //java.io.BufferedReader input = new java.io.BufferedReader(new java.io.StringReader(s));

    GameEngineApp game = new GameEngineApp(
            "src/resources/Museum_of_Planet_of_the_Apes.json",
            new InputStreamReader(System.in),  // ← manually typed input
            System.out);
            game.start();
  }
}
