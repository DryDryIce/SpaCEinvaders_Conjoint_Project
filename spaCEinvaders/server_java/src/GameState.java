import java.util.ArrayList;
import java.util.List;

public class GameState {
    private int playerX;
    private int playerLives;
    private int score;

    private final List<Enemy> enemies;
    private int enemyDirection;

    private Bullet playerBullet;

    private boolean gameOver;
    private int waveNumber;

    private final List<Bunker> bunkers;

    public GameState() {
        this.playerX = 300;
        this.playerLives = 3;
        this.score = 0;

        this.enemies = new ArrayList<>();
        this.enemyDirection = 1;

        this.playerBullet = null;
        this.gameOver = false;
        this.waveNumber = 1;

        this.bunkers = new ArrayList<>();

        createWave();
        createBunkers();
    }

    private void createWave() {
        enemies.clear();

        int id = 0;

        int rows = 3;
        int cols = 8;

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
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

        enemyDirection = 1;
        playerBullet = null;

        System.out.println("Nueva horda creada. Horda #" + waveNumber);
    }

    public synchronized void moveLeft() {
        if (gameOver) {
            return;
        }

        if (playerX > 0) {
            playerX -= 12;
        }
    }

    public synchronized void moveRight() {
        if (gameOver) {
            return;
        }

        if (playerX < 740) {
            playerX += 12;
        }
    }

    public synchronized void shoot() {
        if (gameOver) {
            return;
        }

        if (playerBullet == null || !playerBullet.isActive()) {
            int bulletX = playerX + 28;
            int bulletY = 525;

            playerBullet = new Bullet(bulletX, bulletY);
        }
    }

    public synchronized void updateGame() {
        if (gameOver) {
            return;
        }

        updateEnemies();
        updateBullet();
        checkBulletEnemyCollisions();
        checkEnemiesReachedPlayer();
        checkWaveCompleted();
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

    private synchronized void checkEnemiesReachedPlayer() {
        final int playerDangerY = 540;

        for (Enemy enemy : enemies) {
            if (!enemy.isAlive()) {
                continue;
            }

            int enemyBottom = enemy.getY() + enemy.getHeight();

            if (enemyBottom >= playerDangerY) {
                playerLives--;

                System.out.println("El jugador perdió una vida. Vidas restantes: " + playerLives);

                if (playerLives <= 0) {
                    playerLives = 0;
                    gameOver = true;
                    System.out.println("GAME OVER");
                } else {
                    createWave();
                }

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
                    enemy.move(0, 10);
                }
            }
        } else {
            for (Enemy enemy : enemies) {
                if (enemy.isAlive()) {
                    enemy.move(enemyDirection * 10, 0);
                }
            }
        }
    }

    private synchronized boolean areAllEnemiesDead() {
        for (Enemy enemy : enemies) {
            if (enemy.isAlive()) {
                return false;
            }
        }

        return true;
    }

    private synchronized void startNextWave() {
        waveNumber++;

        System.out.println("Horda completada. Iniciando horda #" + waveNumber);

        createWave();
    }

    private synchronized void checkWaveCompleted() {
        if (areAllEnemiesDead()) {
            startNextWave();
        }
    }

    private void createBunkers() {
        bunkers.clear();

        bunkers.add(new Bunker(0, 100, 440));
        bunkers.add(new Bunker(1, 260, 440));
        bunkers.add(new Bunker(2, 420, 440));
        bunkers.add(new Bunker(3, 580, 440));

        System.out.println("Bunkers creados.");
    }

    public synchronized int getWaveNumber() {
        return waveNumber;
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

    public synchronized boolean isGameOver() {
        return gameOver;
    }

    public synchronized List<Enemy> getEnemiesCopy() {
        return new ArrayList<>(enemies);
    }

    public synchronized Bullet getPlayerBullet() {
        return playerBullet;
    }

    public synchronized List<Bunker> getBunkersCopy() {
        return new ArrayList<>(bunkers);
    }
}