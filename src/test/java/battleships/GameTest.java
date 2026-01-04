package battleships;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class GameTest {

    // Helper: tworzy pustą mapę 10x10 (100 kropek)
    private String createEmptyMap() {
        return ".".repeat(100);
    }

    // Helper: wstawia statek na mapę w podanym punkcie (indeksie)
    private String placeShip(String map, int index) {
        StringBuilder sb = new StringBuilder(map);
        sb.setCharAt(index, '#'); // # to Cells.SHIP
        return sb.toString();
    }

    // Helper: oznacza trafienie na mapie (symuluje zmianę mapy po strzale)
    private String placeHit(String map, int index) {
        StringBuilder sb = new StringBuilder(map);
        sb.setCharAt(index, '@'); // @ to Cells.HIT
        return sb.toString();
    }

    @Test
    void testShootMiss() {
        Game game = new Game();
        String map = createEmptyMap(); // Pusta mapa

        // Strzelamy w A0 (pierwsze pole), gdzie jest pusto
        Command result = game.shoot(map, "A0");

        assertEquals(Command.PUDLO, result, "Strzał w puste pole powinien zwrócić PUDLO");
    }

    @Test
    void testShootHit() {
        Game game = new Game();
        String map = createEmptyMap();

        // Ustawiamy statek 2-masztowy na A0 i A1 (indeksy 0 i 1)
        map = placeShip(map, 0);
        map = placeShip(map, 1);

        // Strzelamy w A0
        Command result = game.shoot(map, "A0");

        // Oczekujemy TRAFIONY, bo statek ma jeszcze nienaruszoną część w A1
        assertEquals(Command.TRAFIONY, result, "Trafienie w fragment statku powinno zwrócić TRAFIONY");
    }

    @Test
    void testShootSunk() {
        Game game = new Game();
        String map = createEmptyMap();

        // Sytuacja: Statek był na A0 i A1.
        // A0 zostało już trafione wcześniej (jest oznaczone jako HIT '@')
        map = placeHit(map, 0);
        // A1 jest nadal statkiem ('#')
        map = placeShip(map, 1);

        // Strzelamy w ostatni fragment (A1)
        Command result = game.shoot(map, "A1");

        // Oczekujemy ZATOPIONY (lub OSTATNI, jeśli to jedyny statek na mapie)
        // Uwaga: Twoja logika 'OSTATNI' może wymagać, aby na mapie nie było innych '#'.
        // Tutaj 'map' w momencie wywołania ma '#' na A1.
        assertTrue(result == Command.ZATOPIONY || result == Command.OSTATNI,
                "Zatopienie ostatniego fragmentu powinno zwrócić ZATOPIONY lub OSTATNI");
    }

    @Test
    void testInvalidCoordinates() {
        Game game = new Game();
        String map = createEmptyMap();

        // Strzał poza planszę
        Command result = game.shoot(map, "Z9");

        assertNull(result, "Nieprawidłowe współrzędne powinny zwrócić null (zgodnie z Twoim kodem)");
    }
}