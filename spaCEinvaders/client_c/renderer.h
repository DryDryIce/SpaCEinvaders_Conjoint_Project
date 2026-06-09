#ifndef RENDERER_H
#define RENDERER_H

#include <SDL2/SDL.h>
#include <SDL2/SDL_image.h>
#include "entities.h"

int init_renderer(SDL_Window **window, SDL_Renderer **renderer);
void render_game(SDL_Renderer *renderer, GameStateClient game_state);
void close_renderer(SDL_Window *window, SDL_Renderer *renderer);

#endif