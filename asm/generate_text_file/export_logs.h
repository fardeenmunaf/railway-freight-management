#ifndef EXPORT_LOGS_H
#define EXPORT_LOGS_H

#include "user.h"

/*
 * Gera um ficheiro de texto (.txt) com os logs de um utilizador.
 *
 * Retorna:
 *   1 em sucesso
 *   0 em erro (I/O, memória, parâmetros inválidos)
 */
int export_user_logs_to_txt(const User *user,
                            const LogEntry *all_logs,
                            int total_logs,
                            const char *filename);

#endif
