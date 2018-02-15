package bg.uni.sofia.fmi.mjt.server;

import bg.uni.sofia.fmi.mjt.battleships.Ship;
import bg.uni.sofia.fmi.mjt.battleships.ShipType;

import java.util.ArrayList;
import java.util.List;

public class Harbor {
    private List<Ship> ships;

    public Harbor() {
        ships = new ArrayList<>();
        ships.add(new Ship(1, ShipType.HUGE));
        ships.add(new Ship(2, ShipType.BIG));
        ships.add(new Ship(3, ShipType.BIG));
        ships.add(new Ship(4, ShipType.MEDIUM));
        ships.add(new Ship(5, ShipType.MEDIUM));
        ships.add(new Ship(6, ShipType.MEDIUM));
        ships.add(new Ship(7, ShipType.SMALL));
        ships.add(new Ship(8, ShipType.SMALL));
        ships.add(new Ship(9, ShipType.SMALL));
        ships.add(new Ship(10, ShipType.SMALL));
    }

    public List<Ship> getShips() {
        return ships;
    }
}
