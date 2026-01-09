package battleships;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class GameTest {

    private String createEmptyMap() {
        return ".".repeat(100);
    }

    private String placeShip(String map, int index) {
        StringBuilder sb = new StringBuilder(map);
        sb.setCharAt(index, '#');
        return sb.toString();
    }

    private String placeHit(String map, int index) {
        StringBuilder sb = new StringBuilder(map);
        sb.setCharAt(index, '@');
        return sb.toString();
    }

    @Test
    void testShootMiss() {
        Game game = new Game();
        String map = createEmptyMap(); 

        Command result = game.shoot(map, "A0");

        assertEquals(Command.PUDLO, result, "Strzał w puste pole powinien zwrócić PUDLO");
    }

    @Test
    void testShootHit() {
        Game game = new Game();
        String map = createEmptyMap();

        map = placeShip(map, 0);
        map = placeShip(map, 1);

        Command result = game.shoot(map, "A0");

        assertEquals(Command.TRAFIONY, result, "Trafienie w fragment statku powinno zwrócić TRAFIONY");
    }

    @Test
    void testShootSunk() {
        Game game = new Game();
        String map = createEmptyMap();


        map = placeHit(map, 0);
        map = placeShip(map, 1);

        Command result = game.shoot(map, "A1");

        assertTrue(result == Command.ZATOPIONY || result == Command.OSTATNI,
                "Zatopienie ostatniego fragmentu powinno zwrócić ZATOPIONY lub OSTATNI");
    }

    @Test
    void testInvalidCoordinates() {
        Game game = new Game();
        String map = createEmptyMap();

        Command result = game.shoot(map, "Z9");

        assertNull(result, "Nieprawidłowe współrzędne powinny zwrócić null (zgodnie z Twoim kodem)");
    }
}
