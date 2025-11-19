CC = riscv32-linux-gcc
CFLAGS = -Wall -Wextra -fanalyzer -g
ARCHFLAGS = -march=rv32imd -mabi=ilp32d
LIBRV32 = $(HOME)/bin/bootlin/riscv32-buildroot-linux-gnu/sysroot

prog.elf: main.o decrypt.o callfunc.o unity.o
	$(CC) $(CFLAGS) $(ARCHFLAGS) main.o decrypt.o callfunc.o unity.o -o prog.elf

main.o: main.c asm.h unity.h
	$(CC) $(CFLAGS) $(ARCHFLAGS) -c main.c

decrypt.o: decrypt.S asm.h
	$(CC) $(CFLAGS) $(ARCHFLAGS) -c decrypt.S

callfunc.o: callfunc.S
	$(CC) $(CFLAGS) $(ARCHFLAGS) -c callfunc.S

unity.o: unity.c unity.h unity_internals.h
	$(CC) $(CFLAGS) $(ARCHFLAGS) -c unity.c

clean:
	rm -f *.o *.elf

run: prog.elf
	qemu-riscv32 -L $(LIBRV32) ./prog.elf
