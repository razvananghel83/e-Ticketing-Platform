package persistance;

import model.TicketType;
import persistance.util.DatabaseConnectionUtil;

import java.sql.*;
import java.util.*;

public class TicketTypeRepository implements GenericRepository<TicketType> {

    private final Map<Integer, TicketType> storage = new HashMap<>();
    private final Connection connection;

    private static final String INSERT_SQL = "INSERT INTO ticket_types(event_id, name, description, price, max_quantity) VALUES (?, ?, ?, ?, ?)";
    private static final String FIND_BY_EVENT_SQL = "SELECT * FROM ticket_types WHERE event_id=?";
    private static final String FIND_BY_ID_SQL = "SELECT * FROM ticket_types WHERE ticket_type_id=?";

    private static volatile TicketTypeRepository instance;

    private TicketTypeRepository() {
        this.connection = DatabaseConnectionUtil.getDatabaseConnection();
    }

    public static TicketTypeRepository getInstance() {
        if (instance == null) {
            synchronized (TicketTypeRepository.class) {
                if (instance == null) {
                    instance = new TicketTypeRepository();
                }
            }
        }
        return instance;
    }

    public List<TicketType> findByEvent(int eventId) {
        try (PreparedStatement stmt = connection.prepareStatement(FIND_BY_EVENT_SQL)) {
            stmt.setInt(1, eventId);
            ResultSet rs = stmt.executeQuery();
            extractResultSet(rs);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return new ArrayList<>(storage.values());
    }

    private void extractResultSet(ResultSet rs) throws SQLException {
        while (rs.next()) {
            TicketType ticketType = new TicketType(
                    rs.getInt("max_quantity"),
                    rs.getBigDecimal("price"),
                    rs.getString("description"),
                    rs.getString("name"),
                    rs.getInt("event_id")
            );
            ticketType.setTicketTypeId(rs.getInt("ticket_type_id"));
            storage.put(ticketType.getTicketTypeId(), ticketType);
        }
    }

    @Override
    public TicketType save(TicketType ticketType) {
        try (PreparedStatement stmt = connection.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, ticketType.getEventId());
            stmt.setString(2, ticketType.getName());
            stmt.setString(3, ticketType.getDescription());
            stmt.setBigDecimal(4, ticketType.getPrice());
            stmt.setInt(5, ticketType.getMaxQuantity());
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    ticketType.setTicketTypeId(rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        storage.put(ticketType.getTicketTypeId(), ticketType);
        return ticketType;
    }

    @Override
    public List<TicketType> findAll() {
        String findAllSql = "SELECT * FROM ticket_types";
        try (PreparedStatement stmt = connection.prepareStatement(findAllSql);
             ResultSet rs = stmt.executeQuery()) {
            extractResultSet(rs);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return new ArrayList<>(storage.values());
    }

    @Override
    public Optional<TicketType> findById(String id) {
        int ticketTypeId = Integer.parseInt(id);
        try (PreparedStatement stmt = connection.prepareStatement(FIND_BY_ID_SQL)) {
            stmt.setInt(1, ticketTypeId);
            ResultSet rs = stmt.executeQuery();
            extractResultSet(rs);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.ofNullable(storage.get(ticketTypeId));
    }

    @Override
    public void update(TicketType ticketType) {
        String updateSql = "UPDATE ticket_types SET event_id=?, name=?, description=?, price=?, max_quantity=? WHERE ticket_type_id=?";
        try (PreparedStatement stmt = connection.prepareStatement(updateSql)) {
            stmt.setInt(1, ticketType.getEventId());
            stmt.setString(2, ticketType.getName());
            stmt.setString(3, ticketType.getDescription());
            stmt.setBigDecimal(4, ticketType.getPrice());
            stmt.setInt(5, ticketType.getMaxQuantity());
            stmt.setInt(6, ticketType.getTicketTypeId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        storage.put(ticketType.getTicketTypeId(), ticketType);
    }

    @Override
    public void delete(TicketType ticketType) {
        String deleteSql = "DELETE FROM ticket_types WHERE ticket_type_id=?";
        try (PreparedStatement stmt = connection.prepareStatement(deleteSql)) {
            stmt.setInt(1, ticketType.getTicketTypeId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        storage.remove(ticketType.getTicketTypeId());
    }
}