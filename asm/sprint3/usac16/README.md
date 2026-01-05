# USAC16 - Railway Track Management System

## User Story
As a User, I intend to manage the operational state of the station's railway tracks to ensure safe and efficient train flow.

## Overview
This system implements a comprehensive railway track management solution that coordinates track assignments, operational status, and visual signaling for a railway station. The implementation integrates three key components:

1. **Track Management** - Core logic for managing track states and assignments
2. **Synoptic Board** - Visual display of track status and system statistics
3. **Light Signals** - Automated light signal control synchronized with track states

## Features

### Core Operations
- **Assign Track to Arriving Train**: Automatically finds and assigns an available track to an incoming train
- **Emergency Stop**: Issues emergency stop when no tracks are available
- **Set Track as Non-Operational**: Marks a track as unavailable for maintenance or other reasons
- **Set Track as Free**: Manually frees up a track for use
- **Give Departure Order**: Authorizes a train to depart from an occupied track

### Integration
All operations automatically update:
- **Board Display**: Real-time visual representation of all tracks
- **Light Signals**: Synchronized light signals reflecting track states
- **Statistics**: Comprehensive tracking of arrivals, departures, and emergency stops

## Technical Implementation

### Assembly Function Requirement ✓
The system includes an assembly implementation of the track assignment function (`assign_track_asm`) which:
- Takes a `TrackSystem` struct pointer as parameter (fulfilling the requirement for at least one function in assembly with a struct parameter)
- Searches for available tracks
- Updates track state and statistics
- Returns track number or -1 for emergency stop

**File**: `tracks_asm.s` (RISC-V 32-bit assembly)

### Component Architecture

```
┌─────────────────────────────────────────────────┐
│                  manager.c                      │
│         (Main control loop & coordination)      │
└────────┬───────────────────────┬────────────────┘
         │                       │
    ┌────▼────┐            ┌────▼────┐
    │ tracks  │            │  board  │
    │ (C+ASM) │            │   (C)   │
    └────┬────┘            └────┬────┘
         │                      │
    ┌────▼───────┐         ┌───▼──────┐
    │ lightsigns │◄────────┤    ui    │
    │    (C)     │         │   (C)    │
    └────────────┘         └──────────┘
```

### File Structure
```
usac16/
├── tracks.h              - Track data structures and function declarations
├── tracks.c              - Track management implementation (C)
├── tracks_asm.s          - Track assignment in assembly (RISC-V)
├── board.h              - Board display declarations
├── board.c              - Synoptic board implementation
├── lightsigns.h         - Light signal structures
├── lightsigns.c         - Light signal control
├── ui.h                 - User interface declarations
├── ui.c                 - User interface implementation
├── manager.c            - Main application and control logic
├── Makefile             - Build configuration
└── README.md            - This file
```

## Building and Running

### Prerequisites
- GCC compiler
- GNU Assembler (as)
- Linux/Unix environment (or WSL on Windows)
- Terminal with ANSI color support

### Build
```bash
make
```

### Run
```bash
make run
```

### Clean
```bash
make clean
```

## Usage Guide

### Login
1. Launch the application
2. Enter your username when prompted
3. You'll be greeted with the main menu

### Main Menu Options

**1. Assign Track to Arriving Train**
- Select option 1
- Enter the train ID (e.g., "TR001", "EXPRESS123")
- System will automatically assign an available track
- If no tracks available, emergency stop is issued

**2. Set Track as Non-Operational**
- Select option 2
- Enter track number (1-6)
- Track will be marked as non-operational (maintenance mode)
- Light signal changes to BLUE

**3. Set Track as Free**
- Select option 3
- Enter track number (1-6)
- Track will be marked as available
- Light signal changes to GREEN

**4. Give Departure Order**
- Select option 4
- Enter track number (1-6)
- Train on that track will be authorized to depart
- Track becomes free after departure
- Light signal changes to GREEN

**5. Refresh Display**
- Select option 5
- Refreshes the entire board display

**0. Exit System**
- Select option 0
- System shuts down gracefully
- Final statistics are displayed

## Track States

| State            | Color  | Light Signal | Description                    |
|------------------|--------|--------------|--------------------------------|
| FREE             | Green  | GREEN        | Available for assignment       |
| OCCUPIED         | Yellow | RED          | Train currently on track       |
| NON-OPERATIONAL  | Red    | BLUE         | Maintenance/unavailable        |
| EMERGENCY        | Magenta| RED          | Emergency stop condition       |

## Light Signal Colors

- **GREEN**: Track is free, train can proceed
- **RED**: Track is occupied or emergency stop
- **YELLOW**: Caution (currently unused, reserved for future)
- **BLUE**: Track under maintenance/non-operational

## Logging

All operations are logged to `track_manager.log` with timestamps:
```
[2026-01-04 10:30:15] User: operator1      | Operation: ASSIGN_TRACK              | Train TR001 assigned to track 1
[2026-01-04 10:31:20] User: operator1      | Operation: GIVE_DEPARTURE            | Departure order given for track 1
```

## Acceptance Criteria Compliance

✓ **Track Assignment**: Assembly function assigns tracks to arriving trains  
✓ **Emergency Stop**: System issues emergency stop when no tracks available  
✓ **Non-Operational State**: Tracks can be set as non-operational  
✓ **Free State**: Tracks can be set as free  
✓ **Departure Orders**: System can issue departure orders  
✓ **Board Integration**: All operations update the synoptic board  
✓ **LightSigns Integration**: All operations update light signals  
✓ **Assembly Requirement**: `assign_track_asm()` implemented in RISC-V assembly with struct parameter  
✓ **Consistency**: All components remain synchronized with track state changes  

## Testing Scenarios

### Scenario 1: Normal Train Flow
1. Start system with 6 available tracks
2. Assign track to train "TR001" → Track 1 assigned, light RED
3. Assign track to train "TR002" → Track 2 assigned, light RED
4. Give departure to Track 1 → Track 1 free, light GREEN
5. Verify statistics updated correctly

### Scenario 2: Emergency Stop
1. Set all tracks as occupied or non-operational
2. Attempt to assign track to train "TR999"
3. Emergency stop should be issued
4. Emergency stop counter incremented

### Scenario 3: Maintenance Mode
1. Set Track 3 as non-operational → Light BLUE
2. Attempt to assign train → Track 3 should be skipped
3. Set Track 3 as free → Light GREEN
4. Track now available for assignment

## System Statistics

The system tracks:
- **Total Tracks**: Number of tracks in the system (default: 6)
- **Free Tracks**: Currently available tracks
- **Occupied Tracks**: Tracks with trains
- **Non-Operational Tracks**: Tracks under maintenance
- **Total Arrivals**: Cumulative train arrivals
- **Total Departures**: Cumulative train departures
- **Emergency Stops**: Number of emergency stops issued

## Design Decisions

### Why Assembly for Track Assignment?
The track assignment operation is critical and frequently used. Implementing it in assembly:
- Demonstrates low-level struct manipulation
- Provides direct control over memory access patterns
- Fulfills the Sprint 3 requirement for assembly with struct parameters

### Integration Philosophy
The system follows a synchronized update model where every track state change triggers:
1. Track system state update
2. Light signal recalculation
3. Board display refresh
4. Log entry creation

This ensures consistency across all components.

## Future Enhancements

- Network connectivity for remote monitoring
- Database integration for persistent storage
- Train scheduling and conflict detection
- Multi-station coordination
- Real-time sensor integration
- Mobile app interface

## Authors
Developed for Sprint 3 - USAC16  
Railway Track Management System  
PI 25-26 G113

## License
Educational project - All rights reserved
