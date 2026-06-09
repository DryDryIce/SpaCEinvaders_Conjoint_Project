#include <stdio.h>
#include <string.h>
#include <winsock2.h>
#include <windows.h>

#define SDL_MAIN_HANDLED
#include <SDL2/SDL.h>

#include "constants.h"
#include "network.h"
#include "entities.h"
#include "renderer.h"

// ── Contexto para el hilo receptor del servidor ──────────────────────────────
typedef struct {
    SOCKET socket_fd;
    GameStateClient *game_state;
    SDL_mutex *mutex;
    int *running;
} ReceiverContext;


// ── Procesamiento de mensajes del servidor ───────────────────────────────────
void process_server_message(GameStateClient *game_state, char *message) {
    if (strncmp(message, "STATE|", 6) == 0) {
        sscanf(
            message,
            "STATE|%d|%d|%d|%d|%d",
            &game_state->player.x,
            &game_state->player.lives,
            &game_state->player.score,
            &game_state->player.game_over,
            &game_state->player.wave_number
        );
    } else if (strncmp(message, "ENEMY|", 6) == 0) {
        if (game_state->enemy_count >= MAX_ENEMIES) {
            return;
        }

        Enemy enemy;

        sscanf(
            message,
            "ENEMY|%d|%d|%d|%d|%d",
            &enemy.id,
            &enemy.x,
            &enemy.y,
            &enemy.points,
            &enemy.type
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
    } else if (strncmp(message, "BUNKER|", 7) == 0) {
        if (game_state->bunker_count >= MAX_BUNKERS) {
            return;
        }

        Bunker bunker;

        sscanf(
            message,
            "BUNKER|%d|%d|%d|%d",
            &bunker.id,
            &bunker.x,
            &bunker.y,
            &bunker.health
        );

        bunker.active = 1;

        game_state->bunkers[game_state->bunker_count] = bunker;
        game_state->bunker_count++;
    } else if (strncmp(message, "UFO|", 4) == 0) {
        sscanf(
            message,
            "UFO|%d|%d|%d",
            &game_state->ufo.x,
            &game_state->ufo.y,
            &game_state->ufo.points
        );

        game_state->ufo.active = 1;
    } else if (strncmp(message, "EBULLET|", 8) == 0) {
        if (game_state->enemy_bullet_count >= MAX_ENEMY_BULLETS) {
            return;
        }

        EnemyBullet bullet;

        sscanf(
            message,
            "EBULLET|%d|%d|%d|%d",
            &bullet.x,
            &bullet.y,
            &bullet.width,
            &bullet.height
        );

        bullet.active = 1;

        game_state->enemy_bullets[game_state->enemy_bullet_count] = bullet;
        game_state->enemy_bullet_count++;
    }
}

// ── Hilo receptor del servidor ───────────────────────────────────────────────
int receive_game_state(SOCKET socket_fd, GameStateClient *game_state) {
    char buffer[BUFFER_SIZE];

    game_state->enemy_count = 0;
    game_state->bullet.active = 0;
    game_state->enemy_bullet_count = 0;
    game_state->ufo.active = 0;
    game_state->bunker_count = 0;

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
        GameStateClient temp_state = {0};

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


// ── Main ─────────────────────────────────────────────────────────────────────
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
    game_state.player.game_over = 0;
    game_state.enemy_count = 0;
    game_state.bunker_count = 0;
    game_state.bullet.x = 0;
    game_state.bullet.y = 0;
    game_state.bullet.width = 0;
    game_state.bullet.height = 0;
    game_state.bullet.active = 0;
    game_state.player.wave_number = 1;

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

    ReceiverContext receiver_ctx;
    receiver_ctx.socket_fd  = socket_fd;
    receiver_ctx.game_state = &game_state;
    receiver_ctx.mutex      = state_mutex;
    receiver_ctx.running    = &running;

    SDL_Thread *receiver = SDL_CreateThread(receiver_thread, "ReceiverThread", &receiver_ctx);
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
                if (event.key.keysym.sym == SDLK_ESCAPE) {
                    running = 0;
                }
            }
        }

        GameStateClient local_state;

        SDL_LockMutex(state_mutex);
        local_state = game_state;
        SDL_UnlockMutex(state_mutex);

        if (local_state.player.game_over) {
            SDL_SetWindowTitle(window, "spaCEinvaders - GAME OVER");
        } else {
            char title[128];
            snprintf(
                title,
                sizeof(title),
                "spaCEinvaders - Vidas: %d | Score: %d | Horda: %d",
                local_state.player.lives,
                local_state.player.score,
                local_state.player.wave_number
            );
            SDL_SetWindowTitle(window, title);
        }

        render_game(renderer, local_state);

        SDL_Delay(16);
    }


    running = 0;
    shutdown(socket_fd, SD_BOTH);


    if (receiver != NULL) {
        SDL_WaitThread(receiver, NULL);
    }



    SDL_DestroyMutex(state_mutex);

    close_renderer(window, renderer);
    close_connection(socket_fd);

    return 0;
}