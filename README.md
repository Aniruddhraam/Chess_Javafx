# Chess Game

## Table of Contents
- [Description](#description)
- [Features](#features)
- [Prerequisites](#prerequisites)
- [Installation](#installation)
- [Usage](#usage)
- [Configuration](#configuration)
- [Controls](#controls)
- [Contributing](#contributing)
- [License](#license)
- [Acknowledgments](#acknowledgments)

## Description
A fully-featured JavaFX-based Chess game offering:
- Custom-drawn chess pieces and board UI
- Standard chess rules (move validation, castling, en passant, pawn promotion)
- Single-player mode against Stockfish AI with adjustable difficulty levels
- Two-player network mode (host or join via IP & port)
- Theme selection, dark mode, and board flipping
- Real-time game log and status updates

## Features
- **Custom Graphics**: Each piece is drawn programmatically with JavaFX Canvas.
- **Rule Enforcement**: Legal move generation, check/checkmate, stalemate detection.
- **AI Opponent**: Integrates Stockfish engine; four difficulty settings (Easy, Medium, Hard, Expert).
- **Network Play**: Host or join games over TCP/IP; built-in chat and connection status.
- **UI Customization**: Predefined themes, dark/light mode toggle, board flip.
- **Game Controls**: Click-to-move interface, promotion dialog, highlighted legal moves.
- **Responsive Layout**: Auto-adjusts board size based on screen resolution.

## Prerequisites
- **Java 11+**
- **JavaFX 11+** (if not bundled with your JDK)
- **Maven** (or Gradle) for dependency management
- **Stockfish** engine binary in project root or system PATH

## Installation
1. **Clone repository**

    ```bash
    git clone https://github.com/yourusername/chess-game.git
    cd chess-game
    ```

2. **Add Stockfish**

    - Download the latest Stockfish binary for your OS.
    - Place the `stockfish` executable in the project root (next to `pom.xml`).

3. **Build**

    ```bash
    mvn clean install
    ```

## Usage
### Running Locally

```bash
mvn javafx:run
```
_or_

```bash
java -jar target/chess-game.jar
```

### Menu Overview
1. **Play vs AI**: Toggle AI, select difficulty, choose playing color.  
2. **Network**: Enter IP & port, click **Host Game** or **Join Game**.  
3. **Theme**: Select board theme, toggle dark mode, flip board.  
4. **New Game**: Reset board and settings.  

## Configuration
- **AI Difficulty**: Easy (depth 1), Medium (7), Hard (12), Expert (20).  
- **Playing Color**: Choose White or Black before starting.  
- **Network**: Default port `8888`; ensure firewall allows inbound/outbound TCP.

## Controls
- **Select & Move**: Click a piece, legal moves highlight, click destination.  
- **Pawn Promotion**: Dialog appears when pawn reaches last rank.  
- **Castling**: Move king two squares toward rook when eligible.  
- **Flip Board**: Toggle orientation for alternate viewpoints.  
- **Dark Mode**: Toggle light/dark UI coloring.

## Contributing
1. Fork the repository.  
2. Create a feature branch:  
    ```bash
    git checkout -b feature-name
    ```  
3. Commit your changes:  
    ```bash
    git commit -m "Add feature"
    ```  
4. Push to branch:  
    ```bash
    git push origin feature-name
    ```  
5. Open a Pull Request.

Please adhere to the existing code style and include unit tests where applicable.

## License
This project is licensed under the MIT License.

## Acknowledgments
- [Stockfish](https://stockfishchess.org/) for the powerful open-source chess engine.  
- JavaFX community for tutorials and support.
