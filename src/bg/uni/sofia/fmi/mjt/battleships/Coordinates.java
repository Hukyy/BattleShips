package bg.uni.sofia.fmi.mjt.battleships;

import java.io.Serializable;

public class Coordinates implements Serializable{
    private int x;
    private int y;

    public Coordinates(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Coordinates(String coords) {
        x = Character.toUpperCase(coords.charAt(0)) - 'A';
        try {
            y = Integer.parseInt(coords.substring(1)) - 1;
        } catch (NumberFormatException e) {
            y = -1;
        }
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public boolean isValidCoordinate(){
        return x>=0 && x <=9 && y>=0 && y<=9;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Coordinates that = (Coordinates) o;

        if (x != that.x) return false;
        return y == that.y;
    }

    @Override
    public int hashCode() {
        int result = x;
        result = 31 * result + y;
        return result;
    }
}
