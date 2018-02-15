package bg.uni.sofia.fmi.mjt.battleships;

import java.io.Serializable;

public enum ShipType implements Serializable {
    HUGE(5),
    BIG(4),
    MEDIUM(3),
    SMALL(2);

    public final int size;

    ShipType(int size) {
        this.size = size;
    }

}
