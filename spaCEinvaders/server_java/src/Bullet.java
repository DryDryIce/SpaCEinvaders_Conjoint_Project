public class Bullet {
    private int x;
    private int y;
    private final int width;
    private final int height;
    private final int speed;
    private boolean active;

    public Bullet(int x, int y) {
        this.x = x;
        this.y = y;
        this.width = 6;
        this.height = 15;
        this.speed = 20;
        this.active = true;
    }

    public synchronized void update() {
        if (!active) {
            return;
        }

        y -= speed;

        if (y < 0) {
            active = false;
        }
    }

    public synchronized int getX() {
        return x;
    }

    public synchronized int getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public synchronized boolean isActive() {
        return active;
    }

    public synchronized void deactivate() {
        active = false;
    }
}