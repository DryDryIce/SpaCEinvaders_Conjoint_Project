#include "renderer.h"
#include "constants.h"

int init_renderer(SDL_Window **window, SDL_Renderer **renderer) {
    if (SDL_Init(SDL_INIT_VIDEO) < 0) {
        return 0;
    }

    *window = SDL_CreateWindow(
        "spaCEinvaders - Cliente Jugador",
        SDL_WINDOWPOS_CENTERED,
        SDL_WINDOWPOS_CENTERED,
        WINDOW_WIDTH,
        WINDOW_HEIGHT,
        SDL_WINDOW_SHOWN
    );

    if (*window == NULL) {
        return 0;
    }

    *renderer = SDL_CreateRenderer(
        *window,
        -1,
        SDL_RENDERER_ACCELERATED
    );

    if (*renderer == NULL) {
        return 0;
    }

    return 1;
}

void render_game(SDL_Renderer *renderer, GameStateClient game_state) {
    SDL_SetRenderDrawColor(renderer, 0, 0, 0, 255);
    SDL_RenderClear(renderer);

    SDL_Rect cannon;
    cannon.x = game_state.player.x;
    cannon.y = PLAYER_Y;
    cannon.w = PLAYER_WIDTH;
    cannon.h = PLAYER_HEIGHT;

    SDL_SetRenderDrawColor(renderer, 0, 255, 0, 255);
    SDL_RenderFillRect(renderer, &cannon);

    for (int i = 0; i < game_state.enemy_count; i++) {
        if (!game_state.enemies[i].active) {
            continue;
        }

        SDL_Rect enemy_rect;
        enemy_rect.x = game_state.enemies[i].x;
        enemy_rect.y = game_state.enemies[i].y;
        enemy_rect.w = ENEMY_WIDTH;
        enemy_rect.h = ENEMY_HEIGHT;

        SDL_SetRenderDrawColor(renderer, 255, 255, 255, 255);
        SDL_RenderFillRect(renderer, &enemy_rect);
    }

    for (int i = 0; i < game_state.bunker_count; i++) {
        if (!game_state.bunkers[i].active) {
            continue;
        }

        SDL_Rect bunker_rect;
        bunker_rect.x = game_state.bunkers[i].x;
        bunker_rect.y = game_state.bunkers[i].y;
        bunker_rect.w = BUNKER_WIDTH;
        bunker_rect.h = BUNKER_HEIGHT;

        if (game_state.bunkers[i].health > 60) {
            SDL_SetRenderDrawColor(renderer, 0, 180, 0, 255);
        } else if (game_state.bunkers[i].health > 30) {
            SDL_SetRenderDrawColor(renderer, 180, 180, 0, 255);
        } else {
            SDL_SetRenderDrawColor(renderer, 180, 0, 0, 255);
        }

        SDL_RenderFillRect(renderer, &bunker_rect);
    }

    if (game_state.bullet.active) {
        SDL_Rect bullet_rect;
        bullet_rect.x = game_state.bullet.x;
        bullet_rect.y = game_state.bullet.y;
        bullet_rect.w = game_state.bullet.width;
        bullet_rect.h = game_state.bullet.height;

        SDL_SetRenderDrawColor(renderer, 255, 0, 0, 255);
        SDL_RenderFillRect(renderer, &bullet_rect);
    }

    SDL_RenderPresent(renderer);
}

void close_renderer(SDL_Window *window, SDL_Renderer *renderer) {
    SDL_DestroyRenderer(renderer);
    SDL_DestroyWindow(window);
    SDL_Quit();
}