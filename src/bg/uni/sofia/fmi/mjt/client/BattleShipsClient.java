package bg.uni.sofia.fmi.mjt.client;


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class BattleShipsClient {
    private Socket socket;
    private DataOutputStream dos;
    private DataInputStream dis;
    private boolean shouldStop;
    private static final String HOST = "localhost";
    private static final int PORT = 22222;
    private static Scanner in = new Scanner(System.in);

    public BattleShipsClient(Socket socket) throws IOException {
        this.socket = socket;
        this.dos = new DataOutputStream(this.socket.getOutputStream());
        this.dis = new DataInputStream(this.socket.getInputStream());
        this.shouldStop = false;
    }


    public void sendName(String name) throws IOException {
        dos.writeUTF("Name: " + name);
        String response = dis.readUTF();
        System.out.println(response);
    }


    public static void main(String[] args) throws IOException {
        Socket socket = new Socket(HOST, PORT);
        BattleShipsClient client = new BattleShipsClient(socket);
        System.out.println("Connect sucessfully");
        String name = "";
        if (args[1] != null) {
            name = args[1];
        }
        client.sendName(name);

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (!client.shouldStop) {
                    try {
                        String response = client.dis.readUTF();
                        System.out.println(response);
                        if (response.equals("You have quit the game")) {
                            System.out.println("Press any key to continue");
                            client.shouldStop = true;
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }
        }).start();

        while (!client.shouldStop) {
            String command = in.nextLine();
            client.dos.writeUTF(command);
        }
        client.dos.close();
        client.dis.close();
        socket.close();

    }

}
