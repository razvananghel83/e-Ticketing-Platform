package persistance;

import model.Ticket;
import persistance.util.DatabaseConnectionUtil;

import java.sql.*;
import java.util.*;

public class TicketRepository implements GenericRepository<Ticket> {

    private final Map<Integer, Ticket> storage = new HashMap<>();
    private final Connection connection;

    private static final String INSERT_SQL = "INSERT INTO tickets(ticket_type_id, payment_id, participant_id, created_at) VALUES (?, ?, ?, ?)";
    private static final String FIND_BY_ID_SQL = "SELECT * FROM tickets WHERE ticket_id=?";
    private static final String FIND_ALL_SQL = "SELECT * FROM tickets";
    private static final String FIND_BY_PARTICIPANT_SQL = "SELECT * FROM tickets WHERE participant_id=?";
    private static final String FIND_BY_TYPE_SQL = "SELECT * FROM tickets WHERE ticket_type_id=?";

    private static volatile TicketRepository instance;

    private TicketRepository() {
        this.connection = DatabaseConnectionUtil.getDatabaseConnection();
    }

    public static TicketRepository getInstance() {
        if (instance == null) {
            synchronized (TicketRepository.class) {
                if (instance == null) {
                    instance = new TicketRepository();
                }
            }
        }
        return instance;
    }

    @Override
    public Ticket save(Ticket ticket) {
        try (PreparedStatement stmt = connection.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, ticket.getTicketTypeId());
            stmt.setInt(2, ticket.getPaymentId());
            stmt.setInt(3, ticket.getParticipantId());
            stmt.setTimestamp(4, Timestamp.valueOf(ticket.getCreatedAt()));
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    ticket.setTicketId(rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        storage.put(ticket.getTicketId(), ticket);
        return ticket;
    }

    public List<Ticket> findByParticipant(int participantId) {
        try (PreparedStatement stmt = connection.prepareStatement(FIND_BY_PARTICIPANT_SQL)) {
            stmt.setInt(1, participantId);
            ResultSet rs = stmt.executeQuery();
            extractResultSet(rs);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return new ArrayList<>(storage.values());
    }

    public List<Ticket> findByTicketType(int ticketTypeId) {
        try (PreparedStatement stmt = connection.prepareStatement(FIND_BY_TYPE_SQL)) {
            stmt.setInt(1, ticketTypeId);
            ResultSet rs = stmt.executeQuery();
            extractResultSet(rs);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return new ArrayList<>(storage.values());
    }

    private void extractResultSet(ResultSet rs) throws SQLException {
        while (rs.next()) {
            Ticket ticket = new Ticket(
                    rs.getTimestamp("created_at").toLocalDateTime(),
                    rs.getInt("participant_id"),
                    rs.getInt("payment_id"),
                    rs.getInt("ticket_type_id")
            );
            ticket.setTicketId(rs.getInt("ticket_id"));
            storage.put(ticket.getTicketId(), ticket);
        }
    }

    @Override
    public List<Ticket> findAll() {
        try (PreparedStatement stmt = connection.prepareStatement(FIND_ALL_SQL);
             ResultSet rs = stmt.executeQuery()) {
            extractResultSet(rs);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return new ArrayList<>(storage.values());
    }

    @Override
    public Optional<Ticket> findById(String id) {
        int ticketId = Integer.parseInt(id);
        try (PreparedStatement stmt = connection.prepareStatement(FIND_BY_ID_SQL)) {
            stmt.setInt(1, ticketId);
            ResultSet rs = stmt.executeQuery();
            extractResultSet(rs);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.ofNullable(storage.get(ticketId));
    }

    @Override
    public void update(Ticket ticket) {
        // Define UPDATE SQL if needed for tickets
        String updateSql = "UPDATE tickets SET ticket_type_id=?, payment_id=?, participant_id=?, created_at=? WHERE ticket_id=?";
        try (PreparedStatement stmt = connection.prepareStatement(updateSql)) {
            stmt.setInt(1, ticket.getTicketTypeId());
            stmt.setInt(2, ticket.getPaymentId());
            stmt.setInt(3, ticket.getParticipantId());
            stmt.setTimestamp(4, Timestamp.valueOf(ticket.getCreatedAt()));
            stmt.setInt(5, ticket.getTicketId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        storage.put(ticket.getTicketId(), ticket);
    }

    @Override
    public void delete(Ticket ticket) {
        // Define DELETE SQL if needed for tickets
        String deleteSql = "DELETE FROM tickets WHERE ticket_id=?";
        try (PreparedStatement stmt = connection.prepareStatement(deleteSql)) {
            stmt.setInt(1, ticket.getTicketId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        storage.remove(ticket.getTicketId());
    }
}