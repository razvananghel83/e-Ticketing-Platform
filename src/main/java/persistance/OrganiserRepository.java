package persistance;

import model.Organiser;
import persistance.util.DatabaseConnectionUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.Optional;

public class OrganiserRepository {

    private static final String INSERT_SQL = "INSERT INTO organisers(user_id) VALUES (?)";
    private static final String FIND_BY_ID_SQL = "SELECT o.*, u.username, u.email, u.password, u.user_type FROM organisers o JOIN users u ON o.user_id = u.user_id WHERE o.user_id=?";

    private final Connection connection;
    private static volatile OrganiserRepository instance;

    private OrganiserRepository() {
        this.connection = DatabaseConnectionUtil.getDatabaseConnection();
    }

    public static OrganiserRepository getInstance() {
        if (instance == null) {
            synchronized (OrganiserRepository.class) {
                if (instance == null) {
                    instance = new OrganiserRepository();
                }
            }
        }
        return instance;
    }

    public void save(Organiser organiser) {
        try (PreparedStatement stmt = connection.prepareStatement(INSERT_SQL)) {
            stmt.setInt(1, organiser.getUserId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Optional<Organiser> findById(int userId) {
        try (PreparedStatement stmt = connection.prepareStatement(FIND_BY_ID_SQL)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String username = rs.getString("username");
                    String email = rs.getString("email");
                    String password = rs.getString("password");
                    String userType = rs.getString("user_type");

                    // Create Organiser with required parameters
                    // Using empty ArrayList for organizedEvents
                    Organiser organiser = new Organiser(username, email, password, userType, new ArrayList<>());
                    organiser.setUserId(userId);
                    return Optional.of(organiser);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }
}