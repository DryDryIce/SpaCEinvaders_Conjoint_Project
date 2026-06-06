public class Bunker {
    private final int id;
    private final int x;
    private final int y;
    private final int width;
    private final int height;
    private int health;
    private boolean active;

    public Bunker(int id, int x, int y) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.width = 80;
        this.height = 35;
        this.health = 100;
        this.active = true;
    }

    public synchronized void takeDamage(int damage) {
        if (!active) {
            return;
        }

        health -= damage;

        if (health <= 0) {
            health = 0;
            active = false;
        }
    }

    public int getId() {
        return id;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public synchronized int getHealth() {
        return health;
    }

    public synchronized boolean isActive() {
        return active;
    }
}