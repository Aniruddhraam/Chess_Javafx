package chesspkg;

import javafx.scene.paint.Color;

public class ChessTheme {
    private final Color lightSquare;
    private final Color darkSquare;
    private final Color boardBorder;
    private final String name;
    
    public ChessTheme(String name, Color lightSquare, Color darkSquare, Color boardBorder) {
        this.name = name;
        this.lightSquare = lightSquare;
        this.darkSquare = darkSquare;
        this.boardBorder = boardBorder;
    }
    
    public Color getLightSquare() { return lightSquare; }
    public Color getDarkSquare() { return darkSquare; }
    public Color getBoardBorder() { return boardBorder; }
    public String getName() { return name; }
    
    @Override
    public String toString() { return name; }
    
    // Predefined themes
    public static final ChessTheme[] PREDEFINED_THEMES = {
        new ChessTheme("Classic", Color.BEIGE, Color.DARKGREEN, Color.BROWN),
        new ChessTheme("Modern", Color.LIGHTGRAY, Color.DARKGRAY, Color.BLACK),
        new ChessTheme("Blue", Color.LIGHTBLUE, Color.NAVY, Color.DARKBLUE),
        new ChessTheme("Red", Color.MISTYROSE, Color.DARKRED, Color.MAROON),
        new ChessTheme("Wood", Color.BURLYWOOD, Color.SADDLEBROWN, Color.SIENNA),
        new ChessTheme("High Contrast", Color.WHITE, Color.BLACK, Color.GRAY)
    };
}