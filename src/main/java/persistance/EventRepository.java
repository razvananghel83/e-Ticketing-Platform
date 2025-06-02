package persistance;

import model.Event;
import persistance.util.DatabaseConnectionUtil;

import java.sql.*;
import java.util.*;

public class EventRepository implements GenericRepository<Event> {

    private final Map<Integer, Event> storage = new HashMap<>();
    private final Connection connection;

    private static final String INSERT_SQL = "INSERT INTO events(location_id, user_id, name, date, artists, type, description) VALUES (?, ?, ?, ?, ?, ?, ?)";
    private static final String UPDATE_SQL = "UPDATE events SET location_id=?, name=?, date=?, artists=?, type=?, description=? WHERE event_id=?";
    private static final String DELETE_SQL = "DELETE FROM events WHERE event_id=?";
    private static final String FIND_BY_ID_SQL = "SELECT * FROM events WHERE event_id=?";
    private static final String FIND_ALL_SQL = "SELECT * FROM events";
    private static final String FIND_BY_ORGANISER_SQL = "SELECT * FROM events WHERE user_id=?";

    private static volatile EventRepository instance;

    private EventRepository() {
        this.connection = DatabaseConnectionUtil.getDatabaseConnection();
    }

    public static EventRepository getInstance() {
        if (instance == null) {
            synchronized (EventRepository.class) {
                if (instance == null) {
                    instance = new EventRepository();
                }
            }
        }
        return instance;
    }

    @Override
    public Event save(Event event) {
        try (PreparedStatement stmt = connection.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, event.getLocationId());
            stmt.setInt(2, event.getUserId());
            stmt.setString(3, event.getName());
            stmt.setTimestamp(4, Timestamp.valueOf(event.getDate()));
            stmt.setString(5, event.getArtists());
            stmt.setString(6, event.getEventType());
            stmt.setString(7, event.getDescription());
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    event.setEventId(rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        storage.put(event.getEventId(), event);
        return event;
    }

    public List<Event> findByOrganiser(int organiserId) {
        try (PreparedStatement stmt = connection.prepareStatement(FIND_BY_ORGANISER_SQL)) {
            stmt.setInt(1, organiserId);
            ResultSet rs = stmt.executeQuery();
            extractResultSet(rs);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return new ArrayList<>(storage.values());
    }

    private void extractResultSet(ResultSet rs) throws SQLException {
        while (rs.next()) {
            Event event = new Event(
                rs.getInt("location_id"),
                rs.getInt("user_id"),
                rs.getString("name"),
                rs.getTimestamp("date").toLocalDateTime(),
                rs.getString("artists"),
                rs.getString("type"),
                rs.getString("description"),
                null // ticketTypes would need to be loaded separately
            );
            event.setEventId(rs.getInt("event_id"));
            storage.put(event.getEventId(), event);
        }
    }

    @Override
    public List<Event> findAll() {
        try (PreparedStatement stmt = connection.prepareStatement(FIND_ALL_SQL);
             ResultSet rs = stmt.executeQuery()) {
            extractResultSet(rs);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return new ArrayList<>(storage.values());
    }

    @Override
    public Optional<Event> findById(String id) {
        int eventId = Integer.parseInt(id);
        try (PreparedStatement stmt = connection.prepareStatement(FIND_BY_ID_SQL)) {
            stmt.setInt(1, eventId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Event event = new Event(
                    rs.getInt("location_id"),
                    rs.getInt("user_id"),
                    rs.getString("name"),
                    rs.getTimestamp("date").toLocalDateTime(),
                    rs.getString("artists"),
                    rs.getString("type"),
                    rs.getString("description"),
                    null // ticketTypes would need to be loaded separately
                );
                event.setEventId(rs.getInt("event_id"));
                storage.put(event.getEventId(), event);
                return Optional.of(event);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }

    @Override
    public void update(Event event) {
        try (PreparedStatement stmt = connection.prepareStatement(UPDATE_SQL)) {
            stmt.setInt(1, event.getLocationId());
            stmt.setString(2, event.getName());
            stmt.setTimestamp(3, Timestamp.valueOf(event.getDate()));
            stmt.setString(4, event.getArtists());
            stmt.setString(5, event.getEventType());
            stmt.setString(6, event.getDescription());
            stmt.setInt(7, event.getEventId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        storage.put(event.getEventId(), event);
    }

    @Override
    public void delete(Event event) {
        try (PreparedStatement stmt = connection.prepareStatement(DELETE_SQL)) {
            stmt.setInt(1, event.getEventId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        storage.remove(event.getEventId());
    }
}
