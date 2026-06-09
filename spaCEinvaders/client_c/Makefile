CC=gcc
CFLAGS=-Wall -Wextra
SDL_FLAGS := $(shell pkg-config --cflags --libs sdl2 SDL2_image)

all: client spectator

client: main.c network.c renderer.c
	$(CC) $(CFLAGS) main.c network.c renderer.c -o client.exe -lws2_32 $(SDL_FLAGS)

spectator: spectator.c network.c renderer.c
	$(CC) $(CFLAGS) spectator.c network.c renderer.c -o spectator.exe -lws2_32 $(SDL_FLAGS)
	
clean:
	rm -f client.exe spectator.exe