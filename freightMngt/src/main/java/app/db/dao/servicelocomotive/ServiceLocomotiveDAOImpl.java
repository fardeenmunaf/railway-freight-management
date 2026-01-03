package app.db.dao.servicelocomotive;

import app.db.dao.BaseDAOImpl;
import app.model.ServiceLocomotive;
import app.model.TrainService;
import app.model.Locomotive;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO implementation for ServiceLocomotive management in the Oracle database.
 * Implements CRUD operations for the ServiceLocomotive entity.
 *
 * @author G113 2DK
 * @version 1.0
 */
public class ServiceLocomotiveDAOImpl extends BaseDAOImpl implements ServiceLocomotiveDAO {

    /**
     * Constructor that initializes the database connection.
     */
    public ServiceLocomotiveDAOImpl() {
        super();
    }

    @Override
    public void create(ServiceLocomotive serviceLocomotive) throws SQLException {
        String sql = "INSERT INTO SERVICE_LOCOMOTIVE (SERVICE_LOCOMOTIVE_ID, SERVICE_ID, LOCOMOTIVE_ID, POSITION_NO) " +
                     "VALUES (?, ?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            if (serviceLocomotive.getServiceLocomotiveId() == null) {
                serviceLocomotive.setServiceLocomotiveId(getNextId());
            }

            pstmt.setInt(1, serviceLocomotive.getServiceLocomotiveId());

            if (serviceLocomotive.getService() != null && serviceLocomotive.getService().getServiceId() != null) {
                pstmt.setInt(2, serviceLocomotive.getService().getServiceId());
            } else {
                pstmt.setNull(2, Types.INTEGER);
            }

            if (serviceLocomotive.getLocomotive() != null && serviceLocomotive.getLocomotive().getLocomotiveId() != null) {
                pstmt.setInt(3, serviceLocomotive.getLocomotive().getLocomotiveId());
            } else {
                pstmt.setNull(3, Types.INTEGER);
            }

            if (serviceLocomotive.getPositionNo() != null) {
                pstmt.setInt(4, serviceLocomotive.getPositionNo());
            } else {
                pstmt.setNull(4, Types.INTEGER);
            }

            pstmt.executeUpdate();
        }
    }

    @Override
    public ServiceLocomotive get(int id) throws SQLException {
        String sql = "SELECT * FROM SERVICE_LOCOMOTIVE WHERE SERVICE_LOCOMOTIVE_ID = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToServiceLocomotive(rs);
                }
            }
        }

        return null;
    }

    @Override
    public List<ServiceLocomotive> getAll() throws SQLException {
        List<ServiceLocomotive> serviceLocomotives = new ArrayList<>();
        String sql = "SELECT * FROM SERVICE_LOCOMOTIVE ORDER BY SERVICE_ID, POSITION_NO";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                serviceLocomotives.add(mapResultSetToServiceLocomotive(rs));
            }
        }

        return serviceLocomotives;
    }

    @Override
    public void update(ServiceLocomotive serviceLocomotive) throws SQLException {
        String sql = "UPDATE SERVICE_LOCOMOTIVE SET SERVICE_ID = ?, LOCOMOTIVE_ID = ?, POSITION_NO = ? " +
                     "WHERE SERVICE_LOCOMOTIVE_ID = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            if (serviceLocomotive.getService() != null && serviceLocomotive.getService().getServiceId() != null) {
                pstmt.setInt(1, serviceLocomotive.getService().getServiceId());
            } else {
                pstmt.setNull(1, Types.INTEGER);
            }

            if (serviceLocomotive.getLocomotive() != null && serviceLocomotive.getLocomotive().getLocomotiveId() != null) {
                pstmt.setInt(2, serviceLocomotive.getLocomotive().getLocomotiveId());
            } else {
                pstmt.setNull(2, Types.INTEGER);
            }

            if (serviceLocomotive.getPositionNo() != null) {
                pstmt.setInt(3, serviceLocomotive.getPositionNo());
            } else {
                pstmt.setNull(3, Types.INTEGER);
            }

            pstmt.setInt(4, serviceLocomotive.getServiceLocomotiveId());

            pstmt.executeUpdate();
        }
    }

    @Override
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM SERVICE_LOCOMOTIVE WHERE SERVICE_LOCOMOTIVE_ID = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }

    @Override
    public List<ServiceLocomotive> getByService(int serviceId) throws SQLException {
        List<ServiceLocomotive> serviceLocomotives = new ArrayList<>();
        String sql = "SELECT * FROM SERVICE_LOCOMOTIVE WHERE SERVICE_ID = ? ORDER BY POSITION_NO";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, serviceId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    serviceLocomotives.add(mapResultSetToServiceLocomotive(rs));
                }
            }
        }

        return serviceLocomotives;
    }

    @Override
    public List<ServiceLocomotive> getByLocomotive(int locomotiveId) throws SQLException {
        List<ServiceLocomotive> serviceLocomotives = new ArrayList<>();
        String sql = "SELECT * FROM SERVICE_LOCOMOTIVE WHERE LOCOMOTIVE_ID = ? ORDER BY SERVICE_ID";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, locomotiveId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    serviceLocomotives.add(mapResultSetToServiceLocomotive(rs));
                }
            }
        }

        return serviceLocomotives;
    }

    @Override
    public void deleteByService(int serviceId) throws SQLException {
        String sql = "DELETE FROM SERVICE_LOCOMOTIVE WHERE SERVICE_ID = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, serviceId);
            pstmt.executeUpdate();
        }
    }

    @Override
    public boolean exists(int id) throws SQLException {
        String sql = "SELECT COUNT(*) AS COUNT FROM SERVICE_LOCOMOTIVE WHERE SERVICE_LOCOMOTIVE_ID = ?";

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
     * Gets the next available ID for a new ServiceLocomotive.
     *
     * @return the next available ID
     * @throws SQLException if an error occurs during the operation
     */
    private int getNextId() throws SQLException {
        String sql = "SELECT NVL(MAX(SERVICE_LOCOMOTIVE_ID), 0) + 1 AS NEXT_ID FROM " + getTableName("SERVICE_LOCOMOTIVE");

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getInt("NEXT_ID");
            }
        }

        return 1;
    }

    /**
     * Maps a ResultSet row to a ServiceLocomotive object.
     *
     * @param rs the ResultSet containing the data
     * @return the ServiceLocomotive object
     * @throws SQLException if an error occurs during the operation
     */
    private ServiceLocomotive mapResultSetToServiceLocomotive(ResultSet rs) throws SQLException {
        ServiceLocomotive serviceLocomotive = new ServiceLocomotive();

        serviceLocomotive.setServiceLocomotiveId(rs.getInt("SERVICE_LOCOMOTIVE_ID"));

        // Set TrainService if available
        if (rs.getObject("SERVICE_ID") != null) {
            TrainService service = new TrainService();
            service.setServiceId(rs.getInt("SERVICE_ID"));
            serviceLocomotive.setService(service);
        }

        // Set Locomotive if available
        if (rs.getObject("LOCOMOTIVE_ID") != null) {
            Locomotive locomotive = new Locomotive();
            locomotive.setLocomotiveId(rs.getInt("LOCOMOTIVE_ID"));
            serviceLocomotive.setLocomotive(locomotive);
        }

        if (rs.getObject("POSITION_NO") != null) {
            serviceLocomotive.setPositionNo(rs.getInt("POSITION_NO"));
        }

        return serviceLocomotive;
    }
}

