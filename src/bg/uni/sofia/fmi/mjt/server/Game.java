package bg.uni.sofia.fmi.mjt.server;

import bg.uni.sofia.fmi.mjt.battleships.Board;
import bg.uni.sofia.fmi.mjt.battleships.Coordinates;

import java.io.Serializable;

public class Game {
    //    private String name;
//    private String creator;
//
//    private Board firstBoard;
//    private Board secondBoard;
//    private boolean start;
    private GameState state;
    transient private Player player1;
    transient private Player player2;
    transient private Player currentPlayer;


    public Game(String name) {
        this.state = new GameState(name);
    }

    public GameState getState() {
        return state;
    }

    public void setState(GameState state) {
        this.state = state;
    }

//    public String getName() {
//        return name;
//    }
//
//    public void setName(String name) {
//        this.name = name;
//    }
//
//    public void setFirstPlayer(Player player){
//        player1 = player;
//        currentPlayer = player;
//    }
//
//    public void setSecondPlayer(Player player){
//        player2 = player;
//    }
//
//    public void setFirstBoard(Board board){
//        this.firstBoard = board;
//    }
//
//    public void setSecondBoard(Board board){
//        this.secondBoard = board;
//    }
//
//    public String getCreator() {
//        return creator;
//    }
//
//    public void setCreator(String creator) {
//        this.creator = creator;
//    }
//
//    public boolean isStart() {
//        return start;
//    }
//
//    public void setStart(boolean hasStart) {
//        this.start = hasStart;
//    }
//

//

//
//    public Board getFirstBoard() {
//        return firstBoard;
//    }
//
//    public Board getSecondBoard() {
//        return secondBoard;
//    }
//


    public synchronized boolean validMove(Player player, String target) {
        if (player == currentPlayer && player.getEnemyBoard().hit(target)) {
            currentPlayer = player.getOpponent();
            state.setCurrentPlayerName(currentPlayer.getNickname());
            return true;
        }
        return false;
    }

    public boolean isGameover() {
        return player1.getMyBoard().allShipsDead() || player2.getMyBoard().allShipsDead();
    }

    public void registerFirstPlayer(Player player) {
        this.player1 = player;
        this.state.setPlayerOneName(player.getNickname());
        this.state.setCurrentPlayerName(this.player1.getNickname());
        this.currentPlayer = player1;
        player1.setMyBoard(state.getBoard1());
        player1.setEnemyBoard(state.getBoard2());
        player1.setInitializedBoard(false);
        player1.setGame(this);
    }

    public void registerSecondPlayer(Player player) {
        this.player2 = player;
        this.state.setPlayerTwoName(player.getNickname());
        player.setMyBoard(state.getBoard2());
        player.setEnemyBoard(state.getBoard1());
        player.setInitializedBoard(false);
        player.setGame(this);
    }

    public Player getPlayer2() {
        return player2;
    }

    public Player getPlayer1() {
        return player1;
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public void setPlayer1(Player player1) {
        this.player1 = player1;
//        this.state.setPlayerOneName(player1.getNickname());
    }


    public void setPlayer2(Player player2) {
        this.player2 = player2;
    }

    public boolean isAvailable() {
        return player1 == null || player2 == null;
    }

    public void setCurrentPlayer(Player currentPlayer) {
        this.currentPlayer = currentPlayer;
    }
}
