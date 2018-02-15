package bg.uni.sofia.fmi.mjt.battleships;

import java.io.Serializable;
import java.util.ArrayList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Board implements Serializable {
    private Cell[][] board;
    private Map<Ship, Coordinates> beginningOfTheShip;

    public Board() {
        this.board = new Cell[10][10];
        for (int y = 0; y < 10; y++) {
            for (int x = 0; x < 10; x++) {
                board[x][y] = new Cell();
            }
        }
        this.beginningOfTheShip = new HashMap<>();
    }

    public void placeShip(int x, int y, Ship ship) {
        if (canPlaceShip(x, y, ship)) {
            Coordinates shipStart = new Coordinates(x, y);
            beginningOfTheShip.put(ship, shipStart);
            boolean vertical = ship.isVertical();
            int size = ship.getType().size;
            if (vertical) {
                for (int i = x; i < x + size; i++) {
                    board[i][y].setShip(ship);
                }
            } else {
                for (int i = y; i < y + size; i++) {
                    board[x][i].setShip(ship);
                }
            }
        }
    }

    public void placeShip(Coordinates coordinates, Ship ship) {
        int x = coordinates.getX();
        int y = coordinates.getY();
        placeShip(x, y, ship);
    }

    public boolean hit(String target) {
        Coordinates coords = new Coordinates(target);
        if (coords.isValidCoordinate()) {
            return hit(coords.getX(), coords.getY());
        }
        return false;
    }

    public boolean hit(int x, int y) {
        if (board[x][y].isShot()) {
            return false;
        }
        boolean thereWasShip = board[x][y].hit();
        if (thereWasShip && !board[x][y].getShip().isAlive()) {
            markSurrounded(board[x][y].getShip());
        }
        return true;
    }

    private void markSurrounded(Ship ship) {
        Coordinates beginning = beginningOfTheShip.get(ship);
        boolean vertical = ship.isVertical();
        if (vertical) {
            for (int x = beginning.getX(); x < beginning.getX() + ship.getType().size; x++) {
                for (Cell neighbour : getNeighbours(x, beginning.getY())) {
                    neighbour.hit();
                }
            }
        } else {
            for (int y = beginning.getY(); y < beginning.getY() + ship.getType().size; y++) {
                for (Cell neighbour : getNeighbours(beginning.getX(), y)) {
                    neighbour.hit();
                }
            }
        }
    }

    public boolean canPlaceShip(Coordinates coordinates, Ship ship) {
        int x = coordinates.getX();
        int y = coordinates.getY();
        return canPlaceShip(x, y, ship);
    }

    public boolean canPlaceShip(int x, int y, Ship ship) {
        boolean vertical = ship.isVertical();
        int size = ship.getType().size;

        if (vertical) {
            for (int i = x; i < x + size; i++) {
                if (!isValidCoordinate(i, y) || board[i][y].getShip() != null) {
                    return false;
                }
                for (Cell neighbour : getNeighbours(i, y)) {
                    if (neighbour.getShip() != null) {
                        return false;
                    }
                }
            }
        } else {
            for (int i = y; i < y + size; i++) {
                if (!isValidCoordinate(x, i) || board[x][i].getShip() != null) {
                    return false;
                }
                for (Cell neighbour : getNeighbours(x, i)) {
                    if (neighbour.getShip() != null) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public boolean allShipsDead() {
        for (Ship ship : beginningOfTheShip.keySet()) {
            if (ship.isAlive()) {
                return false;
            }
        }
        return true;
    }

    public Cell getCell(String coords) {
        Coordinates coordinates = new Coordinates(coords);
        int x = coordinates.getX();
        int y = coordinates.getY();
        return board[x][y];
    }

    private boolean isValidCoordinate(int x, int y) {
        return x >= 0 && x < 10 && y >= 0 && y < 10;
    }

    private List<Cell> getNeighbours(int x, int y) {
        int[] dx = {-1, -1, -1, 0, 0, 1, 1, 1};
        int[] dy = {-1, 0, 1, -1, 1, -1, 0, 1};

        List<Cell> neighbours = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            if (isValidCoordinate(x + dx[i], y + dy[i])) {
                neighbours.add(board[x + dx[i]][y + dy[i]]);
            }
        }
        return neighbours;
    }

    public String print(boolean enemy) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("    1 2 3 4 5 6 7 8 9 10\n");
        for (int x = 0; x < 10; x++) {
            stringBuilder.append((char) (x + 'A'));
            stringBuilder.append("   ");
            for (int y = 0; y < 10; y++) {
                if (enemy) {
                    stringBuilder.append(board[x][y].onEnemyBoard() + " ");
                } else {
                    stringBuilder.append(board[x][y].onMyBoard() + " ");
                }
            }
            stringBuilder.append('\n');
        }
        return stringBuilder.toString();
    }
}
