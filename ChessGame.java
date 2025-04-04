import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class ChessGame extends JFrame {
    private static final int SIZE = 8;
    private ChessPiece[][] board = new ChessPiece[SIZE][SIZE];
    private int selectedRow = -1, selectedCol = -1;
    private ChessPanel chessPanel;
    private boolean whiteTurn = true; // White goes first
    private JLabel statusLabel;
    
    // Tracking for castling
    private boolean whiteKingMoved = false;
    private boolean blackKingMoved = false;
    private boolean whiteRookLeftMoved = false;
    private boolean whiteRookRightMoved = false;
    private boolean blackRookLeftMoved = false;
    private boolean blackRookRightMoved = false;
    
    public ChessGame() {
        setTitle("Chess Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 450); // Added extra height for status bar
        
        // Add status label
        statusLabel = new JLabel("White's turn");
        statusLabel.setHorizontalAlignment(JLabel.CENTER);
        statusLabel.setFont(new Font("Sans-Serif", Font.BOLD, 14));
        add(statusLabel, BorderLayout.NORTH);
        
        chessPanel = new ChessPanel();
        add(chessPanel, BorderLayout.CENTER);
        
        initializeBoard();
        setVisible(true);
    }
    
    private void initializeBoard() {
        // Black pieces
        board[0][0] = new Rook(0, 0, Color.BLACK);
        board[0][7] = new Rook(0, 7, Color.BLACK);
        board[0][1] = new Knight(0, 1, Color.BLACK);
        board[0][6] = new Knight(0, 6, Color.BLACK);
        board[0][2] = new Bishop(0, 2, Color.BLACK);
        board[0][5] = new Bishop(0, 5, Color.BLACK);
        board[0][3] = new Queen(0, 3, Color.BLACK);
        board[0][4] = new King(0, 4, Color.BLACK);
        for (int i = 0; i < SIZE; i++) {
            board[1][i] = new Pawn(1, i, Color.BLACK);
        }
        
        // White pieces
        for (int i = 0; i < SIZE; i++) {
            board[6][i] = new Pawn(6, i, Color.WHITE);
        }
        board[7][0] = new Rook(7, 0, Color.WHITE);
        board[7][7] = new Rook(7, 7, Color.WHITE);
        board[7][1] = new Knight(7, 1, Color.WHITE);
        board[7][6] = new Knight(7, 6, Color.WHITE);
        board[7][2] = new Bishop(7, 2, Color.WHITE);
        board[7][5] = new Bishop(7, 5, Color.WHITE);
        board[7][3] = new Queen(7, 3, Color.WHITE);
        board[7][4] = new King(7, 4, Color.WHITE);
    }
    
    private boolean isPathClear(int startRow, int startCol, int endRow, int endCol) {
        int rowStep = Integer.compare(endRow, startRow);
        int colStep = Integer.compare(endCol, startCol);
        int currentRow = startRow + rowStep;
        int currentCol = startCol + colStep;
        while (currentRow != endRow || currentCol != endCol) {
            if (board[currentRow][currentCol] != null) {
                return false; // Path is blocked
            }
            currentRow += rowStep;
            currentCol += colStep;
        }
        return true; // Path is clear
    }
    
    // Special rule for pawn capture: they can only capture diagonally
    private boolean canPawnCapture(int startRow, int startCol, int endRow, int endCol) {
        ChessPiece piece = board[startRow][startCol];
        if (!(piece instanceof Pawn)) return false;
        int direction = (piece.color == Color.WHITE) ? -1 : 1;
        return (endRow - startRow) == direction && 
               Math.abs(endCol - startCol) == 1 && 
               board[endRow][endCol] != null && 
               board[endRow][endCol].color != piece.color;
    }
    
    // Check if castling is valid
    private boolean canCastle(int startRow, int startCol, int endRow, int endCol) {
        ChessPiece piece = board[startRow][startCol];
        if (!(piece instanceof King)) return false;
        if (piece.color == Color.WHITE && startRow != 7 || piece.color == Color.BLACK && startRow != 0) return false;
        if (startCol != 4) return false;
        if ((piece.color == Color.WHITE && whiteKingMoved) || (piece.color == Color.BLACK && blackKingMoved)) return false;
        if (endRow != startRow || Math.abs(endCol - startCol) != 2) return false;
        
        int rookCol = (endCol == 2) ? 0 : 7;
        ChessPiece rook = board[startRow][rookCol];
        if (rook == null || !(rook instanceof Rook) || rook.color != piece.color) return false;
        if (piece.color == Color.WHITE) {
            if ((rookCol == 0 && whiteRookLeftMoved) || (rookCol == 7 && whiteRookRightMoved)) return false;
        } else {
            if ((rookCol == 0 && blackRookLeftMoved) || (rookCol == 7 && blackRookRightMoved)) return false;
        }
        
        int step = (endCol > startCol) ? 1 : -1;
        for (int c = startCol + step; c != rookCol; c += step) {
            if (board[startRow][c] != null) return false;
        }
        // Simplified: Check if king would castle through or into check omitted.
        return true;
    }
    
    // Execute a castling move
    private void executeCastle(int kingRow, int kingCol, int newCol) {
        board[kingRow][newCol] = board[kingRow][kingCol];
        board[kingRow][kingCol] = null;
        board[kingRow][newCol].col = newCol;
        int oldRookCol = (newCol == 2) ? 0 : 7;
        int newRookCol = (newCol == 2) ? 3 : 5;
        board[kingRow][newRookCol] = board[kingRow][oldRookCol];
        board[kingRow][oldRookCol] = null;
        board[kingRow][newRookCol].col = newRookCol;
        String side = (newCol == 2) ? "queenside" : "kingside";
        statusLabel.setText((whiteTurn ? "White" : "Black") + " castled " + side);
    }
    
    // Show pawn promotion dialog
    private ChessPiece promotePawn(int row, int col, Color color) {
        String[] options = {"Queen", "Rook", "Bishop", "Knight"};
        int choice = JOptionPane.showOptionDialog(
            this, 
            "Choose promotion piece:", 
            "Pawn Promotion", 
            JOptionPane.DEFAULT_OPTION, 
            JOptionPane.QUESTION_MESSAGE, 
            null, 
            options, 
            options[0]
        );
        switch (choice) {
            case 0: return new Queen(row, col, color);
            case 1: return new Rook(row, col, color);
            case 2: return new Bishop(row, col, color);
            case 3: return new Knight(row, col, color);
            default: return new Queen(row, col, color);
        }
    }
    
    class ChessPanel extends JPanel implements MouseListener {
        private static final Color LIGHT_SQUARE = new Color(240, 240, 210);
        private static final Color DARK_SQUARE = new Color(118, 150, 86);
        private static final Color SELECTED_SQUARE = new Color(186, 202, 68);
        private static final Color LEGAL_MOVE = new Color(186, 202, 68, 128);
        
        public ChessPanel() {
            addMouseListener(this);
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            drawBoard(g);
            if (selectedRow != -1 && selectedCol != -1) {
                highlightLegalMoves(g);
            }
            drawPieces(g);
            if (selectedRow != -1 && selectedCol != -1) {
                g.setColor(SELECTED_SQUARE);
                g.drawRect(selectedCol * 50, selectedRow * 50, 49, 49);
                g.drawRect(selectedCol * 50 + 1, selectedRow * 50 + 1, 47, 47);
            }
        }
        
        private void drawBoard(Graphics g) {
            for (int row = 0; row < SIZE; row++) {
                for (int col = 0; col < SIZE; col++) {
                    if ((row + col) % 2 == 0)
                        g.setColor(LIGHT_SQUARE);
                    else
                        g.setColor(DARK_SQUARE);
                    g.fillRect(col * 50, row * 50, 50, 50);
                }
            }
        }
        
        private void drawPieces(Graphics g) {
            for (int row = 0; row < SIZE; row++) {
                for (int col = 0; col < SIZE; col++) {
                    if (board[row][col] != null) {
                        board[row][col].draw(g, col * 50, row * 50);
                    }
                }
            }
        }
        
        private void highlightLegalMoves(Graphics g) {
            if (board[selectedRow][selectedCol] == null) return;
            g.setColor(LEGAL_MOVE);
            for (int row = 0; row < SIZE; row++) {
                for (int col = 0; col < SIZE; col++) {
                    if (isValidMove(selectedRow, selectedCol, row, col)) {
                        g.fillRect(col * 50, row * 50, 50, 50);
                    }
                }
            }
        }
        
        public void mouseClicked(MouseEvent e) {
            int col = e.getX() / 50;
            int row = e.getY() / 50;
            if (row < 0 || row >= SIZE || col < 0 || col >= SIZE) {
                return;
            }
            
            if (selectedRow == -1) {
                if (board[row][col] != null && 
                   ((whiteTurn && board[row][col].color == Color.WHITE) ||
                    (!whiteTurn && board[row][col].color == Color.BLACK))) {
                    selectedRow = row;
                    selectedCol = col;
                }
            } else {
                if (isValidMove(selectedRow, selectedCol, row, col)) {
                    if (board[selectedRow][selectedCol] instanceof King && Math.abs(selectedCol - col) == 2) {
                        executeCastle(selectedRow, selectedCol, col);
                        if (whiteTurn) whiteKingMoved = true; else blackKingMoved = true;
                    } else {
                        movePiece(selectedRow, selectedCol, row, col);
                        updateCastlingFlags(selectedRow, selectedCol);
                        if (board[row][col] instanceof Pawn) {
                            if ((board[row][col].color == Color.WHITE && row == 0) ||
                                (board[row][col].color == Color.BLACK && row == 7)) {
                                board[row][col] = promotePawn(row, col, board[row][col].color);
                                statusLabel.setText("Pawn promoted!");
                            }
                        }
                    }
                    whiteTurn = !whiteTurn;
                    statusLabel.setText(whiteTurn ? "White's turn" : "Black's turn");
                }
                selectedRow = selectedCol = -1;
            }
            repaint();
        }
        
        private void updateCastlingFlags(int startRow, int startCol) {
            ChessPiece piece = board[startRow][startCol];
            if (piece instanceof King) {
                if (piece.color == Color.WHITE) whiteKingMoved = true; else blackKingMoved = true;
            } else if (piece instanceof Rook) {
                if (piece.color == Color.WHITE) {
                    if (startCol == 0) whiteRookLeftMoved = true;
                    else if (startCol == 7) whiteRookRightMoved = true;
                } else {
                    if (startCol == 0) blackRookLeftMoved = true;
                    else if (startCol == 7) blackRookRightMoved = true;
                }
            }
        }
        
        private boolean isValidMove(int startRow, int startCol, int endRow, int endCol) {
            ChessPiece piece = board[startRow][startCol];
            if (piece == null) return false;
            if (piece instanceof King && Math.abs(startCol - endCol) == 2 && startRow == endRow) {
                return canCastle(startRow, startCol, endRow, endCol);
            }
            if (board[endRow][endCol] != null && board[endRow][endCol].color == piece.color) {
                return false;
            }
            if (piece instanceof Pawn) {
                if (startCol == endCol) {
                    if (board[endRow][endCol] != null) {
                        return false;
                    }
                    int direction = (piece.color == Color.WHITE) ? -1 : 1;
                    int startRank = (piece.color == Color.WHITE) ? 6 : 1;
                    if (startRow == startRank && endRow == startRow + 2 * direction) {
                        return board[startRow + direction][startCol] == null;
                    }
                } else if (Math.abs(startCol - endCol) == 1) {
                    int direction = (piece.color == Color.WHITE) ? -1 : 1;
                    if (endRow == startRow + direction && board[endRow][endCol] != null && 
                        board[endRow][endCol].color != piece.color) {
                        return true;
                    }
                    return false;
                }
            }
            if (piece instanceof Rook || piece instanceof Bishop || piece instanceof Queen) {
                if (!piece.isValidMove(endRow, endCol)) {
                    return false;
                }
                return isPathClear(startRow, startCol, endRow, endCol);
            }
            return piece.isValidMove(endRow, endCol);
        }
        
        private void movePiece(int startRow, int startCol, int endRow, int endCol) {
            if (board[endRow][endCol] != null) {
                ChessPiece capturedPiece = board[endRow][endCol];
                String pieceType = getPieceType(capturedPiece);
                String capturedBy = getPieceType(board[startRow][startCol]);
                String color = capturedPiece.color == Color.WHITE ? "White" : "Black";
                statusLabel.setText(color + " " + pieceType + " captured by " + 
                                   (whiteTurn ? "White" : "Black") + " " + capturedBy + "!");
            }
            board[endRow][endCol] = board[startRow][startCol];
            board[endRow][endCol].row = endRow;
            board[endRow][endCol].col = endCol;
            board[startRow][startCol] = null;
        }
        
        private String getPieceType(ChessPiece piece) {
            if (piece instanceof Pawn) return "Pawn";
            if (piece instanceof Rook) return "Rook";
            if (piece instanceof Knight) return "Knight";
            if (piece instanceof Bishop) return "Bishop";
            if (piece instanceof Queen) return "Queen";
            if (piece instanceof King) return "King";
            return "Piece";
        }
        
        public void mousePressed(MouseEvent e) {}
        public void mouseReleased(MouseEvent e) {}
        public void mouseEntered(MouseEvent e) {}
        public void mouseExited(MouseEvent e) {}
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ChessGame());
    }
}

// Abstract chess piece class and implementations

abstract class ChessPiece {
    int row, col;
    Color color;
    
    public ChessPiece(int row, int col, Color color) {
        this.row = row;
        this.col = col;
        this.color = color;
    }
    
    abstract boolean isValidMove(int newRow, int newCol);
    
    abstract void draw(Graphics g, int x, int y);
    
    protected void drawCircle(Graphics g, int x, int y) {
        g.setColor(color);
        g.fillOval(x + 10, y + 10, 30, 30);
        g.setColor(color == Color.WHITE ? Color.BLACK : Color.WHITE);
        g.drawOval(x + 10, y + 10, 30, 30);
    }
}

class Rook extends ChessPiece {
    public Rook(int row, int col, Color color) {
        super(row, col, color);
    }
    
    @Override
    boolean isValidMove(int newRow, int newCol) {
        return (row == newRow || col == newCol);
    }
    
    @Override
    void draw(Graphics g, int x, int y) {
        g.setColor(color);
        g.fillRect(x + 10, y + 30, 30, 10);
        g.fillRect(x + 15, y + 15, 20, 15);
        g.fillRect(x + 10, y + 10, 30, 5);
        g.setColor(color == Color.WHITE ? Color.BLACK : Color.WHITE);
        g.drawRect(x + 10, y + 30, 30, 10);
        g.drawRect(x + 15, y + 15, 20, 15);
        g.drawRect(x + 10, y + 10, 30, 5);
    }
}

class Bishop extends ChessPiece {
    public Bishop(int row, int col, Color color) {
        super(row, col, color);
    }
    
    @Override
    boolean isValidMove(int newRow, int newCol) {
        return Math.abs(row - newRow) == Math.abs(col - newCol);
    }
    
    @Override
    void draw(Graphics g, int x, int y) {
        g.setColor(color);
        g.fillRect(x + 15, y + 30, 20, 10);
        g.fillOval(x + 15, y + 15, 20, 15);
        int[] xPoints = {x + 25, x + 20, x + 30};
        int[] yPoints = {y + 5, y + 15, y + 15};
        g.fillPolygon(xPoints, yPoints, 3);
        g.setColor(color == Color.WHITE ? Color.BLACK : Color.WHITE);
        g.drawRect(x + 15, y + 30, 20, 10);
        g.drawOval(x + 15, y + 15, 20, 15);
        g.drawPolygon(xPoints, yPoints, 3);
    }
}

class Queen extends ChessPiece {
    public Queen(int row, int col, Color color) {
        super(row, col, color);
    }
    
    @Override
    boolean isValidMove(int newRow, int newCol) {
        return (row == newRow || col == newCol) || (Math.abs(row - newRow) == Math.abs(col - newCol));
    }
    
    @Override
    void draw(Graphics g, int x, int y) {
        g.setColor(color);
        g.fillRect(x + 15, y + 35, 20, 5);
        g.fillOval(x + 15, y + 15, 20, 20);
        int[] xPoints = {x + 15, x + 20, x + 25, x + 30, x + 35, x + 15};
        int[] yPoints = {y + 15, y + 5, y + 15, y + 5, y + 15, y + 15};
        g.fillPolygon(xPoints, yPoints, 6);
        g.setColor(color == Color.WHITE ? Color.BLACK : Color.WHITE);
        g.drawRect(x + 15, y + 35, 20, 5);
        g.drawOval(x + 15, y + 15, 20, 20);
        g.drawPolygon(xPoints, yPoints, 6);
    }
}

class Knight extends ChessPiece {
    public Knight(int row, int col, Color color) {
        super(row, col, color);
    }
    
    @Override
    boolean isValidMove(int newRow, int newCol) {
        int rowDiff = Math.abs(row - newRow);
        int colDiff = Math.abs(col - newCol);
        return (rowDiff == 2 && colDiff == 1) || (rowDiff == 1 && colDiff == 2);
    }
    
    @Override
    void draw(Graphics g, int x, int y) {
        g.setColor(color);
        g.fillRect(x + 15, y + 35, 20, 5);
        g.fillOval(x + 15, y + 20, 20, 15);
        int[] xPoints = {x + 20, x + 30, x + 35, x + 25, x + 15};
        int[] yPoints = {y + 5, y + 5, y + 15, y + 20, y + 20};
        g.fillPolygon(xPoints, yPoints, 5);
        g.setColor(color == Color.WHITE ? Color.BLACK : Color.WHITE);
        g.drawRect(x + 15, y + 35, 20, 5);
        g.drawOval(x + 15, y + 20, 20, 15);
        g.drawPolygon(xPoints, yPoints, 5);
    }
}

class King extends ChessPiece {
    public King(int row, int col, Color color) {
        super(row, col, color);
    }
    
    @Override
    boolean isValidMove(int newRow, int newCol) {
        return Math.abs(row - newRow) <= 1 && Math.abs(col - newCol) <= 1;
    }
    
    @Override
    void draw(Graphics g, int x, int y) {
        g.setColor(color);
        g.fillRect(x + 15, y + 35, 20, 5);
        g.fillOval(x + 15, y + 15, 20, 20);
        g.fillRect(x + 23, y + 5, 4, 15);
        g.fillRect(x + 18, y + 10, 14, 4);
        g.setColor(color == Color.WHITE ? Color.BLACK : Color.WHITE);
        g.drawRect(x + 15, y + 35, 20, 5);
        g.drawOval(x + 15, y + 15, 20, 20);
        g.drawRect(x + 23, y + 5, 4, 15);
        g.drawRect(x + 18, y + 10, 14, 4);
    }
}

class Pawn extends ChessPiece {
    public Pawn(int row, int col, Color color) {
        super(row, col, color);
    }
    
    @Override
    boolean isValidMove(int newRow, int newCol) {
        int direction = (color == Color.WHITE) ? -1 : 1;
        if (col == newCol && (newRow - row) == direction) {
            return true;
        }
        if (row == (color == Color.WHITE ? 6 : 1) && col == newCol && (newRow - row) == 2 * direction) {
            return true;
        }
        return false;
    }
    
    @Override
    void draw(Graphics g, int x, int y) {
        g.setColor(color);
        g.fillRect(x + 15, y + 35, 20, 5);
        g.fillOval(x + 17, y + 20, 16, 15);
        g.fillOval(x + 20, y + 10, 10, 10);
        g.setColor(color == Color.WHITE ? Color.BLACK : Color.WHITE);
        g.drawRect(x + 15, y + 35, 20, 5);
        g.drawOval(x + 17, y + 20, 16, 15);
        g.drawOval(x + 20, y + 10, 10, 10);
    }
}
