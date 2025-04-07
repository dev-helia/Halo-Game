package view;

import controller.SwingController;
import model.core.Room;
import model.elements.Fixture;
import model.elements.Item;
import model.obstacle.GameObstacle;
import model.obstacle.Monster;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.List;

public class SwingView extends JFrame {
  private final JLabel imageLabel;
  private final JTextArea descriptionArea;
  private final JLabel healthLabel;
  private final DefaultListModel<String> inventoryModel;
  private final JList<String> inventoryList;

  private final JButton northButton;
  private final JButton southButton;
  private final JButton eastButton;
  private final JButton westButton;

  private final JButton takeButton;
  private final JButton examineButton;
  private final JButton answerButton;
  private final JButton inspectButton;
  private final JButton useButton;
  private final JButton dropButton;

  private JMenuItem aboutMenu;
  private JMenuItem saveItem;
  private JMenuItem restoreItem;
  private JMenuItem exitItem;

  public SwingView() {
    super("Adventure Game - GUI Mode");
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setSize(1000, 700);
    setLayout(new BorderLayout());

    imageLabel = new JLabel();
    imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
    imageLabel.setPreferredSize(new Dimension(300, 300));
    JPanel imagePanel = new JPanel(new BorderLayout());
    imagePanel.setBorder(BorderFactory.createTitledBorder("View"));
    imagePanel.add(imageLabel, BorderLayout.CENTER);

    descriptionArea = new JTextArea(6, 10);
    descriptionArea.setEditable(false);
    descriptionArea.setLineWrap(true);
    descriptionArea.setWrapStyleWord(true);
    JScrollPane descScroll = new JScrollPane(descriptionArea);
    JPanel descPanel = new JPanel(new BorderLayout());
    descPanel.setBorder(BorderFactory.createTitledBorder("Description"));
    descPanel.add(descScroll, BorderLayout.CENTER);

    JPanel leftPanel = new JPanel(new BorderLayout());
    leftPanel.add(imagePanel, BorderLayout.NORTH);
    leftPanel.add(descPanel, BorderLayout.CENTER);

    JPanel directionPanel = new JPanel(new GridLayout(3, 3, 10, 10));
    directionPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    directionPanel.add(new JLabel());
    northButton = createImageButton("resources/images/north.png");
    directionPanel.add(northButton);
    directionPanel.add(new JLabel());

    westButton = createImageButton("resources/images/west.png");
    directionPanel.add(westButton);
    directionPanel.add(new JLabel());
    eastButton = createImageButton("resources/images/east.png");
    directionPanel.add(eastButton);

    directionPanel.add(new JLabel());
    southButton = createImageButton("resources/images/south.png");
    directionPanel.add(southButton);
    directionPanel.add(new JLabel());

    JPanel titledNav = new JPanel(new BorderLayout());
    titledNav.setBorder(BorderFactory.createTitledBorder("Navigation"));
    titledNav.add(directionPanel, BorderLayout.CENTER);

    JPanel actionPanel = new JPanel(new FlowLayout());
    takeButton = new JButton("Take");
    examineButton = new JButton("Examine");
    answerButton = new JButton("Answer");
    actionPanel.add(takeButton);
    actionPanel.add(examineButton);
    actionPanel.add(answerButton);

    inventoryModel = new DefaultListModel<>();
    inventoryList = new JList<>(inventoryModel);
    JPanel inventoryActionPanel = new JPanel(new FlowLayout());
    inspectButton = new JButton("Inspect");
    useButton = new JButton("Use");
    dropButton = new JButton("Drop");
    inventoryActionPanel.add(inspectButton);
    inventoryActionPanel.add(useButton);
    inventoryActionPanel.add(dropButton);

    JPanel inventoryPanel = new JPanel(new BorderLayout());
    inventoryPanel.setBorder(BorderFactory.createTitledBorder("Inventory"));
    inventoryPanel.add(new JScrollPane(inventoryList), BorderLayout.CENTER);
    inventoryPanel.add(inventoryActionPanel, BorderLayout.SOUTH);

    JPanel rightPanel = new JPanel();
    rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
    rightPanel.add(titledNav);
    rightPanel.add(actionPanel);
    rightPanel.add(inventoryPanel);

    JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
    healthLabel = new JLabel("Health: OK");
    statusPanel.add(healthLabel);

    JMenuBar menuBar = new JMenuBar();
    JMenu fileMenu = new JMenu("File");
    aboutMenu = new JMenuItem("About the Engine");
    saveItem = new JMenuItem("Save Game");
    restoreItem = new JMenuItem("Restore Game");
    exitItem = new JMenuItem("Exit");

    fileMenu.add(aboutMenu);
    fileMenu.add(saveItem);
    fileMenu.add(restoreItem);
    fileMenu.addSeparator();
    fileMenu.add(exitItem);
    menuBar.add(fileMenu);
    setJMenuBar(menuBar);

    add(leftPanel, BorderLayout.WEST);
    add(rightPanel, BorderLayout.CENTER);
    add(statusPanel, BorderLayout.SOUTH);

    setVisible(true);
  }

  private JButton createImageButton(String path) {
    JButton button = new JButton(new ImageIcon(path));
    button.setPreferredSize(new Dimension(60, 60));
    button.setBorder(BorderFactory.createEmptyBorder());
    return button;
  }

  public void showMessage(String msg) {
    descriptionArea.append(msg + "\n");
  }

  public void renderRoom(Room room) {
    descriptionArea.setText("");
    showMessage(room.getRoomDescription());

    if (room.getItems() != null && !room.getItems().isEmpty()) {
      showMessage("Items you see here:");
      for (Item item : room.getItems()) {
        showMessage("  - " + item.getName() + ": " + item.getDescription());
      }
    }

    if (room.getFixtures() != null && !room.getFixtures().isEmpty()) {
      showMessage("Fixtures:");
      for (Fixture fixture : room.getFixtures()) {
        showMessage("  - " + fixture.getName());
      }
    }

    if (room.getObstacle() != null) {
      GameObstacle obs = room.getObstacle();
      String type = (obs instanceof Monster) ? "Monster" : "Puzzle";
      showMessage(type + ": " + obs.getDescription());
    }

    String imageName = room.getPicture();
    ImageIcon icon = null;

    if (imageName != null) {
      File imageFile = new File("resources/images/" + imageName);
      if (imageFile.exists()) {
        icon = new ImageIcon(imageFile.getPath());
      }
    }

    if (icon == null) {
      File fallback = new File("resources/images/coming-soon.png");
      if (fallback.exists()) {
        icon = new ImageIcon(fallback.getPath());
      }
    }

    imageLabel.setIcon(icon);
  }

  public void updateStatusBar(String healthStatus, int healthPoints, String rank, int score) {
    healthLabel.setText(String.format("Health: %s (%d)   |   Rank: %s (%d pts)", healthStatus, healthPoints, rank, score));
  }

  public void updateInventory(String[] items) {
    inventoryModel.clear();
    for (String item : items) {
      inventoryModel.addElement(item);
    }
  }

  public void showInventory(String[] items) {
    updateInventory(items);
  }

  public String getInventoryString() {
    return inventoryList.getSelectedValue();
  }

  public void showGameOver(String health, String rank) {
    JOptionPane.showMessageDialog(this,
            "Game Over.\nHealth Status: " + health + "\nRank: " + rank,
            "Game Over",
            JOptionPane.INFORMATION_MESSAGE);
  }

  public String promptForSaveFile() {
    return JOptionPane.showInputDialog(this, "Enter a name to save your game:");
  }

  public String promptForRestoreFile() {
    JFileChooser chooser = new JFileChooser("resources/saves/");
    int result = chooser.showOpenDialog(this);
    if (result == JFileChooser.APPROVE_OPTION) {
      File selected = chooser.getSelectedFile();
      return selected.getAbsolutePath();
    }
    return null;
  }

  public boolean promptYesNo(String msg) {
    int choice = JOptionPane.showConfirmDialog(this, msg, "Confirm", JOptionPane.YES_NO_OPTION);
    return choice == JOptionPane.YES_OPTION;
  }

  public void showAbout() {
    JOptionPane.showMessageDialog(this,
            "Adventure Game Engine\nBuilt with Java Swing\nCS 5004 Team Project",
            "About This Game",
            JOptionPane.INFORMATION_MESSAGE);
  }

  public void showPopupWithImage(String name) {
    String cleaned = name.trim().toLowerCase().replaceAll("\\s+", "-");
    String path = "resources/images/" + cleaned + ".png";
    ImageIcon icon = new ImageIcon(path);
    JLabel label = new JLabel(icon);
    JOptionPane.showMessageDialog(this, label, name, JOptionPane.PLAIN_MESSAGE);
  }

  public void addFeatures(Features f) {
    northButton.addActionListener(e -> f.moveNorth());
    southButton.addActionListener(e -> f.moveSouth());
    eastButton.addActionListener(e -> f.moveEast());
    westButton.addActionListener(e -> f.moveWest());

    takeButton.addActionListener(e -> {
      if (f instanceof SwingController sc) {
        Room room = sc.getCurrentRoom();
        List<Item> items = room.getItems();
        if (items != null && !items.isEmpty()) {
          String[] options = items.stream().map(Item::getName).toArray(String[]::new);
          String selected = (String) JOptionPane.showInputDialog(
                  this,
                  "Take which item?",
                  "Take Item",
                  JOptionPane.PLAIN_MESSAGE,
                  null,
                  options,
                  options[0]
          );
          if (selected != null) {
            f.takeItem(selected);
          }
        } else {
          showMessage("There is nothing to take.");
        }
      }
    });

    examineButton.addActionListener(e -> {
      if (f instanceof SwingController sc) {
        Room room = sc.getCurrentRoom();

        List<String> examineOptions = new java.util.ArrayList<>();

        if (room.getItems() != null) {
          for (Item item : room.getItems()) {
            examineOptions.add(item.getName());
          }
        }

        if (room.getFixtures() != null) {
          for (Fixture fixture : room.getFixtures()) {
            examineOptions.add(fixture.getName());
          }
        }

        if (room.getObstacle() != null && room.getObstacle().isActive()) {
          examineOptions.add(room.getObstacle().getName());
        }

        if (examineOptions.isEmpty()) {
          showMessage("There is nothing to examine.");
        } else {
          String[] options = examineOptions.toArray(new String[0]);
          String selected = (String) JOptionPane.showInputDialog(
                  this,
                  "Examine what?",
                  "Examine",
                  JOptionPane.PLAIN_MESSAGE,
                  null,
                  options,
                  options[0]
          );
          if (selected != null) {
            f.examine(selected);
          }
        }
      }
    });

    inspectButton.addActionListener(e -> {
      String name = getInventoryString();
      if (name != null) f.examine(name);
    });

    useButton.addActionListener(e -> {
      String item = getInventoryString();
      if (item != null) {
        f.useItem(item);
      } else {
        showMessage("Select an item to use from inventory.");
      }
    });

    dropButton.addActionListener(e -> {
      String item = getInventoryString();
      if (item != null) f.dropItem(item);
    });

    answerButton.addActionListener(e -> {
      String answer = JOptionPane.showInputDialog(this, "Enter answer:");
      if (answer != null && !answer.isBlank()) {
        f.answer(answer);
      }
    });

    aboutMenu.addActionListener(e -> f.showAbout());
    saveItem.addActionListener(e -> f.saveGame());
    restoreItem.addActionListener(e -> f.restoreGame());
    exitItem.addActionListener(e -> f.quitGame());
  }

  public JList<String> getInventoryList() {
    return inventoryList;
  }
}
