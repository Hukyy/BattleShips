package bg.uni.sofia.fmi.mjt.server;

import bg.uni.sofia.fmi.mjt.battleships.Board;
import bg.uni.sofia.fmi.mjt.battleships.Cell;
import bg.uni.sofia.fmi.mjt.battleships.Coordinates;
import bg.uni.sofia.fmi.mjt.battleships.Ship;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;


public class Player implements Runnable {
    private Socket socket;
    private DataInputStream dis;
    private DataOutputStream dos;

    private Game game;
    private String nickname;
    private Board myBoard;
    private Board enemyBoard;
    private volatile boolean initializedBoard;
    private volatile boolean startedGame;
    private Player opponent;

    public Player(Socket socket) {
        this.socket = socket;

        try {
            dos = new DataOutputStream(this.socket.getOutputStream());
            dis = new DataInputStream(this.socket.getInputStream());
        } catch (IOException e) {
            System.out.println("Player died " + e.getMessage());
        }
    }


    public void setMyBoard(Board board) {
        this.myBoard = board;
    }

    public void setEnemyBoard(Board board) {
        this.enemyBoard = board;
    }

    public Board getMyBoard() {
        return myBoard;
    }

    public Board getEnemyBoard() {
        return enemyBoard;
    }

    public Player getOpponent() {
        return opponent;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    @Override
    public void run() {
        System.out.println("New player connected");
        while (!socket.isClosed()) {
            try {
                String command = dis.readUTF();
                if (command.startsWith("Name")) {
                    String name = command.substring(6);
                    setNickname(name);
                } else if (command.equalsIgnoreCase("help")) {
                    help();
                } else if (command.startsWith("create-game")) {
                    String[] parts = command.split("\\s+");
                    String name = null;
                    if (parts.length == 1) {
                        dos.writeUTF("You can't create a game without a name");
                        continue;
                    }
                    name = parts[1];
                    createNewGame(name);
                } else if (command.equalsIgnoreCase("list-games")) {
                    showGames();
                } else if (command.startsWith("join-game")) {
                    String[] parts = command.split("\\s+");
                    String gameName = parts.length == 1 ? null : parts[1];
                    joinGame(gameName);
                } else if (command.startsWith("start")) {
                    startGame();
                } else if (command.startsWith("hit") && command.length() > 4) {
                    String target = command.split("\\s+")[1];
                    hit(target);
                } else if (command.equals("leave-game")) {
                    leaveGame();
                } else if (command.equals("quit")) {
                    quit(false);
                    break;
                } else if (command.equals("save-game")) {
                    saveGame();
                } else if (command.startsWith("load-game")) {
                    String[] splitted = command.split("\\s+");
                    if (splitted.length != 2) {
                        dos.writeUTF("Please choose which game you want to load");
                        continue;
                    }
                    String name = splitted[1];
                    loadGame(name, "savedGames.txt");
                } else if (command.equalsIgnoreCase("show-my-games")) {
                    listMyGames("savedGames.txt");
                } else if (command.startsWith("delete-game")) {
                    String[] splitted = command.split("\\s+");
                    if (splitted.length != 2) {
                        dos.writeUTF("Please choose which game you want to delete");
                        continue;
                    }
                    String name = splitted[1];
                    deleteGame(name, "savedGames.txt");
                } else {
                    dos.writeUTF("Unknown command");
                }
            } catch (IOException | InterruptedException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    private void setNickname(String nickname) throws IOException {
        this.nickname = nickname;
        if (Server.players.containsKey(nickname)) {
            dos.writeUTF("Username " + nickname + " is already in use");
            quit(true);
        } else {
            Server.players.put(nickname, this);
            dos.writeUTF("Welcome, " + nickname);
            dos.writeUTF("type \"help\" to all the commands");
        }
    }

    private void help() throws IOException {
        for (AvailableCommands command : AvailableCommands.values()) {
            dos.writeUTF(command.toString());
        }
    }

    private void createNewGame(String gameName) throws IOException {
        String response = "";
        if (Server.games.containsKey(gameName)) {
            response = "This game exists";
        } else {
            Game game = new Game(gameName);
            game.getState().setCreator(this.nickname);
            Server.games.put(gameName, game);
            response = "Room has been created";
        }
        dos.writeUTF(response);
    }

    private void showGames() throws IOException {
        StringBuilder output = new StringBuilder();
        String format = "|%1$-15s|%2$-15s|%3$-15s|%4$-15s|\n";

        output.append(String.format(format, "NAME", "CREATOR", "STATUS", "PLAYERS"));
        output.append("|---------------+---------------+---------------+---------------|\n");
        for (Game game : Server.games.values()) {
            String status = game.getState().isStart() ? "In progress" : "pending";
            int numPlayers = 0;
            if (game.getPlayer1() != null) {
                numPlayers++;
            }
            if (game.getPlayer2() != null) {
                numPlayers++;
            }
            String players = numPlayers + "/2";
            output.append(String.format(format, game.getState().getName(), game.getState().getCreator(), status, players));
        }
        dos.writeUTF(output.toString());
    }

    private void joinGame(String gameName) throws IOException {
        if (game != null) {
            dos.writeUTF("You are already in a game. To join new game, please first leave the current one. Type leave-game");
            return;
        }
        Game game = null;
        String realGameName = null;
        if (gameName == null) {
            for (Game gameChecker : Server.games.values()) {
                if (gameChecker.isAvailable()) {
                    game = gameChecker;
                    realGameName = game.getState().getName();
                    break;
                }
            }
            if (game == null) {
                dos.writeUTF("There are not available games at the moment. Please, try again later.");
                return;
            }
        } else {
            game = Server.games.get(gameName);
            realGameName = gameName;
        }

        if (game == null) {
            dos.writeUTF("Game does not exist");
            return;
        }
        if (game.getPlayer1() == null) {
            game.registerFirstPlayer(this);
            dos.writeUTF("You joined the game " + realGameName);
        } else if (game.getPlayer2() == null) {
            game.registerSecondPlayer(this);
            dos.writeUTF("You joined the game " + realGameName);
        } else {
            dos.writeUTF("This room is full");
        }
    }

    private void startGame() throws IOException, InterruptedException {
        if (game == null) {
            dos.writeUTF("You are not in a game");
            return;
        }
        if (game.getPlayer1() == null || game.getPlayer2() == null) {
            dos.writeUTF("Not enough players");
            return;
        }

        if (game.getPlayer1() == this) {
            opponent = game.getPlayer2();
        } else if (game.getPlayer2() == this) {
            opponent = game.getPlayer1();
        }

        synchronized (game) {
            this.startedGame = true;
            if (!opponent.startedGame) {
                dos.writeUTF("Waiting for your opponent to start the game");
                game.wait(10000);
            } else {
                game.notifyAll();
            }
        }
        if (!opponent.startedGame) {
            this.startedGame = false;
            dos.writeUTF("Opponen't didnt start the game");
            leaveGame();
            return;
        }
        game.getState().setStart(true);

        dos.writeUTF("Start initializing your ships");
        initializeShips();
        this.initializedBoard = true;
        dos.writeUTF("done");
    }

    private void hit(String target) throws IOException {
        if (game == null) {
            dos.writeUTF("You are not in a game");
        } else if (opponent == null) {
            leaveGame();
        } else if (!opponent.initializedBoard) {
            dos.writeUTF("Your opponent hasn't put his ships yet");
        } else if (game.isGameover()) {
            if (myBoard.allShipsDead()) {
                dos.writeUTF("Game over. You lost!");
            } else {
                dos.writeUTF("You won!");
            }
        } else if (game.getCurrentPlayer() != this) {
            dos.writeUTF("It's not your turn");
        } else if (!game.validMove(this, target)) {

            dos.writeUTF(myBoard.print(false));
            dos.writeUTF(enemyBoard.print(true));
            dos.writeUTF("Invalid move. Try again");
        } else {

            dos.writeUTF(myBoard.print(false));
            dos.writeUTF(enemyBoard.print(true));
            dos.writeUTF("Successful attack");
            opponent.dos.writeUTF(enemyBoard.print(false));
            opponent.dos.writeUTF(myBoard.print(true));
            opponent.dos.writeUTF("Opponent last turn: " + target);

            Cell cell = enemyBoard.getCell(target);
            if (cell.getShip() == null) {
                dos.writeUTF("Unfortunately there wasn't enemy ship");
            } else {
                if (cell.getShip().isAlive()) {
                    dos.writeUTF("You hit an enemy ship");
                } else {
                    dos.writeUTF("You sunk an enemy ship");
                }
            }
            if (enemyBoard.allShipsDead()) {
                dos.writeUTF("You won!");
                opponent.dos.writeUTF("You lost!");
            } else {
                opponent.dos.writeUTF("It's your turn.");
            }
        }
    }

    private void leaveGame() throws IOException {
        if (game == null) {
            dos.writeUTF("You are not in a game");
            return;
        }
        myBoard = null;
        enemyBoard = null;
        initializedBoard = false;
        startedGame = false;
        if (opponent != null) {
            opponent.dos.writeUTF("Your opponent left the game. You are moved back to the lobby");
            opponent.opponent = null;
            opponent.leaveGame();
        }
        opponent = null;

        if (game.getPlayer1() == this) {
            game.setPlayer1(null);
            game.getState().setPlayerOneName(null);
            game.getState().clearFirstBoard();
        } else {
            game.setPlayer2(null);
            game.getState().setPlayerTwoName(null);
            game.getState().clearSecondBoard();
        }
        game.getState().setStart(false);
        game = null;
        dos.writeUTF("You left the game");
    }

    private void quit(boolean takenUsername) throws IOException {
        dos.writeUTF("You have quit the game");
        if (!takenUsername) {
            Server.players.remove(nickname);
        }
        dis.close();
        dos.close();
        socket.close();
    }

    private void saveGame() throws IOException {
        if (game == null) {
            dos.writeUTF("You are not in a game");
            return;
        } else if (!game.getState().isStart()) {
            dos.writeUTF("You are not in active game");
            return;
        }
        String filename = "savedGames.txt";

        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename, true));
        oos.writeObject(game.getState());
        oos.flush();
        oos.close();

        dos.writeUTF("Saved game");
    }

    private void loadGame(String name, String filename) throws ClassNotFoundException, IOException {
        if (game != null) {
            dos.writeUTF("You are already in a game. To load a game, please first leave the current one. Type leave-game");
            return;
        }
        GameState state;
        if (Server.loadableGames.get(name) == null) {
            if (!loadOnSever(name, filename)) {
                dos.writeUTF("Game doesn't exist");
                return;
            }
        }

        state = Server.loadableGames.get(name).getState();
        if (!state.getPlayerOneName().equalsIgnoreCase(nickname) && !state.getPlayerTwoName().equalsIgnoreCase(nickname)) {
            dos.writeUTF("This is not your game");
            return;
        }

        game = Server.loadableGames.get(state.getName());

        if (state.getPlayerOneName().equalsIgnoreCase(nickname)) {
            game.setPlayer1(this);
            myBoard = state.getBoard1();
            enemyBoard = state.getBoard2();
        } else if (state.getPlayerTwoName().equalsIgnoreCase(nickname)) {
            game.setPlayer2(this);
            myBoard = state.getBoard2();
            enemyBoard = state.getBoard1();
        }

        if (state.getCurrentPlayerName().equalsIgnoreCase(nickname)) {
            game.setCurrentPlayer(this);
        }

        synchronized (game) {
            if (game.getPlayer1() == null || game.getPlayer2() == null) {
                try {
                    dos.writeUTF("Waiting for your opponent to connect");
                    game.wait(15000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                game.notifyAll();
            }
            startedGame = true;
            initializedBoard = true;
        }
        if (game.getPlayer1() == null || game.getPlayer2() == null) {
            dos.writeUTF("Opponent didn't load the game");
            leaveGame();
            return;
        } else if (game.getPlayer1() == this) {
            opponent = game.getPlayer2();
        } else {
            opponent = game.getPlayer1();
        }
        dos.writeUTF("Successfully loaded the game");
        if (game.getCurrentPlayer() == this) {
            dos.writeUTF(myBoard.print(false));
            dos.writeUTF(enemyBoard.print(true));
            dos.writeUTF("Make your turn");
        } else {
            dos.writeUTF("It's your opponent's turn");
        }
    }

    private void listMyGames(String filename) throws IOException, ClassNotFoundException {
        List<GameState> states = readSavedGames(filename);
        boolean foundAny = false;
        for (GameState state : states) {
            if (state.getPlayerOneName().equalsIgnoreCase(nickname)) {
                dos.writeUTF("Game name: " + state.getName() + " playing vs " + state.getPlayerTwoName());
                foundAny = true;
            } else if (state.getPlayerTwoName().equalsIgnoreCase(nickname)) {
                dos.writeUTF("Game name: " + state.getName() + " playing vs " + state.getPlayerOneName());
                foundAny = true;
            }
        }
        if (!foundAny) {
            dos.writeUTF("You don't have any saved games");
        }
    }

    private void deleteGame(String gameName, String filename) throws IOException, ClassNotFoundException {
        List<GameState> states = readSavedGames(filename);
        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename)); //trunc file content
        for (GameState state : states) {
            if (!state.getName().equalsIgnoreCase(gameName)) {
                oos.writeObject(state);
            }
        }
        oos.close();
    }


    private void initializeShips() throws IOException {
        Harbor harbor = new Harbor();

        for (Ship ship : harbor.getShips()) {
            String input = null;
            dos.writeUTF(myBoard.print(false));
            dos.writeUTF("Please place your ship with size " + ship.getType().size +
                    "\nInsert starting coordinate A-J1-10 and direction (vertical/horizontal) Example: C6 horizontal");

            input = dis.readUTF();

            while (!validInput(input, ship)) {
                dos.writeUTF(myBoard.print(false));
                dos.writeUTF("Invalid input or you can't place that ship there. Try again.\nInsert starting coordinate A-J1-10 and direction (vertical/horizontal) Example: C6 horizontal");
                input = dis.readUTF();
            }
            String[] splitted = input.split("\\s+");
            String direction = splitted[1];
            if (direction.equalsIgnoreCase("horizontal")) {
                ship.setVertical(false);
            }
            Coordinates coordinates = new Coordinates(splitted[0]);
            myBoard.placeShip(coordinates, ship);
        }
        dos.writeUTF(myBoard.print(false));
    }

    private boolean validInput(String input, Ship ship) {
        String[] parts = input.split("\\s+");
        if (parts.length != 2) {
            return false;
        }
        Coordinates coordinates = new Coordinates(parts[0]);
        if (!coordinates.isValidCoordinate()) {
            return false;
        }
        String direction = parts[1];
        if (direction.equalsIgnoreCase("horizontal")) {
            ship.setVertical(false);
        } else if (direction.equalsIgnoreCase("vertical")) {
            ship.setVertical(true);
        }
        return myBoard.canPlaceShip(coordinates, ship);
    }

    public String getNickname() {
        return nickname;
    }


    public void setInitializedBoard(boolean initializedBoard) {
        this.initializedBoard = initializedBoard;
    }

    private boolean loadOnSever(String name, String filename) throws IOException, ClassNotFoundException {
        List<GameState> states = readSavedGames(filename);
        GameState found = null;
        for (GameState state : states) {
            if (state.getName().equalsIgnoreCase(name)) {
                found = state;
                break;
            }
        }
        if (found == null) {
            return false;
        }
        Game game = new Game(found.getName());
        game.setState(found);
        Server.loadableGames.put(found.getName(), game);
        return true;
    }

    private List<GameState> readSavedGames(String filename) throws IOException, ClassNotFoundException {
        FileInputStream fis = new FileInputStream(filename);
        List<GameState> states = new ArrayList<>();
        boolean hasMore = true;
        GameState state = null;
        while (hasMore) {
            if (fis.available() != 0) {
                ObjectInputStream ois = new ObjectInputStream(fis);
                state = (GameState) ois.readObject();
                states.add(state);

            } else {
                hasMore = false;
            }
        }
        fis.close();
        return states;
    }
}
