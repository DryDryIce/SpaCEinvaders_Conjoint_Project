#include "renderer.h"
#include "constants.h"

static SDL_Texture *enemy1Texture = NULL;
static SDL_Texture *enemy2Texture = NULL;
static SDL_Texture *enemy3Texture = NULL;

static SDL_Texture *playerTexture = NULL;
static SDL_Texture *ufoTexture = NULL;


int init_renderer(SDL_Window **window, SDL_Renderer **renderer) {
    if (SDL_Init(SDL_INIT_VIDEO) < 0) {
        return 0;
    }

    if (!(IMG_Init(IMG_INIT_PNG) & IMG_INIT_PNG)) {
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

        enemy1Texture = IMG_LoadTexture(
        *renderer,
        "images/enemy1.png"
    );

    enemy2Texture = IMG_LoadTexture(
        *renderer,
        "images/enemy2.png"
    );

    enemy3Texture = IMG_LoadTexture(
        *renderer,
        "images/enemy3.png"
    );

    playerTexture = IMG_LoadTexture(
        *renderer,
        "images/nave.png"
    );

    ufoTexture = IMG_LoadTexture(
        *renderer,
        "images/ovni.png"
    );

    if (!enemy1Texture ||
        !enemy2Texture ||
        !enemy3Texture ||
        !playerTexture ||
        !ufoTexture) {

        printf(
            "Error cargando imagen: %s\n",
            IMG_GetError()
        );

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

    SDL_RenderCopy(
        renderer,
        playerTexture,
        NULL,
        &cannon
    );

    for (int i = 0; i < game_state.enemy_count; i++) {
        if (!game_state.enemies[i].active) {
            continue;
        }

        SDL_Rect enemy_rect;
        enemy_rect.x = game_state.enemies[i].x;
        enemy_rect.y = game_state.enemies[i].y;
        enemy_rect.w = ENEMY_WIDTH;
        enemy_rect.h = ENEMY_HEIGHT;

        SDL_Texture *enemyTexture;

        if (game_state.enemies[i].type == 0) {
            enemyTexture = enemy1Texture;
        }
        else if (game_state.enemies[i].type == 1) {
            enemyTexture = enemy2Texture;
        }
        else {
            enemyTexture = enemy3Texture;
        }

        SDL_RenderCopy(
            renderer,
            enemyTexture,
            NULL,
            &enemy_rect
        );
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

    for (int i = 0; i < game_state.enemy_bullet_count; i++) {

        if (!game_state.enemy_bullets[i].active) {
            continue;
        }

        SDL_Rect bullet_rect;

        bullet_rect.x =
            game_state.enemy_bullets[i].x;

        bullet_rect.y =
            game_state.enemy_bullets[i].y;

        bullet_rect.w =
            game_state.enemy_bullets[i].width;

        bullet_rect.h =
            game_state.enemy_bullets[i].height;

        SDL_SetRenderDrawColor(
            renderer,
            255,
            255,
            0,
            255
        );

        SDL_RenderFillRect(
            renderer,
            &bullet_rect
        );
    }

    if (game_state.ufo.active) {
        
        

        SDL_Rect ufo_rect;

        ufo_rect.x = game_state.ufo.x;
        ufo_rect.y = game_state.ufo.y;
        ufo_rect.w = 60;
        ufo_rect.h = 25;

        SDL_RenderCopy(
            renderer,
            ufoTexture,
            NULL,
            &ufo_rect
        );
    }

    SDL_RenderPresent(renderer);
}

void close_renderer(SDL_Window *window, SDL_Renderer *renderer) {
    SDL_DestroyRenderer(renderer);
    SDL_DestroyWindow(window);
    SDL_Quit();
}