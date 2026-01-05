# Makefile for USAC16 - Railway Track Management System

CC = gcc
CFLAGS = -Wall -Wextra -g -std=c11 -no-pie -D_DEFAULT_SOURCE
LDFLAGS = -lm

TARGET = manager
OBJS = manager.o tracks.o tracks_asm.o board.o lightsigns.o ui.o sensors_linux.o

.PHONY: all clean run test

all: $(TARGET)

$(TARGET): $(OBJS)
	$(CC) $(CFLAGS) -o $@ $^ $(LDFLAGS)

manager.o: manager.c tracks.h board.h lightsigns.h ui.h
	$(CC) $(CFLAGS) -c manager.c

tracks.o: tracks.c tracks.h
	$(CC) $(CFLAGS) -c tracks.c

tracks_asm.o: tracks_asm.s
	$(CC) $(CFLAGS) -c -o $@ $<

board.o: board.c board.h tracks.h lightsigns.h
	$(CC) $(CFLAGS) -c board.c

lightsigns.o: lightsigns.c lightsigns.h tracks.h
	$(CC) $(CFLAGS) -c lightsigns.c

ui.o: ui.c ui.h board.h
	$(CC) $(CFLAGS) -c ui.c

sensors_linux.o: sensors_linux.c board.h
	$(CC) $(CFLAGS) -c sensors_linux.c

clean:
	rm -f $(OBJS) $(TARGET) track_manager.log

run: $(TARGET)
	./$(TARGET)

test: $(TARGET)
	@echo "Running Track Management System..."
	@echo "Test scenario: Automated operations"
	./$(TARGET)

help:
	@echo "Available targets:"
	@echo "  all    - Build the track management system (default)"
	@echo "  clean  - Remove compiled files and logs"
	@echo "  run    - Build and run the system"
	@echo "  test   - Run system tests"
	@echo "  help   - Show this help message"
