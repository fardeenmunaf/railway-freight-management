# USLP09: Train Assembly - List Available Locomotives and Wagons

## User Story
**As a Traffic Manager**, I want to be able to assemble and assign a train to a route by viewing available locomotives and wagons, distinguishing between those in transit and those parked at stations.

---

## Acceptance Criteria
- Selection Interface: Allow the user to select locomotives and carriages to form a train composition.

- List Visualization (In Transit):
   - Identify locomotives/carriages currently moving.
   - Must display: The final destination of the current route.

- List Visualization (Parked):
   - Identify locomotives/carriages currently stationary.
   - Must display: The station where they are parked.
   - Sorting: Parked items must be sorted in descending order of distance from the route's starting point.

- Technical Constraint: Database access must be performed strictly through PL/SQL functions.


---

## Done Concept
- PL/SQL functions for retrieving rolling stock status created.
- Logic to calculate distance between parked station and route start implemented.
- UI/Component created to list and select items.
- Sorting algorithm implemented correctly.
- Assign logic (link train to route) implemented.
- Unit/Integration tests passed.
- Code reviewed and merged.

---

## Test Cases

- TC1: View "In Transit" Items - The list shows the item and its Final Destination column is populated.
- TC2: View "Parked" Items - The list shows the item and its Current Station is populated.
- TC3: Sort Parked Items - Item at Station A (100km from start) appears before Item at Station B (50km from start).
- TC4: Mixed List - The UI clearly distinguishes between parked and in-transit items in the same selection pool.
- TC5: Database Access - Verify that no direct SELECT queries are used in the app code; data must come from the PL/SQL package.

---

## PL/SQL Functions Overview

### 1. fn_get_available_locomotives(p_facility_id NUMBER)
Returns a cursor with all available locomotives for a given facility/route starting point.

**Returns:** SYS_REFCURSOR with columns:
- locomotive_id, name, brand, model, year, power_kw
- max_speed_kmh, operational_speed_kmh, total_weight_t, length_m
- locomotive_type, current_location, location_id, location_lat, location_lon
- distance_from_departure_km, status, transit_destination, transit_arrival_date, operator_company

### 2. fn_get_available_wagons(p_facility_id NUMBER)
Returns a cursor with all available wagons for a given facility/route starting point.

**Returns:** SYS_REFCURSOR with columns:
- wagon_id, model_name, wagon_type, constructor, service_year
- payload_t, tare_t, volume_m3, length_m, width_m, height_m, max_speed_kmh
- current_location, location_id, location_lat, location_lon, distance_from_departure_km
- status, parking_track_name, parking_track_id, parking_position, operator_company, in_active_freight

### 3. fn_validate_train_composition(p_service_id NUMBER)
Validates if a train composition meets all technical requirements (weight limits, length, speed compatibility, etc.).

**Returns:** VARCHAR2 - Validation result message (e.g., "VALID" or error description)

### 4. fn_assemble_train_composition(p_service_id NUMBER, p_locomotive_ids VARCHAR2, p_wagon_ids VARCHAR2)
Assembles a train by assigning locomotives and wagons to a service. (Optional - for manual testing)

**Returns:** VARCHAR2 - Success/failure message

---

## Author
Diogo Remelgado

## Date
2026-01-02

## Version
1.0

