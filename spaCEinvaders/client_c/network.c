#include <stdio.h>
#include <string.h>
#include <winsock2.h>
#include <ws2tcpip.h>

#include "constants.h"
#include "network.h"

SOCKET connect_to_server() {
    WSADATA wsaData;
    SOCKET socket_fd;
    struct sockaddr_in server_address;

    if (WSAStartup(MAKEWORD(2, 2), &wsaData) != 0) {
        printf("Error iniciando Winsock.\n");
        return INVALID_SOCKET;
    }

    socket_fd = socket(AF_INET, SOCK_STREAM, 0);

    if (socket_fd == INVALID_SOCKET) {
        printf("Error creando socket.\n");
        WSACleanup();
        return INVALID_SOCKET;
    }

    server_address.sin_family = AF_INET;
    server_address.sin_port = htons(SERVER_PORT);
    server_address.sin_addr.s_addr = inet_addr(SERVER_IP);

    if (connect(socket_fd, (struct sockaddr *)&server_address, sizeof(server_address)) == SOCKET_ERROR) {
        printf("Error conectando al servidor.\n");
        closesocket(socket_fd);
        WSACleanup();
        return INVALID_SOCKET;
    }

    return socket_fd;
}

void send_message(SOCKET socket_fd, const char *message) {
    char buffer[BUFFER_SIZE];

    snprintf(buffer, BUFFER_SIZE, "%s\n", message);
    send(socket_fd, buffer, strlen(buffer), 0);
}

int receive_message(SOCKET socket_fd, char *buffer) {
    int index = 0;
    char c;
    int bytes_received;

    while (index < BUFFER_SIZE - 1) {
        bytes_received = recv(socket_fd, &c, 1, 0);

        if (bytes_received <= 0) {
            buffer[0] = '\0';
            return 0;
        }

        if (c == '\n') {
            break;
        }

        if (c != '\r') {
            buffer[index] = c;
            index++;
        }
    }

    buffer[index] = '\0';
    return 1;
}

void close_connection(SOCKET socket_fd) {
    closesocket(socket_fd);
    WSACleanup();
}