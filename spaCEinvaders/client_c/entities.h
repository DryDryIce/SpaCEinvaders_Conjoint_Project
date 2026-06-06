#ifndef ENTITIES_H
#define ENTITIES_H

#define MAX_ENEMIES 100
#define MAX_BUNKERS 4

typedef struct {
    int x;
    int lives;
    int score;
    int game_over;
    int wave_number;
} Player;

typedef struct {
    int id;
    int x;
    int y;
    int points;
    int active;
} Enemy;

typedef struct {
    int x;
    int y;
    int width;
    int height;
    int active;
} Bullet;

typedef struct {
    int id;
    int x;
    int y;
    int health;
    int active;
} Bunker;

typedef struct {
    Player player;
    Enemy enemies[MAX_ENEMIES];
    int enemy_count;
    Bullet bullet;
    Bunker bunkers[MAX_BUNKERS];
    int bunker_count;
} GameStateClient;

#endif