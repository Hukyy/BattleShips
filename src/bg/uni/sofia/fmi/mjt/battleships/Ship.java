package bg.uni.sofia.fmi.mjt.battleships;

import java.io.Serializable;

public class Ship implements Serializable {
    private int id;
    private ShipType type;
    private int health;
    private boolean vertical;
    private boolean alive;

    public Ship(int id, ShipType type) {
        this(id, type, true);
    }

    public Ship(int id, ShipType type, boolean vertical) {
        this.id = id;
        this.type = type;
        this.health = type.size;
        this.vertical = vertical;
        this.alive = true;
    }

    public ShipType getType() {
        return type;
    }


    public int getHealth() {
        return health;
    }


    public boolean isVertical() {
        return vertical;
    }

    public void setVertical(boolean vertical) {
        this.vertical = vertical;
    }

    public boolean isAlive() {
        return alive;
    }

    public void hit() {
        if (isAlive()) {
            health--;
            if (health == 0) {
                alive = false;
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Ship ship = (Ship) o;

        return id == ship.id;
    }

    @Override
    public int hashCode() {
        return id;
    }
}
