CC = riscv32-linux-gcc
CFLAGS = -Wall -Wextra -fanalyzer -g
ARCHFLAGS = -march=rv32imd -mabi=ilp32d
LIBRV32 = ${HOME}/bin/bootlin/riscv32-buildroot-linux-gnu/sysroot

TARGET = program.elf

OBJS = main.o median.o

$(TARGET): $(OBJS)
	$(CC) $(CFLAGS) $(ARCHFLAGS) $(OBJS) -o $(TARGET)

main.o: main.c 
	$(CC) $(CFLAGS) $(ARCHFLAGS) -c main.c

median.o: median.s
	$(CC) $(CFLAGS) $(ARCHFLAGS) -c median.s

clean:
	rm -f *.o *.elf .gdbinit

run: $(TARGET)
	qemu-riscv32 -L $(LIBRV32) ./$(TARGET)

debug: $(TARGET) .gdbinit
	@echo "On another terminal, run:"
	@echo "riscv32-linux-gdb --tui ./$(TARGET)"
	@echo "or paste this command:"
	@echo -n "cd $(CURDIR) ; riscv32-linux-gdb --tui ./$(TARGET)" | xclip -selection clipboard
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
