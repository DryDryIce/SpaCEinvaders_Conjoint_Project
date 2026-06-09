#ifndef ENTITIES_H
#define ENTITIES_H

#define MAX_ENEMIES 100
#define MAX_BUNKERS 4
#define MAX_ENEMY_BULLETS 50

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
    int type;
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
    int x;
    int y;
    int points;
    int active;
} UFO;

typedef struct {
    int x;
    int y;
    int width;
    int height;
    int active;
} EnemyBullet;
typedef struct {
    Player player;
    Enemy enemies[MAX_ENEMIES];
    int enemy_count;
    EnemyBullet enemy_bullets[MAX_ENEMY_BULLETS];
    int enemy_bullet_count;
    Bullet bullet;
    Bunker bunkers[MAX_BUNKERS];
    int bunker_count;
    UFO ufo;
} GameStateClient;

#endif