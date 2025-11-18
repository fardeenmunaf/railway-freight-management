# Compilador e ferramentas
CC = riscv32-linux-gcc
CFLAGS = -Wall -Wextra -fanalyzer -g
ARCHFLAGS = -march=rv32imd -mabi=ilp32d
LIBRV32 = ${HOME}/bin/bootlin/riscv32-buildroot-linux-gnu/sysroot

# Ficheiros do projeto
OBJS = main.o extract.o
TARGET = program.elf

# -------------------------------------------------------------------
# Regras principais
# -------------------------------------------------------------------

$(TARGET): $(OBJS)
	$(CC) $(CFLAGS) $(ARCHFLAGS) $(OBJS) -o $(TARGET)

main.o: main.c
	$(CC) $(CFLAGS) $(ARCHFLAGS) -c main.c -o main.o

extract.o: extract.s
	$(CC) $(CFLAGS) $(ARCHFLAGS) -c extract.s -o extract.o

# -------------------------------------------------------------------
# Limpeza
# -------------------------------------------------------------------

clean:
	rm -f *.o *.elf .gdbinit

# -------------------------------------------------------------------
# Execução no QEMU
# -------------------------------------------------------------------

run: $(TARGET)
	qemu-riscv32 -L $(LIBRV32) ./$(TARGET)

# -------------------------------------------------------------------
# Debug com GDB
# -------------------------------------------------------------------

debug: $(TARGET) .gdbinit
	@echo "On another terminal, run:"
	@echo "riscv32-linux-gdb --tui ./$(TARGET)"
	@echo
	@echo "Or paste this command:"
	@echo -n "cd $(CURDIR) ; riscv32-linux-gdb --tui ./$(TARGET)" \
		| xclip -selection clipboard
	qemu-riscv32 -L $(LIBRV32) -g 1234 $(TARGET) -S

define GDBINIT
target extended-remote localhost:1234
set sysroot ~/bin/bootlin/riscv32-buildroot-linux-gnu/sysroot
break main
continue
endef

export GDBINIT

.gdbinit:
	@echo "$$GDBINIT" > .gdbinit

.PHONY: clean run debug