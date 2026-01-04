#ifndef CONFIG_H
#define CONFIG_H

#include <stdbool.h>

#define MAX_USERS 10
#define MAX_NAME_LEN 64
#define MAX_USERNAME_LEN 32
#define MAX_PASSWORD_LEN 64

typedef struct {
    char name[MAX_NAME_LEN];
    char username[MAX_USERNAME_LEN];
    char password[MAX_PASSWORD_LEN];  // Encrypted password
    int caesarKey;
} User;

typedef struct {
    User users[MAX_USERS];
    int userCount;
} Config;

// Load configuration from file
int config_load(const char *filename, Config *cfg);

// Verify user credentials
bool config_verify_user(const Config *cfg, const char *username, const char *password, char *fullName);

// Caesar cipher functions
extern int encrypt_data(char* in, int key, char* out);
extern int decrypt_data(char* in, int key, char* out);

#endif // CONFIG_H
