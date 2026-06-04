#ifndef NETWORK_H
#define NETWORK_H

#include <winsock2.h>

SOCKET connect_to_server();
void send_message(SOCKET socket_fd, const char *message);
int receive_message(SOCKET socket_fd, char *buffer);
void close_connection(SOCKET socket_fd);

#endif