package battleships;

import java.util.*;

//handles game logic
class Game {
    public Command shoot(String map, String coordinates){
        int point;
        if ((point = getField(coordinates)) == -1){
            System.out.println("Wrong coordinates");
            return null;
        }

        if (map.charAt(point) == Cells.SHIP.getSymbol() || map.charAt(point) == Cells.HIT.getSymbol()){
            if (!isSunk(map, coordinates)){
                return Command.TRAFIONY;
            } else if (map.indexOf(Cells.SHIP.getSymbol()) == -1) {
                return Command.OSTATNI;
            }
            return Command.ZATOPIONY;
        }
        return Command.PUDLO;
    }

    public int getField(String coordinates){
        if (coordinates.length() != 2){
            return -1;
        }
        String[] split = coordinates.split("");
        if (!properCoordinates(split[0].charAt(0), split[1].charAt(0))){
            return -1;
        }
        return (split[0].charAt(0) - 'A') * 10 + (split[1].charAt(0) - '0');
    }

    private boolean properCoordinates(char letter, char number){
        return (!(letter > 'J' || letter < 'A' || number > '9' || number < '0'));

    }


    private boolean isSunk(String map, String hitCoordinates) {
        Set<String> visited = new HashSet<>();
        Queue<String> toCheck = new LinkedList<>();

        toCheck.add(hitCoordinates);
        visited.add(hitCoordinates);

        while (!toCheck.isEmpty()) {
            String current = toCheck.poll();

            for (String neighbor : getOrthogonalNeighbors(current)) {
                if (visited.contains(neighbor)) {
                    continue;
                }

                int point = getField(neighbor);
                if (point == -1) continue;

                char symbol = map.charAt(point);

                if (symbol == Cells.SHIP.getSymbol()) {
                    return false;
                }

                else if (symbol == Cells.HIT.getSymbol()) {
                    visited.add(neighbor);
                    toCheck.add(neighbor);
                }
            }
        }

        return true;
    }

    private List<String> getOrthogonalNeighbors(String coordinates) {
        List<String> neighbors = new ArrayList<>();
        String[] split = coordinates.split("");
        char letter = split[0].charAt(0);
        char number = split[1].charAt(0);

        char[][] deltas = {
                {letter, (char)(number - 1)}, // UP
                {letter, (char)(number + 1)}, // DOWN
                {(char)(letter - 1), number}, // LEFT
                {(char)(letter + 1), number}  // RIGHT
        };

        for (char[] pair : deltas) {
            if (properCoordinates(pair[0], pair[1])) {
                neighbors.add("" + pair[0] + pair[1]);
            }
        }
        return neighbors;
    }
}