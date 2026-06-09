public class UFO extends Enemy {

    private int direction;

    public UFO(int id, int x, int y, int points, int direction) {
        super(id, x, y, points, 3);
        this.direction = direction;
    }

    public int getDirection() {
        return direction;
    }

    public void update() {
        move(direction * 5, 0);
    }
}
