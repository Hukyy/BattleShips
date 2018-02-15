package bg.uni.sofia.fmi.mjt.battleships;

import org.junit.Test;

import static org.junit.Assert.*;

public class ShipTest {

    @Test
    public void smallShipShouldHave2HPWhenCreated(){
        Ship ship = new Ship(1,ShipType.SMALL);
        assertEquals(2,ship.getHealth());
    }

    @Test
    public void hugeShipShouldHave4HPAfter1Hit(){
        Ship ship = new Ship(1,ShipType.HUGE);
        ship.hit();
        assertEquals(4,ship.getHealth());
    }

    @Test
    public void smallShipShouldDieAfterTwoHits(){
        Ship ship = new Ship(1,ShipType.SMALL);
        assertTrue("shouldBeAlive",ship.isAlive());
        ship.hit();
        assertTrue("shouldBeAlive",ship.isAlive());
        ship.hit();
        assertFalse("ShouldBeDead",ship.isAlive());
    }

    @Test
    public void settingShipToVerticalShouldMakeItVertical(){
        Ship ship = new Ship(1, ShipType.BIG);
        ship.setVertical(true);
        assertTrue("Should be vertical",ship.isVertical());
    }

    @Test
    public void smallShipTypeShouldBeSmall(){
        Ship ship = new Ship(1,ShipType.SMALL);
        assertEquals(ShipType.SMALL, ship.getType());
    }

    @Test
    public void shipsWithSameIDShouldBeConsideredAsEquals(){
        Ship ship1 = new Ship(1, ShipType.BIG);
        Ship ship2 = new Ship(1, ShipType.BIG);
        assertTrue(ship1.equals(ship2));
    }

    @Test
    public void shipsWithSameIDShouldNotBeEquals(){
        Ship ship1 = new Ship(1, ShipType.BIG);
        Ship ship2 = new Ship(2, ShipType.BIG);
        assertFalse(ship1.equals(ship2));
    }

}