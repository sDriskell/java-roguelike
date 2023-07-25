package roguelike.maps;

import java.awt.Point;
import java.util.Objects;

import squidpony.squidgrid.util.DirectionCardinal;

public class ConnectionPoint extends Point {
    private static final long serialVersionUID = 6448455982964320426L;

    private DirectionCardinal direction;
    private boolean connected;
    private Room room;
    private Room connectedRoom;

    public boolean isDoor;

    public ConnectionPoint(int x, int y, DirectionCardinal direction, Room room) {
        super(x, y);
        this.direction = direction;
        this.room = room;
    }

    public ConnectionPoint(Point point, DirectionCardinal direction, Room room) {
        super(point);
        this.direction = direction;
        this.room = room;
    }

    public void connectTo(Room room) {
        if (room == null) {
            throw new IllegalArgumentException("connectTo room cannot be null");
        }
        connectedRoom = room;
        connected = true;
    }

    public DirectionCardinal direction() {
        return this.direction;
    }

    public boolean isConnected() {
        return connected;
    }

    public Room room() {
        return this.room;
    }

    public Room connectedRoom() {
        return this.connectedRoom;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + Objects.hash(connected, connectedRoom, direction, isDoor, room);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        ConnectionPoint other = (ConnectionPoint) obj;
        return connected == other.connected && Objects.equals(connectedRoom, other.connectedRoom)
                && direction == other.direction && isDoor == other.isDoor
                && Objects.equals(room, other.room);
    }
}
