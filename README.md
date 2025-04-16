# Chess Game in JavaFX

A simple two player chess game implementation using JavaFX with move validation, castling, pawn promotion, and check detection.

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
```

### Move Validation
```java
private ChessPiece promotePawn(int row, int col, Color color) {
    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
    alert.setTitle("Pawn Promotion");
    alert.setHeaderText("Choose promotion piece:");
    ButtonType queen = new ButtonType("Queen"), 
              rook = new ButtonType("Rook"),
              bishop = new ButtonType("Bishop"), 
              knight = new ButtonType("Knight");
    alert.getButtonTypes().setAll(queen, rook, bishop, knight);
    
    Optional<ButtonType> result = alert.showAndWait();
    return result.map(button -> {
        if (button == rook) return new Rook(row, col, color);
        if (button == bishop) return new Bishop(row, col, color);
        if (button == knight) return new Knight(row, col, color);
        return new Queen(row, col, color);
    }).orElse(new Queen(row, col, color));
}
```

### Pawn Promotion
```java
private ChessPiece promotePawn(int row, int col, Color color) {
    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
    alert.setTitle("Pawn Promotion");
    alert.setHeaderText("Choose promotion piece:");
    ButtonType queen = new ButtonType("Queen"), 
              rook = new ButtonType("Rook"),
              bishop = new ButtonType("Bishop"), 
              knight = new ButtonType("Knight");
    alert.getButtonTypes().setAll(queen, rook, bishop, knight);
    
    Optional<ButtonType> result = alert.showAndWait();
    return result.map(button -> {
        if (button == rook) return new Rook(row, col, color);
        if (button == bishop) return new Bishop(row, col, color);
        if (button == knight) return new Knight(row, col, color);
        return new Queen(row, col, color);
    }).orElse(new Queen(row, col, color));
}
```

### Check Detection
```java
private boolean isKingInCheck(Color kingColor) {
    int[] kingPos = findKing(kingColor);
    if (kingPos == null) return false;
    
    for (int r = 0; r < SIZE; r++) {
        for (int c = 0; c < SIZE; c++) {
            if (board[r][c] != null && board[r][c].color != kingColor) {
                if (isValidMoveWithoutCheckTest(r, c, kingPos[0], kingPos[1])) {
                    return true;
                }
            }
        }
    }
    return false;
}
```

