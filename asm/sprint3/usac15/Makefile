# Makefile for USAC15 - Synoptic Board Display Component

CC = gcc
CFLAGS = -Wall -Wextra -Werror -g -std=c11
LDFLAGS = -lm

TARGET = manager
OBJS = manager.o board.o ui.o sensors_linux.o

.PHONY: all clean run

all: $(TARGET)

$(TARGET): $(OBJS)
	$(CC) $(CFLAGS) -o $@ $^ $(LDFLAGS)

manager.o: manager.c board.h ui.h
	$(CC) $(CFLAGS) -c manager.c

board.o: board.c board.h
	$(CC) $(CFLAGS) -c board.c

ui.o: ui.c ui.h board.h
	$(CC) $(CFLAGS) -c ui.c

sensors_linux.o: sensors_linux.c board.h
	$(CC) $(CFLAGS) -c sensors_linux.c

clean:
	rm -f $(OBJS) $(TARGET) manager.log

run: $(TARGET)
	./$(TARGET)
