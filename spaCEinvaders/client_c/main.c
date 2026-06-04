#include <stdio.h>
#include <string.h>
#include <winsock2.h>

#define SDL_MAIN_HANDLED
#include <SDL2/SDL.h>

#include "constants.h"
#include "network.h"
#include "entities.h"
#include "renderer.h"

typedef struct {
    SOCKET socket_fd;
    GameStateClient *game_state;
    SDL_mutex *mutex;
    int *running;
} ReceiverContext;

void process_server_message(GameStateClient *game_state, char *message) {
    if (strncmp(message, "STATE|", 6) == 0) {
        sscanf(
            message,
            "STATE|%d|%d|%d",
            &game_state->player.x,
            &game_state->player.lives,
            &game_state->player.score
        );
    } else if (strncmp(message, "ENEMY|", 6) == 0) {
        if (game_state->enemy_count >= MAX_ENEMIES) {
            return;
        }

        Enemy enemy;

        sscanf(
            message,
            "ENEMY|%d|%d|%d|%d",
            &enemy.id,
            &enemy.x,
            &enemy.y,
            &enemy.points
        );

        enemy.active = 1;

        game_state->enemies[game_state->enemy_count] = enemy;
        game_state->enemy_count++;
    } else if (strncmp(message, "BULLET|", 7) == 0) {
        sscanf(
            message,
            "BULLET|%d|%d|%d|%d",
            &game_state->bullet.x,
            &game_state->bullet.y,
            &game_state->bullet.width,
            &game_state->bullet.height
        );

        game_state->bullet.active = 1;
    }
}

int receive_game_state(SOCKET socket_fd, GameStateClient *game_state) {
    char buffer[BUFFER_SIZE];

    game_state->enemy_count = 0;
    game_state->bullet.active = 0;

    while (1) {
        if (!receive_message(socket_fd, buffer)) {
            printf("No se pudo recibir mensaje del servidor.\n");
            return 0;
        }

        if (strcmp(buffer, "END") == 0) {
            break;
        }

        process_server_message(game_state, buffer);
    }

    return 1;
}

int receiver_thread(void *data) {
    ReceiverContext *context = (ReceiverContext *)data;

    while (*(context->running)) {
        GameStateClient temp_state;

        if (!receive_game_state(context->socket_fd, &temp_state)) {
            *(context->running) = 0;
            break;
        }

        SDL_LockMutex(context->mutex);
        *(context->game_state) = temp_state;
        SDL_UnlockMutex(context->mutex);
    }

    return 0;
}


int main(int argc, char *argv[]) {
    (void)argc;
    (void)argv;

    SOCKET socket_fd;

    SDL_Window *window = NULL;
    SDL_Renderer *renderer = NULL;
    SDL_Event event;

    int running = 1;

    GameStateClient game_state;
    game_state.player.x = 300;
    game_state.player.lives = 3;
    game_state.player.score = 0;
    game_state.enemy_count = 0;
    game_state.bullet.x = 0;
    game_state.bullet.y = 0;
    game_state.bullet.width = 0;
    game_state.bullet.height = 0;
    game_state.bullet.active = 0;

    socket_fd = connect_to_server();

    if (socket_fd == INVALID_SOCKET) {
        return 1;
    }

    if (!init_renderer(&window, &renderer)) {
        printf("Error iniciando SDL2: %s\n", SDL_GetError());
        close_connection(socket_fd);
        return 1;
    }

    receive_game_state(socket_fd, &game_state);

    SDL_mutex *state_mutex = SDL_CreateMutex();

    ReceiverContext context;
    context.socket_fd = socket_fd;
    context.game_state = &game_state;
    context.mutex = state_mutex;
    context.running = &running;

    SDL_Thread *receiver = SDL_CreateThread(receiver_thread, "ReceiverThread", &context);
    if (receiver == NULL) {
        printf("Error creando hilo receptor: %s\n", SDL_GetError());
        running = 0;
    }

    while (running) {
        while (SDL_PollEvent(&event)) {
            if (event.type == SDL_QUIT) {
                running = 0;
            }

            if (event.type == SDL_KEYDOWN) {
                if (event.key.keysym.sym == SDLK_a) {
                    send_message(socket_fd, "MOVE_LEFT");
                }

                if (event.key.keysym.sym == SDLK_d) {
                    send_message(socket_fd, "MOVE_RIGHT");
                }

                if (event.key.keysym.sym == SDLK_SPACE) {
                    send_message(socket_fd, "SHOOT");
                }

                if (event.key.keysym.sym == SDLK_ESCAPE) {
                    running = 0;
                }
            }
        }

        GameStateClient local_state;

        SDL_LockMutex(state_mutex);
        local_state = game_state;
        SDL_UnlockMutex(state_mutex);

        render_game(renderer, local_state);
        
        SDL_Delay(16);
    }

    running = 0;

    if (receiver != NULL) {
        SDL_WaitThread(receiver, NULL);
    }

    SDL_DestroyMutex(state_mutex);

    close_renderer(window, renderer);
    close_connection(socket_fd);

    return 0;
}