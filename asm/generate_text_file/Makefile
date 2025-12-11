CC        = riscv32-linux-gcc
CFLAGS    = -Wall -Wextra -fanalyzer -g -std=c11
ARCHFLAGS = -march=rv32imd -mabi=ilp32d
LIBRV32   = $(HOME)/bin/bootlin/riscv32-buildroot-linux-gnu/sysroot

program.elf: main.o export_logs.o filter_logs.o
	$(CC) $(CFLAGS) $(ARCHFLAGS) main.o export_logs.o filter_logs.o -o program.elf

main.o: main.c user.h export_logs.h
	$(CC) $(CFLAGS) $(ARCHFLAGS) -c main.c

export_logs.o: export_logs.c user.h filter_logs.h export_logs.h
	$(CC) $(CFLAGS) $(ARCHFLAGS) -c export_logs.c

filter_logs.o: filter_logs.s user.h filter_logs.h
	$(CC) $(CFLAGS) $(ARCHFLAGS) -c filter_logs.s

clean:
	rm -f *.o *.elf *.txt

run: program.elf
	qemu-riscv32 -L $(LIBRV32) ./program.elf
