Chess Game
A JavaFX-based chess game application featuring AI opponents, network play, and customizable themes.
Features

Play against AI with adjustable difficulty levels (Easy, Medium, Hard, Expert).
Play online with another player over a network.
Customizable board themes (e.g., Classic, Blue) and dark mode.
Flip board functionality to switch perspectives.
Game logging and status updates.
Pawn promotion dialog for choosing the promotion piece.
Check and checkmate detection with visual indicators.
Stalemate detection.

Screenshots

Installation

Ensure you have the Java Development Kit (JDK) 8 or later installed.
Download and set up the JavaFX SDK if it’s not included in your JDK.
Clone this repository:git clone https://github.com/yourusername/chess-game.git


Navigate to the project directory:cd chess-game


Compile the application:javac -cp .;path/to/javafx-sdk/lib/* chesspkg/ChessGame.java


Run the application:java -cp .;path/to/javafx-sdk/lib/* chesspkg.ChessGame



Note: Replace path/to/javafx-sdk/lib/* with the actual path to your JavaFX SDK library. On Windows, use semicolons (;) in the classpath; on Unix-based systems, use colons (:).
Usage

Starting a New Game: Click the "New Game" button to reset the board and start a new game.
Game Modes:
Play against AI: Check the "Play against AI" checkbox and select the AI difficulty from the dropdown.
Network Play: Enter the IP address and port to host or join a game.


Settings:
Choose your player color (White or Black) from the "Play as" dropdown.
Select a board theme from the "Board Theme" dropdown.
Toggle dark mode with the "Toggle Dark Mode" button.
Flip the board perspective with the "Flip Board" button.


Making Moves:
Click on a piece to select it.
Click on a valid destination square to move the piece.
For pawn promotion, a dialog will appear to choose the promotion piece (Queen, Rook, Bishop, or Knight).


Special Moves:
Castling: Move the king two squares towards a rook, and the rook will move to the square next to the king (if castling conditions are met).



Controls

Mouse Click: Select and move pieces.
Control Panel: Adjust game settings, themes, and network options.

Network Play

Hosting a Game:
Enter a port number (e.g., 8888) in the "Port" field and click "Host Game".
Share your IP address and port with the opponent.


Joining a Game:
Enter the host’s IP address and port in the respective fields, then click "Join Game".



AI Opponent

The AI uses the Stockfish chess engine.
Adjust the difficulty level via the "AI Difficulty" dropdown to change the AI’s search depth:
Easy: Depth 1
Medium: Depth 7
Hard: Depth 12
Expert: Depth 20



Themes and Customization

Board Themes: Choose from predefined themes like Classic, Blue, etc., via the "Board Theme" dropdown.
Dark Mode: Toggle between light and dark modes using the "Toggle Dark Mode" button.

Development

Contributions are welcome! Please fork the repository and submit pull requests.
Report issues or suggest features via the GitHub issues page.

License
This project is licensed under the MIT License.
Acknowledgements

Stockfish Chess Engine for AI functionality.
JavaFX for the graphical user interface.

