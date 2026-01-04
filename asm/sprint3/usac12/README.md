# USAC12 — Generate a Text File with User Actions

## Context
Implementation of Sprint 3 US to log all user actions to a text file for auditing and monitoring purposes.

## User Story
As a System Administrator, I want to generate a text file with user actions so that I can track user activity, audit system usage, and maintain accountability.

## Implementation

### Log File Format
Every user action is logged to `manager.log` in the following format:

```
timestamp=YYYY-MM-DD HH:MM:SS user=<username> action=<ACTION>
```

**Example:**
```
timestamp=2026-01-04 14:32:15 user=guilherme action=SHOW_ENV
timestamp=2026-01-04 14:32:45 user=guilherme action=LIGHTS_ON
timestamp=2026-01-04 14:33:10 user=guilherme action=EXIT
```

### Logged Actions

| Action | Description |
|--------|-------------|
| `SHOW_ENV` | User requested environmental sensor data (Option 1) |
| `LIGHTS_ON` | User activated all track lights (Option 2) |
| `EXIT` | User exited the program (Option 0) |
| `NONE` | Invalid or unrecognized action |

### Features

- **Automatic logging**: Every action is automatically recorded
- **Append mode**: New logs are added without overwriting existing entries
- **Timestamp**: Each entry includes date and time (YYYY-MM-DD HH:MM:SS format)
- **User identification**: Username is recorded with each action
- **Non-intrusive**: Logging failures don't interrupt normal operation
- **Persistent**: Log file survives program restarts

## File Structure

```
usac12/
├── manager.c              # Main program with log_record() function
├── ui.h, ui.c             # User interface
├── lightsigns.c           # LED control functions
├── sensors_linux.c        # Sensor reading
├── Makefile               # Build configuration
├── manager.log            # Generated log file (created at runtime)
└── README.md              # This file
```

## Build and Execution

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

1. Run the program
2. Login with username
3. Perform actions (show environment, turn on lights, exit)
4. All actions are automatically logged to `manager.log`

## Example Session

### User Actions
```
Username: guilherme

=== MENU MANAGER ===
1 - Mostrar condições ambientais
2 - Ligar todas as luzes
0 - Sair
> 1
[User views sensor data]

> 2
[User turns on all lights]

> 0
Manager encerrado.
```

### Resulting Log File (manager.log)
```
timestamp=2026-01-04 14:32:15 user=guilherme action=SHOW_ENV
timestamp=2026-01-04 14:32:45 user=guilherme action=LIGHTS_ON
timestamp=2026-01-04 14:33:10 user=guilherme action=EXIT
```

## Log Analysis

### View Recent Actions
```bash
tail -n 20 manager.log
```

### Count Actions per User
```bash
grep "user=guilherme" manager.log | wc -l
```

### Find Specific Action
```bash
grep "action=LIGHTS_ON" manager.log
```

### Actions in Last Hour
```bash
grep "$(date '+%Y-%m-%d %H')" manager.log
```

### Extract Timestamps
```bash
cut -d' ' -f1-2 manager.log | cut -d'=' -f2
```

## Technical Implementation

### log_record() Function

```c
void log_record(const char *user, const Instruction *inst)
{
    FILE *f = fopen("manager.log", "a");
    if (!f) return;

    const char *t = "NONE";
    if (inst->type == INST_SHOW_ENV)
        t = "SHOW_ENV";
    else if (inst->type == INST_LIGHTS_ON)
        t = "LIGHTS_ON";
    else if (inst->type == INST_EXIT)
        t = "EXIT";

    // Get current timestamp
    time_t now = time(NULL);
    struct tm *tm_info = localtime(&now);
    char timestamp[20];
    strftime(timestamp, sizeof(timestamp), "%Y-%m-%d %H:%M:%S", tm_info);

    fprintf(f, "timestamp=%s user=%s action=%s\n", timestamp, user, t);
    fclose(f);
}
```

### When Logging Occurs

The `log_record()` function is called:

1. **After SHOW_ENV** (Option 1 - View environmental conditions)
   - Logged when user successfully views sensor data

2. **After LIGHTS_ON** (Option 2 - Turn on all lights)
   - Logged when user activates track lights

3. **On EXIT** (Option 0 - Exit program)
   - Logged when user chooses to exit
   - Also logged on normal program termination

### Error Handling

- If log file cannot be opened, function returns silently
- Program continues normally even if logging fails
- No error messages displayed to user
- Prevents logging from disrupting user experience

## Log File Format Details

### Field Descriptions

- **timestamp**: ISO 8601-like format (YYYY-MM-DD HH:MM:SS)
- **user**: Username entered at login (max 32 characters)
- **action**: One of: SHOW_ENV, LIGHTS_ON, EXIT, NONE

### Parsing

The log format is designed to be:
- **Human-readable**: Easy to read with text editors
- **Machine-parseable**: Simple field=value format
- **Grep-friendly**: Each field can be filtered easily
- **Space-delimited**: Fields separated by single space

### Example Parsing in Shell

```bash
# Extract all fields
while IFS=' ' read -r timestamp user action; do
    ts=$(echo $timestamp | cut -d'=' -f2)
    usr=$(echo $user | cut -d'=' -f2)
    act=$(echo $action | cut -d'=' -f2)
    echo "[$ts] User '$usr' performed '$act'"
done < manager.log
```

## Security Considerations

### File Permissions
```bash
# Recommended permissions (owner read/write only)
chmod 600 manager.log

# Or group-readable for auditing
chmod 640 manager.log
```

### Audit Trail
- Log file provides accountability
- Can track when users access sensors
- Records when lights are activated
- Useful for troubleshooting and compliance

### Limitations
- No log rotation implemented (file grows indefinitely)
- No log encryption (sensitive data should not be logged)
- No integrity checking (logs can be manually edited)

## Log Rotation (Optional)

For long-running systems, implement log rotation:

```bash
# Manual rotation
mv manager.log manager.log.$(date +%Y%m%d)
touch manager.log

# Or use logrotate
# Add to /etc/logrotate.d/manager:
/path/to/manager.log {
    daily
    rotate 7
    compress
    missingok
    notifempty
}
```

## Requirements Met

✅ **Text file generation**: Creates `manager.log`  
✅ **User identification**: Logs username with each action  
✅ **Action tracking**: Records SHOW_ENV, LIGHTS_ON, EXIT  
✅ **Timestamp**: ISO format with date and time  
✅ **Append mode**: Preserves history across sessions  
✅ **Non-intrusive**: Silent operation, no user interruption

## Future Enhancements

- **Sensor values**: Log temperature/humidity readings
- **LED states**: Record which LEDs were activated
- **Error logging**: Track sensor failures, serial errors
- **Log rotation**: Automatic archival of old logs
- **JSON format**: Structured data for easier parsing
- **Remote logging**: Send logs to central server
- **Log encryption**: Protect sensitive information
