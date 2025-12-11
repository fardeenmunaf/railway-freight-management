# Guia de Implementação de DAOs - Projeto FreightMngt

## DAOs Implementados ✅

### Modelos Principais
- ✅ **CompanyDAO** - Gestão de empresas/operadores
- ✅ **TrainDAO** - Gestão de comboios
- ✅ **LineDAO** - Gestão de linhas ferroviárias
- ✅ **FacilityDAO** - Gestão de instalações (armazéns, estações, terminais)
- ✅ **ItemDAO** - Gestão de itens/produtos

### Modelos de Tipo/Categoria
- ✅ **GaugeDAO** - Tipos de bitola
- ✅ **FacilityTypeDAO** - Tipos de instalação
- ✅ **LocomotiveTypeDAO** - Tipos de locomotiva
- ✅ **WagonTypeDAO** - Tipos de vagão
- ✅ **ItemCategoryDAO** - Categorias de item

### Modelos de Segurança
- ✅ **RoleDAO** - Gestão de roles/papéis
- ✅ **UserDAO** - Gestão de utilizadores

## DAOs a Criar (Opcional/Futuro)

### Modelos de Inventário
- [ ] InventoryBalanceDAO
- [ ] InventoryLotDAO
- [ ] InventoryMoveDAO

### Modelos de Armazém
- [ ] WarehouseDAO
- [ ] WarehouseAisleDAO
- [ ] WarehouseBayDAO

### Modelos de Pedidos
- [ ] OrderDAO
- [ ] OrderLineDAO
- [ ] ReturnOrderDAO
- [ ] ReturnLineDAO

### Modelos de Material Circulante
- [ ] LocomotiveDAO
- [ ] WagonDAO
- [ ] WagonModelDAO

### Modelos de Infraestrutura
- [ ] LineSegmentDAO
- [ ] LineSegmentFacilityDAO
- [ ] ParkingTrackDAO
- [ ] ParkingQueueDAO
- [ ] StationDAO

### Modelos de Operação
- [ ] TrainServiceDAO
- [ ] TrainDispatchDAO
- [ ] TrainStopDAO
- [ ] ServiceLocomotiveDAO
- [ ] TrainServiceCompositionDAO

### Outros Modelos
- [ ] CartDAO
- [ ] PickWaveDAO
- [ ] PickTaskDAO
- [ ] QaInspectionDAO
- [ ] QuarantineHoldDAO
- [ ] UserRoleDAO (tabela associativa)
- [ ] FacilityFeatureTypeDAO
- [ ] TrackTypeDAO

## Estrutura dos DAOs Criados

Todos os DAOs seguem o padrão:
```
app.db.dao.{modelo}/
  ├── {Modelo}DAO.java (interface)
  └── {Modelo}DAOImpl.java (implementação)
```

### Métodos CRUD Básicos
Todos os DAOs implementam:
- `create(T entity)` - Criar novo registo
- `get(int id)` - Obter por ID
- `getAll()` - Obter todos
- `update(T entity)` - Atualizar registo
- `delete(int id)` - Remover registo

### Métodos Específicos Adicionais

**CompanyDAO**:
- `getByName(String name)` - Buscar por nome
- `exists(int id)` - Verificar existência

**TrainDAO**:
- `getByOperator(int companyId)` - Buscar por operador

**LineDAO**:
- `getByOwner(int ownerId)` - Buscar por proprietário

**FacilityDAO**:
- `getByIntermodal(boolean isIntermodal)` - Buscar por tipo intermodal

**ItemDAO**:
- `getBySku(String sku)` - Buscar por SKU
- `getByCategory(int categoryId)` - Buscar por categoria

**Todos os DAOs de Tipo/Categoria**:
- `getByName(String name)` - Buscar por nome

## Notas de Implementação

### Conexão com Base de Dados
Todos os DAOs usam `DatabaseConnection.getInstance().getConnection()` para obter a conexão Oracle.

### Geração de IDs
Os DAOs implementam um método privado `getNextId()` que usa:
```sql
SELECT NVL(MAX({ID_CAMPO}), 0) + 1 AS NEXT_ID FROM {TABELA}
```

### Tratamento de Relacionamentos
- DAOs com Foreign Keys (FKs) fazem JOINs nas queries SELECT para carregar objetos relacionados
- No INSERT/UPDATE, apenas o ID da entidade relacionada é usado
- Valores NULL são tratados com `pstmt.setNull(index, Types.INTEGER)`

### Tabelas com Nomes Reservados
Oracle requer aspas duplas para tabelas com nomes reservados:
- `"ROLE"` - Tabela de roles
- `"USER"` - Tabela de utilizadores  
- `"ORDER"` - Tabela de pedidos

## Correções Realizadas

### FacilityDAO
- ✅ Corrigida estrutura invertida (comentários JavaDoc estavam invertidos)
- ✅ Estrutura normalizada seguindo padrão

### TrainDAO
- ✅ Corrigidas referências de `COMPANY_ID` para `OPERATOR_ID` (conforme schema)
- ✅ Corrigido tratamento de `CODE` (VARCHAR2 no schema, mas Integer no modelo)
- ✅ Corrigido tratamento de `DEPARTURE` e `DESTINATION` (NUMBER no schema - facility IDs)

### LineDAO
- ✅ Removidas referências ao campo `NAME` que não existe no modelo Line
- ✅ Ajustados JOINs para carregar Company através de User (owner)

## Exemplos de Uso

### Criar uma Company
```java
CompanyDAO companyDAO = new CompanyDAOImpl();
Company company = new Company(null, "ACME Rail", "ACME", "PT123456789");
companyDAO.create(company); // ID gerado automaticamente
```

### Buscar Item por SKU
```java
ItemDAO itemDAO = new ItemDAOImpl();
Item item = itemDAO.getBySku("SKU-12345");
if (item != null) {
    System.out.println("Item: " + item.getName());
}
```

### Listar todos os Facilities
```java
FacilityDAO facilityDAO = new FacilityDAOImpl();
List<Facility> facilities = facilityDAO.getAll();
for (Facility f : facilities) {
    System.out.println(f.getName() + " - " + f.getFacilityType().getName());
}
```

---

**Última atualização**: 2025-01-10
**Autor**: G113 2DK


## ✅ Estrutura Criada

### DAOs Implementados (com Interface + Implementação)

#### Estrutura de Pastas:
```
app/db/dao/
├── company/
│   ├── CompanyDAO.java (interface)
│   └── CompanyDAOImpl.java (implementação)
├── gauge/
│   ├── GaugeDAO.java
│   └── GaugeDAOImpl.java
├── train/
│   ├── TrainDAO.java
│   └── TrainDAOImpl.java
├── facility/
│   ├── FacilityDAO.java
│   └── FacilityDAOImpl.java
└── user/
    ├── UserDAO.java
    └── UserDAOImpl.java
```

## 📋 DAOs Restantes a Criar

### Prioritários
- [ ] **Line** - linha ferroviária
- [ ] **LineSegment** - segmento de linha
- [ ] **Locomotive** - locomotiva
- [ ] **Wagon** - vagão
- [ ] **WagonModel** - modelo de vagão
- [ ] **TrainDispatch** - despacho de comboio
- [ ] **TrainService** - serviço de comboio

### Tipos/Categorias
- [ ] **FacilityType** - tipo de instalação
- [ ] **FacilityFeatureType** - tipo de característica de instalação
- [ ] **LocomotiveType** - tipo de locomotiva
- [ ] **WagonType** - tipo de vagão
- [ ] **TrackType** - tipo de via
- [ ] **ItemCategory** - categoria de item

### Relações
- [ ] **LineSegmentFacility** - relação linha-instalação
- [ ] **LocomotiveGauge** - bitola de locomotiva
- [ ] **WagonGauge** - bitola de vagão
- [ ] **TrainServiceComposition** - composição de serviço
- [ ] **ServiceLocomotive** - locomotiva em serviço

### Outros Modelos
- [ ] **Item** - item de inventário
- [ ] **Order** - pedido
- [ ] **OrderLine** - linha de pedido
- [ ] **Cart** - carrinho
- [ ] **Role** - função/papel
- [ ] **UserRole** - função de utilizador
- [ ] **ParkingTrack** - via de estacionamento
- [ ] **ParkingQueue** - fila de estacionamento
- [ ] **Warehouse** - armazém
- [ ] **WarehouseAisle** - corredor de armazém
- [ ] **WarehouseBay** - baía de armazém
- [ ] **InventoryBalance** - saldo de inventário
- [ ] **InventoryLot** - lote de inventário
- [ ] **InventoryMove** - movimentação de inventário
- [ ] **PickWave** - onda de picking
- [ ] **PickTask** - tarefa de picking
- [ ] **QaInspection** - inspeção de qualidade
- [ ] **QuarantineHold** - retenção em quarentena
- [ ] **ReturnOrder** - pedido de devolução
- [ ] **ReturnLine** - linha de devolução
- [ ] **TrainStop** - paragem de comboio
- [ ] **RoutePoint** - ponto de rota
- [ ] **LocomotiveDiesel** - locomotiva diesel
- [ ] **LocomotiveEletric** - locomotiva elétrica

## 🎯 Template para Criar Novos DAOs

### Passo 1: Criar Interface (ex: LineDAO.java)

```java
package app.db.dao.line;

import java.sql.SQLException;
import java.util.List;
import app.model.Line;

/**
 * Interface DAO para operações CRUD de Line.
 */
public interface LineDAO {

    void create(Line line) throws SQLException;
    
    Line get(int id) throws SQLException;
    
    List<Line> getAll() throws SQLException;
    
    void update(Line line) throws SQLException;
    
    void delete(int id) throws SQLException;
    
    // Métodos específicos adicionais conforme necessário
}
```

### Passo 2: Criar Implementação (ex: LineDAOImpl.java)

```java
package app.db.dao.line;

import app.db.DatabaseConnection;
import app.model.Line;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementação DAO para gestão de Line na base de dados Oracle.
 */
public class LineDAOImpl implements LineDAO {

    private final Connection connection;

    public LineDAOImpl() {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }

    @Override
    public void create(Line line) throws SQLException {
        String sql = "INSERT INTO LINE (LINE_ID, OWNER_ID) VALUES (?, ?)";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            if (line.getLineId() == null) {
                line.setLineId(getNextId());
            }
            
            pstmt.setInt(1, line.getLineId());
            // Definir outros parâmetros...
            
            pstmt.executeUpdate();
        }
    }

    @Override
    public Line get(int id) throws SQLException {
        String sql = "SELECT * FROM LINE WHERE LINE_ID = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToLine(rs);
                }
            }
        }
        
        return null;
    }

    @Override
    public List<Line> getAll() throws SQLException {
        List<Line> lines = new ArrayList<>();
        String sql = "SELECT * FROM LINE ORDER BY LINE_ID";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                lines.add(mapResultSetToLine(rs));
            }
        }
        
        return lines;
    }

    @Override
    public void update(Line line) throws SQLException {
        String sql = "UPDATE LINE SET OWNER_ID = ? WHERE LINE_ID = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            // Definir parâmetros...
            pstmt.executeUpdate();
        }
    }

    @Override
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM LINE WHERE LINE_ID = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }

    private int getNextId() throws SQLException {
        String sql = "SELECT NVL(MAX(LINE_ID), 0) + 1 AS NEXT_ID FROM LINE";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getInt("NEXT_ID");
            }
        }
        
        return 1;
    }

    private Line mapResultSetToLine(ResultSet rs) throws SQLException {
        // Mapear campos do ResultSet para objeto Line
        return new Line(
            rs.getInt("LINE_ID"),
            // ... outros campos
        );
    }
}
```

## 💡 Dicas Importantes

### 1. Tratamento de Foreign Keys
```java
// Se o objeto relacionado existe:
if (entity.getRelatedObject() != null) {
    pstmt.setInt(n, entity.getRelatedObject().getId());
} else {
    pstmt.setNull(n, Types.INTEGER);
}
```

### 2. Mapeamento de Booleanos
```java
// No INSERT/UPDATE:
pstmt.setInt(n, entity.getIsActive() ? 1 : 0);

// No SELECT:
entity.setIsActive(rs.getInt("IS_ACTIVE") == 1);
```

### 3. JOINs para Objetos Relacionados
```java
String sql = "SELECT t.*, c.COMPANY_ID, c.NAME " +
             "FROM TABLE t " +
             "LEFT JOIN COMPANY c ON t.COMPANY_ID = c.COMPANY_ID " +
             "WHERE t.ID = ?";
```

### 4. Tabela USER
```java
// USER é palavra reservada em Oracle, use aspas:
String sql = "INSERT INTO \"USER\" (USER_ID, ...) VALUES (?, ...)";
```

### 5. Verificação de Nulos no ResultSet
```java
if (rs.getObject("COLUMN_NAME") != null) {
    value = rs.getInt("COLUMN_NAME");
}
```

## 📊 Mapeamento de Tipos

| Java Type | Oracle Type | ResultSet Method | PreparedStatement Method |
|-----------|-------------|------------------|--------------------------|
| Integer   | NUMBER      | getInt()         | setInt()                 |
| String    | VARCHAR2    | getString()      | setString()              |
| Double    | NUMBER      | getDouble()      | setDouble()              |
| Boolean   | NUMBER(1)   | getInt() == 1    | setInt(bool ? 1 : 0)     |
| Date      | DATE        | getDate()        | setDate()                |

## 🔧 Como Usar os DAOs

### Exemplo de Uso:
```java
// Criar instância do DAO
CompanyDAO companyDAO = new CompanyDAOImpl();

// Criar nova empresa
Company company = new Company(null, "ACME Corp", "ACME", "123456789");
companyDAO.create(company); // ID será gerado automaticamente

// Ler empresa
Company found = companyDAO.get(1);

// Listar todas
List<Company> all = companyDAO.getAll();

// Atualizar
found.setName("ACME Corporation");
companyDAO.update(found);

// Deletar
companyDAO.delete(1);
```

## ✅ Próximos Passos

1. Criar DAOs conforme prioridades do projeto
2. Testar cada DAO individualmente
3. Integrar com Controllers/Services existentes
4. Remover/depreciar uso de Repositories in-memory (ArrayList)
5. Criar testes unitários para DAOs

