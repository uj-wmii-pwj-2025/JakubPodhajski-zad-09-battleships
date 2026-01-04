package battleships;

class Printer {
    public void printHello() {
        System.out.println("▗▄▄▖ ▗▞▀▜▌   ■▗▄▄▄▖█ ▗▞▀▚▖ ▗▄▄▖▐▌   ▄ ▄▄▄▄   ▄▄▄");
        System.out.println("▐▌ ▐▌▝▚▄▟▌▗▄▟▙▄▖█  █ ▐▛▀▀▘▐▌   ▐▌   ▄ █   █ ▀▄▄");
        System.out.println("▐▛▀▚▖       ▐▌  █  █ ▝▚▄▄▖ ▝▀▚▖▐▛▀▚▖█ █▄▄▄▀ ▄▄▄▀");
        System.out.println("▐▙▄▞▘       ▐▌  █  █      ▗▄▄▞▘▐▌ ▐▌█ █");
        System.out.println("            ▐▌                        ▀");
    }
    public void printBoards(String playerMap, String enemyMap) {
        System.out.println(" Player board:   |   Enemy board:");
        for (int i = 0; i < 100; i+=10) {
            System.out.println("  " + playerMap.substring(i, i+10) + "     |    " + enemyMap.substring(i, i+10));
        }
    }
}