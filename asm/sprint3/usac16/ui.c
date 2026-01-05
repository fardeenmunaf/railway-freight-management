// ui.c - User interface implementation

#include "ui.h"
#include "board.h"
#include <stdio.h>
#include <string.h>
#include <ctype.h>

// Login function
int ui_login(char *username, size_t usize) {
    printf("\n");
    printf(COLOR_CYAN "════════════════════════════════════════════════════════════════════\n" COLOR_RESET);
    printf(COLOR_CYAN "              RAILWAY TRACK MANAGEMENT SYSTEM LOGIN\n" COLOR_RESET);
    printf(COLOR_CYAN "════════════════════════════════════════════════════════════════════\n" COLOR_RESET);
    printf("\n");
    printf(COLOR_WHITE "Please enter your username: " COLOR_RESET);
    
    if (fgets(username, usize, stdin) == NULL) {
        return -1;
    }
    
    // Remove newline
    size_t len = strlen(username);
    if (len > 0 && username[len - 1] == '\n') {
        username[len - 1] = '\0';
    }
    
    if (strlen(username) == 0) {
        printf(COLOR_RED "Error: Username cannot be empty!\n" COLOR_RESET);
        return -1;
    }
    
    printf(COLOR_GREEN "\nWelcome, %s!\n" COLOR_RESET, username);
    printf(COLOR_CYAN "════════════════════════════════════════════════════════════════════\n" COLOR_RESET);
    
    return 0;
}

// Show menu
void ui_show_menu(void) {
    printf("\n");
    printf(COLOR_YELLOW "╔════════════════════════════════════════════════════════════════════╗\n" COLOR_RESET);
    printf(COLOR_YELLOW "║" COLOR_WHITE "                      OPERATION MENU                               " COLOR_YELLOW "║\n" COLOR_RESET);
    printf(COLOR_YELLOW "╠════════════════════════════════════════════════════════════════════╣\n" COLOR_RESET);
    printf(COLOR_YELLOW "║" COLOR_RESET " 1. " COLOR_GREEN "Assign Track to Arriving Train" COLOR_RESET "                              " COLOR_YELLOW "║\n" COLOR_RESET);
    printf(COLOR_YELLOW "║" COLOR_RESET " 2. " COLOR_RED "Set Track as Non-Operational" COLOR_RESET "                                " COLOR_YELLOW "║\n" COLOR_RESET);
    printf(COLOR_YELLOW "║" COLOR_RESET " 3. " COLOR_GREEN "Set Track as Free" COLOR_RESET "                                           " COLOR_YELLOW "║\n" COLOR_RESET);
    printf(COLOR_YELLOW "║" COLOR_RESET " 4. " COLOR_CYAN "Give Departure Order" COLOR_RESET "                                        " COLOR_YELLOW "║\n" COLOR_RESET);
    printf(COLOR_YELLOW "║" COLOR_RESET " 5. " COLOR_BLUE "Refresh Display" COLOR_RESET "                                             " COLOR_YELLOW "║\n" COLOR_RESET);
    printf(COLOR_YELLOW "║" COLOR_RESET " 0. " COLOR_MAGENTA "Exit System" COLOR_RESET "                                                   " COLOR_YELLOW "║\n" COLOR_RESET);
    printf(COLOR_YELLOW "╚════════════════════════════════════════════════════════════════════╝\n" COLOR_RESET);
    printf(COLOR_WHITE "\nEnter your choice: " COLOR_RESET);
}

// Get instruction from user
UserInstruction ui_get_instruction(void) {
    UserInstruction inst = {OP_NONE, 0, ""};
    char input[100];
    int choice;
    
    ui_show_menu();
    
    if (fgets(input, sizeof(input), stdin) == NULL) {
        return inst;
    }
    
    if (sscanf(input, "%d", &choice) != 1) {
        printf(COLOR_RED "Invalid input! Please enter a number.\n" COLOR_RESET);
        return inst;
    }
    
    switch (choice) {
        case 1: // Assign track
            inst.type = OP_ASSIGN_TRACK;
            printf(COLOR_CYAN "\nEnter Train ID: " COLOR_RESET);
            if (fgets(input, sizeof(input), stdin) != NULL) {
                size_t len = strlen(input);
                if (len > 0 && input[len - 1] == '\n') {
                    input[len - 1] = '\0';
                }
                strncpy(inst.train_id, input, sizeof(inst.train_id) - 1);
                inst.train_id[sizeof(inst.train_id) - 1] = '\0';
            }
            break;
            
        case 2: // Set non-operational
            inst.type = OP_SET_NON_OPERATIONAL;
            printf(COLOR_CYAN "\nEnter Track ID: " COLOR_RESET);
            if (fgets(input, sizeof(input), stdin) != NULL) {
                sscanf(input, "%d", &inst.track_id);
            }
            break;
            
        case 3: // Set free
            inst.type = OP_SET_FREE;
            printf(COLOR_CYAN "\nEnter Track ID: " COLOR_RESET);
            if (fgets(input, sizeof(input), stdin) != NULL) {
                sscanf(input, "%d", &inst.track_id);
            }
            break;
            
        case 4: // Give departure
            inst.type = OP_GIVE_DEPARTURE;
            printf(COLOR_CYAN "\nEnter Track ID: " COLOR_RESET);
            if (fgets(input, sizeof(input), stdin) != NULL) {
                sscanf(input, "%d", &inst.track_id);
            }
            break;
            
        case 5: // Refresh display
            inst.type = OP_REFRESH_DISPLAY;
            break;
            
        case 0: // Exit
            inst.type = OP_EXIT;
            break;
            
        default:
            printf(COLOR_RED "Invalid choice! Please try again.\n" COLOR_RESET);
            inst.type = OP_NONE;
            break;
    }
    
    return inst;
}
