# USBD38: Add a New Gauge

## User Story
**As a Planner**, I want to add a new gauge to the database so that it can later be associated with wagons and rail lines.

---

## Acceptance Criteria
- A new gauge can be successfully added to the database with all required attributes (e.g., Gauge Code/ID, Width, Standard Name).  
- Insert fails if the gauge already exists (Duplicate Primary Key).  
- Insert succeeds when valid gauge data is provided.  
- All database constraints (PK, NOT NULL, CHECK) remain satisfied.

---

## Done Concept
- A database function exists to register a new gauge.  
- Gauge data (e.g., Gauge ID, value in mm) is inserted correctly into the Gauge table.  
- Inserts fail if required gauge fields are missing or invalid (e.g., negative width).  
- Documentation updated (tables, attributes, and steps for registration).  
- Anonymous block created to demonstrate success and failure scenarios.

---

## Database Schema

### GAUGE Table Structure
```sql
CREATE TABLE GAUGE
(
    gauge_id NUMBER PRIMARY KEY,
    name     VARCHAR2(255) NOT NULL,
    size_mm  NUMBER        NOT NULL
);
```

### Table Attributes
| Attribute | Type | Constraints | Description |
|-----------|------|-------------|-------------|
| gauge_id | NUMBER | PRIMARY KEY | Unique identifier for the gauge |
| name | VARCHAR2(255) | NOT NULL | Standard name of the gauge |
| size_mm | NUMBER | NOT NULL | Width of the gauge in millimeters |

---

## Function: `register_gauge`

### Purpose
Registers a new gauge in the database with comprehensive validation.

### Signature
```sql
FUNCTION register_gauge(
    p_gauge_id IN NUMBER,
    p_name IN VARCHAR2,
    p_size_mm IN NUMBER
) RETURN VARCHAR2
```

### Parameters
| Parameter | Type | Description |
|-----------|------|-------------|
| p_gauge_id | NUMBER | Unique identifier for the new gauge |
| p_name | VARCHAR2 | Name/standard designation of the gauge |
| p_size_mm | NUMBER | Width of the gauge in millimeters |

### Return Value
Returns a VARCHAR2 string:
- **SUCCESS message**: If the gauge was registered successfully
- **ERROR message**: If validation failed or insertion was not possible

### Validations Performed

1. **NULL Gauge ID Check**: Ensures gauge_id is provided
2. **NULL Name Check**: Ensures gauge name is provided
3. **Empty Name Check**: Ensures gauge name is not just whitespace
4. **NULL Size Check**: Ensures gauge size is provided
5. **Negative/Zero Size Check**: Ensures size is positive (> 0 mm)
6. **Maximum Size Check**: Ensures size is realistic (≤ 3000 mm)
7. **Duplicate ID Check**: Prevents duplicate primary key violations
8. **Duplicate Gauge Check**: Prevents inserting gauges with identical name and size

---

## Test Cases

### Test Suite Overview
The test suite includes 12 comprehensive test cases covering all acceptance criteria:

| Test # | Test Name | Type | Expected Result |
|--------|-----------|------|-----------------|
| 1 | Valid Gauge Registration | SUCCESS | Gauge inserted successfully |
| 2 | Duplicate Gauge ID | ERROR | Primary key violation error |
| 3 | Duplicate Name and Size | ERROR | Duplicate gauge error |
| 4 | NULL Gauge ID | ERROR | NULL gauge_id error |
| 5 | NULL Gauge Name | ERROR | NULL name error |
| 6 | Empty Gauge Name | ERROR | Empty name error |
| 7 | NULL Gauge Size | ERROR | NULL size error |
| 8 | Negative Gauge Size | ERROR | Invalid size error |
| 9 | Zero Gauge Size | ERROR | Invalid size error |
| 10 | Unreasonably Large Size | ERROR | Maximum size exceeded error |
| 11 | Second Valid Registration | SUCCESS | Second gauge inserted successfully |
| 12 | Duplicate Existing Gauge | ERROR | Duplicate existing ID error |

---

**Implementation Date**: 2026-01-01  
**Version**: 1.0  
**Status**: Complete

