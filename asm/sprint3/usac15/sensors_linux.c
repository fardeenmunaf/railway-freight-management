// sensors_linux.c - Sensor simulation for USAC15

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <stdbool.h>
#include <fcntl.h>
#include <unistd.h>
#include <termios.h>
#include <time.h>

#include "board.h"

// Define CRTSCTS if not available on this system
#ifndef CRTSCTS
#define CRTSCTS 0
#endif

// ----------------- Serial port management -----------------

int serial_fd = -1;

int open_serial_port(const char *device) {
    printf("Trying to open serial port: %s\n", device);
    int fd = open(device, O_RDWR | O_NOCTTY | O_SYNC | O_NONBLOCK);
    if (fd < 0) {
        perror("  -> Failed to open");
        return -1;
    }
    printf("  -> Opened successfully (fd=%d)\n", fd);
    
    struct termios tty;
    if (tcgetattr(fd, &tty) < 0) {
        perror("  -> Failed tcgetattr - not a valid serial port");
        close(fd);
        return -1;  // Reject this port - it's not working properly
    }
    
    cfsetospeed(&tty, B115200);
    cfsetispeed(&tty, B115200);
    
    tty.c_cflag = (tty.c_cflag & ~CSIZE) | CS8;
    tty.c_cflag |= CLOCAL | CREAD;
    tty.c_iflag = 0;
    tty.c_oflag = 0;
    tty.c_lflag = 0;
    tty.c_cc[VMIN] = 0;
    tty.c_cc[VTIME] = 5;
    
    if (tcsetattr(fd, TCSANOW, &tty) < 0) {
        perror("  -> Failed tcsetattr");
        close(fd);
        return -1;
    }
    
    printf("  -> Serial port configured OK\n");
    serial_fd = fd;
    return fd;
}

// ----------------- Sensor data simulation -----------------

SensorData request_sensors(int fd) {
    SensorData data;
    
    // Send GTH command to hardware if available
    if (fd >= 0) {
        write(fd, "GTH\n", 4);
        fsync(fd);
    }
    
    // Always use simulation mode for sensor data
    srand(time(NULL));
    
    // Temperature between 15°C and 30°C
    data.temperature = 15.0 + ((float)rand() / RAND_MAX) * 15.0;
    
    // Humidity between 30% and 80%
    data.humidity = 30.0 + ((float)rand() / RAND_MAX) * 50.0;
    
    // Simulate occasional errors (5% chance)
    data.error = (rand() % 100) < 5;
    
    // Small delay to simulate sensor reading time
    sleep(1);
    
    return data;
}

// ----------------- Moving median filter -----------------

#define BUFFER_SIZE 5
const int BUFFER_SIZE_CONST = BUFFER_SIZE;

SensorData buffer[BUFFER_SIZE];
int bufIndex = 0;
int bufCount = 0;

// Comparison function for qsort
static int compare_float(const void *a, const void *b) {
    float fa = *(const float *)a;
    float fb = *(const float *)b;
    return (fa > fb) - (fa < fb);
}

bool movingMedian(SensorData *out) {
    if (bufCount == 0) {
        out->error = true;
        return false;
    }
    
    // Check if any sensor has error
    int errorCount = 0;
    for (int i = 0; i < bufCount; i++) {
        if (buffer[i].error) errorCount++;
    }
    
    // If more than half have errors, return error
    if (errorCount > bufCount / 2) {
        out->error = true;
        return false;
    }
    
    // Calculate median for temperature and humidity
    float temps[BUFFER_SIZE];
    float hums[BUFFER_SIZE];
    int validCount = 0;
    
    for (int i = 0; i < bufCount; i++) {
        if (!buffer[i].error) {
            temps[validCount] = buffer[i].temperature;
            hums[validCount] = buffer[i].humidity;
            validCount++;
        }
    }
    
    if (validCount == 0) {
        out->error = true;
        return false;
    }
    
    // Sort and find median
    qsort(temps, validCount, sizeof(float), compare_float);
    qsort(hums, validCount, sizeof(float), compare_float);
    
    int mid = validCount / 2;
    if (validCount % 2 == 0) {
        out->temperature = (temps[mid - 1] + temps[mid]) / 2.0;
        out->humidity = (hums[mid - 1] + hums[mid]) / 2.0;
    } else {
        out->temperature = temps[mid];
        out->humidity = hums[mid];
    }
    
    out->error = false;
    return true;
}
