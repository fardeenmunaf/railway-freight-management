#define _DEFAULT_SOURCE
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <fcntl.h>
#include <termios.h>
#include <time.h>
#include "board.h"

// Global serial file descriptor
int serial_fd = -1;

// Open serial port
int open_serial_port(const char *port_name) {
    int fd = open(port_name, O_RDWR | O_NOCTTY | O_NDELAY);
    if (fd < 0) {
        return -1;
    }
    
    // Configure serial port
    struct termios options;
    tcgetattr(fd, &options);
    
    cfsetispeed(&options, B9600);
    cfsetospeed(&options, B9600);
    
    options.c_cflag |= (CLOCAL | CREAD);
    options.c_cflag &= ~PARENB;
    options.c_cflag &= ~CSTOPB;
    options.c_cflag &= ~CSIZE;
    options.c_cflag |= CS8;
    options.c_lflag &= ~(ICANON | ECHO | ECHOE | ISIG);
    options.c_oflag &= ~OPOST;
    options.c_cc[VMIN] = 0;
    options.c_cc[VTIME] = 10;
    
    tcsetattr(fd, TCSANOW, &options);
    tcflush(fd, TCIOFLUSH);
    
    return fd;
}

// Read simulated sensor data (for testing without hardware)
int read_simulated_sensors(float *temp, float *hum) {
    static int initialized = 0;
    static float base_temp = 20.0;
    static float base_hum = 50.0;
    
    if (!initialized) {
        srand(time(NULL));
        initialized = 1;
    }
    
    // Simulate realistic variations
    *temp = base_temp + ((rand() % 100) - 50) / 10.0;  // ±5°C variation
    *hum = base_hum + ((rand() % 100) - 50) / 5.0;      // ±10% variation
    
    // Keep within realistic bounds
    if (*temp < 5.0) *temp = 5.0;
    if (*temp > 40.0) *temp = 40.0;
    if (*hum < 20.0) *hum = 20.0;
    if (*hum > 90.0) *hum = 90.0;
    
    return 1;  // Success
}
