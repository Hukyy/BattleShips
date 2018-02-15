package bg.uni.sofia.fmi.mjt.battleships;

import java.io.Serializable;

public class Cell implements Serializable {
    private Ship ship;
    private boolean shot;

    Cell() {
        this.ship = null;
        this.shot = false;
    }


    public Ship getShip() {
        return ship;
    }

    public void setShip(Ship ship) {
        this.ship = ship;
    }

    public boolean isShot() {
        return shot;
    }

    public boolean hit() {
        if (!isShot()) {
            shot = true;
            if (ship != null) {
                ship.hit();
                return true;
            }
        }
        return false;
    }

    public String onMyBoard() {
        String result = null;
        if (!shot) {
            if (ship != null) {
                result = "#";
            } else {
                result = ".";
            }
        } else if (shot) {
            if (ship != null) {
                result = "X";
            } else {
                result = "O";
            }
        }
        return result;
    }

    public String onEnemyBoard() {
        String result = null;
        if (!shot) {
            result = ".";
        } else if (shot) {
            if (ship != null) {
                result = "X";
            } else {
                result = "O";
            }
        }
        return result;
    }

}
