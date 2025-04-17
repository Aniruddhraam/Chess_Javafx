# Java Chess Game with Stockfish AI Integration

A fully functional Chess game built with **JavaFX** and enhanced with the powerful **Stockfish** chess engine for AI-based move generation.

## Features

- Classic chess game logic (rules, check, checkmate, castling, promotion, stalemate).  
- Play against another player or versus Stockfish AI.  
- Adjustable AI difficulty by changing Stockfish search depth.  
- Flip board orientation anytime.  
- Highlight available legal moves.  
- Graphical UI with scalable vector-like piece rendering.  
- Uses UCI (Universal Chess Interface) to communicate with Stockfish.  

## Setup

1. **Download Stockfish**  
   Get the Stockfish binary for your OS from: https://stockfishchess.org/download/

2. **Project Structure**  
   ```text
   chesspkg/
   ├── ChessGame.java
   ├── StockfishEngine.java
   └── pieces/
       ├── ChessPiece.java
       ├── Rook.java
       ├── Bishop.java
       ├── Knight.java
       ├── Queen.java
       ├── King.java
       └── Pawn.java
   ```

3. **Configure Stockfish Path**  
   In `StockfishEngine.java`, update the path to your Stockfish executable:
   ```java
   String stockfishPath = "C:\\Path\\To\\Your\\stockfish.exe";
   ```

## Running the Game

Ensure JavaFX is installed, then compile and run:

```bash
javac -d bin -cp "path_to_javafx_lib/*" chesspkg/*.java
java -cp "bin;path_to_javafx_lib/*" chesspkg.ChessGame
```

> Replace `path_to_javafx_lib` with your JavaFX SDK `lib` directory.

## How to Play

- Toggle **Play against AI** to enable engine opponent.  
- Select side (White or Black) and AI difficulty.  
- Legal moves are highlighted; pawn promotions are prompted.  
- Use **Flip Board** button to change orientation.  

## AI Difficulty Mapping

| Label   | Stockfish Search Depth |
|---------|------------------------|
| Easy    | 1                      |
| Medium  | 7                      |
| Hard    | 12                     |
| Expert  | 20                     |

## FEN Conversion Example

Convert board to FEN for Stockfish evaluation:

```java
String fen = stockfish.boardToFEN(board, whiteTurn, castlingRights);
```

## Closing the Engine

Always terminate Stockfish on exit:

```java
stockfish.close();
```

## Dependencies

- Java 17+  
- JavaFX SDK  
- Stockfish executable  

## Credits

- Chess AI: [Stockfish](https://stockfishchess.org/)  
- GUI & graphics: JavaFX  
