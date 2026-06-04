import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class GameServer {
    private final int port;
    private final GameState gameState;
    private final List<ClientHandler> clients;
    private boolean running;

    public GameServer(int port) {
        this.port = port;
        this.gameState = new GameState();
        this.clients = new ArrayList<>();
        this.running = true;
    }

    public void start() {
        System.out.println("Servidor iniciado en puerto " + port);

        Thread gameLoopThread = new Thread(this::gameLoop);
        gameLoopThread.start();

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            while (running) {
                Socket clientSocket = serverSocket.accept();

                ClientHandler client = new ClientHandler(clientSocket, gameState, this);

                synchronized (clients) {
                    clients.add(client);
                    System.out.println("Cliente conectado. Total: " + clients.size());
                }

                Thread thread = new Thread(client);
                thread.start();
            }
        } catch (IOException e) {
            System.out.println("Error en servidor: " + e.getMessage());
        }
    }

    private void gameLoop() {
        while (running) {
            try {
                Thread.sleep(100); 
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }

            gameState.updateGame();
            broadcastGameState();
        }
    }

    public void broadcastGameState() {
        synchronized (clients) {
            for (ClientHandler client : clients) {
                client.sendGameStateToClient();
            }
        }
    }

    public void removeClient(ClientHandler client) {
        synchronized (clients) {
            clients.remove(client);
            System.out.println("Cliente desconectado. Total: " + clients.size());
        }
    }
}