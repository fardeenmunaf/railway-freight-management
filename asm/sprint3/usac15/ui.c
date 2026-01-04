#include "ui.h"
#include "board.h"
#include <stdio.h>
#include <string.h>
#include <ctype.h>
#include <unistd.h>
#include <sys/select.h>

int ui_login(char *username, size_t usize) {
    printf(COLOR_CYAN "\n╔════════════════════════════════════════════════════════════════════╗\n" COLOR_RESET);
    printf(COLOR_CYAN "║" COLOR_YELLOW "                           USER LOGIN                              " COLOR_CYAN "║\n" COLOR_RESET);
    printf(COLOR_CYAN "╚════════════════════════════════════════════════════════════════════╝\n" COLOR_RESET);
    printf("\n");
    printf(COLOR_WHITE "Enter username: " COLOR_RESET);
    fflush(stdout);  // Force output to display immediately
    
    if (!fgets(username, usize, stdin)) {
        return 0;
    }
    
    // Remove newline
    size_t len = strlen(username);
    if (len > 0 && username[len-1] == '\n') {
        username[len-1] = '\0';
    }
    
    if (strlen(username) == 0) {
        return 0;
    }
    
    printf(COLOR_GREEN "\nWelcome, %s!\n" COLOR_RESET, username);
    printf(COLOR_YELLOW "Loading system...\n" COLOR_RESET);
    fflush(stdout);
    return 1;
}

Instruction ui_get_instruction(void) {
    Instruction inst = {INST_NONE, 0, ""};
    
    board_show_footer();
    printf(COLOR_WHITE "\nEnter command: " COLOR_RESET);
    fflush(stdout);
    
    char line[100];
    memset(line, 0, sizeof(line));  // Clear buffer
    
    // Clear any leftover input
    int c;
    fd_set readfds;
    struct timeval tv;
    FD_ZERO(&readfds);
    FD_SET(0, &readfds);  // stdin is fd 0
    tv.tv_sec = 0;
    tv.tv_usec = 0;
    if (select(1, &readfds, NULL, NULL, &tv) > 0) {
        // There's leftover input, consume it
        while ((c = getchar()) != '\n' && c != EOF);
    }
    
    if (!fgets(line, sizeof(line), stdin)) {
        // EOF or error - exit gracefully
        inst.type = INST_EXIT;
        return inst;
    }
    
    // Remove newline
    size_t len = strlen(line);
    if (len > 0 && line[len-1] == '\n') {
        line[len-1] = '\0';
    }
    
    // Check if empty - just return INST_NONE and loop again
    if (strlen(line) == 0) {
        inst.type = INST_NONE;
        printf(COLOR_YELLOW "Empty command. Please try again.\n" COLOR_RESET);
        sleep(1);
        return inst;
    }
    
    // Convert to uppercase for comparison
    for (size_t i = 0; i < strlen(line); i++) {
        line[i] = toupper(line[i]);
    }
    
    if (strcmp(line, "S") == 0 || strcmp(line, "SHOW") == 0) {
        inst.type = INST_SHOW_ENV;
    } 
    else if (strcmp(line, "L") == 0 || strcmp(line, "LIGHT") == 0 || strcmp(line, "LIGHTS") == 0) {
        inst.type = INST_CONTROL_LIGHTS;
        
        // Ask for track number
        printf(COLOR_WHITE "  Enter track number (1-8): " COLOR_RESET);
        if (scanf("%d", &inst.track) != 1) {
            inst.track = 1;
        }
        getchar(); // consume newline
        
        // Ask for light color
        printf(COLOR_WHITE "  Enter light color (RE/YE/GE/RB): " COLOR_RESET);
        if (scanf("%2s", inst.color) != 1) {
            strcpy(inst.color, "RE");
        }
        getchar(); // consume newline
        
        // Convert to uppercase
        for (int i = 0; inst.color[i]; i++) {
            inst.color[i] = toupper(inst.color[i]);
        }
    }
    else if (strcmp(line, "E") == 0 || strcmp(line, "EXIT") == 0 || strcmp(line, "Q") == 0 || strcmp(line, "QUIT") == 0) {
        inst.type = INST_EXIT;
    }
    else {
        inst.type = INST_NONE;
        printf(COLOR_RED "\nUnknown command '%s'. Please try again.\n" COLOR_RESET, line);
        sleep(1);
    }
    
    return inst;
}
