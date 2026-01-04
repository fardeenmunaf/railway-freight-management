#include <stdio.h>
#include <stdlib.h>
#include "user.h"
#include "filter_logs.h"
#include "export_logs.h"

int export_user_logs_to_txt(const User *user,
                            const LogEntry *all_logs,
                            int total_logs,
                            const char *filename)
{
    if (!user || !all_logs || total_logs <= 0 || !filename)
        return 0;

    UserFilter filter = { .user_id = user->id };

    // alocar buffer para, no máximo, todos os logs
    LogEntry *filtered = malloc((size_t) total_logs * sizeof(LogEntry));
    if (!filtered)
        return 0;

    int count = filter_user_logs(filter,
                                 (LogEntry *) all_logs,
                                 total_logs,
                                 filtered,
                                 total_logs);

    if (count < 0) {
        free(filtered);
        return 0;
    }

    FILE *f = fopen(filename, "w");
    if (!f) {
        free(filtered);
        return 0;
    }

    fprintf(f, "User: %s (id=%d)\n", user->username, user->id);
    fprintf(f, "Total logs: %d\n\n", count);

    if (count == 0) {
        fprintf(f, "No activity found for this user.\n");
    } else {
        for (int i = 0; i < count; i++) {
            fprintf(f, "[%d] %s\n",
                    filtered[i].timestamp,
                    filtered[i].action);
        }
    }

    fclose(f);
    free(filtered);
    return 1;
}
