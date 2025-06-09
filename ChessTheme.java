import javafx.scene.paint.Color;
class ChessTheme {
    private String name;
    private Color lightSquare;
    private Color darkSquare;
    private Color boardBorder;
    
    public static final ChessTheme[] PREDEFINED_THEMES = {
        new ChessTheme("Classic", Color.WHEAT, Color.SADDLEBROWN, Color.BLACK),
        new ChessTheme("Blue", Color.LIGHTBLUE, Color.DARKBLUE, Color.BLACK),
        new ChessTheme("Green", Color.LIGHTGREEN, Color.DARKGREEN, Color.BLACK),
        new ChessTheme("Gray", Color.LIGHTGRAY, Color.DARKGRAY, Color.BLACK)
    };
    
    public ChessTheme(String name, Color lightSquare, Color darkSquare, Color boardBorder) {
        this.name = name;
        this.lightSquare = lightSquare;
        this.darkSquare = darkSquare;
        this.boardBorder = boardBorder;
    }
    
    public String getName() {
        return name;
    }
    
    public Color getLightSquare() {
        return lightSquare;
    }
    
    public Color getDarkSquare() {
        return darkSquare;
    }
    
    public Color getBoardBorder() {
        return boardBorder;
    }
    
    @Override
    public String toString() {
        return name;
    }
}