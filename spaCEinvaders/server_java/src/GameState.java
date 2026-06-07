import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameState {
    private int playerX;
    private int playerLives;
    private int score;

    private final List<Enemy> enemies;
    private int enemyDirection;
    private int enemySpeed;

    private UFO ufo;
    private final Random random;
    private int ufoSpawnCounter;

    private Bullet playerBullet;
    private final List<EnemyBullet> enemyBullets;

    private boolean gameOver;
    private int waveNumber;

    private final List<Bunker> bunkers;

    public GameState() {
        this.playerX = 300;
        this.playerLives = 3;
        this.score = 0;

        this.enemies = new ArrayList<>();
        this.enemyDirection = 1;
        this.enemySpeed = 10;

        this.playerBullet = null;
        this.enemyBullets = new ArrayList<>();
        this.gameOver = false;
        this.waveNumber = 1;

        this.bunkers = new ArrayList<>();

        this.random = new Random();      
        this.ufo = null;                 
        this.ufoSpawnCounter = 0;

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

    private synchronized void enemyShoot() {
        List<Enemy> aliveEnemies = new ArrayList<>();

        for (Enemy enemy : enemies) {
            if (enemy.isAlive()) {
                aliveEnemies.add(enemy);
            }
        }

        if (aliveEnemies.isEmpty()) {
            return;
        }

        Enemy shooter =
            aliveEnemies.get(
                (int)(Math.random() * aliveEnemies.size())
            );

        enemyBullets.add(
            new EnemyBullet(
                shooter.getX() + 20,
                shooter.getY() + 25
            )
        );

        System.out.println(
            "Disparo enemigo creado en "
            + shooter.getX()
            + ","
            + shooter.getY()
        );
    }

    public synchronized void updateGame() {
        if (gameOver) {
            return;
        }

        if (Math.random() < 0.02) {
            enemyShoot();
        }

        updateEnemies();
        updateBullet();

        updateUFO();
        checkBulletUFOCollision();

        checkEnemyBulletBunkerCollisions();
        checkPlayerBulletBunkerCollisions();

        checkBulletEnemyCollisions();
        updateEnemyBullets();
        checkEnemiesReachedPlayer();
        checkWaveCompleted();
    }

    private synchronized void updateBullet() {
        if (playerBullet != null && playerBullet.isActive()) {
            playerBullet.update();
        }
    }

    private synchronized void updateEnemyBullets() {
        enemyBullets.removeIf(
            bullet -> !bullet.isActive()
        );

        for (EnemyBullet bullet : enemyBullets) {
            bullet.update();
        }
    }

    private synchronized void updateUFO() {

        ufoSpawnCounter++;

        if (ufo == null || !ufo.isAlive()) {

            if (ufoSpawnCounter >= 300) {

                ufoSpawnCounter = 0;

                int direction = random.nextBoolean() ? 1 : -1;

                int startX = (direction == 1) ? -50 : 820;

                int bonusPoints = random.nextInt(1000) + 500;

                ufo = new UFO(
                    999,
                    startX,
                    30,
                    bonusPoints,
                    direction
                );

                System.out.println(
                    "OVNI creado con "
                    + bonusPoints
                    + " puntos"
                );
            }

            return;
        }

        ufo.update();

        if (ufo.getX() < -100 || ufo.getX() > 900) {
            ufo = null;
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
    
    private synchronized void checkBulletUFOCollision() {

        if (ufo == null ||
            !ufo.isAlive() ||
            playerBullet == null ||
            !playerBullet.isActive()) {
            return;
        }

        if (isColliding(
                playerBullet.getX(),
                playerBullet.getY(),
                playerBullet.getWidth(),
                playerBullet.getHeight(),
                ufo.getX(),
                ufo.getY(),
                ufo.getWidth(),
                ufo.getHeight()
        )) {

            score += ufo.getPoints();

            System.out.println(
                "OVNI destruido. Bonus: "
                + ufo.getPoints()
            );

            playerBullet.deactivate();
            ufo.destroy();
        }
    }

    public synchronized UFO getUfo() {
        return ufo;
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

            int nextX = enemy.getX() + enemyDirection * enemySpeed;

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
                    enemy.move(enemyDirection * enemySpeed, 0);
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
        playerLives++;
        enemySpeed += 2;
        
        repairBunkers();

        System.out.println("Horda completada. Vida extra obtenida. Vidas " 
            + playerLives + " Iniciando horda #" + waveNumber);

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

    public synchronized List<EnemyBullet> getEnemyBulletsCopy() {
        return new ArrayList<>(enemyBullets);
    }

    private synchronized void checkEnemyBulletBunkerCollisions() {
       for (EnemyBullet bullet : enemyBullets) {

            if (!bullet.isActive()) {
                continue;
            }

            for (Bunker bunker : bunkers) {

                if (!bunker.isActive()) {
                    continue;
                }

                if (isColliding(
                        bullet.getX(),
                        bullet.getY(),
                        bullet.getWidth(),
                        bullet.getHeight(),
                        bunker.getX(),
                        bunker.getY(),
                        bunker.getWidth(),
                        bunker.getHeight()
                )) {

                    bunker.takeDamage(20);

                    bullet.deactivate();

                    System.out.println(
                        "Bunker "
                        + bunker.getId()
                        + " recibió daño. Vida: "
                        + bunker.getHealth()
                    );

                    break;
                }
            }
        } 
    }

    private synchronized void checkPlayerBulletBunkerCollisions() {
        if (playerBullet == null ||
            !playerBullet.isActive()) {
            return;
        }

        for (Bunker bunker : bunkers) {

            if (!bunker.isActive()) {
                continue;
            }

            if (isColliding(
                    playerBullet.getX(),
                    playerBullet.getY(),
                    playerBullet.getWidth(),
                    playerBullet.getHeight(),
                    bunker.getX(),
                    bunker.getY(),
                    bunker.getWidth(),
                    bunker.getHeight()
            )) {

                bunker.takeDamage(10);

                playerBullet.deactivate();

                break;
            }
        } 
    } 

    private synchronized void repairBunkers() {
        for (Bunker bunker : bunkers) {
            bunker.repair();
        }
        System.out.println(
            "Bunkers reparados para la siguiente horda."
        );
    }

}