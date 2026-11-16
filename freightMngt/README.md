# freightMngt — Java application

Console application for the Railway Freight Management System. It loads sample data from CSV files on start-up and runs everything in memory, except menu option 18, which uses the Oracle database.

## Build and run

Requirements: **JDK 23** and **Maven 3.9+**.

```sh
mvn test                      # build and run the 2062 unit tests
mvn -q dependency:build-classpath -Dmdep.outputFile=cp.txt
java -cp "target/classes:$(cat cp.txt)" app.Main
```

Run the commands from this folder, because the CSV datasets are read with relative paths.

## Menu

| # | Option | What it shows |
|---|---|---|
| 1 | Calculate Travel Time | Travel time between stations for a chosen locomotive |
| 2 | Bay Content Overview | Stock in each warehouse bay after the wagons are unloaded |
| 3 | Dispatch Stock | FEFO/FIFO dispatch across bays, with a preview before confirming |
| 4 | Relocate Stock | Moving lots between bays with capacity checks |
| 5 | Inventory Orders Pick | Order allocation and picking plans with carts |
| 6 | Returned Orders Quarantine | Returns workflow: quarantine, decisions, quality inspection, audit log |
| 7 | Station Search | Searching and filtering the 62 000+ European stations |
| 8 | 2DTree | Balanced 2D-tree built over station coordinates |
| 9 | Nearest Station Search | N nearest stations to a point (Haversine distance) |
| 10 | Radius Search | Stations within a radius of a point |
| 11 | Range Query | Stations inside a latitude/longitude rectangle |
| 12 | Train Dispatch Scheduler | Creating and scheduling train dispatches |
| 13 | Station Upgrade | Topological sort of upgrade dependencies, with cycle detection |
| 14 | Freights Route View | Creating freights and assigning routes |
| 15 | Aware Shortest Paths | Shortest paths that allow negative costs (Bellman-Ford) |
| 16 | Rail Hub Centrality | Hub ranking by betweenness, harmonic closeness and strength (takes up to a minute) |
| 17 | Maximum Throughput | Max flow between two hubs (Edmonds-Karp) |
| 18 | Train Assembly | Assembling a train with PL/SQL validation (needs the Oracle database, see the root README) |

## Code layout

| Package | Contents |
|---|---|
| `app.model` | Domain classes (stations, wagons, locomotives, lots, orders…) |
| `app.repositories` | In-memory repositories |
| `app.controller` | Controllers used by the views |
| `app.service` | Business logic, grouped by sprint |
| `app.utils.graph` | Graph library and algorithms (Dijkstra, Bellman-Ford, Brandes, MST, max flow, topological sort) |
| `app.db` | Oracle connection and DAOs |
| `app.view` | Console menus |

Algorithm write-ups and complexity analyses are in [`docs/`](docs).
