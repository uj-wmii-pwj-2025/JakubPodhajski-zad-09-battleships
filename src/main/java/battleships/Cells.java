package battleships;
public enum Cells {
    EMPTY('.'),
    SHIP('#'),
    HIT('@'),
    MISS('~'),
    UNKNOWN('?');

    private final char symbol;

    Cells(char symbol) {
        this.symbol = symbol;
    }
    public char getSymbol() {
        return symbol;
    }

}

//private static final char EMPTY_CELL = '.';
//private static final char SHIP_CELL = '#';
//private static final char HIT_CELL = '@';
//private static final char MISS_CELL = '~';
//private static final char UNKNOWN_CELL = '?';