# SnA*ke

Welcome to SnA*ke, a Java Swing implementation of the classic Snake game with intelligent pathfinding using the A* algorithm.

![Snake](https://i.imgur.com/TcbzVZL.png)

## Features

- **Intelligent Pathfinding**: The snake uses the A* algorithm to navigate the game board intelligently, optimizing its path to reach the apple while avoiding traps and obstacles.

- **Trap Detection**: There's an additional heuristic that attempts to avoid potential trapping situations, setting a very costly weight to moves that lead to a subset of the grid that's smaller than the current snake's size.

- **Smart Goal Evaluation**: The snake evaluates the available spaces around the apple, whether there's enough space around it to provide for a potential escape or if it has 3 or more adjacent squares are blocked, this is also done by setting costly weights to these potential moves.

- **Session Statistics**: Track the current game's progress and average moves and snake size across multiple games.


## Installation

To run SnA*ke locally, follow these steps:

1. Clone the repository:

   ```bash
   git clone https://github.com/TardivoJP/SnA-ke.git
   ```

2. Navigate to the project directory:

   ```bash
   cd SnA-ke
   ```

3. Compile and run the game:

   ```bash
   javac SnakeGUI.java
   java SnakeGUI
   ```


---

Thank you for checking out SnA*ke! If you have any questions or feedback, feel free to open an issue or reach out. Enjoy the game! 🐍✨