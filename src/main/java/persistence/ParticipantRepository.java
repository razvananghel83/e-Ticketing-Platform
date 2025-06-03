package persistence;

import model.Participant;
import persistence.util.DatabaseConnectionUtil;

import java.sql.*;
import java.util.*;
import java.util.ArrayList;

public class ParticipantRepository implements GenericRepository<Participant> {

    private static final String INSERT_SQL = "INSERT INTO participants(user_id) VALUES (?)";
    private static final String FIND_BY_ID_SQL = "SELECT p.*, u.username, u.email, u.password, u.user_type FROM participants p JOIN users u ON p.user_id = u.user_id WHERE p.user_id=?";
    private static final String FIND_ALL_SQL = "SELECT p.*, u.username, u.email, u.password, u.user_type FROM participants p JOIN users u ON p.user_id = u.user_id";
    private static final String UPDATE_SQL = "UPDATE participants SET user_id=? WHERE user_id=?";
    private static final String DELETE_SQL = "DELETE FROM participants WHERE user_id=?";

    private final Map<Integer, Participant> storage = new HashMap<>();
    private final Connection connection;
    private static volatile ParticipantRepository instance;

    private ParticipantRepository() {
        this.connection = DatabaseConnectionUtil.getDatabaseConnection();
    }

    public static ParticipantRepository getInstance() {
        if (instance == null) {
            synchronized (ParticipantRepository.class) {
                if (instance == null) {
                    instance = new ParticipantRepository();
                }
            }
        }
        return instance;
    }

    @Override
    public Participant save(Participant participant) {
        try (PreparedStatement stmt = connection.prepareStatement(INSERT_SQL)) {
            stmt.setInt(1, participant.getUserId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        storage.put(participant.getUserId(), participant);
        return participant;
    }

    public Optional<Participant> findById(int userId) {
        try (PreparedStatement stmt = connection.prepareStatement(FIND_BY_ID_SQL)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Participant participant = createParticipantFromResultSet(rs);
                    storage.put(userId, participant);
                    return Optional.of(participant);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Participant> findById(String id) {
        return findById(Integer.parseInt(id));
    }

    @Override
    public List<Participant> findAll() {
        try (PreparedStatement stmt = connection.prepareStatement(FIND_ALL_SQL);
             ResultSet rs = stmt.executeQuery()) {
            extractResultSet(rs);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return new ArrayList<>(storage.values());
    }

    @Override
    public void update(Participant participant) {
        try (PreparedStatement stmt = connection.prepareStatement(UPDATE_SQL)) {
            stmt.setInt(1, participant.getUserId());
            stmt.setInt(2, participant.getUserId()); // Using same ID for both params as it's the identifier
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        storage.put(participant.getUserId(), participant);
    }

    @Override
    public void delete(Participant participant) {
        try (PreparedStatement stmt = connection.prepareStatement(DELETE_SQL)) {
            stmt.setInt(1, participant.getUserId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        storage.remove(participant.getUserId());
    }

    private void extractResultSet(ResultSet rs) throws SQLException {
        while (rs.next()) {
            Participant participant = createParticipantFromResultSet(rs);
            storage.put(participant.getUserId(), participant);
        }
    }

    private Participant createParticipantFromResultSet(ResultSet rs) throws SQLException {
        int userId = rs.getInt("user_id");
        String username = rs.getString("username");
        String email = rs.getString("email");
        String password = rs.getString("password");
        String userType = rs.getString("user_type");

        // Create a participant with all required parameters
        // Currently creating with empty bank cards list
        Participant participant = new Participant(username, email, password, userType, new ArrayList<>());
        participant.setUserId(userId);
        return participant;
    }
}