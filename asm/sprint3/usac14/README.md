# USAC14 — Control Track Light Signals

## Context
Implementation of Sprint 3 US with at least one function using assembly, where at least one of its parameters is a struct.

## User Story
As a User, I intend to control the track light signals to manage train movement safely.

## Implementation

### Hardware
- **Microcontroller**: Raspberry Pi Pico W
- **LEDs**: 3 physical LEDs
  - Red LED: GPIO 18
  - Yellow LED: GPIO 17
  - Green LED: GPIO 16
- **Sensor**: DHT11 (Temperature/Humidity) on GPIO 1
- **Communication**: Serial port /dev/ttyACM0 at 115200 baud

### Assembly Function
The `control_led()` function is implemented in **RISC-V assembly** (`lightsigns_asm.s`) and accepts a struct parameter:

```assembly
int control_led(LedControl *led)
```

**Parameters:**
- `led`: Pointer to LedControl struct containing:
  - `led_id` (int): LED ID (offset 0)
  - `state` (int): State 0=OFF, 1=ON (offset 4)
  - `count` (int): LED counter (offset 8)

**Returns:**
- Updated count value

**Assembly Features:**
- Uses struct member offsets (0, 4, 8 bytes)
- Increments count when turning LED on
- Decrements count when turning LED off
- Calls printf to display LED status

### Functionality

#### Option 1 - Show Environmental Conditions (USAC13)
- Reads DHT11 sensor via serial (GTH command)
- Calculates moving median of 5 readings
- Displays temperature and humidity
- Automatically controls LED based on temperature:
  - **Red LED**: Temperature > 30°C
  - **Green LED**: Temperature ≤ 30°C

#### Option 2 - Turn On All Lights (USAC14)
- Sequentially turns on all 3 LEDs
- Each LED: ON for 5 seconds, then OFF
- Sequence: Red → Yellow → Green
- Uses assembly function `control_led()` with struct parameter

### Serial Protocol

**Commands sent to Pico W:**
- `GTH\n` - Get temperature and humidity
- `RE\n` - Red LED ON
- `YE\n` - Yellow LED ON
- `GE\n` - Green LED ON
- `RB\n` - Red LED OFF (Blank)
- `YB\n` - Yellow LED OFF
- `GB\n` - Green LED OFF

**Responses from Pico W:**
- `T:21.0,H:59.0\n` - Temperature and humidity data
- `ERROR:DHT11\n` - Sensor read error

## File Structure

```
usac14/
├── lightsigns.h          # Header with LedControl struct
├── lightsigns_asm.s      # Assembly function control_led() (RISC-V)
├── lightsigns.c          # C wrapper functions for LED control
├── manager.c             # Main program with menu system
├── ui.h, ui.c            # User interface
├── sensors_linux.c       # Sensor reading and serial communication
├── pico_simple_led_control.ino  # Arduino firmware for Pico W
├── Makefile              # RISC-V cross-compilation
└── README.md             # This file
```

## Build and Execution

### 1. Upload Firmware to Pico W
1. Open `pico_simple_led_control.ino` in Arduino IDE
2. Select board: Raspberry Pi Pico W
3. Install DHT sensor library
4. Upload to Pico W

### 2. Compile and Run AntiX Program
```bash
make clean
make run
```

Or manually:
```bash
make
qemu-riscv32 -L /home/guest/bin/bootlin/riscv32-buildroot-linux-gnu/sysroot ./manager
```

## Usage

1. Connect Raspberry Pi Pico W via USB
2. Run the program
3. Login with username (e.g., "g")
4. Menu:
   - **1**: Show environmental conditions (reads DHT11, controls LED by temperature)
   - **2**: Turn on all lights sequentially
   - **0**: Exit

## Example Output

### Option 1 - Environmental Conditions
```
A recolher dados dos sensores...

+------------------------------+
|    ESTADO AMBIENTE ATUAL     |
+------------------------------+
| Temp:  21.0 C  Hum:  57.5 % |
+------------------------------+
[LightSigns] CMD=GE, track=1
[Hardware] Comando GE enviado ao Pico W
```

### Option 2 - Turn On All Lights
```
=== Ligar Todas as Luzes ===
  [LED 1] Ligando...
[Hardware] Comando RE enviado ao Pico W
(aguarda 5 segundos)

  [LED 1] Desligando...
  [LED 2] Ligando...
[Hardware] Comando YE enviado ao Pico W
(aguarda 5 segundos)

  [LED 2] Desligando...
  [LED 3] Ligando...
[Hardware] Comando GE enviado ao Pico W
(aguarda 5 segundos)

  [LED 3] Desligando...
============================
```

## Technical Details

### RISC-V Assembly (lightsigns_asm.s)
- Architecture: RV32IMD (32-bit RISC-V with multiply/divide and double-precision float)
- ABI: ilp32d
- Compiler: riscv32-linux-gcc
- Calling convention: Standard RISC-V
- Stack frame: 16 bytes
- Preserved registers: ra, s0, s1

### Sensor Reading (sensors_linux.c)
- Moving median filter with window size of 5 readings
- Non-blocking serial I/O
- 2-second delay between readings (DHT11 requirement)
- Line-by-line parsing to handle multiple response formats
- Error handling for sensor failures

### C Wrapper (lightsigns.c)
- `turn_on_all_lights()`: Controls all 3 LEDs sequentially
- Uses assembly function `control_led()` for each LED operation
- Sends serial commands to Pico W hardware

## Reusability

The assembly function `control_led()` can be reused in other modules:

```c
#include "lightsigns.h"

// Turn on specific LED
LedControl led = {.led_id = 1, .state = 1, .count = 0};
int result = control_led(&led);

// Turn off LED
led.state = 0;
result = control_led(&led);
```

## Requirements Met

✅ **Assembly function**: `control_led()` in RISC-V assembly  
✅ **Struct parameter**: LedControl struct passed by pointer  
✅ **Struct member access**: Uses offsets 0, 4, 8 bytes  
✅ **Functionality**: Controls track light signals  
✅ **Hardware integration**: Communicates with Pico W via serial  
✅ **Sensor integration**: Reads DHT11 for environmental monitoring
