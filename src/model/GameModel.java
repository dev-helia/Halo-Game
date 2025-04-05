package model;

import model.core.HealthStatus;
import model.core.Player;
import model.core.PlayerRank;
import model.core.WorldEngine;
import model.elements.Item;
import model.core.Room;

import java.io.IOException;
import java.util.List;

public class GameModel implements IModel {

  private final WorldEngine engine = new WorldEngine();
  private Player player;

  @Override
  public void generateWorld(String jsonFilePath) throws IOException {
    engine.generateWorld(jsonFilePath);
  }
  @Override
  public boolean saveGame(String filePath) {
    return engine.saveState(filePath, player);
  }

  @Override
  public boolean loadGame(String filePath) {
    Player temp = new Player("TEMP", new Room(0, "TEMP", "Temporary"));
    boolean success = engine.restoreState(filePath, temp);
    if (success) this.player = temp;
    return success;
  }

  @Override
  public Player getPlayer() {
    return this.player;
  }

  @Override
  public void initializePlayer(String name) {
    this.player = new Player(name, engine.getRoom(1));
  }

  @Override
  public boolean movePlayer(String direction) {
    return player.move(direction, engine.getWorldMap());
  }

  @Override
  public Room getCurrentRoom() {
    return player.getCurrentRoom();
  }

  @Override
  public boolean pickItem(String name) {
    return player.pickItem(name);
  }

  @Override
  public boolean dropItem(String name) {
    return player.dropItem(name);
  }

  @Override
  public String useItem(String name) {
    return player.useItem(name);
  }

  @Override
  public List<Item> getInventory() {
    return player.getInventory();
  }

  @Override
  public boolean answerPuzzle(String answer) {
    return player.answerCorrect(answer, player.getCurrentRoom());
  }

  @Override
  public HealthStatus getHealthStatus() {
    return player.getHealthStatus();
  }

  @Override
  public double getHealth() {
    return player.getHealth();
  }

  @Override
  public double getScore() {
    return player.getScore();
  }

  @Override
  public PlayerRank getPlayerRank() {
    return player.getRank();
  }

  @Override
  public Player getPlayerReference() {
    return player;
  }

}