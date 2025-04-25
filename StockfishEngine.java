package chesspkg;

import java.io.*;
import java.util.concurrent.TimeUnit;
import javafx.scene.paint.Color;

public class StockfishEngine {
    private Process engineProcess;
    private BufferedReader processReader;
    private BufferedWriter processWriter;
    private int searchDepth = 10; // Adjust based on desired difficulty
    
    public StockfishEngine() {
        try {
            // Path to Stockfish executable - update this to your path
            String stockfishPath = "C:\\Users\\Aniruddh\\OneDrive - Amrita vishwa vidyapeetham\\Personal\\Programming\\Java\\Sem 2\\stockfish\\stockfish-windows-x86-64-avx2.exe"; 
            
            // Use ProcessBuilder instead of Runtime.exec()
            ProcessBuilder processBuilder = new ProcessBuilder(stockfishPath);
            processBuilder.redirectErrorStream(true); // Merge error stream with input stream
            
            engineProcess = processBuilder.start();
            processReader = new BufferedReader(new InputStreamReader(engineProcess.getInputStream()));
            processWriter = new BufferedWriter(new OutputStreamWriter(engineProcess.getOutputStream()));
            
            // Initialize the engine
            sendCommand("uci");
            sendCommand("isready");
            waitForReady();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }    
    
    private void sendCommand(String command) {
        try {
            processWriter.write(command + "\n");
            processWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private String readLine() {
        try {
            return processReader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    private void waitForReady() {
        String line;
        try {
            while ((line = processReader.readLine()) != null) {
                if (line.equals("readyok")) break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    // Set search depth (difficulty level)
    public void setSearchDepth(int depth) {
        this.searchDepth = depth;
    }
    
    // Convert chess board to FEN notation including turn and castling rights
    public String boardToFEN(ChessPiece[][] board, boolean whiteTurn, CastlingRights castlingRights) {
        StringBuilder fen = new StringBuilder();
        
        // Board position
        for (int i = 0; i < 8; i++) {
            int emptyCount = 0;
            for (int j = 0; j < 8; j++) {
                ChessPiece piece = board[i][j];
                if (piece == null) {
                    emptyCount++;
                } else {
                    if (emptyCount > 0) {
                        fen.append(emptyCount);
                        emptyCount = 0;
                    }
                    char pieceChar = getPieceChar(piece);
                    fen.append(pieceChar);
                }
            }
            if (emptyCount > 0) {
                fen.append(emptyCount);
            }
            if (i < 7) fen.append('/');
        }
        
        // Active color
        fen.append(whiteTurn ? " w " : " b ");
        
        // Castling availability
        StringBuilder castling = new StringBuilder();
        if (castlingRights.canCastle(Color.WHITE, true)) castling.append("K");
        if (castlingRights.canCastle(Color.WHITE, false)) castling.append("Q");
        if (castlingRights.canCastle(Color.BLACK, true)) castling.append("k");
        if (castlingRights.canCastle(Color.BLACK, false)) castling.append("q");
        
        if (castling.length() == 0) {
            fen.append("- ");
        } else {
            fen.append(castling).append(" ");
        }
        
        // En passant target square (not implemented yet)
        fen.append("- ");
        
        // Halfmove clock and fullmove number
        fen.append("0 1");
        
        return fen.toString();
    }
    
    private char getPieceChar(ChessPiece piece) {
        char pieceChar = ' ';
        if (piece instanceof Pawn) pieceChar = 'p';
        else if (piece instanceof Rook) pieceChar = 'r';
        else if (piece instanceof Knight) pieceChar = 'n';
        else if (piece instanceof Bishop) pieceChar = 'b';
        else if (piece instanceof Queen) pieceChar = 'q';
        else if (piece instanceof King) pieceChar = 'k';
        
        return piece.color == Color.WHITE ? Character.toUpperCase(pieceChar) : pieceChar;
    }
    
    public String getBestMove(String fen) {
        sendCommand("position fen " + fen);
        sendCommand("go depth " + searchDepth);
        
        String line;
        String bestMove = null;
        
        try {
            while ((line = processReader.readLine()) != null) {
                if (line.startsWith("bestmove")) {
                    bestMove = line.split("\\s+")[1];
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return bestMove;
    }
    
    // Convert algebraic notation to board coordinates
    public int[] algebraicToCoords(String algebraic) {
        int col = algebraic.charAt(0) - 'a';
        int row = 8 - Character.getNumericValue(algebraic.charAt(1));
        return new int[]{row, col};
    }
    
    // Close the engine process
    public void close() {
        sendCommand("quit");
        try {
            if (!engineProcess.waitFor(5, TimeUnit.SECONDS)) {
                engineProcess.destroyForcibly();
            }
        } catch (InterruptedException e) {
            engineProcess.destroyForcibly();
        }
    }
}