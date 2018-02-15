package bg.uni.sofia.fmi.mjt.battleships;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class CellTest {

    private Cell cell;

    @Before
    public void initialize() {
        cell = new Cell();
    }

    @Test
    public void newCellShouldNotBeShot() {
        assertFalse("new cell shouldn't be shot", cell.isShot());
    }

    @Test
    public void newCellShouldHaveNoShip() {
        assertNull(cell.getShip());
    }

    @Test
    public void cellShouldBeShotAfterHit() {
        cell.hit();
        assertTrue("Cell should be marked as shot", cell.isShot());
    }

    @Test
    public void shoutCellShouldNotGetHitAgain() {
        cell.hit();
        assertFalse("You can't hit it again", cell.hit());
    }

    @Test
    public void shotCellWithoutShipOnMyBoardShouldGetPrintedAsO() {
        cell.hit();
        assertEquals("O", cell.onMyBoard());
    }

    @Test
    public void shotCellWithShipOnMyBoardShouldGetPrintedAsX() {
        Ship ship = new Ship(1, ShipType.SMALL);
        cell.setShip(ship);
        cell.hit();
        assertEquals("X", cell.onMyBoard());
    }

    @Test
    public void notShotCellWithShipOnMyBoardShouldGetPrintedAsDiez() {
        Ship ship = new Ship(1, ShipType.SMALL);
        cell.setShip(ship);
        assertEquals("#", cell.onMyBoard());
    }

    @Test
    public void notShotCellWithoutShipOnMyBoardShouldGetPrintedAsDot() {
        assertEquals(".", cell.onMyBoard());
    }

    @Test
    public void shotCellWithoutShipOnEnemyBoardShouldGetPrintedAsO() {
        cell.hit();
        assertEquals("O", cell.onEnemyBoard());
    }

    @Test
    public void shotCellWithShipOnEnemyBoardShouldGetPrintedAsX() {
        Ship ship = new Ship(1, ShipType.SMALL);
        cell.setShip(ship);
        cell.hit();
        assertEquals("X", cell.onEnemyBoard());
    }

    @Test
    public void notShotCellOnEnemyBoardShouldGetPrintedAsDot() {
        assertEquals(".", cell.onMyBoard());
    }
}