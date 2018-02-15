package bg.uni.sofia.fmi.mjt.server;

public enum AvailableCommands {
    HELP("help", "shows list of available commands"),
    CREATEGAME("create-game", "creates a game with a specific name"),
    LISTGAMES("list-games", "prints a list of all active games and some info about them"),
    JOINGAME("join-game", "player joins in a game with given name or in a random game, if game name is not provided"),
    START("start", "starts the game that you already joined in"),
    HIT("hit", "attack enemy ship on given coordinate"),
    LEAVEGAME("leave-game", "leave your current game"),
    QUIT("quit", "stops the program (quit from the server, not just game room)"),
    SAVEGAME("save-game", "saves current game in a file. You can load this game in the future"),
    LOADGAME("load-game", "loads a game with given name if was saved. Both players must load the game in order to continue to play"),
    SHOWMYGAMES("show-my-games", "shows the saved games you were in"),
    DELETEGAME("delete-game", "deletes game with a given name from saved games");


    private String command;
    private String description;

    AvailableCommands(String command, String description) {
        this.command = command;
        this.description = description;
    }

    @Override
    public String toString() {
        return String.format("%1$-15s|%2$s", command, description);
    }
}
