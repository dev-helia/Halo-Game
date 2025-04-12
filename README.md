## 1. Overview

### 1.1 Description

This project is a modular, extensible adventure game engine supporting both text-based and graphical (Swing) interfaces. Players can navigate through rooms, interact with items and obstacles (monsters and puzzles), and save or restore game state. It follows the MVC pattern with clear separation between model, view, and controller layers.

### 1.2 How to Run

To run the game in different modes:

#### Graphics Mode (GUI)
java -jar out/artifacts/game_engine_jar/halo.jar resources/maps/Align_Quest_Game_Elements.json -graphics
#### Text Mode (Terminal)
java -jar out/artifacts/game_engine_jar/halo.jar resources/maps/Museum_of_Planet_of_the_Apes.json -text

#### Batch Mode (Automated Text Input)

- Output to terminal:
  java -jar out/artifacts/game_engine_jar/halo.jar resources/maps/<mapname>.json -batch resources/input.txt
- Output to file:java -jar out/artifacts/game_engine_jar/halo.jar resources/maps/<mapname>.json -batch resources/input.txt resources/output.txt

> Game image and save files must be stored under `resources/images/` and `resources/saves/`, respectively.

---
## 2. Enhancements

#### 2.1 Refactored Model Abstraction

Introduced `IModel` interface and a new `GameModel` implementation to decouple the controller from `WorldEngine`. This improves modularity, enables mocking, and supports multiple controller types.

#### 2.2 Refactored Controller Logic

Moved shared logic for `use`, `answer`, `examine`, and inventory handling into `AbstractController` to avoid duplication between the text and GUI controllers.

#### 2.3 Save/Restore Logic Fixes

Addressed serialization issues by delegating state saving to `WorldEngine`, passing the `Player` object to persist game state. The restored player is copied using `copyFrom()` to maintain reference.

#### 2.4 Puzzle and Monster Exit Fixes

After solving puzzles or defeating monsters, negative exits now correctly unblock by flipping to positive room IDs. This resolves previous issues where puzzles were solved but movement remained blocked.

#### 2.5 Health Logic

Monster damage and health deduction were re-validated, including proper rendering of health status and detection of game-over condition when the player reaches 0 health.

---

## 3. New Features

#### 3.1 Java Swing GUI

Added a complete graphical interface using Java Swing. This includes room image rendering, action buttons, directional buttons with image icons, popup dialogs, and an inventory pane.

#### 3.2 SwingController and Features Interface

Added `SwingController` that implements a new `Features` interface. This allows the view to remain decoupled from the controller and promotes event-driven interaction.

#### 3.3 Graphical Inventory Interaction

Players can select inventory items from a list and perform actions such as **Use**, **Drop**, or **Inspect** via dedicated buttons.

#### 3.4 Popup Dialog Integration

Room fixtures, items, and obstacles can be examined through graphical popups. Monsters trigger a warning popup with effects and attack messages when encountered.

#### 3.5 MenuBar Integration

Added application-level controls including **About**, **Save**, **Restore**, and **Exit** to the Swing UI via a custom menu bar, per HW9 requirements.

#### 3.6 Dynamic Room Image Rendering

Room images are displayed based on the `"picture"` field in JSON. If the image is missing, a fallback `coming-soon.png` image is shown.

#### 3.7 Batch Mode Support

The engine supports fully automated command input using `-batch` mode. Output is redirected to a specified file or the console. This supports grading and scripting use cases.

#### 3.8 Final Summary Dialogs

On **Quit**, **Save**, or **Game Over** events, the engine displays the final health status and rank summary in a GUI dialog.

#### 3.9 Resource Directory Relocation

Moved the `resources/` folder outside of `src/` to ensure image paths and save directories are relative and accessible regardless of deployment environment.

---

## 4. Design Highlights

#### 4.1 Separation of Concerns

Our controller logic is now cleanly split between GUI and text modes. Shared command logic is factored into `AbstractController`, ensuring minimal code duplication.

#### 4.2 Open/Closed Principle

By defining interfaces such as `IModel`, `Features`, and `View`, the engine is now extensible to support new interaction styles or game logic without major rewrites.

#### 4.3 Testability

With the model abstracted behind `IModel`, we can now easily mock model behavior in controller unit tests without relying on actual game data.

---

## 5. Deployment Notes

#### 5.1 Directory Structure

Ensure the following directory structure when running the JAR:
- /out/artifacts/game_engine_jar/halo.jar /resources/maps/<your-map>.json /resources/images/ /resources/saves/


#### 5.2 Image Resources

All images used in the game must be placed in `resources/images/`. The image filename must match the `"picture"` field in the JSON map. Missing images default to `coming-soon.png`.

#### 5.3 Save Files

Save files will be stored in `resources/saves/`, which must be created if not already present.

---

## 6. Shout-out
Special thanks to our project TA **Kiersten Grieco** for clear guidance and constructive feedback. Also special thanks to TA **Feng Hua Tan** for the assistance on the batch processing explanation.
