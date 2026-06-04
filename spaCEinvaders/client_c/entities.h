#ifndef ENTITIES_H
#define ENTITIES_H

#define MAX_ENEMIES 100

typedef struct {
    int x;
    int lives;
    int score;
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
    Player player;
    Enemy enemies[MAX_ENEMIES];
    int enemy_count;
    Bullet bullet;
} GameStateClient;

#endif