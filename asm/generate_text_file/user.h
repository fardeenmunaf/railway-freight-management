#ifndef USER_H
#define USER_H

// Representa um utilizador
typedef struct {
    int  id;
    char username[32];
} User;

// Entrada de log
typedef struct {
    int  user_id;           // ID do utilizador a quem pertence
    int  timestamp;         // ordem cronológica ou epoch
    char action[128];       // descrição da ação
} LogEntry;

// Estrutura usada pela função em Assembly
typedef struct {
    int user_id;
} UserFilter;

#endif
