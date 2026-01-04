package battleships;

import java.net.Socket;
import java.io.*;
import java.util.*;



class Client{
    public static void startClient(int port, String host, String playerMap) {

        try {
            Socket socket = new Socket(host, port);
            Session session = new Session(socket, playerMap, true);
            session.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

