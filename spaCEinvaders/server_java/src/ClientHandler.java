import java.io.*;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private final Socket socket;
    private final GameState gameState;
    private final GameServer server;
    private PrintWriter out;
    private BufferedReader in;

    public ClientHandler(Socket socket, GameState gameState, GameServer server) {
        this.socket = socket;
        this.gameState = gameState;
        this.server = server;
    }

    @Override
    public void run() {
        try {
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            sendGameStateToClient();

            String message;

            while ((message = in.readLine()) != null) {
                System.out.println("Mensaje recibido: " + message);
                processMessage(message);
            }

        } catch (IOException e) {
            System.out.println("Cliente desconectado.");
        }
        finally {
            server.removeClient(this);

            try {
                socket.close();
            } catch (IOException ignored) {
            }
        }
    }

    private void processMessage(String message) {
        switch (message) {
            case "MOVE_LEFT":
                gameState.moveLeft();
                break;

            case "MOVE_RIGHT":
                gameState.moveRight();
                break;

            case "SHOOT":
                gameState.shoot();
                break;

            default:
                System.out.println("Mensaje desconocido: " + message);
                break;
        }

        server.broadcastGameState();
    }

    public void sendGameStateToClient() {
        sendMessage(
            "STATE|" +
            gameState.getPlayerX() + "|" +
            gameState.getPlayerLives() + "|" +
            gameState.getScore()
        );

        for (Enemy enemy : gameState.getEnemiesCopy()) {
            if (enemy.isAlive()) {
                sendMessage(
                    "ENEMY|" +
                    enemy.getId() + "|" +
                    enemy.getX() + "|" +
                    enemy.getY() + "|" +
                    enemy.getPoints()
                );
            }
        }

        Bullet bullet = gameState.getPlayerBullet();

        if (bullet != null && bullet.isActive()) {
            sendMessage(
                "BULLET|" +
                bullet.getX() + "|" +
                bullet.getY() + "|" +
                bullet.getWidth() + "|" +
                bullet.getHeight()
            );
        }

        sendMessage("END");
    }

    public void sendMessage(String message) {
        if (out != null) {
            out.println(message);
        }
    }
}