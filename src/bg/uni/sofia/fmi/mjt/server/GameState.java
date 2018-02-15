package bg.uni.sofia.fmi.mjt.server;

import bg.uni.sofia.fmi.mjt.battleships.Board;

import java.io.Serializable;

public class GameState implements Serializable {
    private String name;
    private String creator;
    private Board board1;
    private Board board2;
    private String playerOneName;
    private String playerTwoName;
    private String currentPlayerName;
    private boolean start;

    public GameState(String name) {
        this.name = name;
        this.clearFirstBoard();
        this.clearSecondBoard();
    }

    public String getName() {
        return name;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public void clearFirstBoard() {
        this.board1 = new Board();
    }

    public Board getBoard1() {
        return this.board1;
    }

    public void clearSecondBoard() {
        this.board2 = new Board();
    }

    public Board getBoard2() {
        return this.board2;
    }

    public String getPlayerOneName() {
        return playerOneName;
    }

    public void setPlayerOneName(String playerOneName) {
        this.playerOneName = playerOneName;
    }

    public String getPlayerTwoName() {
        return playerTwoName;
    }

    public void setPlayerTwoName(String playerTwoName) {
        this.playerTwoName = playerTwoName;
    }

    public String getCurrentPlayerName() {
        return currentPlayerName;
    }

    public void setCurrentPlayerName(String currentPlayerName) {
        this.currentPlayerName = currentPlayerName;
    }

    public boolean isStart() {
        return start;
    }

    public void setStart(boolean start) {
        this.start = start;
    }
}
