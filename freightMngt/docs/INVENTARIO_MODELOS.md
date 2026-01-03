# 📁 Inventário Completo de Modelos - FreightMngt

**Total de Modelos**: 48 + 1 auxiliar  
**Data**: 2025-01-10

---

## Lista Alfabética Completa

| # | Modelo | DAO Existe? | Tabela BD | Categoria | Prioridade |
|---|--------|-------------|-----------|-----------|------------|
| 1 | Cart | ❌ | CART | Picking | Média |
| 2 | Company | ✅ | COMPANY | Core | - |
| 3 | Dimensions | N/A | - | Auxiliar | - |
| 4 | Facility | ✅ | FACILITY | Core | - |
| 5 | FacilityFeatureType | ❌ | FACILITY_FEATURE_TYPE | Tipo | Baixa |
| 6 | FacilityType | ✅ | FACILITY_TYPE | Tipo | - |
| 7 | Gauge | ✅ | GAUGE | Tipo | - |
| 8 | InventoryBalance | ❌ | INVENTORY_BALANCE | Armazém | Média |
| 9 | InventoryLot | ❌ | INVENTORY_LOT | Armazém | Média |
| 10 | InventoryMove | ❌ | INVENTORY_MOVE | Armazém | Média |
| 11 | Item | ✅ | ITEM | Core | - |
| 12 | ItemCategory | ✅ | ITEM_CATEGORY | Tipo | - |
| 13 | Line | ✅ | LINE | Infraestrutura | - |
| 14 | LineSegment | ❌ | LINE_SEGMENT | Infraestrutura | Média |
| 15 | LineSegmentFacility | ❌ | LINE_SEGMENT_FACILITY | Associativa | Baixa |
| 16 | Locomotive | ❌ | LOCOMOTIVE | Material | **Alta** |
| 17 | LocomotiveDiesel | ❌ | LOCOMOTIVE_DIESEL | Material | Média |
| 18 | LocomotiveEletric | ❌ | LOCOMOTIVE_ELECTRIC | Material | Média |
| 19 | LocomotiveGauge | ❌ | LOCOMOTIVE_GAUGE | Associativa | Baixa |
| 20 | LocomotiveType | ✅ | LOCOMOTIVE_TYPE | Tipo | - |
| 21 | Order | ❌ | "ORDER" | Core | **Alta** |
| 22 | OrderLine | ❌ | ORDER_LINE | Core | **Alta** |
| 23 | ParkingQueue | ❌ | PARKING_QUEUE | Infraestrutura | Baixa |
| 24 | ParkingTrack | ❌ | PARKING_TRACK | Infraestrutura | Baixa |
| 25 | PickTask | ❌ | PICK_TASK | Picking | Média |
| 26 | PickWave | ❌ | PICK_WAVE | Picking | Média |
| 27 | QaInspection | ❌ | QA_INSPECTION | Qualidade | Baixa |
| 28 | QuarantineHold | ❌ | QUARANTINE_HOLD | Qualidade | Baixa |
| 29 | ReturnLine | ❌ | RETURN_LINE | Devolução | Baixa |
| 30 | ReturnOrder | ❌ | RETURN_ORDER | Devolução | Baixa |
| 31 | Role | ✅ | "ROLE" | Core | - |
| 32 | RoutePoint | ❌ | ? | Roteamento | Baixa |
| 33 | ServiceLocomotive | ❌ | SERVICE_LOCOMOTIVE | Operação | Baixa |
| 34 | Station | ❌ | FACILITY (especialização) | Infraestrutura | **Alta** |
| 35 | TrackType | ❌ | TRACK_TYPE | Tipo | Baixa |
| 36 | Train | ✅ | TRAIN | Core | - |
| 37 | TrainDispatch | ❌ | ? | Operação | Baixa |
| 38 | TrainService | ❌ | TRAIN_SERVICE | Operação | Baixa |
| 39 | TrainServiceComposition | ❌ | TRAIN_SERVICE_COMPOSITION | Operação | Baixa |
| 40 | TrainStop | ❌ | TRAIN_STOP | Operação | Baixa |
| 41 | User | ✅ | "USER" | Core | - |
| 42 | UserRole | ❌ | USER_ROLE | Associativa | Média |
| 43 | Wagon | ❌ | WAGON | Material | **Alta** |
| 44 | WagonGauge | ❌ | WAGON_GAUGE | Associativa | Baixa |
| 45 | WagonModel | ❌ | WAGON_MODEL | Material | **Alta** |
| 46 | WagonType | ✅ | WAGON_TYPE | Tipo | - |
| 47 | Warehouse | ❌ | WAREHOUSE | Core | **Alta** |
| 48 | WarehouseAisle | ❌ | WAREHOUSE_AISLE | Armazém | Média |
| 49 | WarehouseBay | ❌ | WAREHOUSE_BAY | Armazém | Média |

---

## Por Categoria

### ✅ Core do Sistema (9 modelos)
- ✅ Company
- ✅ Facility
- ✅ Item
- ✅ Train
- ✅ User
- ✅ Role
- ❌ **Order** ⭐
- ❌ **OrderLine** ⭐
- ❌ **Warehouse** ⭐

### Tipos/Categorias (7 modelos)
- ✅ FacilityType
- ✅ Gauge
- ✅ ItemCategory
- ✅ LocomotiveType
- ✅ WagonType
- ❌ FacilityFeatureType
- ❌ TrackType

### ✅ Infraestrutura Ferroviária (8 modelos)
- ✅ Line
- ❌ LineSegment
- ❌ LineSegmentFacility (associativa)
- ❌ ParkingTrack
- ❌ ParkingQueue
- ❌ **Station** ⭐ (extends Facility)
- ❌ TrackType

### Material Circulante (9 modelos)
- ✅ LocomotiveType
- ✅ WagonType
- ❌ **Locomotive** ⭐
- ❌ LocomotiveDiesel
- ❌ LocomotiveEletric
- ❌ LocomotiveGauge (associativa)
- ❌ **Wagon** ⭐
- ❌ **WagonModel** ⭐
- ❌ WagonGauge (associativa)

### Armazém e Inventário (7 modelos)
- ✅ Item
- ✅ ItemCategory
- ❌ **Warehouse** ⭐
- ❌ WarehouseAisle
- ❌ WarehouseBay
- ❌ InventoryLot
- ❌ InventoryBalance
- ❌ InventoryMove

### Pedidos e Devoluções (6 modelos)
- ❌ **Order** ⭐
- ❌ **OrderLine** ⭐
- ❌ ReturnOrder
- ❌ ReturnLine
- ❌ QaInspection
- ❌ QuarantineHold

### Operações de Picking (3 modelos)
- ❌ Cart
- ❌ PickWave
- ❌ PickTask

### Operações Ferroviárias (5 modelos)
- ❌ TrainService
- ❌ TrainDispatch
- ❌ TrainStop
- ❌ ServiceLocomotive
- ❌ TrainServiceComposition

### Tabelas Associativas (5 modelos)
- ❌ LineSegmentFacility
- ❌ LocomotiveGauge
- ❌ WagonGauge
- ❌ UserRole

### Auxiliares
- Dimensions (classe de apoio, não precisa DAO)
- RoutePoint

---

## 🎯 TOP 10 Prioridades para Próximos DAOs

1. **OrderDAO** - Fundamental para gestão de pedidos
2. **OrderLineDAO** - Linhas dos pedidos
3. **WarehouseDAO** - Gestão de armazéns
4. **LocomotiveDAO** - Gestão de locomotivas
5. **WagonDAO** - Gestão de vagões
6. **WagonModelDAO** - Modelos de vagão
7. **StationDAO** - Estações ferroviárias
8. **InventoryBalanceDAO** - Saldo de inventário
9. **InventoryLotDAO** - Lotes de inventário
10. **LineSegmentDAO** - Segmentos de linha

---

## 📊 Estatísticas Rápidas

- **Total**: 48 modelos + 1 auxiliar
- **Com DAO**: 12 (25%)
- **Sem DAO**: 36 (75%)
- **Prioridade Alta**: 6 modelos
- **Prioridade Média**: 10 modelos
- **Prioridade Baixa**: 20 modelos

### Localização dos Modelos
📂 `src/main/java/app/model/`

### Localização dos DAOs
📂 `src/main/java/app/db/dao/{modelo}/`

---

**Legenda**:
- ✅ DAO já existe
- ❌ DAO não existe
- ⭐ Prioridade ALTA
- N/A Não aplicável

**Gerado em**: 2025-01-10

