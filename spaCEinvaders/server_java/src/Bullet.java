public class Bullet {
    protected int x;
    protected int y;
    private final int width;
    private final int height;
    private final int speed;
    private boolean active;

    public Bullet(int x, int y) {
        this.x = x;
        this.y = y;
        this.width = 6;
        this.height = 15;
        this.speed = 30;
        this.active = true;
    }

    public synchronized void update() {
        if (!active) {
            return;
        }

        moveUp();

        if (y < 0) {
            active = false;
        }
    }

    protected synchronized void moveUp() {
        y -= speed;
    }

    protected synchronized void moveDown() {
        y += speed;
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