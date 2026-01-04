# USBD45: Add a New Line with Initial Segment

## User Story
**As a Planner**, I want to add a new line with its initial segment to the database. A line must have at least one segment (Business Rule).

---

## Acceptance Criteria
- A new line can be successfully added to the database only if a valid initial segment is defined simultaneously.  
- The operation must be atomic: The Line and the initial Segment are inserted in the same transaction. If the Segment insertion fails, the Line insertion must be rolled back.  
- Insert succeeds when valid Line data (ID, name, owner) and valid initial Segment data (length, gauge, etc.) are provided.  
- Insert fails if the Line ID already exists.  
- Insert fails if the referenced Gauge or Owner do not exist.  
- All database constraints (PK, FK, NOT NULL, CHECK) remain satisfied.

---

## Done Concept
- A PL/SQL function exists to register a new Line and its first Segment atomically.  
- Line table is updated with the new line definition.  
- Line_Segment table is updated with the first segment linked to the new line.  
- Relationships between Segment and Gauge (FK) are valid.  
- Inserts fail if segment data is missing (enforcing the "at least one segment" rule).  
- Documentation updated (tables, attributes, and steps for registration).  
- Test cases demonstrate success and failure scenarios including atomicity/rollback.

---

## Database Schema

### LINE Table Structure
```sql
CREATE TABLE LINE
(
    line_id  NUMBER PRIMARY KEY,
    name     VARCHAR2(255) NOT NULL,
    owner_id NUMBER        NOT NULL,
    CONSTRAINT fk_line_owner FOREIGN KEY (owner_id) REFERENCES COMPANY (company_id)
);
```

### LINE_SEGMENT Table Structure
```sql
CREATE TABLE LINE_SEGMENT
(
    segment_id      NUMBER PRIMARY KEY,
    line_id         NUMBER       NOT NULL,
    length_km       NUMBER(8, 3) NOT NULL,
    electrif_type   NUMBER(1)    NOT NULL,
    gauge_id        NUMBER       NOT NULL,
    max_weight_kg_m NUMBER       NOT NULL,
    speed_limit_kmh NUMBER       NOT NULL,
    track_number    NUMBER       NOT NULL,
    position        NUMBER       NOT NULL,
    CONSTRAINT fk_line_segment_line FOREIGN KEY (line_id) REFERENCES LINE (line_id),
    CONSTRAINT fk_line_segment_gauge FOREIGN KEY (gauge_id) REFERENCES GAUGE (gauge_id)
);
```

### Table Attributes
| Table | Attribute | Type | Constraints | Description |
|-------|-----------|------|-------------|-------------|
| LINE | line_id | NUMBER | PRIMARY KEY | Unique identifier for the line |
| LINE | name | VARCHAR2(255) | NOT NULL | Name of the line |
| LINE | owner_id | NUMBER | NOT NULL, FK | Owner identifier (FK to COMPANY table) |
| LINE_SEGMENT | segment_id | NUMBER | PRIMARY KEY | Unique identifier for the segment |
| LINE_SEGMENT | line_id | NUMBER | NOT NULL, FK | Line identifier (FK to LINE table) |
| LINE_SEGMENT | length_km | NUMBER(8, 3) | NOT NULL | Length of the segment in kilometers |
| LINE_SEGMENT | electrif_type | NUMBER(1) | NOT NULL | Electrification type |
| LINE_SEGMENT | gauge_id | NUMBER | NOT NULL, FK | Gauge identifier (FK to GAUGE table) |
| LINE_SEGMENT | max_weight_kg_m | NUMBER | NOT NULL | Maximum weight per meter in kg/m |
| LINE_SEGMENT | speed_limit_kmh | NUMBER | NOT NULL | Speed limit in km/h |
| LINE_SEGMENT | track_number | NUMBER | NOT NULL | Track number |
| LINE_SEGMENT | position | NUMBER | NOT NULL | Position of the segment in the line |

---

## Function: `register_line_with_segment`

### Purpose
Registers a new line with its initial segment in the database atomically with comprehensive validation.

### Signature
```sql
FUNCTION register_line_with_segment(
    p_line_id IN NUMBER,
    p_line_name IN VARCHAR2,
    p_owner_id IN NUMBER,
    p_segment_id IN NUMBER,
    p_length_km IN NUMBER,
    p_electrif_type IN NUMBER,
    p_gauge_id IN NUMBER,
    p_max_weight_kg_m IN NUMBER,
    p_speed_limit_kmh IN NUMBER,
    p_track_number IN NUMBER,
    p_position IN NUMBER
) RETURN VARCHAR2
```

### Parameters
| Parameter | Type | Description |
|-----------|------|-------------|
| p_line_id | NUMBER | Unique identifier for the new line |
| p_line_name | VARCHAR2 | Name of the line |
| p_owner_id | NUMBER | Owner identifier (must exist in COMPANY table) |
| p_segment_id | NUMBER | Unique identifier for the initial segment (mandatory) |
| p_length_km | NUMBER | Length of the segment in kilometers |
| p_electrif_type | NUMBER | Electrification type code |
| p_gauge_id | NUMBER | Gauge identifier (must exist in GAUGE table) |
| p_max_weight_kg_m | NUMBER | Maximum weight per meter in kg/m |
| p_speed_limit_kmh | NUMBER | Speed limit in km/h |
| p_track_number | NUMBER | Track number |
| p_position | NUMBER | Position of the segment in the line (usually 1 for first segment) |

### Return Value
Returns a VARCHAR2 string:
- **SUCCESS message**: If the line and segment were registered successfully
- **ERROR message**: If validation failed or insertion was not possible

### Validations Performed

#### Line Validations:
1. **NULL Line ID Check**: Ensures line_id is provided
2. **NULL Name Check**: Ensures line name is provided
3. **Empty Name Check**: Ensures line name is not just whitespace
4. **NULL Owner ID Check**: Ensures owner_id is provided
5. **Positive Owner ID Check**: Ensures owner_id is positive (> 0)
6. **Owner Existence Check**: Verifies owner exists in COMPANY table
7. **Duplicate ID Check**: Prevents duplicate primary key violations
8. **Duplicate Line Check**: Prevents inserting lines with identical name and owner_id

#### Segment Validations (Mandatory):
9. **NULL Segment ID Check**: Ensures segment_id is provided (enforces business rule)
10. **NULL Length Check**: Ensures segment length is provided
11. **Positive Length Check**: Ensures length is positive (> 0)
12. **NULL Electrification Type Check**: Ensures electrif_type is provided
13. **NULL Gauge ID Check**: Ensures gauge_id is provided
14. **Gauge Existence Check**: Verifies gauge exists in GAUGE table
15. **NULL Max Weight Check**: Ensures max_weight_kg_m is provided
16. **Positive Max Weight Check**: Ensures max_weight_kg_m is positive (> 0)
17. **NULL Speed Limit Check**: Ensures speed_limit_kmh is provided
18. **Positive Speed Limit Check**: Ensures speed_limit_kmh is positive (> 0)
19. **NULL Track Number Check**: Ensures track_number is provided
20. **Positive Track Number Check**: Ensures track_number is positive (> 0)
21. **NULL Position Check**: Ensures position is provided
22. **Positive Position Check**: Ensures position is positive (> 0)
23. **Duplicate Segment ID Check**: Prevents duplicate segment primary key violations

---

## Test Cases

### Test Suite Overview
The test suite includes 13 comprehensive test cases covering all acceptance criteria:

| Test # | Test Name | Type | Expected Result |
|--------|-----------|------|-----------------|
| 1 | Valid Line and Segment Registration | SUCCESS | Line and segment inserted atomically |
| 2 | Missing Segment Data | ERROR | Segment ID NULL error (business rule) |
| 3 | Invalid Gauge ID | ERROR | Gauge does not exist, Line not inserted (rollback) |
| 4 | Duplicate Line ID | ERROR | Primary key violation error |
| 5 | Atomicity/Rollback Test | ERROR | Negative length error, Line not inserted (atomicity) |
| 6 | NULL Line ID | ERROR | NULL line_id error |
| 7 | NULL Line Name | ERROR | NULL name error |
| 8 | NULL Owner ID | ERROR | NULL owner_id error |
| 9 | Non-existent Owner ID | ERROR | Owner does not exist error |
| 10 | NULL Segment Length | ERROR | NULL length error |
| 11 | Zero Segment Length | ERROR | Positive length required error |
| 12 | Second Valid Registration | SUCCESS | Second line and segment inserted successfully |
| 13 | Duplicate Segment ID | ERROR | Segment ID already exists, Line not inserted (atomicity) |

---

**Implementation Date**: 2026-01-01  
**Version**: 2.0  
**Status**: Complete

