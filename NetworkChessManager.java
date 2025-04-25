package chesspkg;

import java.io.*;
import java.net.*;
import java.util.concurrent.*;
import javafx.application.Platform;

public class NetworkChessManager {
    private ServerSocket serverSocket;
    private Socket clientSocket;
    private ObjectOutputStream outputStream;
    private ObjectInputStream inputStream;
    private ExecutorService executor;
    private boolean isConnected = false;
    private boolean isServer = false;
    
    // Callback interface for game events
    public interface GameEventListener {
        void onMoveReceived(int startRow, int startCol, int endRow, int endCol, char promotionType);
        void onConnectionEstablished(boolean isWhite);
        void onConnectionLost();
        void onGameMessage(String message);
    }
    
    private GameEventListener listener;
    
    public NetworkChessManager(ChessGame game) {
        this.game = game;
        this.executor = Executors.newCachedThreadPool();
    }
    
    public void setEventListener(GameEventListener listener) {
        this.listener = listener;
    }
    
    public boolean isConnected() {
        return isConnected;
    }
    
    public boolean isServer() {
        return isServer;
    }
    
    // Start a server on the specified port
    public void startServer(int port) {
        isServer = true;
        executor.submit(() -> {
            try {
                serverSocket = new ServerSocket(port);
                Platform.runLater(() -> {
                    if (listener != null) {
                        listener.onGameMessage("Server started. Waiting for opponent to connect...");
                        listener.onGameMessage("Your IP address: " + getLocalIpAddress());
                    }
                });
                
                clientSocket = serverSocket.accept();
                setupStreams();
                
                Platform.runLater(() -> {
                    isConnected = true;
                    if (listener != null) {
                        listener.onConnectionEstablished(true); // Server plays as white
                        listener.onGameMessage("Player connected! You are playing as WHITE.");
                    }
                });
                
                startReceiving();
            } catch (IOException e) {
                Platform.runLater(() -> {
                    if (listener != null) {
                        listener.onGameMessage("Server error: " + e.getMessage());
                    }
                });
            }
        });
    }
    
    // Connect to a server at the specified address and port
    public void connectToServer(String address, int port) {
        isServer = false;
        executor.submit(() -> {
            try {
                Platform.runLater(() -> {
                    if (listener != null) {
                        listener.onGameMessage("Connecting to server at " + address + ":" + port + "...");
                    }
                });
                
                clientSocket = new Socket(address, port);
                setupStreams();
                
                Platform.runLater(() -> {
                    isConnected = true;
                    if (listener != null) {
                        listener.onConnectionEstablished(false); // Client plays as black
                        listener.onGameMessage("Connected to server! You are playing as BLACK.");
                    }
                });
                
                startReceiving();
            } catch (IOException e) {
                Platform.runLater(() -> {
                    if (listener != null) {
                        listener.onGameMessage("Connection error: " + e.getMessage());
                    }
                });
            }
        });
    }
    
    // Set up input and output streams
    private void setupStreams() throws IOException {
        outputStream = new ObjectOutputStream(clientSocket.getOutputStream());
        outputStream.flush();
        inputStream = new ObjectInputStream(clientSocket.getInputStream());
    }
    
    // Send a move to the opponent
    public void sendMove(int startRow, int startCol, int endRow, int endCol, char promotionType) {
        if (!isConnected) return;
        
        executor.submit(() -> {
            try {
                ChessMove move = new ChessMove(startRow, startCol, endRow, endCol, promotionType);
                outputStream.writeObject(move);
                outputStream.flush();
            } catch (IOException e) {
                handleDisconnection();
            }
        });
    }
    
    // Start receiving messages from the opponent
    private void startReceiving() {
        executor.submit(() -> {
            try {
                while (isConnected) {
                    Object receivedObject = inputStream.readObject();
                    
                    if (receivedObject instanceof ChessMove) {
                        ChessMove move = (ChessMove) receivedObject;
                        Platform.runLater(() -> {
                            if (listener != null) {
                                listener.onMoveReceived(
                                    move.startRow, 
                                    move.startCol, 
                                    move.endRow, 
                                    move.endCol, 
                                    move.promotionType
                                );
                            }
                        });
                    }
                }
            } catch (IOException | ClassNotFoundException e) {
                handleDisconnection();
            }
        });
    }
    
    // Handle disconnection events
    private void handleDisconnection() {
        Platform.runLater(() -> {
            isConnected = false;
            if (listener != null) {
                listener.onConnectionLost();
                listener.onGameMessage("Connection with opponent has been lost.");
            }
        });
        
        cleanup();
    }
    
    // Clean up resources
    public void cleanup() {
        isConnected = false;
        
        try {
            if (inputStream != null) inputStream.close();
            if (outputStream != null) outputStream.close();
            if (clientSocket != null) clientSocket.close();
            if (serverSocket != null) serverSocket.close();
        } catch (IOException e) {
            System.err.println("Error during cleanup: " + e.getMessage());
        }
    }
    
    // Get local IP address to display for server host
    private String getLocalIpAddress() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            return "Could not determine IP address";
        }
    }
    
    // Class to represent a chess move for network transmission
    public static class ChessMove implements Serializable {
        private static final long serialVersionUID = 1L;
        
        public int startRow;
        public int startCol;
        public int endRow;
        public int endCol;
        public char promotionType;
        
        public ChessMove(int startRow, int startCol, int endRow, int endCol, char promotionType) {
            this.startRow = startRow;
            this.startCol = startCol;
            this.endRow = endRow;
            this.endCol = endCol;
            this.promotionType = promotionType;
        }
    }
}