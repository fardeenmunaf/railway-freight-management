#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <fcntl.h>
#include <termios.h>
#include <stdbool.h>
#include <signal.h>
#include <errno.h>

#define SERIAL_PORT "/dev/ttyACM0"
#define BAUDRATE    B115200

// flag de controlo definida em manager.c
extern volatile sig_atomic_t running;

// ---------------- TIPOS ----------------

typedef struct {
    float temperature;
    float humidity;
    bool  error;
} SensorData;

// ---------------- ESTADO GLOBAL ----------------

int serial_fd = -1;

#define BUFFER_SIZE 5
#define MEDIAN_WIN  3

SensorData buffer[BUFFER_SIZE];
int bufIndex = 0;
int bufCount = 0;

// exposto como const para o manager.c
const int BUFFER_SIZE_CONST = BUFFER_SIZE;

// ---------------- ABRIR SERIAL ----------------

int open_serial(const char *port) {
    int fd = open(port, O_RDWR | O_NOCTTY | O_SYNC | O_NONBLOCK);
    if (fd < 0) {
        perror("open serial");
        return -1;
    }

    struct termios tty;
    if (tcgetattr(fd, &tty) < 0) {
        perror("tcgetattr");
        close(fd);
        return -1;
    }

    cfsetospeed(&tty, BAUDRATE);
    cfsetispeed(&tty, BAUDRATE);

    tty.c_cflag = (tty.c_cflag & ~CSIZE) | CS8;
    tty.c_cflag |= CLOCAL | CREAD;
    tty.c_iflag = 0;
    tty.c_oflag = 0;
    tty.c_lflag = 0;

    tty.c_cc[VMIN]  = 0;
    tty.c_cc[VTIME] = 5;   // ~0.5 s

    if (tcsetattr(fd, TCSANOW, &tty) < 0) {
        perror("tcsetattr");
        close(fd);
        return -1;
    }

    return fd;
}

// ---------------- LER SENSORES ----------------

SensorData request_sensors(int fd) {
    SensorData sd = {.temperature = 0.0f, .humidity = 0.0f, .error = true};

    if (!running || fd < 0) return sd;

    ssize_t w = write(fd, "GTH\n", 4);
    if (w < 0) {
        if (errno != EINTR) {
            perror("write serial");
        }
        return sd;
    }

    char buf[128] = {0};
    int n = -1;
    int tries = 0;
    const int MAX_TRIES = 5;

    while (running && tries < MAX_TRIES) {
        n = read(fd, buf, sizeof(buf) - 1);

        if (n > 0) break;            // temos dados
        if (n == 0) {                // timeout
            tries++;
            continue;
        }

        if (n < 0) {
            if (errno == EAGAIN || errno == EWOULDBLOCK) {
                tries++;
                continue;
            }
            if (errno == EINTR) {
                if (!running) return sd;
                continue;
            }
            perror("read serial");
            return sd;
        }
    }

    if (!running || n <= 0) {
        return sd;
    }

    if (strncmp(buf, "ERROR", 5) == 0) {
        sd.error = true;
    } else if (sscanf(buf,
                      "TEMP&unit:C&value:%f#HUM&unit:%%&value:%f",
                      &sd.temperature, &sd.humidity) == 2) {
        sd.error = false;
    }

    return sd;
}

// ---------------- MOVING MEDIAN ----------------

static int cmp_float(const void *a, const void *b) {
    float fa = *(const float *)a;
    float fb = *(const float *)b;
    return (fa > fb) - (fa < fb);
}

bool movingMedian(SensorData *out) {
    if (bufCount < MEDIAN_WIN) return false;

    float t[MEDIAN_WIN], h[MEDIAN_WIN];
    int c = 0;
    int i = bufIndex - 1;
    if (i < 0) i += BUFFER_SIZE;

    int steps = 0;
    while (c < MEDIAN_WIN && steps < BUFFER_SIZE) {
        if (!buffer[i].error) {
            t[c] = buffer[i].temperature;
            h[c] = buffer[i].humidity;
            c++;
        }
        i = (i - 1 + BUFFER_SIZE) % BUFFER_SIZE;
        steps++;
    }

    if (c < MEDIAN_WIN) {
        return false; // não há dados válidos suficientes
    }

    qsort(t, MEDIAN_WIN, sizeof(float), cmp_float);
    qsort(h, MEDIAN_WIN, sizeof(float), cmp_float);

    out->temperature = t[MEDIAN_WIN / 2];
    out->humidity    = h[MEDIAN_WIN / 2];
    out->error = false;
    return true;
}
