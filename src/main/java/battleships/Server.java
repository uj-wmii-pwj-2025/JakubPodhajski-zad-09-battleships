package battleships;


import java.net.ServerSocket;
import java.net.Socket;
import java.io.*;

class Server{
    public static void startServer(int port, String playerMap) {
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println("Server started");
            Socket socket = serverSocket.accept();
            System.out.println("Client connected");
            Session session = new Session(socket, playerMap, false);
            session.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

