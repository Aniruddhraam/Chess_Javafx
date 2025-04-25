package chesspkg;

import javafx.application.Application;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.scene.input.MouseEvent;
import javafx.scene.SnapshotParameters;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;
import javafx.scene.control.TextField;
import javafx.scene.control.TextArea;
import javafx.scene.control.ScrollPane;
import java.net.InetAddress;
import java.net.UnknownHostException;

interface Drawable {
    void draw(GraphicsContext gc, int x, int y);
}

interface MoveValidator {
    boolean isValidMove(int newRow, int newCol);
}

class CastlingRights {
    private boolean whiteKingMoved, blackKingMoved;
    private boolean whiteLeftRookMoved, whiteRightRookMoved;
    private boolean blackLeftRookMoved, blackRightRookMoved;

    boolean canCastle(Color color, boolean kingside) {
        if (color == Color.WHITE) {
            return !whiteKingMoved && (kingside ? !whiteRightRookMoved : !whiteLeftRookMoved);
        }
        return !blackKingMoved && (kingside ? !blackRightRookMoved : !blackLeftRookMoved);
    }

    void markKingMoved(Color color) {
        if (color == Color.WHITE) whiteKingMoved = true;
        else blackKingMoved = true;
    }

    void markRookMoved(Color color, boolean left) {
        if (color == Color.WHITE) {
            if (left) whiteLeftRookMoved = true;
            else whiteRightRookMoved = true;
        } else {
            if (left) blackLeftRookMoved = true;
            else blackRightRookMoved = true;
        }
    }
    
    public void reset() {
        whiteKingMoved = blackKingMoved = false;
        whiteLeftRookMoved = whiteRightRookMoved = false;
        blackLeftRookMoved = blackRightRookMoved = false;
    }
}

abstract class ChessPiece implements Drawable, MoveValidator {
    int row, col;
    Color color;

    ChessPiece(int row, int col, Color color) {
        this.row = row;
        this.col = col;
        this.color = color;
    }

    protected void drawCircle(GraphicsContext gc, int x, int y) {
        gc.setFill(color);
        gc.fillOval(x + 10, y + 10, 30, 30);
        gc.setStroke(color == Color.WHITE ? Color.BLACK : Color.WHITE);
        gc.strokeOval(x + 10, y + 10, 30, 30);
    }
}

class Rook extends ChessPiece {
    Rook(int row, int col, Color color) { super(row, col, color); }
    @Override public boolean isValidMove(int newRow, int newCol) {
        return row == newRow || col == newCol;
    }
    @Override public void draw(GraphicsContext gc, int x, int y) {
        gc.setFill(color);
        gc.fillRect(x + 10, y + 30, 30, 10);
        gc.fillRect(x + 15, y + 15, 20, 15);
        gc.fillRect(x + 10, y + 10, 30, 5);
        gc.setStroke(color == Color.WHITE ? Color.BLACK : Color.WHITE);
        gc.strokeRect(x + 10, y + 30, 30, 10);
        gc.strokeRect(x + 15, y + 15, 20, 15);
        gc.strokeRect(x + 10, y + 10, 30, 5);
    }
}

class Bishop extends ChessPiece {
    Bishop(int row, int col, Color color) { super(row, col, color); }
    @Override public boolean isValidMove(int newRow, int newCol) {
        return Math.abs(row - newRow) == Math.abs(col - newCol);
    }
    @Override public void draw(GraphicsContext gc, int x, int y) {
        gc.setFill(color);
        gc.fillRect(x + 15, y + 30, 20, 10);
        gc.fillOval(x + 15, y + 15, 20, 15);
        double[] xPoints = {x + 25, x + 20, x + 30};
        double[] yPoints = {y + 5, y + 15, y + 15};
        gc.fillPolygon(xPoints, yPoints, 3);
        gc.setStroke(color == Color.WHITE ? Color.BLACK : Color.WHITE);
        gc.strokeRect(x + 15, y + 30, 20, 10);
        gc.strokeOval(x + 15, y + 15, 20, 15);
        gc.strokePolygon(xPoints, yPoints, 3);
    }
}

class Knight extends ChessPiece {
    Knight(int row, int col, Color color) { super(row, col, color); }
    @Override public boolean isValidMove(int newRow, int newCol) {
        int rowDiff = Math.abs(row - newRow);
        int colDiff = Math.abs(col - newCol);
        return (rowDiff == 2 && colDiff == 1) || (rowDiff == 1 && colDiff == 2);
    }
    @Override public void draw(GraphicsContext gc, int x, int y) {
        gc.setFill(color);
        gc.fillRect(x + 15, y + 35, 20, 5);
        gc.fillOval(x + 15, y + 20, 20, 15);
        double[] xPoints = {x + 20, x + 30, x + 35, x + 25, x + 15};
        double[] yPoints = {y + 5, y + 5, y + 15, y + 20, y + 20};
        gc.fillPolygon(xPoints, yPoints, 5);
        gc.setStroke(color == Color.WHITE ? Color.BLACK : Color.WHITE);
        gc.strokeRect(x + 15, y + 35, 20, 5);
        gc.strokeOval(x + 15, y + 20, 20, 15);
        gc.strokePolygon(xPoints, yPoints, 5);
    }
}

class Queen extends ChessPiece {
    Queen(int row, int col, Color color) { super(row, col, color); }
    @Override public boolean isValidMove(int newRow, int newCol) {
        return (row == newRow || col == newCol) || (Math.abs(row - newRow) == Math.abs(col - newCol));
    }
    @Override public void draw(GraphicsContext gc, int x, int y) {
        gc.setFill(color);
        gc.fillRect(x + 15, y + 35, 20, 5);
        gc.fillOval(x + 15, y + 15, 20, 20);
        double[] xPoints = {x + 15, x + 20, x + 25, x + 30, x + 35, x + 15};
        double[] yPoints = {y + 15, y + 5, y + 15, y + 5, y + 15, y + 15};
        gc.fillPolygon(xPoints, yPoints, 6);
        gc.setStroke(color == Color.WHITE ? Color.BLACK : Color.WHITE);
        gc.strokeRect(x + 15, y + 35, 20, 5);
        gc.strokeOval(x + 15, y + 15, 20, 20);
        gc.strokePolygon(xPoints, yPoints, 6);
    }
}

class King extends ChessPiece {
    King(int row, int col, Color color) { super(row, col, color); }
    @Override public boolean isValidMove(int newRow, int newCol) {
        return Math.abs(row - newRow) <= 1 && Math.abs(col - newCol) <= 1;
    }
    @Override public void draw(GraphicsContext gc, int x, int y) {
        gc.setFill(color);
        gc.fillRect(x + 15, y + 35, 20, 5);
        gc.fillOval(x + 15, y + 15, 20, 20);
        gc.fillRect(x + 23, y + 5, 4, 15);
        gc.fillRect(x + 18, y + 10, 14, 4);
        gc.setStroke(color == Color.WHITE ? Color.BLACK : Color.WHITE);
        gc.strokeRect(x + 15, y + 35, 20, 5);
        gc.strokeOval(x + 15, y + 15, 20, 20);
        gc.strokeRect(x + 23, y + 5, 4, 15);
        gc.strokeRect(x + 18, y + 10, 14, 4);
    }
}

class Pawn extends ChessPiece {
    Pawn(int row, int col, Color color) { super(row, col, color); }
    @Override public boolean isValidMove(int newRow, int newCol) {
        int direction = (color == Color.WHITE) ? -1 : 1;
        if (col == newCol && (newRow - row) == direction) return true;
        if (row == (color == Color.WHITE ? 6 : 1) && col == newCol && (newRow - row) == 2 * direction) return true;
        return false;
    }
    @Override public void draw(GraphicsContext gc, int x, int y) {
        gc.setFill(color);
        gc.fillRect(x + 15, y + 35, 20, 5);
        gc.fillOval(x + 17, y + 20, 16, 15);
        gc.fillOval(x + 20, y + 10, 10, 10);
        gc.setStroke(color == Color.WHITE ? Color.BLACK : Color.WHITE);
        gc.strokeRect(x + 15, y + 35, 20, 5);
        gc.strokeOval(x + 17, y + 20, 16, 15);
        gc.strokeOval(x + 20, y + 10, 10, 10);
    }
}

public class ChessGame extends Application {
    private static final int SIZE = 8;
    private static int SQUARE_SIZE = 50;
    private ChessPiece[][] board = new ChessPiece[SIZE][SIZE];
    private int selectedRow = -1, selectedCol = -1;
    private boolean whiteTurn = true;
    private Label statusLabel;
    private ChessBoard chessBoard;
    private boolean whiteKingInCheck, blackKingInCheck;
    private Timeline kingFlashAnimation;
    private final CastlingRights castlingRights = new CastlingRights();
    private Map<String, Image> pieceImages = new HashMap<>();
    private Set<Point2D> legalMoveCache = new HashSet<>();
    private int[] whiteKingPos = new int[2];
    private int[] blackKingPos = new int[2];
    private boolean boardFlipped = false;
    
    private StockfishEngine stockfish;
    private boolean playingAgainstAI = true;
    private boolean aiIsBlack = true; // AI plays as black
    private ComboBox<String> difficultyComboBox;
    private ComboBox<String> playerColorComboBox;
    
    private ComboBox<ChessTheme> themeComboBox;
    private ChessTheme currentTheme;
    
    private boolean darkModeEnabled = false;
    private Button darkModeButton;
    private BorderPane root;
    
    private NetworkChessManager networkManager;
    private boolean playingOnline = false;
    private boolean isMyTurn = true;
    private TextArea gameLogArea;
    private TextField ipAddressField;
    private TextField portField;
    private HBox connectionBox;
    
    // Colors for the background dark theme
    private static final Color DARK_INDIGO = Color.web("#1A1A2E");
    private static final Color DARK_TEXT = Color.web("#E0E0E0");
    private static final Color DARK_CONTROL_BG = Color.web("#16213E");
    private static final Color LIGHT_BG = Color.web("#F5F5F5");
    private static final Color LIGHT_TEXT = Color.BLACK;
    private Button flipBoardButton;

    @Override
    public void start(Stage primaryStage) {
        // Existing initialization code (Stockfish, screen size, etc.) remains unchanged
        primaryStage.setTitle("Chess Game");
        stockfish = new StockfishEngine();
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        double screenHeight = screenBounds.getHeight();
        double screenWidth = screenBounds.getWidth();
        int optimalSquareSize = (int)((screenHeight - 100) / SIZE);
        SQUARE_SIZE = optimalSquareSize;

        root = new BorderPane();
        root.setPadding(new Insets(10));

        // Top: Status Label
        statusLabel = new Label("White's turn");
        statusLabel.setFont(Font.font("Sans-Serif", FontWeight.BOLD, 20));
        HBox topBox = new HBox(statusLabel);
        topBox.setAlignment(Pos.CENTER);
        topBox.setPadding(new Insets(10));
        root.setTop(topBox);

        // Center: Chessboard
        chessBoard = new ChessBoard();
        chessBoard.setCache(true);
        root.setCenter(chessBoard);

        // King flash animation setup (unchanged)
        kingFlashAnimation = new Timeline(new KeyFrame(Duration.seconds(0.5), e -> {
            if (chessBoard != null) {
                chessBoard.toggleKingHighlight();
            }
        }));
        kingFlashAnimation.setCycleCount(Timeline.INDEFINITE);
        kingFlashAnimation.setAutoReverse(true);

        // Right: Control Panel
        VBox controlPanel = new VBox(10);
        controlPanel.setPadding(new Insets(10));
        controlPanel.setAlignment(Pos.TOP_CENTER);
        controlPanel.setPrefWidth(300); // Set a reasonable width

        // Game Settings
        Label gameSettingsLabel = new Label("Game Settings");
        gameSettingsLabel.setFont(Font.font("Sans-Serif", FontWeight.BOLD, 14));
        CheckBox aiCheckBox = new CheckBox("Play against AI");
        aiCheckBox.setSelected(playingAgainstAI);
        aiCheckBox.setOnAction(e -> playingAgainstAI = aiCheckBox.isSelected());
        Label difficultyLabel = new Label("AI Difficulty:");
        difficultyComboBox = new ComboBox<>();
        difficultyComboBox.getItems().addAll("Easy", "Medium", "Hard", "Expert");
        difficultyComboBox.setValue("Medium");
        difficultyComboBox.setOnAction(e -> {
            String difficulty = difficultyComboBox.getValue();
            switch (difficulty) {
                case "Easy": stockfish.setSearchDepth(1); break;
                case "Medium": stockfish.setSearchDepth(7); break;
                case "Hard": stockfish.setSearchDepth(12); break;
                case "Expert": stockfish.setSearchDepth(20); break;
            }
        });
        Label colorLabel = new Label("Play as:");
        playerColorComboBox = new ComboBox<>();
        playerColorComboBox.getItems().addAll("White", "Black");
        playerColorComboBox.setValue("White");
        playerColorComboBox.setOnAction(e -> {
            aiIsBlack = "White".equals(playerColorComboBox.getValue());
            resetGame();
        });
        Button resetButton = new Button("New Game");
        resetButton.setOnAction(e -> resetGame());
        HBox gameSettings = new HBox(10, aiCheckBox, difficultyLabel, difficultyComboBox,
                colorLabel, playerColorComboBox, resetButton);
        gameSettings.setAlignment(Pos.CENTER_LEFT);

        // Theme Settings
        Label themeSettingsLabel = new Label("Theme Settings");
        themeSettingsLabel.setFont(Font.font("Sans-Serif", FontWeight.BOLD, 14));
        currentTheme = ChessTheme.PREDEFINED_THEMES[0];
        Label themeLabel = new Label("Board Theme:");
        themeLabel.setFont(Font.font("Sans-Serif", FontWeight.BOLD, 14));
        themeComboBox = new ComboBox<>();
        themeComboBox.getItems().addAll(ChessTheme.PREDEFINED_THEMES);
        themeComboBox.setValue(currentTheme);
        themeComboBox.setOnAction(e -> {
            currentTheme = themeComboBox.getValue();
            chessBoard.draw();
        });
        themeComboBox.setStyle("-fx-font-size: 14pt;");
        darkModeButton = new Button("Toggle Dark Mode");
        darkModeButton.setOnAction(e -> toggleDarkMode());
        darkModeButton.setStyle("-fx-font-size: 14pt;");
        flipBoardButton = new Button("Flip Board");
        flipBoardButton.setOnAction(e -> {
            boardFlipped = !boardFlipped;
            chessBoard.draw();
        });
        flipBoardButton.setStyle("-fx-font-size: 14pt;");
        HBox themeSettings = new HBox(10, themeLabel, themeComboBox, darkModeButton, flipBoardButton);
        themeSettings.setAlignment(Pos.CENTER_LEFT);

        // Network Settings (created in initializeNetworkControls)
        Label networkSettingsLabel = new Label("Network Settings");
        networkSettingsLabel.setFont(Font.font("Sans-Serif", FontWeight.BOLD, 14));

        // Add sections to control panel (connectionBox added after initialization)
        controlPanel.getChildren().addAll(gameSettingsLabel, gameSettings, themeSettingsLabel, themeSettings,
                networkSettingsLabel);

        root.setRight(controlPanel);

        // Bottom: Game Log (initialized later)
        initializeNetworkControls();
		controlPanel.getChildren().add(connectionBox);
        ScrollPane logScrollPane = new ScrollPane(gameLogArea);
        logScrollPane.setFitToWidth(true);
        logScrollPane.setFitToHeight(true);
        root.setBottom(logScrollPane);

        // Scene setup (unchanged)
        Scene scene = new Scene(root, screenWidth * 0.9, screenHeight * 0.9);
        primaryStage.setScene(scene);
        primaryStage.setMaximized(true);

        applyTheme(false);
        initializeBoard();
        initializePieceImages();
        chessBoard.draw();
        if (playingAgainstAI && !aiIsBlack) {
            makeAIMove();
        }
        primaryStage.show();
    }

    private void initializeNetworkControls() {
        ipAddressField = new TextField();
        ipAddressField.setPromptText("IP Address");
        portField = new TextField("8888");
        portField.setPromptText("Port");
        Button hostButton = new Button("Host Game");
        hostButton.setOnAction(e -> startHosting());
        Button joinButton = new Button("Join Game");
        joinButton.setOnAction(e -> joinGame());
        connectionBox = new HBox(10, new Label("IP:"), ipAddressField, new Label("Port:"), portField, hostButton, joinButton);
        connectionBox.setAlignment(Pos.CENTER);

        gameLogArea = new TextArea();
        gameLogArea.setEditable(false);
        gameLogArea.setPrefHeight(150);

        networkManager = new NetworkChessManager(this);
        networkManager.setEventListener(new NetworkEventHandler());
        try {
            String localIp = InetAddress.getLocalHost().getHostAddress();
            ipAddressField.setText(localIp);
        } catch (UnknownHostException ex) {
            ipAddressField.setText("localhost");
        }
    }
    
    private class NetworkEventHandler implements NetworkChessManager.GameEventListener {
        @Override
        public void onMoveReceived(int startRow, int startCol, int endRow, int endCol, char promotionType) {
            movePiece(startRow, startCol, endRow, endCol, promotionType);
            updateCheckStatus();
            chessBoard.draw();
            isMyTurn = true;
        }

        @Override
        public void onConnectionEstablished(boolean isWhite) {
            isMyTurn = isWhite; // Server (white) goes first
            resetGame();
        }

        @Override
        public void onConnectionLost() {
            playingOnline = false;
            isMyTurn = true;
            logGameMessage("Connection lost. Returning to local play.");
        }

        @Override
        public void onGameMessage(String message) {
            logGameMessage(message);
        }
    }


    // Update the applyTheme method to incorporate the new style elements
    private void applyTheme(boolean darkMode) {
        if (darkMode) {
            // Apply dark theme
            root.setStyle("-fx-background-color: #1A1A2E;");
            statusLabel.setStyle("-fx-text-fill: #E0E0E0; -fx-font-size: 20pt;");
            
            // Style right panel controls
            VBox rightPanel = (VBox) root.getRight();
            for (Node node : rightPanel.getChildren()) {
                applyNodeStyle(node, true);
            }

            // Style bottom scroll pane content
            ScrollPane logScrollPane = (ScrollPane) root.getBottom();
            if (logScrollPane.getContent() instanceof TextArea) {
                TextArea logArea = (TextArea) logScrollPane.getContent();
                logArea.setStyle("-fx-control-inner-background: #1A1A2E; -fx-text-fill: #E0E0E0;");
            }

            darkModeButton.setText("Toggle Light Mode");
        } else {
            // Apply light theme
            root.setStyle("-fx-background-color: #F5F5F5;");
            statusLabel.setStyle("-fx-text-fill: black; -fx-font-size: 20pt;");
            
            // Reset right panel styles
            VBox rightPanel = (VBox) root.getRight();
            for (Node node : rightPanel.getChildren()) {
                applyNodeStyle(node, false);
            }

            // Reset bottom scroll pane content style
            ScrollPane logScrollPane = (ScrollPane) root.getBottom();
            if (logScrollPane.getContent() instanceof TextArea) {
                TextArea logArea = (TextArea) logScrollPane.getContent();
                logArea.setStyle("");
            }

            darkModeButton.setText("Toggle Dark Mode");
        }
    }

    // Helper method to apply styles recursively
    private void applyNodeStyle(Node node, boolean darkMode) {
        if (node instanceof HBox) {
            HBox hbox = (HBox) node;
            for (Node subNode : hbox.getChildren()) {
                applyNodeStyle(subNode, darkMode);
            }
        } else if (node instanceof Button) {
            Button btn = (Button) node;
            if (darkMode) {
                btn.setStyle("-fx-background-color: #16213E; -fx-text-fill: #E0E0E0; -fx-font-size: 14pt;");
            } else {
                btn.setStyle("-fx-font-size: 14pt;");
            }
        } else if (node instanceof Label) {
            Label lbl = (Label) node;
            if (darkMode) {
                lbl.setStyle("-fx-text-fill: #E0E0E0; -fx-font-size: 14pt;");
            } else {
                lbl.setStyle("-fx-font-size: 14pt;");
            }
        } else if (node instanceof ComboBox) {
            ComboBox<?> cb = (ComboBox<?>) node;
            if (darkMode) {
                cb.setStyle("-fx-background-color: #D2B48C; " +
                           "-fx-text-fill: #000000; " +
                           "-fx-prompt-text-fill: #333333; " +
                           "-fx-control-inner-background: #F5DEB3; " +
                           "-fx-selection-bar: #BFA67A; " +
                           "-fx-selection-bar-text-fill: #000000; " +
                           "-fx-cell-hover-color: #E6CCB2; " +
                           "-fx-font-size: 14pt;");
            } else {
                cb.setStyle("-fx-font-size: 14pt;");
            }
        } else if (node instanceof TextField) {
            TextField tf = (TextField) node;
            if (darkMode) {
                tf.setStyle("-fx-background-color: #16213E; " +
                           "-fx-text-fill: #E0E0E0; " +
                           "-fx-prompt-text-fill: #666666;");
            } else {
                tf.setStyle("");
            }
        }
    }
    
    private void toggleDarkMode() {
        darkModeEnabled = !darkModeEnabled;
        applyTheme(darkModeEnabled);
    }

    private void initializeBoard() {
        placePieces(0, 1, Color.BLACK);
        placePieces(7, 6, Color.WHITE);
        whiteKingPos[0] = 7;
        whiteKingPos[1] = 4;
        blackKingPos[0] = 0;
        blackKingPos[1] = 4;
        chessBoard.draw();
    }

    private void initializePieceImages() {
        for (Color color : new Color[]{Color.WHITE, Color.BLACK}) {
            String colorName = (color == Color.WHITE) ? "White" : "Black";
            renderPieceToImage(new Pawn(0, 0, color), "Pawn" + colorName);
            renderPieceToImage(new Rook(0, 0, color), "Rook" + colorName);
            renderPieceToImage(new Knight(0, 0, color), "Knight" + colorName);
            renderPieceToImage(new Bishop(0, 0, color), "Bishop" + colorName);
            renderPieceToImage(new Queen(0, 0, color), "Queen" + colorName);
            renderPieceToImage(new King(0, 0, color), "King" + colorName);
        }
    }

    private void renderPieceToImage(ChessPiece piece, String key) {
        Canvas canvas = new Canvas(SQUARE_SIZE, SQUARE_SIZE);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        
        // Use scale factor for drawing the piece
        double scaleFactor = SQUARE_SIZE / 50.0; // 50 was the original size
        gc.save();
        gc.scale(scaleFactor, scaleFactor);
        piece.draw(gc, 0, 0);
        gc.restore();
        
        SnapshotParameters params = new SnapshotParameters();
        params.setFill(Color.TRANSPARENT);
        WritableImage image = canvas.snapshot(params, null);
        pieceImages.put(key, image);
    }

    private void placePieces(int backRow, int pawnRow, Color color) {
        for (int i = 0; i < SIZE; i++) board[pawnRow][i] = new Pawn(pawnRow, i, color);
        board[backRow][0] = new Rook(backRow, 0, color);
        board[backRow][7] = new Rook(backRow, 7, color);
        board[backRow][1] = new Knight(backRow, 1, color);
        board[backRow][6] = new Knight(backRow, 6, color);
        board[backRow][2] = new Bishop(backRow, 2, color);
        board[backRow][5] = new Bishop(backRow, 5, color);
        board[backRow][3] = new Queen(backRow, 3, color);
        board[backRow][4] = new King(backRow, 4, color);
    }

    private boolean isPathClear(int startRow, int startCol, int endRow, int endCol) {
        int rowStep = Integer.compare(endRow, startRow);
        int colStep = Integer.compare(endCol, startCol);
        int currentRow = startRow + rowStep;
        int currentCol = startCol + colStep;
        while (currentRow != endRow || currentCol != endCol) {
            if (board[currentRow][currentCol] != null) return false;
            currentRow += rowStep;
            currentCol += colStep;
        }
        return true;
    }

    private boolean isKingInCheck(Color kingColor) {
        int[] kingPos = (kingColor == Color.WHITE) ? whiteKingPos : blackKingPos;
        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                if (board[r][c] != null && board[r][c].color != kingColor) {
                    if (isValidMoveWithoutCheckTest(r, c, kingPos[0], kingPos[1])) return true;
                }
            }
        }
        return false;
    }

    private void updateKingPosition(Color color, int row, int col) {
        if (color == Color.WHITE) {
            whiteKingPos[0] = row;
            whiteKingPos[1] = col;
        } else {
            blackKingPos[0] = row;
            blackKingPos[1] = col;
        }
    }

    private boolean isValidMoveWithoutCheckTest(int startRow, int startCol, int endRow, int endCol) {
        ChessPiece piece = board[startRow][startCol];
        if (piece == null) return false;
        if (board[endRow][endCol] != null && board[endRow][endCol].color == piece.color) return false;
        if (piece instanceof Pawn) {
            int direction = (piece.color == Color.WHITE) ? -1 : 1;
            int startRank = (piece.color == Color.WHITE) ? 6 : 1;
            if (startCol == endCol) {
                if (board[endRow][endCol] != null) return false;
                if (startRow == startRank && endRow == startRow + 2 * direction) {
                    return board[startRow + direction][startCol] == null;
                }
                return endRow == startRow + direction;
            } else if (Math.abs(startCol - endCol) == 1) {
                return endRow == startRow + direction && 
                       board[endRow][endCol] != null && 
                       board[endRow][endCol].color != piece.color;
            }
            return false;
        }
        if (!piece.isValidMove(endRow, endCol)) return false;
        if (piece instanceof Rook || piece instanceof Bishop || piece instanceof Queen) {
            return isPathClear(startRow, startCol, endRow, endCol);
        }
        return true;
    }

    private boolean canCastle(int startRow, int startCol, int endRow, int endCol) {
        ChessPiece king = board[startRow][startCol];
        if (!(king instanceof King)) return false;
        boolean isWhite = king.color == Color.WHITE;
        int homeRow = isWhite ? 7 : 0;
        if (startRow != homeRow || startCol != 4 || endRow != homeRow || Math.abs(endCol - startCol) != 2) return false;
        if (!castlingRights.canCastle(king.color, endCol == 6)) return false;
        if ((isWhite && whiteKingInCheck) || (!isWhite && blackKingInCheck)) return false;
        int rookCol = (endCol == 2) ? 0 : 7;
        ChessPiece rook = board[homeRow][rookCol];
        if (rook == null || !(rook instanceof Rook) || rook.color != king.color) return false;
        return isPathClear(startRow, startCol, startRow, rookCol);
    }

    private ChessPiece createPromotionPiece(char type, int row, int col, Color color) {
        switch (Character.toLowerCase(type)) {
            case 'q': return new Queen(row, col, color);
            case 'r': return new Rook(row, col, color);
            case 'b': return new Bishop(row, col, color);
            case 'n': return new Knight(row, col, color);
            default: return new Queen(row, col, color); // Default to Queen
        }
    }
    
    private void resetGame() {
        board = new ChessPiece[SIZE][SIZE];
        initializeBoard();
        whiteTurn = true;
        selectedRow = selectedCol = -1;
        legalMoveCache.clear();
        whiteKingInCheck = blackKingInCheck = false;
        castlingRights.reset();
        
        // Make AI's first move if player is black
        if (playingAgainstAI && !aiIsBlack) {
            makeAIMove();
        }
        
        updateCheckStatus();
        chessBoard.draw();
    }
    
    @Override
    public void stop() throws Exception {
        // Close Stockfish engine when the application closes
        if (stockfish != null) {
            stockfish.close();
        }
        
        // Clean up network resources
        if (networkManager != null) {
            networkManager.cleanup();
        }
        
        super.stop();
    }
    
    private void makeAIMove() {
        // Convert board to FEN notation for Stockfish
        String fen = stockfish.boardToFEN(board, whiteTurn, castlingRights);
        
        // Get best move from Stockfish
        String bestMove = stockfish.getBestMove(fen);
        if (bestMove != null && bestMove.length() >= 4) {
            // Parse Stockfish move
            int startCol = bestMove.charAt(0) - 'a';
            int startRow = 8 - Character.getNumericValue(bestMove.charAt(1));
            int endCol = bestMove.charAt(2) - 'a';
            int endRow = 8 - Character.getNumericValue(bestMove.charAt(3));
            
            // Handle promotion if move length is 5
            char promotionPiece = (bestMove.length() >= 5) ? bestMove.charAt(4) : ' ';
            
            // Make the move
            movePiece(startRow, startCol, endRow, endCol, promotionPiece);
            
            updateCheckStatus();
            chessBoard.draw();
        }
    }
    
    private boolean movePiece(int startRow, int startCol, int endRow, int endCol, char promotionType) {
        ChessPiece piece = board[startRow][startCol];
        if (piece == null) return false;
        
        // Check if it's the correct player's turn
        boolean isWhitePiece = piece.color == Color.WHITE;
        if ((whiteTurn && !isWhitePiece) || (!whiteTurn && isWhitePiece)) return false;
        
        // Check for castling move
        if (piece instanceof King && Math.abs(startCol - endCol) == 2) {
            if (!canCastle(startRow, startCol, endRow, endCol)) return false;
            
            // Move the king
            board[endRow][endCol] = board[startRow][startCol];
            board[startRow][startCol] = null;
            board[endRow][endCol].row = endRow;
            board[endRow][endCol].col = endCol;
            
            // Move the rook
            int rookStartCol = (endCol == 2) ? 0 : 7;
            int rookEndCol = (endCol == 2) ? 3 : 5;
            board[endRow][rookEndCol] = board[endRow][rookStartCol];
            board[endRow][rookStartCol] = null;
            board[endRow][rookEndCol].row = endRow;
            board[endRow][rookEndCol].col = rookEndCol;
            
            // Update king position
            updateKingPosition(piece.color, endRow, endCol);
            
            // Mark king and rook as moved for castling rights
            castlingRights.markKingMoved(piece.color);
            castlingRights.markRookMoved(piece.color, rookStartCol == 0);
            
            switchTurn();
            return true;
        }
        
        // Regular move validation
        if (!isValidMoveWithoutCheckTest(startRow, startCol, endRow, endCol)) return false;
        
        // Temporarily make the move to check if it would put the king in check
        ChessPiece capturedPiece = board[endRow][endCol];
        board[endRow][endCol] = piece;
        board[startRow][startCol] = null;
        piece.row = endRow;
        piece.col = endCol;
        
        // Update king position if king is moving
        boolean isKing = piece instanceof King;
        int[] originalKingPos = null;
        if (isKing) {
            originalKingPos = isWhitePiece ? whiteKingPos.clone() : blackKingPos.clone();
            updateKingPosition(piece.color, endRow, endCol);
        }
        
        // Check if the move would leave the king in check
        boolean kingInCheck = isKingInCheck(piece.color);
        
        // Undo the move if it would leave the king in check
        if (kingInCheck) {
            board[startRow][startCol] = piece;
            board[endRow][endCol] = capturedPiece;
            piece.row = startRow;
            piece.col = startCol;
            
            // Restore king position if it was moved
            if (isKing) {
                if (isWhitePiece) {
                    whiteKingPos = originalKingPos;
                } else {
                    blackKingPos = originalKingPos;
                }
            }
            
            return false;
        }
        
        // Update castling rights if king or rook moved
        if (piece instanceof King) {
            castlingRights.markKingMoved(piece.color);
        } else if (piece instanceof Rook) {
            if (startRow == (isWhitePiece ? 7 : 0)) {
                castlingRights.markRookMoved(piece.color, startCol == 0);
            }
        }
        
        // Handle pawn promotion
        if (piece instanceof Pawn && (endRow == 0 || endRow == 7)) {
            if (promotionType != ' ') {
                board[endRow][endCol] = createPromotionPiece(promotionType, endRow, endCol, piece.color);
            } else {
                // Default to queen if no promotion type specified
                board[endRow][endCol] = new Queen(endRow, endCol, piece.color);
            }
        }
        
        switchTurn();
        if (playingOnline && isConnected()) {
            networkManager.sendMove(startRow, startCol, endRow, endCol, promotionType);
            isMyTurn = false; // Wait for opponent's move
        }
        
        // Return the result from the original implementation
        return true; // or the actual result
    }
    
    private void switchTurn() {
        whiteTurn = !whiteTurn;
        if (playingOnline) {
            isMyTurn = !isMyTurn;
        }
        statusLabel.setText(whiteTurn ? "White's turn" : "Black's turn");
        
        // If it's AI's turn, make AI move after a short delay
        if (playingAgainstAI && 
            ((whiteTurn && !aiIsBlack) || (!whiteTurn && aiIsBlack))) {
            // Use a small delay to allow UI to update first
            Timeline timeline = new Timeline(new KeyFrame(
                Duration.millis(500),
                ae -> makeAIMove()));
            timeline.play();
        }
    }
    
    private void updateCheckStatus() {
        whiteKingInCheck = isKingInCheck(Color.WHITE);
        blackKingInCheck = isKingInCheck(Color.BLACK);
        
        // Stop any existing animation
        kingFlashAnimation.stop();
        
        // Check game status
        if (whiteKingInCheck || blackKingInCheck) {
            // Start flashing animation for king in check
            kingFlashAnimation.play();
            
            // Check for checkmate
            boolean isCheckmate = isCheckmate(whiteKingInCheck ? Color.WHITE : Color.BLACK);
            if (isCheckmate) {
                String winner = whiteKingInCheck ? "Black" : "White";
                statusLabel.setText("Checkmate! " + winner + " wins!");
                showGameOverDialog(winner + " wins by checkmate!");
            } else {
                statusLabel.setText((whiteKingInCheck ? "White" : "Black") + " is in check!");
            }
        } else {
            // Check for stalemate
            boolean whiteStalemate = isStalemate(Color.WHITE);
            boolean blackStalemate = isStalemate(Color.BLACK);
            
            if ((whiteTurn && whiteStalemate) || (!whiteTurn && blackStalemate)) {
                statusLabel.setText("Stalemate! Game ends in a draw.");
                showGameOverDialog("Draw by stalemate!");
            }
        }
    }
    
    private boolean isCheckmate(Color kingColor) {
        if (!isKingInCheck(kingColor)) return false;
        
        // Try all possible moves for all pieces of this color
        for (int startRow = 0; startRow < SIZE; startRow++) {
            for (int startCol = 0; startCol < SIZE; startCol++) {
                ChessPiece piece = board[startRow][startCol];
                if (piece != null && piece.color == kingColor) {
                    // Try all possible destination squares
                    for (int endRow = 0; endRow < SIZE; endRow++) {
                        for (int endCol = 0; endCol < SIZE; endCol++) {
                            // Skip same position
                            if (startRow == endRow && startCol == endCol) continue;
                            
                            // Try this move
                            ChessPiece capturedPiece = board[endRow][endCol];
                            int originalRow = piece.row;
                            int originalCol = piece.col;
                            
                            // Skip if move is not valid
                            if (!isValidMoveWithoutCheckTest(startRow, startCol, endRow, endCol)) continue;
                            
                            // Make the move temporarily
                            board[endRow][endCol] = piece;
                            board[startRow][startCol] = null;
                            piece.row = endRow;
                            piece.col = endCol;
                            
                            // Update king position if king moved
                            int[] originalKingPos = null;
                            if (piece instanceof King) {
                                originalKingPos = (kingColor == Color.WHITE) ? whiteKingPos.clone() : blackKingPos.clone();
                                updateKingPosition(kingColor, endRow, endCol);
                            }
                            
                            // Check if king is still in check
                            boolean stillInCheck = isKingInCheck(kingColor);
                            
                            // Undo the move
                            board[startRow][startCol] = piece;
                            board[endRow][endCol] = capturedPiece;
                            piece.row = originalRow;
                            piece.col = originalCol;
                            
                            // Restore king position if moved
                            if (piece instanceof King) {
                                if (kingColor == Color.WHITE) {
                                    whiteKingPos = originalKingPos;
                                } else {
                                    blackKingPos = originalKingPos;
                                }
                            }
                            
                            // If this move gets out of check, it's not checkmate
                            if (!stillInCheck) return false;
                        }
                    }
                }
            }
        }
        
        // If no move can get the king out of check, it's checkmate
        return true;
    }
    
    private boolean isStalemate(Color color) {
        if (isKingInCheck(color)) return false;
        
        // Check if any legal move exists
        for (int startRow = 0; startRow < SIZE; startRow++) {
            for (int startCol = 0; startCol < SIZE; startCol++) {
                ChessPiece piece = board[startRow][startCol];
                if (piece != null && piece.color == color) {
                    // Try all possible destination squares
                    for (int endRow = 0; endRow < SIZE; endRow++) {
                        for (int endCol = 0; endCol < SIZE; endCol++) {
                            // Skip same position
                            if (startRow == endRow && startCol == endCol) continue;
                            
                            // Check if move is valid and wouldn't put king in check
                            if (!isValidMoveWithoutCheckTest(startRow, startCol, endRow, endCol)) continue;
                            
                            // Make temporary move
                            ChessPiece capturedPiece = board[endRow][endCol];
                            board[endRow][endCol] = piece;
                            board[startRow][startCol] = null;
                            int originalRow = piece.row;
                            int originalCol = piece.col;
                            piece.row = endRow;
                            piece.col = endCol;
                            
                            // Update king position if king moved
                            int[] originalKingPos = null;
                            if (piece instanceof King) {
                                originalKingPos = (color == Color.WHITE) ? whiteKingPos.clone() : blackKingPos.clone();
                                updateKingPosition(color, endRow, endCol);
                            }
                            
                            // Check if king would be in check
                            boolean kingInCheck = isKingInCheck(color);
                            
                            // Undo the move
                            board[startRow][startCol] = piece;
                            board[endRow][endCol] = capturedPiece;
                            piece.row = originalRow;
                            piece.col = originalCol;
                            
                            // Restore king position if moved
                            if (piece instanceof King) {
                                if (color == Color.WHITE) {
                                    whiteKingPos = originalKingPos;
                                } else {
                                    blackKingPos = originalKingPos;
                                }
                            }
                            
                            // If this move doesn't put the king in check, it's a legal move
                            if (!kingInCheck) return false;
                        }
                    }
                }
            }
        }
        
        // If no legal move exists, it's stalemate
        return true;
    }
    
    private void showGameOverDialog(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Game Over");
        alert.setHeaderText(null);
        alert.setContentText(message);
        
        ButtonType playAgainButton = new ButtonType("Play Again");
        ButtonType quitButton = new ButtonType("Quit");
        alert.getButtonTypes().setAll(playAgainButton, quitButton);
        
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent()) {
            if (result.get() == playAgainButton) {
                resetGame();
            } else {
                System.exit(0);
            }
        }
    }
    
    private void handleSquareClicked(int row, int col) {
        // Check if it's AI's turn
        if (playingAgainstAI && 
            ((whiteTurn && !aiIsBlack) || (!whiteTurn && aiIsBlack))) {
            // It's AI's turn, ignore clicks
            return;
        }
        
        // In online play, only allow clicks if it's the player's turn
        if (playingOnline && !isMyTurn) {
            return;
        }
        
        if (selectedRow == -1 && selectedCol == -1) {
            // No piece selected yet
            ChessPiece piece = board[row][col];
            if (piece != null && ((whiteTurn && piece.color == Color.WHITE) || (!whiteTurn && piece.color == Color.BLACK))) {
                selectedRow = row;
                selectedCol = col;
                
                // Calculate legal moves for the selected piece
                calculateLegalMoves(row, col);
                
                chessBoard.draw();
            }
        } else {
            // A piece is already selected
            if (row == selectedRow && col == selectedCol) {
                // Deselect the piece
                selectedRow = selectedCol = -1;
                legalMoveCache.clear();
                chessBoard.draw();
            } else {
                // Try to move the piece
                ChessPiece piece = board[selectedRow][selectedCol];
                boolean isPawn = piece instanceof Pawn;
                boolean isPawnPromotion = isPawn && (row == 0 || row == 7);
                
                if (isPawnPromotion && legalMoveCache.contains(new Point2D(row, col))) {
                    // Show promotion dialog for pawn only if it's a legal move
                    showPromotionDialog(selectedRow, selectedCol, row, col);
                } else {
                    if (movePiece(selectedRow, selectedCol, row, col, ' ')) {
                        // Move was successful
                        selectedRow = selectedCol = -1;
                        legalMoveCache.clear();
                        updateCheckStatus();
                        chessBoard.draw();
                    } else {
                        // Invalid move, check if selecting a new piece
                        ChessPiece newPiece = board[row][col];
                        if (newPiece != null && ((whiteTurn && newPiece.color == Color.WHITE) || (!whiteTurn && newPiece.color == Color.BLACK))) {
                            selectedRow = row;
                            selectedCol = col;
                            calculateLegalMoves(row, col);
                        } else {
                            selectedRow = selectedCol = -1;
                            legalMoveCache.clear();
                        }
                        chessBoard.draw();
                    }
                }
            }
        }
    }
    
    private void calculateLegalMoves(int row, int col) {
        legalMoveCache.clear();
        ChessPiece piece = board[row][col];
        if (piece == null) return;
        
        for (int endRow = 0; endRow < SIZE; endRow++) {
            for (int endCol = 0; endCol < SIZE; endCol++) {
                // Skip same position
                if (row == endRow && col == endCol) continue;
                
                // Check if move is valid without putting king in check
                if (isValidMoveWithoutCheckTest(row, col, endRow, endCol)) {
                    // Temporarily make the move
                    ChessPiece capturedPiece = board[endRow][endCol];
                    board[endRow][endCol] = piece;
                    board[row][col] = null;
                    int originalRow = piece.row;
                    int originalCol = piece.col;
                    piece.row = endRow;
                    piece.col = endCol;
                    
                    // Update king position if king moved
                    int[] originalKingPos = null;
                    if (piece instanceof King) {
                        originalKingPos = (piece.color == Color.WHITE) ? whiteKingPos.clone() : blackKingPos.clone();
                        updateKingPosition(piece.color, endRow, endCol);
                    }
                    
                    // Check if king would be in check
                    boolean kingInCheck = isKingInCheck(piece.color);
                    
                    // Undo the move
                    board[row][col] = piece;
                    board[endRow][endCol] = capturedPiece;
                    piece.row = originalRow;
                    piece.col = originalCol;
                    
                    // Restore king position if moved
                    if (piece instanceof King) {
                        if (piece.color == Color.WHITE) {
                            whiteKingPos = originalKingPos;
                        } else {
                            blackKingPos = originalKingPos;
                        }
                    }
                    
                    // If this move doesn't put the king in check, it's a legal move
                    if (!kingInCheck) {
                        legalMoveCache.add(new Point2D(endRow, endCol));
                    }
                }
            }
        }
        
        // Check for castling moves
        if (piece instanceof King) {
            // Kingside castling
            if (canCastle(row, col, row, col + 2)) {
                legalMoveCache.add(new Point2D(row, col + 2));
            }
            // Queenside castling
            if (canCastle(row, col, row, col - 2)) {
                legalMoveCache.add(new Point2D(row, col - 2));
            }
        }
    }
    
    private void showPromotionDialog(int startRow, int startCol, int endRow, int endCol) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Pawn Promotion");
        alert.setHeaderText("Choose a piece to promote your pawn to:");
        
        ButtonType queenButton = new ButtonType("Queen");
        ButtonType rookButton = new ButtonType("Rook");
        ButtonType bishopButton = new ButtonType("Bishop");
        ButtonType knightButton = new ButtonType("Knight");
        
        alert.getButtonTypes().setAll(queenButton, rookButton, bishopButton, knightButton);
        
        Optional<ButtonType> result = alert.showAndWait();
        char promotionType = 'q'; // Default to queen
        
        if (result.isPresent()) {
            ButtonType choice = result.get();
            if (choice == rookButton) promotionType = 'r';
            else if (choice == bishopButton) promotionType = 'b';
            else if (choice == knightButton) promotionType = 'n';
        }
        
        if (movePiece(startRow, startCol, endRow, endCol, promotionType)) {
            selectedRow = selectedCol = -1;
            legalMoveCache.clear();
            updateCheckStatus();
            chessBoard.draw();
        }
    }
    private void startHosting() {
        try {
            int port = Integer.parseInt(portField.getText());
            resetGame();
            playingOnline = true;
            playingAgainstAI = false;
            whiteTurn = true;  // Host plays as white
            isMyTurn = true;
            networkManager.startServer(port);
            logGameMessage("Starting server on port " + port);
            logGameMessage("Waiting for opponent to connect...");
        } catch (NumberFormatException e) {
            logGameMessage("Invalid port number");
        }
    }
    
    private void joinGame() {
        try {
            String address = ipAddressField.getText();
            int port = Integer.parseInt(portField.getText());
            resetGame();
            playingOnline = true;
            playingAgainstAI = false;
            whiteTurn = true;  // Game starts with white's turn
            isMyTurn = false;  // Client plays as black, waits for first move
            networkManager.connectToServer(address, port);
            logGameMessage("Connecting to " + address + ":" + port);
        } catch (NumberFormatException e) {
            logGameMessage("Invalid port number");
        }
    }

    private void logGameMessage(String message) {
        if (gameLogArea != null) {
            gameLogArea.appendText(message + "\n");
        }
    }
    
    private boolean isConnected() {
        return networkManager != null && networkManager.isConnected();
    }
    
    
    private class ChessBoard extends StackPane {
        private Canvas canvas;
        private GraphicsContext gc;
        private boolean highlightKing = false;
        
        ChessBoard() {
            canvas = new Canvas(SIZE * SQUARE_SIZE, SIZE * SQUARE_SIZE);
            gc = canvas.getGraphicsContext2D();
            getChildren().add(canvas);
            
            canvas.setOnMouseClicked(this::handleMouseClick);
            setAlignment(Pos.CENTER);
        }
        
        void toggleKingHighlight() {
            highlightKing = !highlightKing;
            draw();
        }
        
        private void handleMouseClick(MouseEvent event) {
            int col = (int) (event.getX() / SQUARE_SIZE);
            int row = (int) (event.getY() / SQUARE_SIZE);
            
            // Convert visual position to logical position based on board orientation
            int logicalRow = boardFlipped ? (SIZE - 1 - row) : row;
            int logicalCol = boardFlipped ? (SIZE - 1 - col) : col;
            
            if (logicalRow >= 0 && logicalRow < SIZE && logicalCol >= 0 && logicalCol < SIZE) {
                handleSquareClicked(logicalRow, logicalCol);
            }
        }
        
        void draw() {
            gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
            
            // Draw border around the board
            gc.setFill(currentTheme.getBoardBorder());
            double borderSize = SQUARE_SIZE * 0.1;
            gc.fillRect(-borderSize, -borderSize, 
                        SIZE * SQUARE_SIZE + 2 * borderSize, 
                        SIZE * SQUARE_SIZE + 2 * borderSize);
            
            // Draw the board
            for (int row = 0; row < SIZE; row++) {
                for (int col = 0; col < SIZE; col++) {
                    // Calculate visual position based on flip status
                    int visualRow = boardFlipped ? (SIZE - 1 - row) : row;
                    int visualCol = boardFlipped ? (SIZE - 1 - col) : col;
                    
                    boolean isLight = (row + col) % 2 == 0;
                    gc.setFill(isLight ? currentTheme.getLightSquare() : currentTheme.getDarkSquare());
                    
                    Color textColor = isLight ? currentTheme.getDarkSquare() : currentTheme.getLightSquare();
                    gc.setFill(textColor);
                    
                    // Highlight selected square
                    if (row == selectedRow && col == selectedCol) {
                        gc.setFill(Color.YELLOW);
                    }
                    
                    // Highlight legal moves
                    if (legalMoveCache.contains(new Point2D(row, col))) {
                        // Use golden highlight for blue theme, keep lightblue for other themes
                        if (currentTheme.getName().equalsIgnoreCase("Blue")) {
                            gc.setFill(Color.GOLD);  // Or use Color.web("#FFD700") for true gold
                        } else {
                            gc.setFill(Color.LIGHTBLUE);
                        }
                    }
                    
                    // Highlight king in check
                    if (highlightKing && board[row][col] instanceof King) {
                        if ((board[row][col].color == Color.WHITE && whiteKingInCheck) ||
                            (board[row][col].color == Color.BLACK && blackKingInCheck)) {
                            gc.setFill(Color.RED);
                        }
                    }
                    
                    gc.fillRect(visualCol * SQUARE_SIZE, visualRow * SQUARE_SIZE, SQUARE_SIZE, SQUARE_SIZE);
                    
                    // Draw piece if present
                    if (board[row][col] != null) {
                        ChessPiece piece = board[row][col];
                        String colorName = piece.color == Color.WHITE ? "White" : "Black";
                        String pieceName = "";
                        
                        if (piece instanceof Pawn) pieceName = "Pawn";
                        else if (piece instanceof Rook) pieceName = "Rook";
                        else if (piece instanceof Knight) pieceName = "Knight";
                        else if (piece instanceof Bishop) pieceName = "Bishop";
                        else if (piece instanceof Queen) pieceName = "Queen";
                        else if (piece instanceof King) pieceName = "King";
                        
                        Image pieceImage = pieceImages.get(pieceName + colorName);
                        if (pieceImage != null) {
                            gc.drawImage(pieceImage, visualCol * SQUARE_SIZE, visualRow * SQUARE_SIZE);
                        } else {
                            // Fallback to manual drawing if image not found
                            piece.draw(gc, visualCol * SQUARE_SIZE, visualRow * SQUARE_SIZE);
                        }
                    }
                    
                    // Draw board coordinates
                    gc.setFill(isLight ? Color.DARKGREEN : Color.BEIGE);
                    if (visualRow == SIZE - 1) {
                        // The file labels (a-h)
                        char file = (char)('a' + (boardFlipped ? (SIZE - 1 - col) : col));
                        gc.fillText(String.valueOf(file), 
                                    (visualCol * SQUARE_SIZE) + SQUARE_SIZE - 10, 
                                    (visualRow * SQUARE_SIZE) + SQUARE_SIZE - 2);
                    }
                    if (visualCol == 0) {
                        // The rank labels (1-8)
                        int rank = boardFlipped ? (row + 1) : (8 - row);
                        gc.fillText(String.valueOf(rank), 
                                    (visualCol * SQUARE_SIZE) + 2, 
                                    (visualRow * SQUARE_SIZE) + 12);
                    }
                }
            }
        }
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}