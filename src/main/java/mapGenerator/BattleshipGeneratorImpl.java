package mapGenerator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

class BattleshipGeneratorImpl implements BattleshipGenerator {
    private static final int BOARD_SIZE = 10;
    private static final char EMPTY_CELL = '.';
    private static final char SHIP_CELL = '#';
    private static final int[] FLEET_SIZES = {4, 3, 3, 2, 2, 2, 1, 1, 1, 1};

    private static final int DIRECTION_UP = 0;
    private static final int DIRECTION_DOWN = 1;
    private static final int DIRECTION_LEFT = 2;
    private static final int DIRECTION_RIGHT = 3;

    private final Random random = new Random();

    @Override
    public String generateMap() {
        char[][] map = new char[BOARD_SIZE][BOARD_SIZE];

        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                map[i][j] = EMPTY_CELL;
            }
        }

        boolean[][] occupied = new boolean[BOARD_SIZE][BOARD_SIZE];

        for (int i = 0; i < BOARD_SIZE; i++) {
            while (true){
                if (placeShip(map,occupied,FLEET_SIZES[i])){
                    break;
                }
            }
        }
        String anws = new String();

        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                anws += map[i][j];
            }
        }

        return anws;
    }
    private boolean placeShip(char[][] map, boolean[][] occupied, int length){

        int ship_start[] = randomProperPoint(map, occupied);
        int ship[][] = new int[length][2];
        ship[0] = ship_start;
        for (int i = 1; i < length; i++) {
            if (!continueBuildingShip(map, occupied, ship, i)) return false;
        }

        for (int i = 0; i < length; i++) {
            map[ship[i][0]][ship[i][1]] = SHIP_CELL;
            occupySurrounding(ship[i][0], ship[i][1], occupied);
        }

        return true;
    }
    private void occupySurrounding(int row, int col, boolean[][] occupied){
        occupied[row][col] = true;
        if (row > 0) occupied[row-1][col] = true;
        if (row < 9) occupied[row+1][col] = true;
        if (col > 0) occupied[row][col-1] = true;
        if (col < 9) occupied[row][col+1] = true;
        if (row > 0 && col > 0) occupied[row-1][col-1] = true;
        if (row > 0 && col < 9) occupied[row-1][col+1] = true;
        if (row < 9 && col > 0) occupied[row+1][col-1] = true;
        if (row < 9 && col < 9) occupied[row+1][col+1] = true;
    }

    private int[] randomProperPoint(char[][] map, boolean[][] occupied){
        int[] result = new int[2];
        while (true){
            result[0] = random.nextInt(BOARD_SIZE);
            result[1] = random.nextInt(BOARD_SIZE);

            if (!occupied[result[0]][result[1]]) return result;
        }
    }
    private boolean continueBuildingShip(char[][] map, boolean[][] occupied, int[][] ship, int shipLength){
        int[] newestField = ship[shipLength - 1];

        List<Integer> possibleDirections = new ArrayList<>(Arrays.asList(0, 1, 2, 3));

        while (!possibleDirections.isEmpty()){
            int randomIndex = random.nextInt(possibleDirections.size());
            int direction = possibleDirections.get(randomIndex);

            int newRow = newestField[0];
            int newCol = newestField[1];

            switch(direction) {
                case DIRECTION_UP: newRow--; break;
                case DIRECTION_DOWN: newRow++; break;
                case DIRECTION_LEFT: newCol--; break;
                case DIRECTION_RIGHT: newCol++; break;
            }

            if (newRow >= 0 && newRow < BOARD_SIZE &&
                    newCol >= 0 && newCol < BOARD_SIZE) {

                if (!occupied[newRow][newCol]) {
                    boolean alreadyInShip = false;

                    for (int i = 0; i < shipLength; i++) {
                        if (ship[i][0] == newRow && ship[i][1] == newCol) {
                            alreadyInShip = true;
                            break;
                        }
                    }

                    if (!alreadyInShip) {
                        ship[shipLength][0] = newRow;
                        ship[shipLength][1] = newCol;
                        return true;
                    }
                }
            }
            possibleDirections.remove(randomIndex);
        }
        return false;
    }
}