public class Enemy {
    private final int id;
    private int x;
    private int y;
    private final int points;
    private boolean alive;
    private final int width;
    private final int height;

    public Enemy(int id, int x, int y, int points) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.points = points;
        this.alive = true;
        this.width = 40;
        this.height = 25;
    }

    public int getId() {
        return id;
    }

    public synchronized int getX() {
        return x;
    }

    public synchronized int getY() {
        return y;
    }

    public int getPoints() {
        return points;
    }

    public synchronized boolean isAlive() {
        return alive;
    }

    public synchronized void move(int dx, int dy) {
        this.x += dx;
        this.y += dy;
    }

    public synchronized void destroy() {
        this.alive = false;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}