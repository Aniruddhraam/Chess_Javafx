package chesspkg;

import java.io.*;
import java.net.*;
import java.util.Enumeration;
import java.util.concurrent.*;
import javafx.application.Platform;

/**
 * Manages network communications for a multiplayer chess game.
 * Handles server creation, client connections, and transmitting chess moves
 * between players over a network.
 */
public class NetworkChessManager {
    private ServerSocket serverSocket;
    private Socket clientSocket;
    private ObjectOutputStream outputStream;
    private ObjectInputStream inputStream;
    private ExecutorService executor;
    private boolean isConnected = false;
    private boolean isServer = false;
    private ChessGame game;
    
    /**
     * Interface for game event callbacks.
     * Implementers can receive notifications about network events and game state changes.
     */
    public interface GameEventListener {
        /**
         * Called when a move is received from the opponent.
         * 
         * @param startRow The starting row of the move
         * @param startCol The starting column of the move
         * @param endRow The destination row of the move
         * @param endCol The destination column of the move
         * @param promotionType The piece type for pawn promotion, or 0 if not a promotion
         */
        void onMoveReceived(int startRow, int startCol, int endRow, int endCol, char promotionType);
        
        /**
         * Called when a connection with another player is established.
         * 
         * @param isWhite Whether this player will play as white
         */
        void onConnectionEstablished(boolean isWhite);
        
        /**
         * Called when the connection with the opponent is lost.
         */
        void onConnectionLost();
        
        /**
         * Called when there is a game-related message to display.
         * 
         * @param message The message to display
         */
        void onGameMessage(String message);
    }
    
    private GameEventListener listener;
    
    /**
     * Creates a new NetworkChessManager.
     * 
     * @param game The ChessGame instance that this network manager will control
     */
    public NetworkChessManager(ChessGame game) {
        this.game = game;
        this.executor = Executors.newCachedThreadPool();
    }
    
    /**
     * Sets the event listener for network and game events.
     * 
     * @param listener The listener to receive event callbacks
     */
    public void setEventListener(GameEventListener listener) {
        this.listener = listener;
    }
    
    /**
     * Checks if the manager is currently connected to another player.
     * 
     * @return true if connected, false otherwise
     */
    public boolean isConnected() {
        return isConnected;
    }
    
    /**
     * Checks if this instance is acting as the server.
     * 
     * @return true if this is the server, false if it's a client
     */
    public boolean isServer() {
        return isServer;
    }
    
    /**
     * Starts a chess server on the specified port.
     * The server will wait for a client to connect and then establish
     * a game connection. The server always plays as white.
     * 
     * @param port The port number to listen on
     */
    public void startServer(int port) {
        isServer = true;
        executor.submit(() -> {
            try {
                // Get the correct IP address first
                String ipAddress = getLocalIpAddress();
                // Bind specifically to this address
                serverSocket = new ServerSocket(port, 50, InetAddress.getByName(ipAddress));
                
                Platform.runLater(() -> {
                    if (listener != null) {
                        listener.onGameMessage("Server started. Waiting for opponent to connect...");
                        listener.onGameMessage("Your IP address: " + ipAddress);
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
    
    /**
     * Connects to a chess server at the specified address and port.
     * The client always plays as black.
     * 
     * @param address The server's IP address or hostname
     * @param port The server's port number
     */
    public void connectToServer(String address, int port) {
        isServer = false;
        executor.submit(() -> {
            try {
                Platform.runLater(() -> {
                    if (listener != null) {
                        listener.onGameMessage("Connecting to server at " + address + ":" + port + "...");
                    }
                });
                
                // Add more detailed error handling
                try {
                    clientSocket = new Socket();
                    // Set a reasonable timeout
                    clientSocket.connect(new InetSocketAddress(address, port), 5000);
                } catch (ConnectException ce) {
                    throw new IOException("Connection refused: The server might not be running or the IP/port might be incorrect");
                } catch (SocketTimeoutException ste) {
                    throw new IOException("Connection timed out: The server might be behind a firewall or network address translation");
                }
                
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
    
    public void testConnection(String address, int port) {
        executor.submit(() -> {
            try {
                Socket testSocket = new Socket();
                testSocket.connect(new InetSocketAddress(address, port), 5000);
                Platform.runLater(() -> {
                    if (listener != null) {
                        listener.onGameMessage("Test connection successful!");
                    }
                });
                testSocket.close();
            } catch (Exception e) {
                Platform.runLater(() -> {
                    if (listener != null) {
                        listener.onGameMessage("Test connection failed: " + e.getMessage());
                    }
                });
            }
        });
    }
    
    /**
     * Sets up input and output streams for network communication.
     * This method must be called after establishing a connection.
     * 
     * @throws IOException If an I/O error occurs during stream creation
     */
    private void setupStreams() throws IOException {
        outputStream = new ObjectOutputStream(clientSocket.getOutputStream());
        outputStream.flush();
        inputStream = new ObjectInputStream(clientSocket.getInputStream());
    }
    
    /**
     * Sends a chess move to the opponent.
     * 
     * @param startRow The starting row of the move
     * @param startCol The starting column of the move
     * @param endRow The destination row of the move
     * @param endCol The destination column of the move
     * @param promotionType The piece type for pawn promotion, or 0 if not a promotion
     */
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
    
    /**
     * Starts a background thread to receive moves from the opponent.
     * This method should be called after connection is established.
     */
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
    
    /**
     * Handles disconnection events by updating status
     * and notifying listeners.
     */
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
    
    /**
     * Cleans up resources used by the network connection.
     * Should be called when the game ends or when disconnected.
     */
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
    
    /**
     * Gets the local IP address of this machine.
     * Useful for displaying to the user so they can share it
     * with their opponent.
     * 
     * @return The local IP address as a string
     */
    private String getLocalIpAddress() {
        try {
            // Try to find a non-loopback address
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface networkInterface = interfaces.nextElement();
                if (networkInterface.isUp() && !networkInterface.isLoopback()) {
                    Enumeration<InetAddress> addresses = networkInterface.getInetAddresses();
                    while (addresses.hasMoreElements()) {
                        InetAddress addr = addresses.nextElement();
                        if (addr instanceof Inet4Address) {
                            return addr.getHostAddress();
                        }
                    }
                }
            }
            
            // Fall back to the default method if no suitable address is found
            return InetAddress.getLocalHost().getHostAddress();
        } catch (Exception e) {
            return "Could not determine IP address";
        }
    }
    
    /**
     * Represents a chess move for network transmission.
     * This class is serializable to allow sending over object streams.
     */
    public static class ChessMove implements Serializable {
        private static final long serialVersionUID = 1L;
        
        /** The starting row of the move */
        public int startRow;
        
        /** The starting column of the move */
        public int startCol;
        
        /** The destination row of the move */
        public int endRow;
        
        /** The destination column of the move */
        public int endCol;
        
        /** The piece type for pawn promotion, or 0 if not a promotion */
        public char promotionType;
        
        /**
         * Creates a new chess move.
         * 
         * @param startRow The starting row of the move
         * @param startCol The starting column of the move
         * @param endRow The destination row of the move
         * @param endCol The destination column of the move
         * @param promotionType The piece type for pawn promotion, or 0 if not a promotion
         */
        public ChessMove(int startRow, int startCol, int endRow, int endCol, char promotionType) {
            this.startRow = startRow;
            this.startCol = startCol;
            this.endRow = endRow;
            this.endCol = endCol;
            this.promotionType = promotionType;
        }
    }
}