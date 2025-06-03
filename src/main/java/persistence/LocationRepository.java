package persistence;

import model.Location;
import persistence.util.DatabaseConnectionUtil;

import java.sql.*;
import java.util.*;

public class LocationRepository implements GenericRepository<Location> {

    private final Map<Integer, Location> storage = new HashMap<>();
    private final Connection connection;

    private static final String INSERT_SQL = "INSERT INTO locations(name, type) VALUES (?, ?)";
    private static final String UPDATE_SQL = "UPDATE locations SET name=?, type=? WHERE location_id=?";
    private static final String DELETE_SQL = "DELETE FROM locations WHERE location_id=?";
    private static final String FIND_BY_ID_SQL = "SELECT * FROM locations WHERE location_id=?";
    private static final String FIND_ALL_SQL = "SELECT * FROM locations";
    private static final String FIND_BY_NAME_SQL = "SELECT * FROM locations WHERE name LIKE ?";

    private static volatile LocationRepository instance;

    private LocationRepository() {
        this.connection = DatabaseConnectionUtil.getDatabaseConnection();
    }

    public static LocationRepository getInstance() {
        if (instance == null) {
            synchronized (LocationRepository.class) {
                if (instance == null) {
                    instance = new LocationRepository();
                }
            }
        }
        return instance;
    }

    @Override
    public Location save(Location location) {
        try (PreparedStatement stmt = connection.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, location.getName());
            stmt.setString(2, location.getType());
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    location.setLocationId(rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        storage.put(location.getLocationId(), location);
        return location;
    }

    public List<Location> findByName(String namePattern) {
        try (PreparedStatement stmt = connection.prepareStatement(FIND_BY_NAME_SQL)) {
            stmt.setString(1, "%" + namePattern + "%");
            ResultSet rs = stmt.executeQuery();
            extractResultSet(rs);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return new ArrayList<>(storage.values());
    }

    private void extractResultSet(ResultSet rs) throws SQLException {
        while (rs.next()) {
            Location location = new Location(
                    rs.getString("name"),
                    rs.getString("type")
            );
            location.setLocationId(rs.getInt("location_id"));
            storage.put(location.getLocationId(), location);
        }
    }

    @Override
    public List<Location> findAll() {
        try (PreparedStatement stmt = connection.prepareStatement(FIND_ALL_SQL);
             ResultSet rs = stmt.executeQuery()) {
            extractResultSet(rs);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return new ArrayList<>(storage.values());
    }

    @Override
    public Optional<Location> findById(String id) {
        int locationId = Integer.parseInt(id);
        try (PreparedStatement stmt = connection.prepareStatement(FIND_BY_ID_SQL)) {
            stmt.setInt(1, locationId);
            ResultSet rs = stmt.executeQuery();
            extractResultSet(rs);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.ofNullable(storage.get(locationId));
    }

    @Override
    public void update(Location location) {
        try (PreparedStatement stmt = connection.prepareStatement(UPDATE_SQL)) {
            stmt.setString(1, location.getName());
            stmt.setString(2, location.getType());
            stmt.setInt(3, location.getLocationId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        storage.put(location.getLocationId(), location);
    }

    @Override
    public void delete(Location location) {
        try (PreparedStatement stmt = connection.prepareStatement(DELETE_SQL)) {
            stmt.setInt(1, location.getLocationId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        storage.remove(location.getLocationId());
    }
}
