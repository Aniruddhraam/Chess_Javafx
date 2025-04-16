# Chess Game in JavaFX

A simple chess game implementation using JavaFX with move validation, castling, pawn promotion, and check detection.

## Features
- Full chess rule implementation including:
  - Piece movement validation
  - Castling (king & rook)
  - Pawn promotion with user selection
  - Check detection and visualization
  - Turn-based play
  - Basic GUI with board highlighting

## Requirements
- Java 8+
- JavaFX SDK

## Getting Started
1. Clone the repository
2. Open in IDE with JavaFX configured
3. Run `ChessGame.java`

## Code Structure
Key components in `ChessGame.java`:

### ChessPiece Hierarchy
```java
abstract class ChessPiece {
    int row, col;
    Color color;
    abstract boolean isValidMove(int newRow, int newCol);
    abstract void draw(GraphicsContext gc, int x, int y);
}

class Rook extends ChessPiece {
    @Override
    boolean isValidMove(int newRow, int newCol) {
        return row == newRow || col == newCol;
    }
}