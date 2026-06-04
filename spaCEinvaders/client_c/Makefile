CC=gcc
CFLAGS=-Wall -Wextra
SDL_FLAGS := $(shell pkg-config --cflags --libs sdl2)

all: client

client: main.c network.c renderer.c
	$(CC) $(CFLAGS) main.c network.c renderer.c -o client.exe -lws2_32 $(SDL_FLAGS)

clean:
	rm -f client.exe