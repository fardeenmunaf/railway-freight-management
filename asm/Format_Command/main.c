#include <stdio.h>
#include <string.h>

int format_command(char *str, int value, char *cmd);

int main() {
    int value;
    char str[20];
    char cmd[20];
    int res;

    value = 5;
    strcpy(str, " rB ");
    res = format_command(str, value, cmd);
    printf("%d: %s\n", res, cmd); //1:RB,05

    strcpy(str, " Ye ");
    value = 25;
    res = format_command(str, value, cmd);
    printf("%d: %s\n", res, cmd); //1:YE,25

    strcpy(str, " Ye ");
    value = 125;
    res = format_command(str, value, cmd);
    printf("%d: %s\n", res, cmd); //0:

    strcpy(str, " aa ");
    value = 25;
    res = format_command(str, value, cmd);
    printf("%d: %s\n", res, cmd); //0:

    strcpy(str, " gTh ");
    value = 25;
    res = format_command(str, value, cmd);
    printf("%d: %s\n", res, cmd); //1: GTH

    return 0;
}
