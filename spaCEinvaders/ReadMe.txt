Se creo que VSCode y se utilizo C, Java, gcc, MSYS UCRT64, SDL2, entonces descargen esto para que funcione por favor

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
Estructura actual del codigo

Servidor Java
├── Main.java          → arranca el servidor
├── GameServer.java    → conexiones, clientes y game loop
├── ClientHandler.java → escucha comandos de cada cliente
├── GameState.java     → lógica completa del juego
├── Enemy.java         → modelo de enemigos
└── Bullet.java        → modelo de bala

Cliente C
├── main.c             → ciclo principal del cliente
├── network.c          → sockets TCP con Winsock
├── network.h          → funciones de red
├── renderer.c         → dibuja en SDL2
├── renderer.h         → funciones gráficas
├── entities.h         → structs del cliente
├── constants.h        → constantes del cliente
└── Makefile           → compilación