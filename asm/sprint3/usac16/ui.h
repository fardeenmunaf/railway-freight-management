#ifndef UI_H
#define UI_H

#include <stddef.h>

// Operation types
typedef enum {
    OP_NONE = 0,
    OP_ASSIGN_TRACK,
    OP_SET_NON_OPERATIONAL,
    OP_SET_FREE,
    OP_GIVE_DEPARTURE,
    OP_REFRESH_DISPLAY,
    OP_EXIT
} OperationType;

// User instruction structure
typedef struct {
    OperationType type;
    int track_id;              // For operations on specific track
    char train_id[20];         // For track assignment
} UserInstruction;

// UI functions
int ui_login(char *username, size_t usize);
void ui_show_menu(void);
UserInstruction ui_get_instruction(void);

#endif // UI_H
