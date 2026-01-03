package app.db.dao.company;

import app.db.dao.BaseDAOImpl;
import app.model.Company;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementação DAO para gestão de Company na base de dados Oracle.
 * Implementa operações CRUD para a entidade Company.
 *
 * @author G113 2DK
 * @version 1.0
 */
public class CompanyDAOImpl extends BaseDAOImpl implements CompanyDAO {

    /**
     * Construtor que inicializa a conexão com a base de dados.
     */
    public CompanyDAOImpl() {
        super();
    }

    @Override
    public void create(Company company) throws SQLException {
        String sql = "INSERT INTO " + getTableName("COMPANY") + " (COMPANY_ID, NAME, SHORT_NAME, VAT) VALUES (?, ?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            // Se o ID não foi definido, obtém o próximo ID disponível
            if (company.getCompanyId() == null) {
                company.setCompanyId(getNextId());
            }

            pstmt.setInt(1, company.getCompanyId());
            pstmt.setString(2, company.getName());
            pstmt.setString(3, company.getShortName());
            pstmt.setString(4, company.getVat());

            pstmt.executeUpdate();
        }
    }

    @Override
    public Company get(int id) throws SQLException {
        String sql = "SELECT * FROM " + getTableName("COMPANY") + " WHERE COMPANY_ID = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToCompany(rs);
                }
            }
        }

        return null;
    }

    @Override
    public List<Company> getAll() throws SQLException {
        List<Company> companies = new ArrayList<>();
        String sql = "SELECT * FROM " + getTableName("COMPANY") + " ORDER BY COMPANY_ID";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                companies.add(mapResultSetToCompany(rs));
            }
        }

        return companies;
    }

    @Override
    public void update(Company company) throws SQLException {
        String sql = "UPDATE " + getTableName("COMPANY") + " SET NAME = ?, SHORT_NAME = ?, VAT = ? WHERE COMPANY_ID = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, company.getName());
            pstmt.setString(2, company.getShortName());
            pstmt.setString(3, company.getVat());
            pstmt.setInt(4, company.getCompanyId());

            pstmt.executeUpdate();
        }
    }

    @Override
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM " + getTableName("COMPANY") + " WHERE COMPANY_ID = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }

    @Override
    public Company getByName(String name) throws SQLException {
        String sql = "SELECT * FROM " + getTableName("COMPANY") + " WHERE NAME = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, name);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToCompany(rs);
                }
            }
        }

        return null;
    }

    public Company getByVat(String vat) throws SQLException {
        String sql = "SELECT * FROM " + getTableName("COMPANY") + " WHERE VAT = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, vat);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToCompany(rs);
                }
            }
        }

        return null;
    }

    @Override
    public boolean exists(int id) throws SQLException {
        String sql = "SELECT COUNT(*) AS COUNT FROM " + getTableName("COMPANY") + " WHERE COMPANY_ID = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("COUNT") > 0;
                }
            }
        }

        return false;
    }

    /**
     * Obtem o proximo ID disponivel para uma nova Company.
     *
     * @return o proximo ID disponivel
     * @throws SQLException se ocorrer erro na operacao
     */
    private int getNextId() throws SQLException {
        String sql = "SELECT NVL(MAX(COMPANY_ID), 0) + 1 AS NEXT_ID FROM " + getTableName("COMPANY");

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getInt("NEXT_ID");
            }
        }

        return 1;
    }

    /**
     * Mapeia um ResultSet para um objeto Company.
     *
     * @param rs o ResultSet a mapear
     * @return o objeto Company mapeado
     * @throws SQLException se ocorrer erro ao ler o ResultSet
     */
    private Company mapResultSetToCompany(ResultSet rs) throws SQLException {
        return new Company(
                rs.getInt("COMPANY_ID"),
                rs.getString("NAME"),
                rs.getString("SHORT_NAME"),
                rs.getString("VAT")
        );
    }
}

