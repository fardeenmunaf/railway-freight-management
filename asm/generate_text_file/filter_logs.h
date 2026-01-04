#ifndef FILTER_LOGS_H
#define FILTER_LOGS_H

#include "user.h"

/*
 * Filtra logs pertencentes a um utilizador.
 *
 * filter      -> struct com user_id (passada como struct → requisito da sprint)
 * all_logs    -> array de logs completo
 * total_logs  -> número total de logs
 * out         -> array onde serão guardados os logs filtrados
 * max_out     -> capacidade do array out
 *
 * Retorna:
 *   número de logs copiados (>= 0)
 */
int filter_user_logs(UserFilter filter,
                     LogEntry *all_logs,
                     int total_logs,
                     LogEntry *out,
                     int max_out);

#endif
