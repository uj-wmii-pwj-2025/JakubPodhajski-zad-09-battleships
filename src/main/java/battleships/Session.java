package battleships;

import java.net.*;
import java.io.*;
import java.util.*;
import java.nio.charset.StandardCharsets;
class Session {
    private final Socket socket;
    private String playerMap;
    private final boolean isClient;

    public Session(Socket socket, String playerMap, boolean isClient) {
        this.socket = socket;
        this.playerMap = playerMap;
        this.isClient = isClient;
    }

    public void start() {

        try {

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
            PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));
            BufferedReader stdReader = new BufferedReader(new InputStreamReader(System.in));
            socket.setSoTimeout(10000);

            Game game = new Game();
            Printer printer = new Printer();
            String enemyMap = ("" + Cells.UNKNOWN.getSymbol()).repeat(100);

            printer.printHello();
            printer.printBoards(playerMap, enemyMap);
            String userInput = "";
            if (isClient) {
                System.out.println("take a shoot!");
                userInput = stdReader.readLine();
                if (!(validateUserInput(userInput))) {
                    System.out.println("Wrong input");
                    userInput = stdReader.readLine();
                }

                String start = "start;" + userInput;

                printWriter.println(start);
                printWriter.flush();
            }

            String response;

            String lastMessageSent = "";
            int attempts = 0;
            while (attempts < 3) {
                try {

                    response = bufferedReader.readLine();
                    if (response == null) break;
                    attempts = 0;
                    if (response.equals("ostatni zatopiony")) {
                        printer.printHello();
                        printer.printBoards(playerMap, enemyMap);
                        System.out.println("Wygrana");
                        break;
                    }
                    System.out.println(response);
                    if (!validateResposne(response)) throw new IOException("invalid response");

                    String[] splited = response.split(";");
                    Command responseCommand = findCommand(splited[0]);
                    int responseTarget = game.getField(splited[1]);

                    Command shootResult = game.shoot(playerMap, splited[1]);

                    playerMap = adjustPlayerMap(playerMap, shootResult, responseTarget);

                    if (!userInput.isEmpty()) {
                        enemyMap = adjustEnemyMap(enemyMap, responseCommand, game.getField(userInput));
                    }

                    printer.printBoards(playerMap, enemyMap);
                    System.out.println("take a shoot!");

                    userInput = stdReader.readLine();
                    while (!validateUserInput(userInput)) {
                        System.out.println("Wrong input");
                        userInput = stdReader.readLine();
                    }

                    if (shootResult == Command.ZATOPIONY) {
                        printWriter.println("trafiony zatopiony" + ";" + userInput);
                        lastMessageSent = "trafiony zatopiony" + ";" + userInput;
                    } else if (shootResult == Command.OSTATNI) {
                        printWriter.println("ostatni zatopiony");
                        lastMessageSent = "ostatni zatopiony";

                    } else if (shootResult == Command.PUDLO) {
                        printWriter.println("pudło" + ";" + userInput);
                        lastMessageSent = "pudło" + ";" + userInput;

                    } else {
                        printWriter.println(shootResult.toString().toLowerCase() + ";" + userInput);
                        lastMessageSent = shootResult.toString().toLowerCase() + ";" + userInput;
                    }

                    printWriter.flush();
                    printer.printHello();
                    printer.printBoards(playerMap, enemyMap);
                    Thread.sleep(2000);

                    if (shootResult == Command.ZATOPIONY) {
                        System.out.println("Przegrana");
                        break;
                    }

                } catch (SocketTimeoutException e) {
                    System.out.println("Brak odpowiedzi, ponawiam wysyłanie...");
                    attempts++;
                    if (!lastMessageSent.isEmpty()) {
                        printWriter.println(lastMessageSent);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                    break;
                }
            }
            if (attempts == 3){
                System.out.println("Błąd komunikacji");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    private static boolean properCoordinates(char letter, char number){
        return (!(letter > 'J' || letter < 'A' || number > '9' || number < '0'));

    }
    private boolean validateUserInput(String input) throws IOException {
        if (input.isEmpty()) return false;
        if (input.length() != 2) return false;

        return properCoordinates(input.charAt(0), input.charAt(1));
    }
    private boolean validateResposne(String input)  {
        if (input.isEmpty()) return false;
        String[] splited = input.split(";");
        if (splited.length != 2) return false;
        String command = splited[0];
        String coordinates = splited[1];
        if (!properCoordinates(coordinates.charAt(0), coordinates.charAt(1))) return false;
        return findCommand(command) != null;
    }

    private Command findCommand(String command){
        return switch (command) {
            case "pudło" -> Command.PUDLO;
            case "trafiony" -> Command.TRAFIONY;
            case "ostatni" -> Command.OSTATNI;
            case "start" -> Command.START;
            case "trafiony zatopiony" -> Command.ZATOPIONY;
            default -> null;
        };
    }

    private String adjustPlayerMap(String Map, Command result, int target){
        return switch (result) {
            case Command.TRAFIONY -> Map.substring(0, target) + Cells.HIT.getSymbol() + Map.substring(target + 1);
            case Command.PUDLO -> Map.substring(0, target) + Cells.MISS.getSymbol() + Map.substring(target + 1);
            case ZATOPIONY -> Map.substring(0, target) + Cells.HIT.getSymbol() + Map.substring(target + 1);
            case OSTATNI -> Map.substring(0, target) + Cells.HIT.getSymbol() + Map.substring(target + 1);
            default -> Map;//start
        };
    }
    private String adjustEnemyMap(String Map, Command result, int target){
        return switch (result) {
            case Command.TRAFIONY -> Map.substring(0, target) + Cells.SHIP.getSymbol() + Map.substring(target + 1);
            case Command.PUDLO -> Map.substring(0, target) + Cells.EMPTY.getSymbol() + Map.substring(target + 1);
            case ZATOPIONY, OSTATNI -> {
                Map = Map.substring(0, target) + Cells.SHIP.getSymbol() + Map.substring(target + 1);

                yield surroundSunkShip(Map, target);
            }
            default -> Map;//start
        };
    }

    private String surroundSunkShip(String Map, int target) {
        int width = 10;
        StringBuilder sb = new StringBuilder(Map);

        char shipSymbol = Cells.SHIP.getSymbol();
        char emptySymbol = Cells.EMPTY.getSymbol();

        Set<Integer> shipIndices = new HashSet<>();
        Queue<Integer> toCheck = new LinkedList<>();

        toCheck.add(target);
        shipIndices.add(target);
        int[][] orthogonal = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};

        while (!toCheck.isEmpty()) {
            int current = toCheck.poll();
            int r = current / width;
            int c = current % width;

            for (int[] move : orthogonal) {
                int nr = r + move[0];
                int nc = c + move[1];

                if (nr >= 0 && nr < width && nc >= 0 && nc < width) {
                    int neighborIdx = nr * width + nc;

                    if (sb.charAt(neighborIdx) == shipSymbol && !shipIndices.contains(neighborIdx)) {
                        shipIndices.add(neighborIdx);
                        toCheck.add(neighborIdx);
                    }
                }
            }
        }

        int[][] surroundings = {
                {-1, -1}, {-1, 0}, {-1, 1},
                {0, -1},           {0, 1},
                {1, -1},  {1, 0},  {1, 1}
        };

        for (int shipIndex : shipIndices) {
            int r = shipIndex / width;
            int c = shipIndex % width;

            for (int[] sur : surroundings) {
                int nr = r + sur[0];
                int nc = c + sur[1];

                if (nr >= 0 && nr < width && nc >= 0 && nc < width) {
                    int neighborIdx = nr * width + nc;

                    if (sb.charAt(neighborIdx) != shipSymbol) {
                        sb.setCharAt(neighborIdx, emptySymbol);
                    }
                }
            }
        }

        return sb.toString();
    }




}


