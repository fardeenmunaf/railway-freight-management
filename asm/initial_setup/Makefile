CC        = riscv32-linux-gcc
CFLAGS    = -Wall -Wextra -fanalyzer -g -std=c11
ARCHFLAGS = -march=rv32imd -mabi=ilp32d
LIBRV32   = $(HOME)/bin/bootlin/riscv32-buildroot-linux-gnu/sysroot

program.elf: main.o parse_config.o
	$(CC) $(CFLAGS) $(ARCHFLAGS) main.o parse_config.o -o program.elf

main.o: main.c config_types.h parse_config.h
	$(CC) $(CFLAGS) $(ARCHFLAGS) -c main.c

parse_config.o: parse_config.c config_types.h parse_config.h
	$(CC) $(CFLAGS) $(ARCHFLAGS) -c parse_config.c

clean:
	rm -f *.o *.elf

run: program.elf
	qemu-riscv32 -L $(LIBRV32) ./program.elf

.PHONY: clean run

