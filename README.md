# JavaFX Chess Game

A fully-featured Chess application built with JavaFX, offering:

- **Human vs. Human** or **Human vs. AI** play (Stockfish engine integration)  
- Adjustable **AI difficulty** (Easy, Medium, Hard, Expert)  
- **Themes** (board color schemes), **Dark Mode**, and **Board Flip**  
- Responsive to any screen size (auto-scales to available resolution)  
- Full chess rules: legal moves, check/checkmate, stalemate, castling, pawn promotion  

---

## Table of Contents

1. [Demo](#demo)  
2. [Features](#features)  
3. [Getting Started](#getting-started)  
   - [Prerequisites](#prerequisites)  
   - [Clone & Build](#clone--build)  
4. [Usage](#usage)  
5. [Contributing](#contributing)  

---

## Demo

![ChessGame Screenshot](./docs/screenshot.png)  
*Example of the Classic theme in action, with dark mode toggled off.*

---

## Features

- **Play Modes**  
  - Human vs. Human on the same machine  
  - Human vs. AI powered by Stockfish (configurable search depth)  
- **AI Difficulty**  
  - Easy (depth 1), Medium (7), Hard (12), Expert (20)  
- **Full Rules Enforcement**  
  - Legal move validation for all piece types (King, Queen, Rook, Bishop, Knight, Pawn)  
  - Castling (king- and queen-side) with move tracking  
  - Pawn double-step, en passant capture, promotion dialog  
  - Check, checkmate, and stalemate detection with flashing highlight  
- **Customizable UI**  
  - Multiple board themes (light/dark squares, border accents)  
  - Toggle **Dark Mode** for the entire application  
  - **Flip Board** control to view from Black’s perspective  
  - Auto-scaling to fit any screen resolution (max square size 80px)  
- **Developer-Friendly**  
  - Modular piece-drawing via `Drawable` interface  
  - Clear separation of board logic, move validation, AI integration  
  - Easily extendable themes via `ChessTheme` enum  

---

## Getting Started

### Prerequisites

- **Java 11+** (JDK with JavaFX modules)  
- **Maven** (or Gradle) for build and dependency management  
- Optionally, download [Stockfish](https://stockfishchess.org/) and place the binary in your system path  

### Clone & Build

```bash
# Clone the repository
git clone https://github.com/your-username/javafx-chess-game.git
cd javafx-chess-game

# Build with Maven
mvn clean package

# Run the application
mvn javafx:run

or, after packaging

java --module-path /path/to/javafx-sdk/lib \
     --add-modules javafx.controls,javafx.fxml \
     -jar target/chessgame.jar

```

## Usage

### Game Settings
1. **Play against AI**: Check/uncheck “Play against AI”  
2. **AI Difficulty**: Select Easy, Medium, Hard, or Expert  
3. **Play as**: Choose White or Black color  
4. **New Game**: Reset board and settings  

### Theme Settings
1. **Board Theme**: Pick from predefined color schemes  
2. **Toggle Dark Mode**: Switch full UI to dark/light styling  
3. **Flip Board**: Mirror board orientation (lets Black view from bottom)  

### Controls & Interaction
- **Select & Move**: Click your piece, then click a highlighted square  
- **Pawn Promotion**: On reaching last rank, choose Queen, Rook, Bishop, or Knight  
- **Check Highlight**: King in check flashes red  
- **Game Over Dialog**: Offers “Play Again” or “Quit” on checkmate/stalemate  

---

## Contributing

1. Fork this repository  
2. Create your feature branch (`git checkout -b feature/YourFeature`)  
3. Commit your changes (`git commit -m "Add YourFeature"`)  
4. Push to the branch (`git push origin feature/YourFeature`)  
5. Open a Pull Request  

Please follow the existing code style and include relevant tests.  