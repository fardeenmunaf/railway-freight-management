# INSERT_ELECTRIC_LOCOMOTIVE Function Documentation

## 📋 Overview

The `insert_electric_locomotive` function is a PL/SQL stored function designed to insert electric locomotive records into the railway database system. It validates electric-specific attributes (voltage and frequency) and ensures data integrity across multiple tables.

**Function Type:** PL/SQL Function  
**Return Type:** NUMBER (Locomotive ID)  
**Dependency:** Function inputs only - no SYSDATE used for full testability

---

## 🔧 Function Signature

```sql
CREATE OR REPLACE FUNCTION insert_electric_locomotive(
    p_operator_id IN NUMBER,
    p_locomotive_type IN NUMBER,
    p_name IN VARCHAR2,
    p_brand IN VARCHAR2,
    p_model IN VARCHAR2,
    p_year IN NUMBER,
    p_power_kw IN NUMBER,
    p_acc_ms2 IN NUMBER,
    p_length_m IN NUMBER,
    p_width_m IN NUMBER,
    p_height_m IN NUMBER,
    p_max_weight_t IN NUMBER,
    p_total_weight_t IN NUMBER,
    p_max_speed_kmh IN NUMBER,
    p_oper_speed_kmh IN NUMBER,
    p_num_bogies IN NUMBER,
    p_bogie_type IN VARCHAR2,
    p_traction_kn IN VARCHAR2,
    p_voltage IN NUMBER,
    p_frequency_hz IN NUMBER,
    p_start_facility IN NUMBER   
)
RETURN NUMBER
```

---

## 📝 Parameters

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `p_operator_id` | NUMBER | ✅ Yes | Company ID of operator (FK to USER table) |
| `p_locomotive_type` | NUMBER | ✅ Yes | Type ID: 1=Electric, 2=Diesel (FK to LOCOMOTIVE_TYPE) |
| `p_name` | VARCHAR2 | ✅ Yes | Locomotive name/identifier (NOT NULL) |
| `p_brand` | VARCHAR2 | ✅ Yes | Manufacturer brand |
| `p_model` | VARCHAR2 | ✅ Yes | Model designation |
| `p_year` | NUMBER | ✅ Yes | Manufacturing year |
| `p_power_kw` | NUMBER | ✅ Yes | Power output in kilowatts |
| `p_acc_ms2` | NUMBER | ✅ Yes | Acceleration in m/s² |
| `p_length_m` | NUMBER | ✅ Yes | Length in meters |
| `p_width_m` | NUMBER | ✅ Yes | Width in meters |
| `p_height_m` | NUMBER | ✅ Yes | Height in meters |
| `p_max_weight_t` | NUMBER | ✅ Yes | Maximum weight in tonnes |
| `p_total_weight_t` | NUMBER | ✅ Yes | Total operating weight in tonnes |
| `p_max_speed_kmh` | NUMBER | ✅ Yes | Maximum speed in km/h |
| `p_oper_speed_kmh` | NUMBER | ✅ Yes | Operational speed in km/h |
| `p_num_bogies` | NUMBER | ✅ Yes | Number of bogies |
| `p_bogie_type` | VARCHAR2 | ✅ Yes | Bogie configuration (e.g., 'Bo-Bo') |
| `p_traction_kn` | VARCHAR2 | ✅ Yes | Traction force in kilonewtons |
| `p_voltage` | NUMBER | ✅ **REQUIRED FOR ELECTRIC** | Voltage in kV (NOT NULL for electric) |
| `p_frequency_hz` | NUMBER | ✅ **REQUIRED FOR ELECTRIC** | Frequency in Hz (NOT NULL for electric) |
| `p_start_facility` | NUMBER | ✅ Yes | Starting facility ID |

---

## ✅ Validation Rules

### Electric-Specific Validation
```sql
IF p_voltage IS NULL OR p_frequency_hz IS NULL THEN
    RAISE_APPLICATION_ERROR(-20001, 
        'Electric locomotive requires voltage and frequency.');
END IF;
```

**Error Code:** `-20001`  
**Condition:** Both `voltage` and `frequency_hz` must be NOT NULL  
**Exception:** `ORA-20001` is raised if either is NULL

### Database Constraints
- **Foreign Keys:** operator_id, locomotive_type must exist in referenced tables
- **CHECK Constraints:** locomotive_type must be 1 or 2
- **NOT NULL:** name, brand, model, year, power_kw, etc.

---

## 🔄 Function Workflow

```
1. VALIDATE electric attributes
   ↓ (If NULL → raise ORA-20001)
   ↓
2. INSERT into LOCOMOTIVE table
   - Insert main attributes
   - Use CURRENT_TIMESTAMP for created_at
   - Return locomotive_id
   ↓
3. INSERT into LOCOMOTIVE_ELECTRIC table
   - Link locomotive_id to electric attributes
   - Store voltage and frequency_hz
   ↓
4. RETURN locomotive_id
   ✅ Success
```

---

## 📊 Data Model

### LOCOMOTIVE Table
- `locomotive_id` (PK, auto-generated)
- `operator_id` (FK to USER)
- `locomotive_type` (FK to LOCOMOTIVE_TYPE, CHECK 1-2)
- `name` (NOT NULL)
- Basic specifications (brand, model, year, power, etc.)
- starting facility
- `created_at` (CURRENT_TIMESTAMP)

### LOCOMOTIVE_ELECTRIC Table
- `locomotive_id` (PK, FK to LOCOMOTIVE)
- `voltage` (NOT NULL)
- `frequency_hz` (NOT NULL)

---

## 🧪 Test Suite

Three anonymous blocks test different scenarios:

### TEST 1: Valid Insert ✅

**File:** `testblock1.sql`

**Purpose:** Verify successful insertion with all valid attributes

**Test Data:**
```
Name: E-Loco Valid
Brand: Siemens
Model: Vectron-V1
Voltage: 25 kV
Frequency: 50 Hz
Start Facility: 50
```

**Expected Result:**
```
✅ SUCCESS - Locomotive created!
   Locomotive ID: [auto-generated]
   VERIFICATION confirms row in both tables
```

**What it tests:**
- ✅ Valid INSERT into LOCOMOTIVE
- ✅ Valid INSERT into LOCOMOTIVE_ELECTRIC
- ✅ Automatic ID generation
- ✅ Foreign key constraints pass
- ✅ Data retrieval from both tables
- ✅ Exception handling for errors

**Execution:**
```sql
@testblock1.sql
```

---

### TEST 2: Missing Voltage Validation ❌

**File:** `testblock2.sql`

**Purpose:** Verify validation catches missing voltage (NULL)

**Test Data:**
```
Name: E-Loco Missing Voltage
Brand: Siemens
Model: Vectron-Test
Voltage: NULL  ← INTENTIONALLY NULL
Frequency: 50 Hz
Start Facility: 50
```

**Expected Result:**
```
✅ EXPECTED ERROR CAUGHT:
   Error Code: -20001
   Error Message: Electric locomotive requires voltage and frequency.
   Status: VALIDATION WORKING CORRECTLY
   
VERIFICATION - inserted_count = 0
(No row inserted due to ROLLBACK)
```

**What it tests:**
- ✅ Validation function detects NULL voltage
- ✅ Correct error code (-20001) is raised
- ✅ Transaction is rolled back automatically
- ✅ No corrupted data in database

**Execution:**
```sql
@testblock2.sql
```

---

### TEST 3: Missing Frequency Validation ❌

**File:** `testblock3.sql`

**Purpose:** Verify validation catches missing frequency (NULL)

**Test Data:**
```
Name: E-Loco-No-Frequency
Brand: Siemens
Model: Test-Model
Voltage: 25 kV
Frequency: NULL  ← INTENTIONALLY NULL
Start Facility: 50
```

**Expected Result:**
```
✅ EXPECTED ERROR CAUGHT:
   Error Code: -20001
   Error Message: Electric locomotive requires voltage and frequency.
   Status: VALIDATION WORKING CORRECTLY
```

**What it tests:**
- ✅ Validation function detects NULL frequency
- ✅ Correct error code (-20001) is raised
- ✅ Transaction is rolled back automatically
- ✅ No corrupted data in database

**Execution:**
```sql
@testblock3.sql
```

---

## 🛠️ How to Use

### Prerequisites
1. Schema must be initialized
2. COMPANY must exist
3. USER/OPERATOR must exist (for FK constraint)
4. LOCOMOTIVE_TYPE must exist (id=1 for Electric)

### Run Tests
```bash
# Test 1: Valid insert
sqlplus appuser/password @testblock1.sql

# Test 2: Validation error (missing voltage)
sqlplus appuser/password @testblock2.sql

# Test 3: Validation error (missing frequency)
sqlplus appuser/password @testblock3.sql
```

---

## 🐛 Troubleshooting

| Error | Cause | Solution |
|-------|-------|----------|
| `ORA-20001` | Missing voltage or frequency | Both must be NOT NULL for electric locomotives |
| `ORA-01400` | NULL in required field | Check all mandatory parameters have values |
| `ORA-02291` | FK constraint violation | Verify operator_id and locomotive_type exist |
| `ORA-02290` | CHECK constraint violation | locomotive_type must be 1 or 2 |

---

## 📌 Important Notes

- **No SYSDATE in function:** Uses input parameters only, making tests fully deterministic
- **Automatic ROLLBACK:** On any error, transaction rolls back automatically
- **ID Generation:** Uses Oracle IDENTITY to auto-generate locomotive_id
- **Two-Table Insert:** Atomic transaction spanning LOCOMOTIVE and LOCOMOTIVE_ELECTRIC
- **Test Independence:** Each test block is independent and can run separately

---

## 📚 Related Objects

- **LOCOMOTIVE** - Main locomotive table
- **LOCOMOTIVE_ELECTRIC** - Electric-specific attributes
- **LOCOMOTIVE_TYPE** - Type reference (1=Electric, 2=Diesel)
- **USER** - Operator/company reference

---

**Last Updated:** 2025-12-10  
**Status:** Production Ready  
**Version:** 1.0
