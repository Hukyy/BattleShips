package bg.uni.sofia.fmi.mjt.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.HashMap;
import java.util.Map;

public class Server {
    public static Map<String, Player> players = new HashMap<>();
    public static Map<String, Game> games = new HashMap<>();
    public static Map<String, Game> loadableGames = new HashMap<>();

    public static void main(String[] args) {

        try (ServerSocket serverSocket = new ServerSocket(22222)) {

            System.out.println("Socket opened " + serverSocket);
            while (true) {

                Player player = new Player(serverSocket.accept());
                new Thread(player).start();

            }

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("You couldn't start the server");
        }

    }
}
