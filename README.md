# Railway Freight Management System

![Java](https://img.shields.io/badge/Java_23-ED8B00?logo=openjdk&logoColor=white)
![Oracle](https://img.shields.io/badge/Oracle_SQL%20%26%20PL%2FSQL-F80000?logo=oracle&logoColor=white)
![RISC-V](https://img.shields.io/badge/RISC--V_Assembly-283272)
![C](https://img.shields.io/badge/C-A8B9CC?logo=c&logoColor=black)
![Tests](https://img.shields.io/badge/tests-2062_passing-brightgreen)

> Group project for the **Integrative Project (Sem3PI)** of the Informatics Engineering degree (LEI) at **ISEP**, 2025/26.

A system for managing freight on a European railway network: warehouses at train stations, picking and dispatching stock, planning and assembling trains, and analysing the rail network with graph algorithms. It also includes low-level code that controls track signals on a microcontroller.

## What it does

**Warehouse and logistics**
- Unloads wagons into warehouse bays and dispatches stock using FEFO/FIFO rules (first expiring / first in, first out)
- Plans order picking, relocates stock and handles returned goods with quarantine and quality inspection
- Writes an audit log of every return decision

**Stations and spatial search** (62 000+ European stations)
- Balanced 2D-tree over latitude and longitude
- Nearest-station, radius and rectangular range searches using the Haversine distance

**Rail network analysis** (graph algorithms)
- Travel time between stations for a given locomotive
- Risk-aware shortest paths that allow negative costs (Bellman-Ford)
- Minimum spanning tree for a minimal backbone network, exported to Graphviz DOT and SVG
- Topological sort for station upgrade plans
- Hub centrality ranking (betweenness, harmonic closeness, strength)
- Maximum throughput between two hubs (max flow)
- Train dispatch scheduling

**Database** (Oracle SQL and PL/SQL)
- Relational model with 47 tables, sample data, queries, triggers, functions and procedures
- Train assembly that picks locomotives and wagons and validates the composition with PL/SQL functions

**Embedded systems** (RISC-V assembly and C)
- Circular buffers, sorting, median and encryption routines in RISC-V assembly
- A track manager that reads sensors, controls track light signals and talks to a Raspberry Pi Pico board

## Repository layout

| Folder | Contents |
|---|---|
| [`freightMngt/`](freightMngt) | Java console application (Maven) and its 2062 unit tests |
| [`sql-scripts/`](sql-scripts) | Oracle schema, sample data, queries and PL/SQL, one folder per user story |
| [`asm/`](asm) | RISC-V assembly and C programs, plus the Raspberry Pi Pico code |
| [`doc/`](doc) | Scrum artefacts and user story documentation |
| [`docker-compose.yml`](docker-compose.yml) | Local Oracle database with the schema and data already loaded |

## Running the Java application

Requirements: **JDK 23** and **Maven 3.9+**.

```sh
cd freightMngt
mvn test                      # build and run the 2062 tests
mvn -q dependency:build-classpath -Dmdep.outputFile=cp.txt
java -cp "target/classes:$(cat cp.txt)" app.Main
```

The app loads its sample data from CSV files on start-up (about 3 seconds) and opens a menu with 18 options. Everything except option 18 runs in memory, so no database is needed.

Option 16 (Rail Hub Centrality) computes metrics over the whole network and takes up to a minute.

### Database (only for option 18, Train Assembly)

Requirements: **Docker**.

```sh
docker compose up -d          # from the repository root
docker compose logs -f        # wait for "DATABASE IS READY TO USE!" (1–2 minutes the first time)
```

This starts Oracle Database Free and loads the schema, sample data and PL/SQL functions from `sql-scripts/`. To use another Oracle database, set `FREIGHT_DB_URL`, `FREIGHT_DB_USER` and `FREIGHT_DB_PASSWORD`.

## Building the assembly and C code

The programs in `asm/` target **RISC-V** and are built on Linux with the RISC-V GNU toolchain (`riscv64-linux-gnu-gcc`) and run with QEMU. Each folder has its own `Makefile`. The `.ino` sketches run on a Raspberry Pi Pico.

## Team

| Name | GitHub |
|---|---|
| Diogo Remelgado | [@Dremel123](https://github.com/Dremel123) |
| Eduardo Pacheco | [@eduardo-pacheco7](https://github.com/eduardo-pacheco7) |
| Fardeen Munaf | [@fardeenmunaf](https://github.com/fardeenmunaf) |
| Guilherme Tavares | [@gatavares](https://github.com/gatavares) |
| Mariana Gabriel | [@marianargabriel](https://github.com/marianargabriel) |
