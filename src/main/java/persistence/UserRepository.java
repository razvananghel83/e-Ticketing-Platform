package persistence;

import model.User;
import persistence.util.DatabaseConnectionUtil;

import java.sql.*;
import java.util.*;

public class UserRepository implements GenericRepository<User> {

    private final Map<Integer, User> storage = new HashMap<>();
    private final Connection connection;

    private static final String INSERT_SQL = "INSERT INTO users(username, email, password, user_type) VALUES (?, ?, ?, ?)";
    private static final String UPDATE_SQL = "UPDATE users SET username=?, email=?, password=?, user_type=? WHERE user_id=?";
    private static final String DELETE_SQL = "DELETE FROM users WHERE user_id=?";
    private static final String FIND_BY_ID_SQL = "SELECT * FROM users WHERE user_id=?";
    private static final String FIND_ALL_SQL = "SELECT * FROM users";
    private static final String FIND_BY_USERNAME_SQL = "SELECT * FROM users WHERE username=?";
    private static final String FIND_BY_EMAIL_SQL = "SELECT * FROM users WHERE email=?";

    private static volatile UserRepository instance;

    private UserRepository() {
        this.connection = DatabaseConnectionUtil.getDatabaseConnection();
    }

    public static UserRepository getInstance() {
        if (instance == null) {
            synchronized (UserRepository.class) {
                if (instance == null) {
                    instance = new UserRepository();
                }
            }
        }
        return instance;
    }

    @Override
    public User save(User user) {
        try (PreparedStatement stmt = connection.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getPassword());
            stmt.setString(4, user.getUserType());
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    user.setUserId(rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        storage.put(user.getUserId(), user);
        return user;
    }

    @Override
    public List<User> findAll() {
        try (PreparedStatement stmt = connection.prepareStatement(FIND_ALL_SQL);
             ResultSet rs = stmt.executeQuery()) {
            extractResultSet(rs);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return new ArrayList<>(storage.values());
    }

    @Override
    public Optional<User> findById(String id) {
        int userId = Integer.parseInt(id);
        try (PreparedStatement stmt = connection.prepareStatement(FIND_BY_ID_SQL)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            extractResultSet(rs);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.ofNullable(storage.get(userId));
    }

    public Optional<User> findByUsername(String username) {
        try (PreparedStatement stmt = connection.prepareStatement(FIND_BY_USERNAME_SQL)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int userId = rs.getInt("user_id");
                    User user = new User(
                            rs.getString("username"),
                            rs.getString("email"),
                            rs.getString("password"),
                            rs.getString("user_type")
                    );
                    user.setUserId(userId);
                    storage.put(userId, user);
                    return Optional.of(user);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }

    public Optional<User> findByEmail(String email) {
        try (PreparedStatement stmt = connection.prepareStatement(FIND_BY_EMAIL_SQL)) {
            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int userId = rs.getInt("user_id");
                    User user = new User(
                            rs.getString("username"),
                            rs.getString("email"),
                            rs.getString("password"),
                            rs.getString("user_type")
                    );
                    user.setUserId(userId);
                    storage.put(userId, user);
                    return Optional.of(user);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }

    private void extractResultSet(ResultSet rs) throws SQLException {
        while (rs.next()) {
            int userId = rs.getInt("user_id");
            User user = new User(
                    rs.getString("username"),
                    rs.getString("email"),
                    rs.getString("password"),
                    rs.getString("user_type")
            );
            user.setUserId(userId);
            storage.put(userId, user);
        }
    }

    @Override
    public void update(User user) {
        try (PreparedStatement stmt = connection.prepareStatement(UPDATE_SQL)) {
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getPassword());
            stmt.setString(4, user.getUserType());
            stmt.setInt(5, user.getUserId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        storage.put(user.getUserId(), user);
    }

    @Override
    public void delete(User user) {
        try (PreparedStatement stmt = connection.prepareStatement(DELETE_SQL)) {
            stmt.setInt(1, user.getUserId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        storage.remove(user.getUserId());
    }

}