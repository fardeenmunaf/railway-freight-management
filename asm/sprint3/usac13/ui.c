#include <stdio.h>
#include <string.h>
#include "ui.h"

int ui_login(char *username, size_t usize) {
    printf("Username: ");
    if (!fgets(username, usize, stdin)) return 0;
    username[strcspn(username, "\n")] = 0;
    return username[0] != '\0';
}

Instruction ui_get_instruction(void) {
    Instruction inst = { .type = INST_NONE };
    char buf[16];

    printf("\n=== MENU MANAGER ===\n");
    printf("1 - Mostrar condições ambientais\n");
    printf("0 - Sair\n> ");

    if (!fgets(buf, sizeof(buf), stdin)) {
        inst.type = INST_EXIT;
        return inst;
    }

    if (buf[0] == '1')
        inst.type = INST_SHOW_ENV;
    else if (buf[0] == '0')
        inst.type = INST_EXIT;
    else
        inst.type = INST_NONE;

    return inst;
}
