#include <stdio.h>
#include "user.h"
#include "export_logs.h"

int main(void)
{
    User u = { .id = 2, .username = "alice" };

    LogEntry logs[] = {
        {2, 1001, "Login"},
        {1, 1002, "Viewed dashboard"},
        {2, 1003, "Edited profile"},
        {2, 1004, "Logout"}
    };

    int total_logs = sizeof(logs) / sizeof(logs[0]);

    if (export_user_logs_to_txt(&u, logs, total_logs, "alice_logs.txt")) {
        printf("Ficheiro gerado com sucesso.\n");
    } else {
        printf("Erro ao gerar ficheiro.\n");
    }

    return 0;
}
