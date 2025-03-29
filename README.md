# Game Engine Design Documentation

## **1.High-Level Design Overview**

Our team’s final implementation adopts a modular design inspired by the Model-View-Controller (MVC) pattern. The project is divided into six main packages:

• **controller:** Contains the GameController, which manages user commands, updates the game state, and interacts with the view. 
   
• **enginedriver:** Hosts the entry point GameEngineApp, responsible for initializing the game world from a JSON file, accepting input sources, and starting the game loop. 
   
• **model:** This package is further divided into three subpackages:
   
• **core:** Core gameplay classes like Player, Room, WorldEngine, and enums (HealthStatus, PlayerRank). 
   
• **elements:** In-game entities such as Item, Fixture, and a shared abstract class GameElements. 
   
• **obstacle:** Logic for Puzzle and Monster, both extending the abstract GameObstacle.
   
•  **view:** Provides a View interface and its CLI implementation ConsoleView, separating UI concerns from game logic.
   
•  **utils:** Contains JSON parsers and helper classes to dynamically load game content (ItemParser, RoomParser, PuzzleParser, etc.).
   
•  **resources:** Includes external assets like .json game maps and images.
   This structure improves cohesion within each package and keeps responsibilities well-separated across components.

## **2.Evolution from Homework 7**
   
### ****2.1** Modifications**

Compared to our HW7 design, which was more tightly coupled and manually coded, this version introduces major architectural refinements:
   
•	Game world creation is no longer hardcoded. Instead, we parse external .json files, allowing easy updates and expansions.
   
•	Parsing utilities now handle elements like rooms, items, fixtures, puzzles, and monsters, making the game data-driven and easier to test.
   
•	Core game logic (e.g., player health, movement, scoring) has been consolidated and separated from I/O operations.
   
•	A controller layer (GameController) was added to manage all user commands in one place, improving readability and maintainability.
   
•	We replaced print statements with a standardized View interface, making it easier to plug in future UI implementations.
   
### **2.2 Updated Scenarios from HW7**

   **Scenario 1:** Player Encounters a Monster (Player, Room, Monster, Inventory, Fixture)
   Player Jess enters the Foyer and is confronted by a monster: the Teddy Bear, which blocks the eastward path to the Spooky Library. The game displays a warning that a monster growls at Jess and blocks the way. The Teddy Bear attacks automatically, slapping Jess and dealing 25 points of damage. Jess's health decreases from 100 to 75, and the game updates his health status to FATIGUED.
   Jess attempts to move east by entering the command E, but the game blocks the path with a message stating the direction is inaccessible. This is because the monster is still active and the room’s exit value is set as a negative number.
   Jess checks his inventory using the **I** command and sees that he has a Watering Can. He tries to use it by entering U Watering Can, but the system responds that it didn’t seem to work.
   Looking around the room using L, Jess notices a fixture called Bookshelf. When he attempts to pick it up with T Bookshelf, the game informs him that fixtures cannot be taken.
   After further exploration, Jess finds an item called Slippers, picks it up with T Slippers, and then uses it by entering U Slippers. The system checks and recognizes that Slippers is the correct item to defeat the Teddy Bear. The monster is deactivated and removed from the room. Internally, the blocked eastward path is updated from -4 to 4, allowing Jess to move east. A message is displayed confirming that the Teddy Bear has been defeated. Jess is now free to enter the Spooky Library by moving east.
   
**Scenario 2:** Player Solves a Puzzle (Player, Room, Puzzle)
   Player Jess enters the Spooky Library, where a puzzle blocks the northward path to the Kitchen. A message is displayed describing the puzzle: a computer controls an invisible forcefield, and a password screen is waiting for input.
   Jess attempts to solve the puzzle by entering A wrongpass, and the system responds that the answer was incorrect. The puzzle remains active, and the northward exit is still blocked with a negative room number.
   After examining the room and its fixtures, Jess discovers a clue: MOD-SPOOKY-VOICE. Recalling a password from an earlier note, Jess tries entering A Align. This time, the system verifies the answer, confirms it is correct, and deactivates the puzzle. The puzzle is removed from the room, and the blocked northward exit is updated internally from -6 to 6.
   Jess is now able to move north using the command N and successfully enters the Kitchen.

## **3.Additional Improvements**
   **•	Scalability:** Adding new rooms, items, or obstacles only requires updating the JSON file, not modifying the codebase.
   **•	Testability:** Code is now unit-testable in isolation due to clean separation of model and controller logic.
   **•	Flexibility:** The game supports saving and restoring game state with object serialization.
   **•	Maintainability:** Each class has a single clear responsibility, and enums are used to improve clarity for health status and ranking logic.
   **•	Extensibility:** The architecture supports future enhancements, like alternative views or more complex obstacles, without disrupting existing functionality.

## **4.Custom Game Engine Behavior**
  
 Our team made a few design choices where the project allowed flexibility:
   
### **4.1 Health Status Logic**
   
We defined four tiers of health using the HealthStatus enum:
   
•	AWAKE (70–100): Full health.
   
•	FATIGUED (40–69): Moderate health.
   
•	WOOZY (1–39): Low health.
   
•	SLEEP (0 or less): Player is unconscious, triggering game over.
   
Health is reduced when attacked by monsters and capped between 0 and 100.
   
### **4.2 Player Scoring and Rank System**
   
Players gain points for solving puzzles and defeating monsters. Each obstacle has a predefined score value from the JSON file.
   The player's final rank is determined by their total score:
   
•	Novice (0–59)
   
•	Intermediate (60–119)
   
•	Expert (120–199)
   
•	Legend (200+)
   
We opted not to factor inventory item value into the score calculation, but the structure supports such extensions.
   
### **4.3 Information Density**

   After each command, the user interface displays the current room name and description, any active obstacles (like puzzles or monsters), a list of visible items, and the player’s health, score, and health status (e.g., AWAKE, WOOZY). At the beginning and when using the L command, the console also reminds the player of all available commands for navigation and interaction. Messages are printed to confirm actions like picking up items, solving puzzles, or taking damage. This structure ensures the player always receives relevant updates without overwhelming repetition.


