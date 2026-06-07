public class EnemyBullet extends Bullet {

    public EnemyBullet(int x, int y) {
        super(x, y);
    }

    @Override
    public synchronized void update() {

        if (!isActive()) {
            return;
        }

        moveDown();

        if (getY() > 600) {
            deactivate();
        }
    }
}