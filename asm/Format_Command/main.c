#include <stdio.h>
#include <string.h>
#include "asm.h"

int main() {

    char cmd[100];
    int res;

    struct {
        char *input;
        int n;
        int exp_res;
        char *exp_cmd;
    } tests[] = {
        {"", 0, 0, ""},
        {" re ", 23, 1, "RE,23"},
        {" ye ", 91, 1, "YE,91"},
        {" gTh ", 17, 1, "GTH"},
        {"Off", 7, 0, ""},
        {"Rb      ", 15, 1, "RB,15"},
        {" cmD ", 11, 0, ""},
        {" shdhsdh %444 sdjshd", 64, 0, ""},
        {"40  adsads", 40, 0, ""},
        {"Offy   ", 0, 0, ""},
        {"Off y  2   ", 0, 0, ""},
        {"CmD   ", 0, 0, ""}
    };

    int num_tests = sizeof(tests) / sizeof(tests[0]);

    for (int i = 0; i < num_tests; i++) {

        memset(cmd, 0, sizeof(cmd));
        res = format_command(tests[i].input, tests[i].n, cmd);

        int success = 1;

        if (res != tests[i].exp_res)
            success = 0;

        if (tests[i].exp_res == 1) {
            if (strcmp(cmd, tests[i].exp_cmd) != 0)
                success = 0;
        }

        printf("Test %d:\n", i+1);
        printf("  Input:   \"%s\"\n", tests[i].input);
        printf("  n:       %d\n", tests[i].n);
        printf("  Output:  \"%s\"\n", cmd);
        printf("  Result:  %s\n\n", success ? "SUCCESS" : "FAIL");
    }

    return 0;
}
