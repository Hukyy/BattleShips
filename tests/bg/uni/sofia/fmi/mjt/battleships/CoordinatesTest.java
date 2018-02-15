package bg.uni.sofia.fmi.mjt.battleships;

import org.junit.Test;

import static org.junit.Assert.*;

public class CoordinatesTest {

    @Test
    public void getXOnCoordinateA1ShouldBe0(){
        Coordinates coordinates = new Coordinates("A1");
        assertEquals(0,coordinates.getX());
    }

    @Test
    public void getYOnCoordinateC7ShouldBe6(){
        Coordinates coordinates = new Coordinates("C7");
        assertEquals(6,coordinates.getY());
    }

    @Test
    public void H4ShouldBeValidCoordinate(){
        Coordinates coordinates = new Coordinates("H4");
        assertTrue(coordinates.isValidCoordinate());
    }

    @Test
    public void K6ShouldNotBeValidCoordinate(){
        Coordinates coordinates = new Coordinates("K6");
        assertFalse(coordinates.isValidCoordinate());
    }

    @Test
    public void A1ShouldEquals00(){
        Coordinates coordinates1 = new Coordinates("A1");
        Coordinates coordinates2 = new Coordinates(0,0);

        assertTrue(coordinates1.equals(coordinates2));
    }

    @Test
    public void invalidYOnStringCoordinateShouldBeConvertedToMinus1(){
        Coordinates coordinates = new Coordinates("AB");
        assertEquals(-1,coordinates.getY());
    }
}