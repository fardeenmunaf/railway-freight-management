# REGISTER_WAGON_FULL Function Documentation

## Overview

The `REGISTER_WAGON_FULL` function is an Oracle PL/SQL stored procedure designed to register and manage railway wagon models and instances in a rail transportation database system. This function handles the complex operation of creating or retrieving wagon models and associating them with physical wagon instances and their operational parameters.

## Function Signature

```sql
CREATE OR REPLACE FUNCTION register_wagon_full (
  p_model_name IN WAGON_MODEL.name%TYPE,
  p_wagon_type IN WAGON_MODEL.wagon_type%TYPE,
  p_constructor IN WAGON_MODEL.constructor%TYPE,
  p_number_bogies IN WAGON_MODEL.number_bogies%TYPE,
  p_bogies IN WAGON_MODEL.bogies%TYPE,
  p_tare_t IN WAGON_MODEL.tare_t%TYPE,
  p_payload_t IN WAGON_MODEL.payload_t%TYPE,
  p_volume_m3 IN WAGON_MODEL.volume_m3%TYPE,
  p_length_m IN WAGON_MODEL.length_m%TYPE,
  p_width_m IN WAGON_MODEL.width_m%TYPE,
  p_height_m IN WAGON_MODEL.height_m%TYPE,
  p_max_speed_kmh IN WAGON_MODEL.max_speed_kmh%TYPE,
  p_operator_id IN WAGON.operator_id%TYPE,
  p_service_year IN WAGON.service_year%TYPE,
  p_gauges IN GAUGE.gauge_id%TYPE
)
RETURN NUMBER
```

## Parameters

### Wagon Model Parameters (WAGON_MODEL table)

| Parameter | Type | Description |
|-----------|------|-------------|
| `p_model_name` | VARCHAR2 | Unique identifier name for the wagon model |
| `p_wagon_type` | NUMBER | Type classification of the wagon |
| `p_constructor` | VARCHAR2 | Manufacturer/constructor identifier |
| `p_number_bogies` | NUMBER | Number of bogies (wheel assemblies) on the wagon |
| `p_bogies` | VARCHAR2 | Bogie configuration description |
| `p_tare_t` | NUMBER | Tare weight in metric tonnes (empty wagon weight) |
| `p_payload_t` | NUMBER | Maximum payload capacity in metric tonnes |
| `p_volume_m3` | NUMBER | Interior volume capacity in cubic meters |
| `p_length_m` | NUMBER | Wagon length in meters |
| `p_width_m` | NUMBER | Wagon width in meters |
| `p_height_m` | NUMBER | Wagon height in meters |
| `p_max_speed_kmh` | NUMBER | Maximum permitted speed in kilometers per hour |

### Wagon Instance Parameters

| Parameter | Type | Description |
|-----------|------|-------------|
| `p_operator_id` | NUMBER | Identifier of the operator managing the wagon |
| `p_service_year` | NUMBER | Year the wagon entered service |
| `p_gauges` | NUMBER | Track gauge identifier (foreign key reference) |

## Return Value

**Type:** NUMBER

**Description:** Returns the `wagon_id` of the newly created physical wagon instance. This ID uniquely identifies a specific wagon unit in the database.

## Function Logic Flow

### Step 1: Retrieve or Create WAGON_MODEL

The function first attempts to locate an existing wagon model in the `WAGON_MODEL` table using all the provided model specifications as matching criteria:

```sql
SELECT wagon_model_id INTO v_model_id
FROM WAGON_MODEL
WHERE name = p_model_name
  AND wagon_type = p_wagon_type
  AND constructor = p_constructor
  AND number_bogies = p_number_bogies
  AND bogies = p_bogies
  AND tare_t = p_tare_t
  AND payload_t = p_payload_t
  AND volume_m3 = p_volume_m3
  AND length_m = p_length_m
  AND width_m = p_width_m
  AND height_m = p_height_m
  AND max_speed_kmh = p_max_speed_kmh;
```

**If NO_DATA_FOUND Exception occurs:**
- A new wagon model is inserted into the `WAGON_MODEL` table with all provided specifications
- The newly generated `wagon_model_id` is captured using the `RETURNING` clause
- This ensures the function avoids duplicate model definitions for identical wagon specifications

### Step 2: Create Physical Wagon Instance

A new physical wagon instance is created in the `WAGON` table:

```sql
INSERT INTO WAGON (wagon_model, operator_id, service_year)
VALUES (v_model_id, p_operator_id, p_service_year)
RETURNING wagon_id INTO v_wagon_id;
```

**Key points:**
- The wagon is associated with the model (either existing or newly created)
- The operator and service year information is recorded
- The system generates a unique `wagon_id` for this physical instance

### Step 3: Associate Model with Track Gauge

The wagon model is linked to track gauge specifications in the `WAGON_GAUGE` table:

```sql
INSERT INTO WAGON_GAUGE (wagon_id, gauge_id)
VALUES (v_model_id, p_gauges);
```

**Exception Handling:**
- If a `DUP_VAL_ON_INDEX` exception occurs (duplicate model-gauge combination already exists), the error is silently ignored
- This prevents constraint violations when the same model is registered multiple times with identical gauge specifications

### Step 4: Return Wagon ID

The function returns the `wagon_id` of the newly created physical wagon instance, allowing the caller to reference the specific wagon unit.

## Error Handling

The function includes comprehensive exception handling:

```sql
EXCEPTION
WHEN OTHERS THEN
  DBMS_OUTPUT.PUT_LINE('ERROR in register_wagon_full_nodup: ' || SQLERRM);
  RAISE;
```

**Behavior:**
- All exceptions are caught and logged to output
- The exception is re-raised to the calling environment for proper error propagation
- The caller can handle specific error conditions based on the error message

---

## Test Cases

### Test Block 1: Valid Registration (Success Case)

**File:** `testblock1.sql`

**Purpose:** Validate successful registration of a wagon with all valid parameters.

```sql
DECLARE
  v_id NUMBER;
BEGIN
  v_id := REGISTER_WAGON_FULL(
    'Model_Valid',      -- Model name
    1,                  -- Wagon type
    'CP-123456',        -- Constructor
    2,                  -- Number of bogies
    'Duplo',            -- Bogie configuration
    20,                 -- Tare weight (t)
    50,                 -- Payload capacity (t)
    80,                 -- Volume (m³)
    12,                 -- Length (m)
    3,                  -- Width (m)
    4,                  -- Height (m)
    120,                -- Max speed (km/h)
    2,                  -- Operator ID
    2020,               -- Service year
    1                   -- Gauge ID
  );
  DBMS_OUTPUT.put_line('Wagon id = ' || v_id);
  COMMIT;
END;
```

**Expected Outcome:**
- Function executes successfully without errors
- A new wagon model (`Model_Valid`) is created in `WAGON_MODEL` table
- A new physical wagon instance is created in `WAGON` table
- The wagon is associated with gauge ID 1
- The generated wagon ID is displayed in the output
- All changes are committed to the database

**Test Scenario:** ✅ Happy path with all valid inputs and existing gauge reference.

---

### Test Block 2: Invalid Gauge Reference (Foreign Key Violation)

**File:** `testblock2.sql`

**Purpose:** Verify proper error handling when an invalid (non-existent) gauge ID is provided.

```sql
DECLARE
  v_id NUMBER;
BEGIN
  BEGIN
    v_id := REGISTER_WAGON_FULL(
      'Model_InvalidGauge', -- Model name
      1,                    -- Wagon type
      'CP-INVALID',         -- Constructor
      2,                    -- Number of bogies
      'Duplo',              -- Bogie configuration
      20,                   -- Tare weight (t)
      50,                   -- Payload capacity (t)
      80,                   -- Volume (m³)
      12,                   -- Length (m)
      3,                    -- Width (m)
      4,                    -- Height (m)
      120,                  -- Max speed (km/h)
      2,                    -- Operator ID
      2020,                 -- Service year
      999999                -- INVALID: Non-existent gauge ID
    );
  EXCEPTION
    WHEN OTHERS THEN
      RAISE_APPLICATION_ERROR(
        -20001,
        'Gauge inválido na criação de wagon.'
      );
  END;
  DBMS_OUTPUT.put_line('Wagon id = ' || v_id);
  COMMIT;
END;
```

**Expected Outcome:**
- The function attempts to insert into `WAGON_GAUGE` with a non-existent gauge ID
- The foreign key constraint fails
- The exception handler catches the error
- A custom application error (-20001) is raised with descriptive message
- The transaction does not complete; no wagon is created
- Error message: "Gauge inválido na criação de wagon." (Invalid gauge in wagon creation)

**Test Scenario:** ❌ Negative test case validating referential integrity constraints.

**Learning Objective:** Demonstrates the importance of validating foreign key references before inserting related data. The gauge ID must exist in the `GAUGE` table.

---

### Test Block 3: Missing Required Field (NOT NULL Constraint Violation)

**File:** `testblock3.sql`

**Purpose:** Test behavior when a mandatory column value (NOT NULL constraint) is not provided.

```sql
DECLARE
  v_id NUMBER;
BEGIN
  v_id := REGISTER_WAGON_FULL(
    'Model_MissingType',  -- Model name
    NULL,                 -- INVALID: wagon_type is NOT NULL in schema
    'CP-NOTNULL',         -- Constructor
    2,                    -- Number of bogies
    'Duplo',              -- Bogie configuration
    20,                   -- Tare weight (t)
    50,                   -- Payload capacity (t)
    80,                   -- Volume (m³)
    12,                   -- Length (m)
    3,                    -- Width (m)
    4,                    -- Height (m)
    120,                  -- Max speed (km/h)
    2,                    -- Operator ID
    2020,                 -- Service year
    1                     -- Gauge ID
  );
  DBMS_OUTPUT.put_line('Wagon id = ' || v_id);
  COMMIT;
EXCEPTION
  WHEN OTHERS THEN
    RAISE_APPLICATION_ERROR(
      -20002,
      'Erro esperado: campo obrigatório em falta (NOT NULL). Detalhe: ' || SQLERRM
    );
END;
```

**Expected Outcome:**
- The function attempts to insert a wagon model with `wagon_type = NULL`
- The `WAGON_MODEL` table has a NOT NULL constraint on the `wagon_type` column
- The INSERT statement raises a constraint violation error
- The outer exception handler catches this error
- A custom application error (-20002) is raised with contextual message
- The error message includes the original database error details (SQLERRM)
- The transaction does not complete; no wagon is created
- Error message: "Erro esperado: campo obrigatório em falta (NOT NULL). Detalhe: [ORA-01400 or similar]"

**Test Scenario:** ❌ Negative test case validating schema constraints.

**Learning Objective:** Demonstrates the importance of providing all mandatory fields. The `wagon_type` parameter cannot be NULL and must contain a valid integer value.

**Production Consideration:** In production systems, consider adding parameter validation at the function entry point to catch NULL values earlier and provide more user-friendly error messages before attempting database operations.

---

## Summary of Test Coverage

| Test | Scenario | Status | Key Validation |
|------|----------|--------|-----------------|
| **Test Block 1** | Valid registration with all correct parameters | ✅ PASS | Full workflow execution, wagon creation, model registration |
| **Test Block 2** | Invalid gauge foreign key reference | ❌ FAIL | Referential integrity, error handling, constraint validation |
| **Test Block 3** | Missing required field (NULL constraint) | ❌ FAIL | Schema constraints, NOT NULL validation, parameter validation |

## Key Design Patterns

### 1. Upsert (Update or Insert) Pattern
The function implements the upsert pattern by attempting to select an existing model before inserting a new one. This prevents duplicate model registrations for identical specifications.

### 2. Exception-Driven Control Flow
The function relies on exception handling (`NO_DATA_FOUND`) as part of its core logic, which is a valid but less modern approach. Modern implementations might use `MERGE` statements for better readability.

### 3. Composite Key Matching
The model lookup uses all wagon specification columns as matching criteria, ensuring uniqueness based on complete specification rather than just a name field.

### 4. Graceful Duplicate Handling
The function uses `DUP_VAL_ON_INDEX` exception handling in the gauge association step to gracefully handle cases where the same model-gauge combination is registered multiple times.