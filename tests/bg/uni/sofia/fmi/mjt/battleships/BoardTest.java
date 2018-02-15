package bg.uni.sofia.fmi.mjt.battleships;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class BoardTest {
    private Board board;

    @Before
    public void initialize() {
        board = new Board();
    }

    @Test
    public void placingShipWithValidCoordinatesOnEmptyBoardShouldBePossible() {
        Ship ship = new Ship(1, ShipType.HUGE);
        Coordinates coordinates = new Coordinates("A1");
        assertTrue(board.canPlaceShip(coordinates, ship));
    }

    @Test
    public void placingShipOutsideTheBoardShouldFail() {
        Ship ship = new Ship(1, ShipType.HUGE);
        Coordinates coordinates = new Coordinates("Z10");
        assertFalse(board.canPlaceShip(coordinates, ship));
    }

    @Test
    public void crossingOverIsNotAllowed() {
        Ship ship = new Ship(1, ShipType.HUGE);
        ship.setVertical(false);
        Coordinates coordinates = new Coordinates("B4");
        Ship ship1 = new Ship(2, ShipType.HUGE);
        ship1.setVertical(true);
        Coordinates coordinates1 = new Coordinates("A6");
        Ship ship2 = new Ship(1, ShipType.HUGE);
        ship2.setVertical(false);
        Coordinates coordinates2 = new Coordinates("J4");
        board.placeShip(coordinates, ship);
        assertFalse(board.canPlaceShip(coordinates1, ship1));
    }

    @Test
    public void hittingOnceIsAllowed() {
        assertTrue(board.hit("A1"));
    }

    @Test
    public void hittingTwiceIsNotAllowed() {
        assertTrue(board.hit("c4"));
        assertFalse(board.hit("c4"));
    }

    @Test
    public void hittingInvalidCoordinatesShouldFail() {
        assertFalse(board.hit("A11"));
    }

    @Test
    public void allShipsShouldBeDead() {
        Ship ship = new Ship(1, ShipType.SMALL, false);
        Coordinates coordinates = new Coordinates("A1");
        board.placeShip(coordinates, ship);
        board.hit("A1");
        board.hit("A2");

        assertTrue(board.allShipsDead());
    }

    @Test
    public void someShipsAreStillAlive() {
        Ship ship = new Ship(1, ShipType.SMALL, false);
        Coordinates coordinates = new Coordinates("A1");
        Ship ship1 = new Ship(2, ShipType.SMALL, true);
        Coordinates coordinates1 = new Coordinates("A10");
        board.placeShip(coordinates, ship);
        board.placeShip(coordinates1, ship1);
        board.hit("A10");
        board.hit("B10");
        board.hit("A1");
        assertFalse(board.allShipsDead());
    }

    @Test
    public void printingBoardAsPlayerBoardShouldContainsDiez() {
        Board board = new Board();
        Ship ship = new Ship(1, ShipType.HUGE);
        board.placeShip(new Coordinates("A1"), ship);

        String printedBoard = board.print(false);
        assertTrue(printedBoard.contains("#"));
    }

    @Test
    public void printingBoardAsEnemyBoardShouldNotContainsDiez() {
        Board board = new Board();
        Ship ship = new Ship(1, ShipType.HUGE);
        board.placeShip(new Coordinates("A1"), ship);

        String printedBoard = board.print(true);
        assertFalse(printedBoard.contains("#"));
    }

}