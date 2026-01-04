# Compiler
CC := gcc

# Flags
CFLAGS := -Wall -Wextra -std=c11

# Executável
TARGET := manager

# Fontes
SRC := manager.c ui.c sensors_linux.c

# =========================
# Build
all: $(TARGET)

$(TARGET): $(SRC)
	$(CC) $(CFLAGS) $(SRC) -o $(TARGET)

# =========================
# Utilitários
run: all
	./$(TARGET)

clean:
	rm -f $(TARGET)
	@echo "Clean feito"

.PHONY: all run clean
