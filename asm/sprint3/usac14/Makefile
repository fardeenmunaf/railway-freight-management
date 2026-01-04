# Compiler para RISC-V
CC := riscv32-linux-gcc
AS := riscv32-linux-gcc

# Flags
CFLAGS := -Wall -Wextra -std=c11
ARCHFLAGS := -march=rv32imd -mabi=ilp32d
LIBRV32 := $(HOME)/bin/bootlin/riscv32-buildroot-linux-gnu/sysroot

# Executável
TARGET := manager

# Fontes
C_SRC := manager.c ui.c sensors_linux.c lightsigns.c
ASM_SRC := lightsigns_asm.s

# Objetos
C_OBJ := $(C_SRC:.c=.o)
ASM_OBJ := $(ASM_SRC:.s=.o)
OBJ := $(C_OBJ) $(ASM_OBJ)

# =========================
# Build
all: $(TARGET)

$(TARGET): $(OBJ)
	$(CC) $(CFLAGS) $(ARCHFLAGS) $(OBJ) -o $(TARGET)

%.o: %.c
	$(CC) $(CFLAGS) $(ARCHFLAGS) -c $< -o $@

%.o: %.s
	$(AS) $(CFLAGS) $(ARCHFLAGS) -c $< -o $@

# =========================
# Utilitários
run: all
	qemu-riscv32 -L $(LIBRV32) ./$(TARGET)

clean:
	rm -f $(TARGET) $(OBJ)
	@echo "Clean feito"

.PHONY: all run clean
