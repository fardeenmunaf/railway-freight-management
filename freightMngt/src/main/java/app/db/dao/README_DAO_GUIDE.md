# Guia de Implementação de DAOs

## Estrutura Criada

### 1. Interface Genérica DAO
- **Localização**: `app.db.dao.DAO`
- **Métodos CRUD básicos**:
  - `create(T t)` - Criar novo registo
  - `get(int id)` - Obter por ID
  - `getAll()` - Obter todos
  - `update(T t)` - Atualizar registo
  - `delete(int id)` - Remover registo

### 2. DAOs Implementados (Exemplos)
- ✅ **CompanyDAO** - Exemplo completo com métodos adicionais
- ✅ **GaugeDAO** - Exemplo simples
- ✅ **FacilityDAO** - Exemplo com relacionamentos (FacilityType)

## Como Criar um Novo DAO

### Template Básico

```java
package app.db.dao;

import app.db.DatabaseConnection;
import app.model.{NomeModelo};

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para gestão de {NomeModelo} na base de dados Oracle.
 */
public class {NomeModelo}DAO implements DAO<{NomeModelo}> {

    private final Connection connection;

    public {NomeModelo}DAO() {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }

    @Override
    public void create({NomeModelo} entity) throws SQLException {
        String sql = "INSERT INTO {NOME_TABELA} (campos...) VALUES (?, ?, ...)";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            if (entity.getId() == null) {
                entity.setId(getNextId());
            }
            
            // Definir parâmetros
            pstmt.setInt(1, entity.getId());
            // ... outros campos
            
            pstmt.executeUpdate();
        }
    }

    @Override
    public {NomeModelo} get(int id) throws SQLException {
        String sql = "SELECT * FROM {NOME_TABELA} WHERE {ID_CAMPO} = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetTo{NomeModelo}(rs);
                }
            }
        }
        
        return null;
    }

    @Override
    public List<{NomeModelo}> getAll() throws SQLException {
        List<{NomeModelo}> list = new ArrayList<>();
        String sql = "SELECT * FROM {NOME_TABELA} ORDER BY {ID_CAMPO}";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                list.add(mapResultSetTo{NomeModelo}(rs));
            }
        }
        
        return list;
    }

    @Override
    public void update({NomeModelo} entity) throws SQLException {
        String sql = "UPDATE {NOME_TABELA} SET campo1 = ?, campo2 = ? WHERE {ID_CAMPO} = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            // Definir parâmetros
            pstmt.setString(1, entity.getCampo1());
            // ...
            pstmt.setInt(99, entity.getId()); // ID no último parâmetro
            
            pstmt.executeUpdate();
        }
    }

    @Override
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM {NOME_TABELA} WHERE {ID_CAMPO} = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }

    private int getNextId() throws SQLException {
        String sql = "SELECT NVL(MAX({ID_CAMPO}), 0) + 1 AS NEXT_ID FROM {NOME_TABELA}";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getInt("NEXT_ID");
            }
        }
        
        return 1;
    }

    private {NomeModelo} mapResultSetTo{NomeModelo}(ResultSet rs) throws SQLException {
        return new {NomeModelo}(
            rs.getInt("ID_CAMPO"),
            rs.getString("CAMPO1"),
            rs.getDouble("CAMPO2")
            // ... outros campos
        );
    }
}
```

## Lista de DAOs a Criar

### Modelos Principais
- [ ] TrainDAO
- [ ] TrainDispatchDAO
- [ ] TrainServiceDAO
- [ ] LocomotiveDAO
- [ ] WagonDAO
- [ ] LineDAO
- [ ] LineSegmentDAO
- [ ] StationDAO (se aplicável)

### Modelos de Tipo/Categoria
- [ ] FacilityTypeDAO
- [ ] FacilityFeatureTypeDAO
- [ ] LocomotiveTypeDAO
- [ ] WagonTypeDAO
- [ ] TrackTypeDAO

### Modelos de Inventário
- [ ] ItemDAO
- [ ] ItemCategoryDAO
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

### Modelos de Utilizador/Segurança
- [ ] UserDAO
- [ ] RoleDAO
- [ ] UserRoleDAO

### Outros
- [ ] CartDAO
- [ ] ParkingTrackDAO
- [ ] ParkingQueueDAO
- [ ] PickWaveDAO
- [ ] PickTaskDAO
- [ ] QaInspectionDAO
- [ ] QuarantineHoldDAO

## Notas Importantes

### Mapeamento de Tipos Java ↔ Oracle
- `Integer` → `NUMBER` → `rs.getInt()` / `pstmt.setInt()`
- `String` → `VARCHAR2` → `rs.getString()` / `pstmt.setString()`
- `Double` → `NUMBER` → `rs.getDouble()` / `pstmt.setDouble()`
- `Boolean` → `NUMBER(1)` → `rs.getInt() == 1` / `pstmt.setInt(val ? 1 : 0)`
- `Date` → `DATE` → `rs.getDate()` / `pstmt.setDate()`

### Relacionamentos
- Para FK simples: carregar apenas o ID
- Para objetos completos: fazer JOIN e criar objeto relacionado
- Ver exemplo em `FacilityDAO` (relacionamento com `FacilityType`)

### Tratamento de Nulos
- Usar `rs.getObject("campo") != null` antes de ler valores
- Usar `NVL()` em queries Oracle quando apropriado

## Próximos Passos

1. Criar DAOs conforme necessidade do projeto
2. Atualizar Repositories para usar DAOs em vez de ArrayList
3. Criar testes unitários para cada DAO
4. Adicionar métodos de busca específicos conforme necessário

