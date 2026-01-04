package battleships;


import java.util.*;
import java.io.*;
import mapGenerator.*;


public class Main{
    public static void main(String[] args) throws Exception{
        BattleshipGenerator battleshipGenerator = BattleshipGenerator.defaultInstance();
        String map = battleshipGenerator.generateMap();
        Mode mode = null;
        int port = 3333;
        String host = "";
        for (int i = 0; i < args.length; i++) {
            switch (args[i]){
                case "-mode":
                    if (args[i+1].equals("client")){
                        mode = Mode.Client;
                    }
                    else if (args[i+1].equals("server")){
                        mode = Mode.Server;
                    }
                    else{
                        throw new Exception("Invalid mode");
                    }
                    break;
                case "-port":
                    port = Integer.parseInt(args[i+1]);
                    break;
                case "-map":
                    String mapFilePath = args[i+1];
                    java.nio.file.Path path = java.nio.file.Path.of(mapFilePath);

                    if (java.nio.file.Files.exists(path)) {
                        map = java.nio.file.Files.readString(path).replaceAll("\\s+", "");
                        if (map.length() != 100) {
                            throw new Exception("nieprawidłowa długość mapy");
                        }
                    } else {
                        throw new Exception("Nie znaleziono pliku mapy: " + mapFilePath);
                    }
                    break;
                case "-host":
                    host = args[i+1];
                    break;
                default:
                    throw new Exception("Invalid argument");
            }
            i += 1;
        }
        if (mode == null) throw new Exception("Mode not specified");
        if (port < 0 || port > 65535) throw new Exception("Invalid port");
        if (host.isEmpty() && mode == Mode.Client) throw new Exception("Host not specified");
        if (mode == Mode.Client) Client.startClient(port, host, map);
        else Server.startServer(port, map);
    }

    private enum Mode{
        Client,
        Server
    }

}

