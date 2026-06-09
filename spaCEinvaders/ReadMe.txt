Se creo que VSCode y se utilizo C, Java, gcc, MSYS UCRT64, SDL2, SDL2_image entonces descargen esto para que funcione por favor

=====================================================================================================================================================
Inicio de servidor de Java
Abre powershell o VSCode bash
cd server_java/src
javac *.java
java Main

O puedes ejecutar directamente el Main.java

=====================================================================================================================================================
Inicio de servidor cliente de C
Abre powershell
Tu propio path al proyecto
Jia: cd "C:\Users\User\OneDrive\Documentos\CodeDocuments\spaCEinvaders\client_c"
.\client.exe

(Usar esto en caso de ser necesario): gcc main.c network.c -o client.exe -lws2_32

=====================================================================================================================================================
Actualizar Make con MSYS UCRT64 (Debe de realizarse si se hacen cambios al codigo de C)
Jia: cd /c/Users/User/OneDrive/Documentos/CodeDocuments/spaCEinvaders/client_c
make clean
make

=====================================================================================================================================================
Estructura actual del código

Servidor Java
├── Main.java          → arranca el servidor
├── GameServer.java    → maneja conexiones, clientes conectados, desconexiones y game loop
├── ClientHandler.java → escucha comandos de cada cliente y envía el estado del juego
├── GameState.java     → lógica completa del juego: jugador, enemigos, balas, score, vidas, hordas y bunkers
├── Enemy.java         → modelo de cada enemigo: posición, puntos, tamaño, movimiento y estado vivo/muerto
├── Bullet.java        → modelo de la bala del jugador: posición, tamaño, velocidad y estado activo
├── Bunker.java        → modelo de los bunkers: posición, tamaño, vida, daño recibido y estado activo/inactivo
├── UFO.java           → modelo del enemigo UFO: posición, puntos, tamaño, movimiento y estado vivo/muerto
└── EnemyBullet.java   → modelo de la bala de los enemigos: posición, tamaño, velocidad y estado activo

Cliente C
├── main.c             → ciclo principal del cliente, eventos de teclado, hilo receptor y procesamiento de mensajes del servidor
├── network.c          → sockets TCP con Winsock: conexión, envío, recepción línea por línea y cierre
├── network.h          → declaraciones de funciones de red
├── renderer.c         → dibuja en SDL2: jugador, enemigos, bala y bunkers
├── renderer.h         → declaraciones de funciones gráficas
├── entities.h         → structs del cliente: Player, Enemy, Bullet, Bunker y GameStateClient
├── constants.h        → constantes del cliente: ventana, jugador, enemigos, bala, bunkers, IP y puerto
├── Makefile           → compilación del cliente C con SDL2 y Winsock
└── spectator.c        → ciclo principal del cliente, spectacion del juego y procesamiento de mensajes del servidor