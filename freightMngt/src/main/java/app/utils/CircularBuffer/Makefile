# Compilador e ferramentas
CC = riscv32-linux-gcc
CFLAGS = -Wall -Wextra -fanalyzer -g
ARCHFLAGS = -march=rv32imd -mabi=ilp32d
LIBRV32 = ${HOME}/bin/bootlin/riscv32-buildroot-linux-gnu/sysroot

# Ficheiros do projeto
OBJS = main.o enqueue_value.o
TARGET = circular_buffer.elf

# -------------------------------------------------------------------
# Regras principais
# -------------------------------------------------------------------

$(TARGET): $(OBJS)
	$(CC) $(CFLAGS) $(ARCHFLAGS) $(OBJS) -o $(TARGET)

main.o: main.c
	$(CC) $(CFLAGS) $(ARCHFLAGS) -c main.c -o main.o

enqueue_value.o: enqueue_value.s
	$(CC) $(CFLAGS) $(ARCHFLAGS) -c enqueue_value.s -o enqueue_value.o

# -------------------------------------------------------------------
# Execução
# -------------------------------------------------------------------

run: $(TARGET)
	qemu-riscv32 -L $(LIBRV32) ./$(TARGET)

# -------------------------------------------------------------------
# Limpeza
# -------------------------------------------------------------------

clean:
	rm -f *.o *.elf .gdbinit

