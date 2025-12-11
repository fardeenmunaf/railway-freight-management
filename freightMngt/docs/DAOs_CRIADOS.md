# DAOs Implementados - FreightMngt

## 📊 Visão Geral

**Total de Modelos Identificados**: 48  
**DAOs Criados**: 12  
**Percentagem Completa**: 25%  
**Status**: Em Desenvolvimento Ativo

---

## Resumo de DAOs Criados

Este documento lista todos os DAOs (Data Access Objects) que foram criados para o sistema FreightMngt.

### ✅ DAOs Completos e Funcionais (12/48)

#### 1. **CompanyDAO** ✅
- **Pacote**: `app.db.dao.company`
- **Modelo**: Company
- **Tabela**: COMPANY
- **Métodos Adicionais**:
  - `getByName(String name)`
  - `exists(int id)`

#### 2. **TrainDAO** ✅
- **Pacote**: `app.db.dao.train`
- **Modelo**: Train
- **Tabela**: TRAIN
- **Métodos Adicionais**:
  - `getByOperator(int companyId)`
- **Correções**: Ajustado OPERATOR_ID, CODE e DEPARTURE/DESTINATION

#### 3. **GaugeDAO** ✅
- **Pacote**: `app.db.dao.gauge`
- **Modelo**: Gauge
- **Tabela**: GAUGE
- **Métodos Adicionais**:
  - `getByName(String name)`

#### 4. **FacilityDAO** ✅
- **Pacote**: `app.db.dao.facility`
- **Modelo**: Facility
- **Tabela**: FACILITY
- **Métodos Adicionais**:
  - `getByIntermodal(boolean isIntermodal)`
- **Correções**: Estrutura JavaDoc corrigida

#### 5. **FacilityTypeDAO** ✅
- **Pacote**: `app.db.dao.facilitytype`
- **Modelo**: FacilityType
- **Tabela**: FACILITY_TYPE
- **Métodos Adicionais**:
  - `getByName(String name)`

#### 6. **LineDAO** ✅
- **Pacote**: `app.db.dao.line`
- **Modelo**: Line
- **Tabela**: LINE
- **Métodos Adicionais**:
  - `getByOwner(int ownerId)`
- **Correções**: Removido campo NAME que não existe no modelo

#### 7. **LocomotiveTypeDAO** ✅
- **Pacote**: `app.db.dao.locomotivetype`
- **Modelo**: LocomotiveType
- **Tabela**: LOCOMOTIVE_TYPE
- **Métodos Adicionais**:
  - `getByName(String name)`

#### 8. **WagonTypeDAO** ✅
- **Pacote**: `app.db.dao.wagontype`
- **Modelo**: WagonType
- **Tabela**: WAGON_TYPE
- **Métodos Adicionais**:
  - `getByName(String name)`

#### 9. **ItemCategoryDAO** ✅
- **Pacote**: `app.db.dao.itemcategory`
- **Modelo**: ItemCategory
- **Tabela**: ITEM_CATEGORY
- **Métodos Adicionais**:
  - `getByName(String name)`

#### 10. **ItemDAO** ✅
- **Pacote**: `app.db.dao.item`
- **Modelo**: Item
- **Tabela**: ITEM
- **Métodos Adicionais**:
  - `getBySku(String sku)`
  - `getByCategory(int categoryId)`
- **Correções**: Removido conteúdo duplicado/invertido

#### 11. **RoleDAO** ✅
- **Pacote**: `app.db.dao.role`
- **Modelo**: Role
- **Tabela**: "ROLE" (com aspas - nome reservado)
- **Métodos Adicionais**:
  - `getByName(String name)`

#### 12. **UserDAO** ✅
- **Pacote**: `app.db.dao.user`
- **Modelo**: User
- **Tabela**: "USER" (com aspas - nome reservado)
- **Métodos Adicionais**:
  - `getByUsername(String username)`
  - `getByEmail(String email)`
  - `getByCompany(int companyId)`

---

## 📋 LISTA COMPLETA: Todos os 48 Modelos

### ✅ Modelos COM DAO (12)
1. ✅ Company → CompanyDAO
2. ✅ Facility → FacilityDAO  
3. ✅ FacilityType → FacilityTypeDAO
4. ✅ Gauge → GaugeDAO
5. ✅ Item → ItemDAO
6. ✅ ItemCategory → ItemCategoryDAO
7. ✅ Line → LineDAO
8. ✅ LocomotiveType → LocomotiveTypeDAO
9. ✅ Role → RoleDAO
10. ✅ Train → TrainDAO
11. ✅ User → UserDAO
12. ✅ WagonType → WagonTypeDAO

### ❌ Modelos SEM DAO (36)

#### Infraestrutura Ferroviária (6 modelos)
13. ❌ **LineSegment** - Segmentos de linha ferroviária
14. ❌ **LineSegmentFacility** - Relação segmento-instalação (tabela associativa)
15. ❌ **ParkingTrack** - Vias de estacionamento
16. ❌ **ParkingQueue** - Fila de estacionamento de vagões
17. ❌ **Station** - Estações (extends Facility)
18. ❌ **TrackType** - Tipos de via

#### Material Circulante - Locomotivas (5 modelos)
19. ❌ **Locomotive** - Locomotivas
20. ❌ **LocomotiveDiesel** - Locomotivas diesel (extends Locomotive)
21. ❌ **LocomotiveEletric** - Locomotivas elétricas (extends Locomotive)
22. ❌ **LocomotiveGauge** - Relação locomotiva-bitola (tabela associativa)

#### Material Circulante - Vagões (4 modelos)
23. ❌ **Wagon** - Vagões
24. ❌ **WagonModel** - Modelos de vagão
25. ❌ **WagonGauge** - Relação vagão-bitola (tabela associativa)

#### Armazém e Inventário (6 modelos)
26. ❌ **Warehouse** - Armazéns
27. ❌ **WarehouseAisle** - Corredores de armazém
28. ❌ **WarehouseBay** - Baias/posições de armazém
29. ❌ **InventoryLot** - Lotes de inventário
30. ❌ **InventoryBalance** - Saldo de inventário
31. ❌ **InventoryMove** - Movimentos de inventário

#### Pedidos e Devoluções (6 modelos)
32. ❌ **Order** - Pedidos
33. ❌ **OrderLine** - Linhas de pedido
34. ❌ **ReturnOrder** - Ordens de devolu��ão
35. ❌ **ReturnLine** - Linhas de devolução
36. ❌ **QaInspection** - Inspeções de qualidade
37. ❌ **QuarantineHold** - Retenções em quarentena

#### Operações de Picking (3 modelos)
38. ❌ **Cart** - Carrinhos de picking
39. ❌ **PickWave** - Ondas de picking
40. ❌ **PickTask** - Tarefas de picking

#### Operações Ferroviárias (5 modelos)
41. ❌ **TrainService** - Serviços/composições de comboio
42. ❌ **TrainDispatch** - Despachos de comboio
43. ❌ **TrainStop** - Paragens de comboio
44. ❌ **ServiceLocomotive** - Locomotivas em serviço
45. ❌ **TrainServiceComposition** - Composição do serviço

#### Outros (4 modelos)
46. ❌ **UserRole** - Relação utilizador-role (tabela associativa)
47. ❌ **FacilityFeatureType** - Tipos de características de instalação
48. ❌ **RoutePoint** - Pontos de rota
49. ❌ **Dimensions** - Dimensões (classe auxiliar - pode não precisar DAO)

---

## 🎯 Prioridades Sugeridas para Próximos DAOs

### Prioridade ALTA (Operações Core)
1. **OrderDAO** - Gestão de pedidos
2. **WarehouseDAO** - Gestão de armazéns
3. **LocomotiveDAO** - Gestão de locomotivas
4. **WagonDAO** - Gestão de vagões
5. **WagonModelDAO** - Modelos de vagão
6. **StationDAO** - Estações ferroviárias

### Prioridade MÉDIA (Operações Diárias)
7. **OrderLineDAO**
8. **InventoryBalanceDAO**
9. **InventoryLotDAO**
10. **WarehouseAisleDAO**
11. **WarehouseBayDAO**
12. **LineSegmentDAO**

### Prioridade BAIXA (Funcionalidades Avançadas)
13. **TrainServiceDAO**
14. **PickWaveDAO**
15. **CartDAO**
16. **ReturnOrderDAO**
17. Restantes modelos...

---

## Padrão de Implementação

Todos os DAOs seguem o mesmo padrão:

### Estrutura de Pastas
```
app.db.dao.{modelo}/
  ├── {Modelo}DAO.java        (Interface)
  └── {Modelo}DAOImpl.java    (Implementação)
```

### Métodos CRUD Básicos (Obrigatórios)
```java
void create(T entity) throws SQLException;
T get(int id) throws SQLException;
List<T> getAll() throws SQLException;
void update(T entity) throws SQLException;
void delete(int id) throws SQLException;
```

### Métodos Privados Auxiliares
```java
private int getNextId() throws SQLException;
private T mapResultSetTo{Modelo}(ResultSet rs) throws SQLException;
```

### Tratamento de Foreign Keys
- **INSERT/UPDATE**: Apenas o ID da entidade relacionada é usado
- **SELECT**: JOIN com tabelas relacionadas para carregar objetos completos
- **NULL values**: Usa `pstmt.setNull(index, Types.INTEGER)`

### Tabelas com Nomes Reservados
Requerem aspas duplas no Oracle:
- `"USER"`
- `"ROLE"`
- `"ORDER"`

---

## 📊 Estatísticas Atualizadas

- **Total de Modelos**: 48 (mais Dimensions como auxiliar)
- **DAOs Criados**: 12
- **DAOs Pendentes**: 36
- **Percentagem Completa**: 25% (12/48)
- **DAOs de Tipos/Categorias**: 5/7 criados (71%)
- **DAOs Core de Negócio**: 7/20 criados (35%)
- **DAOs de Tabelas Associativas**: 0/5 criados (0%)

### Por Categoria
| Categoria | Criados | Total | % |
|-----------|---------|-------|---|
| Tipos/Categorias | 5 | 7 | 71% |
| Core Negócio | 7 | 20 | 35% |
| Infraestrutura | 2 | 8 | 25% |
| Material Circulante | 1 | 9 | 11% |
| Armazém/Inventário | 1 | 7 | 14% |
| Operações | 0 | 8 | 0% |
| Tabelas Associativas | 0 | 5 | 0% |

---

**Última Atualização**: 2025-01-10  
**Status**: 12/48 DAOs Criados (25%)  
**Autor**: G113 2DK


#### 1. **CompanyDAO** 
- **Pacote**: `app.db.dao.company`
- **Modelo**: Company
- **Tabela**: COMPANY
- **Métodos Adicionais**:
  - `getByName(String name)`
  - `exists(int id)`

#### 2. **TrainDAO**
- **Pacote**: `app.db.dao.train`
- **Modelo**: Train
- **Tabela**: TRAIN
- **Métodos Adicionais**:
  - `getByOperator(int companyId)`
- **Correções**: Ajustado OPERATOR_ID, CODE e DEPARTURE/DESTINATION

#### 3. **GaugeDAO**
- **Pacote**: `app.db.dao.gauge`
- **Modelo**: Gauge
- **Tabela**: GAUGE
- **Métodos Adicionais**:
  - `getByName(String name)`

#### 4. **FacilityDAO**
- **Pacote**: `app.db.dao.facility`
- **Modelo**: Facility
- **Tabela**: FACILITY
- **Métodos Adicionais**:
  - `getByIntermodal(boolean isIntermodal)`
- **Correções**: Estrutura JavaDoc corrigida

#### 5. **FacilityTypeDAO**
- **Pacote**: `app.db.dao.facilitytype`
- **Modelo**: FacilityType
- **Tabela**: FACILITY_TYPE
- **Métodos Adicionais**:
  - `getByName(String name)`

#### 6. **LineDAO**
- **Pacote**: `app.db.dao.line`
- **Modelo**: Line
- **Tabela**: LINE
- **Métodos Adicionais**:
  - `getByOwner(int ownerId)`
- **Correções**: Removido campo NAME que não existe no modelo

#### 7. **LocomotiveTypeDAO**
- **Pacote**: `app.db.dao.locomotivetype`
- **Modelo**: LocomotiveType
- **Tabela**: LOCOMOTIVE_TYPE
- **Métodos Adicionais**:
  - `getByName(String name)`

#### 8. **WagonTypeDAO**
- **Pacote**: `app.db.dao.wagontype`
- **Modelo**: WagonType
- **Tabela**: WAGON_TYPE
- **Métodos Adicionais**:
  - `getByName(String name)`

#### 9. **ItemCategoryDAO**
- **Pacote**: `app.db.dao.itemcategory`
- **Modelo**: ItemCategory
- **Tabela**: ITEM_CATEGORY
- **Métodos Adicionais**:
  - `getByName(String name)`

#### 10. **ItemDAO**
- **Pacote**: `app.db.dao.item`
- **Modelo**: Item
- **Tabela**: ITEM
- **Métodos Adicionais**:
  - `getBySku(String sku)`
  - `getByCategory(int categoryId)`
- **Correções**: Removido conteúdo duplicado/invertido

#### 11. **RoleDAO**
- **Pacote**: `app.db.dao.role`
- **Modelo**: Role
- **Tabela**: "ROLE" (com aspas - nome reservado)
- **Métodos Adicionais**:
  - `getByName(String name)`

#### 12. **UserDAO**
- **Pacote**: `app.db.dao.user`
- **Modelo**: User
- **Tabela**: "USER" (com aspas - nome reservado)
- **Métodos Adicionais**:
  - `getByUsername(String username)`
  - `getByEmail(String email)`
  - `getByCompany(int companyId)`

---

## Modelos Principais Ainda Sem DAO

### Infraestrutura
- [ ] LineSegmentDAO
- [ ] LineSegmentFacilityDAO
- [ ] ParkingTrackDAO
- [ ] ParkingQueueDAO
- [ ] StationDAO
- [ ] TrackTypeDAO

### Material Circulante
- [ ] LocomotiveDAO
- [ ] LocomotiveDieselDAO
- [ ] LocomotiveEletricDAO
- [ ] LocomotiveGaugeDAO (tabela associativa)
- [ ] WagonDAO
- [ ] WagonModelDAO
- [ ] WagonGaugeDAO (tabela associativa)

### Armazém e Inventário
- [ ] WarehouseDAO
- [ ] WarehouseAisleDAO
- [ ] WarehouseBayDAO
- [ ] InventoryLotDAO
- [ ] InventoryBalanceDAO
- [ ] InventoryMoveDAO

### Pedidos e Devoluções
- [ ] OrderDAO
- [ ] OrderLineDAO
- [ ] ReturnOrderDAO
- [ ] ReturnLineDAO
- [ ] QaInspectionDAO
- [ ] QuarantineHoldDAO

### Operações de Picking
- [ ] CartDAO
- [ ] PickWaveDAO
- [ ] PickTaskDAO

### Operações Ferroviárias
- [ ] TrainServiceDAO
- [ ] TrainDispatchDAO
- [ ] TrainStopDAO
- [ ] ServiceLocomotiveDAO
- [ ] TrainServiceCompositionDAO

### Outros
- [ ] UserRoleDAO (tabela associativa)
- [ ] FacilityFeatureTypeDAO
- [ ] RoutePointDAO

---

## Padrão de Implementação

Todos os DAOs seguem o mesmo padrão:

### Estrutura de Pastas
```
app.db.dao.{modelo}/
  ├── {Modelo}DAO.java        (Interface)
  └── {Modelo}DAOImpl.java    (Implementação)
```

### Métodos CRUD Básicos (Obrigatórios)
```java
void create(T entity) throws SQLException;
T get(int id) throws SQLException;
List<T> getAll() throws SQLException;
void update(T entity) throws SQLException;
void delete(int id) throws SQLException;
```

### Métodos Privados Auxiliares
```java
private int getNextId() throws SQLException;
private T mapResultSetTo{Modelo}(ResultSet rs) throws SQLException;
```

### Tratamento de Foreign Keys
- **INSERT/UPDATE**: Apenas o ID da entidade relacionada é usado
- **SELECT**: JOIN com tabelas relacionadas para carregar objetos completos
- **NULL values**: Usa `pstmt.setNull(index, Types.INTEGER)`

### Tabelas com Nomes Reservados
Requerem aspas duplas no Oracle:
- `"USER"`
- `"ROLE"`
- `"ORDER"`

---

## Estatísticas

- **Total de Modelos**: ~50
- **DAOs Criados**: 12
- **Percentagem Completa**: ~24%
- **DAOs Críticos Completos**: Company, Train, Facility, User, Item, Line ✅

---

**Última Atualização**: 2025-01-10  
**Status**: Em Desenvolvimento  
**Autor**: G113 2DK

