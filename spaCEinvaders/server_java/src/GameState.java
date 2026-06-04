import java.util.ArrayList;
import java.util.List;

public class GameState {
    private int playerX;
    private int playerLives;
    private int score;

    private final List<Enemy> enemies;
    private int enemyDirection;

    private Bullet playerBullet;

    public GameState() {
        this.playerX = 300;
        this.playerLives = 3;
        this.score = 0;

        this.enemies = new ArrayList<>();
        this.enemyDirection = 1;

        this.playerBullet = null;

        createInitialEnemies();
    }

    private void createInitialEnemies() {
        int id = 0;

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 8; col++) {
                int x = 80 + col * 70;
                int y = 60 + row * 50;

                int points;

                if (row == 0) {
                    points = 40;
                } else if (row == 1) {
                    points = 20;
                } else {
                    points = 10;
                }

                enemies.add(new Enemy(id, x, y, points));
                id++;
            }
        }
    }

    public synchronized void moveLeft() {
        if (playerX > 0) {
            playerX -= 10;
        }
    }

    public synchronized void moveRight() {
        if (playerX < 740) {
            playerX += 10;
        }
    }

    public synchronized void shoot() {
        if (playerBullet == null || !playerBullet.isActive()) {
            int bulletX = playerX + 30;
            int bulletY = 525;

            playerBullet = new Bullet(bulletX, bulletY);
        }
    }

    public synchronized void updateGame() {
        updateEnemies();
        updateBullet();
        checkBulletEnemyCollisions();
    }

    private synchronized void updateBullet() {
        if (playerBullet != null && playerBullet.isActive()) {
            playerBullet.update();
        }
    }

    private synchronized void checkBulletEnemyCollisions() {
        if (playerBullet == null || !playerBullet.isActive()) {
            return;
        }

        for (Enemy enemy : enemies) {
            if (!enemy.isAlive()) {
                continue;
            }

            if (isColliding(
                    playerBullet.getX(),
                    playerBullet.getY(),
                    playerBullet.getWidth(),
                    playerBullet.getHeight(),
                    enemy.getX(),
                    enemy.getY(),
                    enemy.getWidth(),
                    enemy.getHeight()
            )) {
                enemy.destroy();
                playerBullet.deactivate();
                score += enemy.getPoints();
                break;
            }
        }
    }

    private boolean isColliding(
            int x1, int y1, int w1, int h1,
            int x2, int y2, int w2, int h2
    ) {
        return x1 < x2 + w2 &&
               x1 + w1 > x2 &&
               y1 < y2 + h2 &&
               y1 + h1 > y2;
    }

    public synchronized void updateEnemies() {
        boolean shouldGoDown = false;

        for (Enemy enemy : enemies) {
            if (!enemy.isAlive()) {
                continue;
            }

            int nextX = enemy.getX() + enemyDirection * 10;

            if (nextX <= 20 || nextX >= 740) {
                shouldGoDown = true;
                break;
            }
        }

        if (shouldGoDown) {
            enemyDirection *= -1;

            for (Enemy enemy : enemies) {
                if (enemy.isAlive()) {
                    enemy.move(0, 20);
                }
            }
        } else {
            for (Enemy enemy : enemies) {
                if (enemy.isAlive()) {
                    enemy.move(enemyDirection * 3, 0);
                }
            }
        }
    }

    public synchronized int getPlayerX() {
        return playerX;
    }

    public synchronized int getPlayerLives() {
        return playerLives;
    }

    public synchronized int getScore() {
        return score;
    }

    public synchronized List<Enemy> getEnemiesCopy() {
        return new ArrayList<>(enemies);
    }

    public synchronized Bullet getPlayerBullet() {
        return playerBullet;
    }
}