#ifndef UI_H
#define UI_H

#include <stddef.h>

typedef enum {
    INST_NONE = 0,
    INST_SHOW_ENV,
    INST_CONTROL_LIGHTS,
    INST_EXIT
} InstructionType;

typedef struct {
    InstructionType type;
    int track;      // For light control
    char color[3];  // For light control: "RE", "YE", "GE", "RB"
} Instruction;

int  ui_login(char *username, size_t usize);
Instruction ui_get_instruction(void);

#endif // UI_H
