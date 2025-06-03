package persistence;

import model.BankCard;
import persistence.util.DatabaseConnectionUtil;

import java.sql.*;
import java.sql.Date;
import java.util.*;

public class BankCardRepository implements GenericRepository<BankCard> {

    private final Map<String, BankCard> storage = new HashMap<>();
    private final Connection connection;

    private static final String INSERT_SQL = "INSERT INTO bank_cards(card_number, cvv, expiry_date, card_holder_name, user_id) VALUES (?, ?, ?, ?, ?)";
    private static final String UPDATE_SQL = "UPDATE bank_cards SET cvv=?, expiry_date=?, card_holder_name=? WHERE card_number=?";
    private static final String DELETE_SQL = "DELETE FROM bank_cards WHERE card_number=?";
    private static final String FIND_BY_ID_SQL = "SELECT * FROM bank_cards WHERE card_number=?";
    private static final String FIND_ALL_SQL = "SELECT * FROM bank_cards";
    private static final String FIND_BY_USER_SQL = "SELECT * FROM bank_cards WHERE user_id=?";

    private static volatile BankCardRepository instance;

    private BankCardRepository() {
        this.connection = DatabaseConnectionUtil.getDatabaseConnection();
    }

    public static BankCardRepository getInstance() {
        if (instance == null) {
            synchronized (BankCardRepository.class) {
                if (instance == null) {
                    instance = new BankCardRepository();
                }
            }
        }
        return instance;
    }

    @Override
    public BankCard save(BankCard card) {
        try (PreparedStatement stmt = connection.prepareStatement(INSERT_SQL)) {
            stmt.setString(1, card.getCardNumber());
            stmt.setString(2, card.getCvv());
            stmt.setDate(3, Date.valueOf(card.getExpiryDate()));
            stmt.setString(4, card.getCardHolderName());
            stmt.setInt(5, card.getUserId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        storage.put(card.getCardNumber(), card);
        return card;
    }

    @Override
    public List<BankCard> findAll() {
        try (PreparedStatement stmt = connection.prepareStatement(FIND_ALL_SQL);
             ResultSet rs = stmt.executeQuery()) {
            extractResultSet(rs);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return new ArrayList<>(storage.values());
    }

    @Override
    public Optional<BankCard> findById(String id) {
        try (PreparedStatement stmt = connection.prepareStatement(FIND_BY_ID_SQL)) {
            stmt.setString(1, id);
            ResultSet rs = stmt.executeQuery();
            extractResultSet(rs);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public void update(BankCard card) {
        try (PreparedStatement stmt = connection.prepareStatement(UPDATE_SQL)) {
            stmt.setString(1, card.getCvv());
            stmt.setDate(2, Date.valueOf(card.getExpiryDate()));
            stmt.setString(3, card.getCardHolderName());
            stmt.setString(4, card.getCardNumber());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        storage.put(card.getCardNumber(), card);
    }

    @Override
    public void delete(BankCard card) {
        try (PreparedStatement stmt = connection.prepareStatement(DELETE_SQL)) {
            stmt.setString(1, card.getCardNumber());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        storage.remove(card.getCardNumber());
    }

    public List<BankCard> findByUserId(int userId) {
        try (PreparedStatement stmt = connection.prepareStatement(FIND_BY_USER_SQL)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            extractResultSet(rs);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return new ArrayList<>(storage.values());
    }

    private void extractResultSet(ResultSet rs) throws SQLException {
        while (rs.next()) {
            BankCard card = new BankCard(
                    rs.getString("card_number"),
                    rs.getString("cvv"),
                    rs.getString("card_holder_name"),
                    rs.getDate("expiry_date").toLocalDate(),
                    rs.getInt("user_id")
            );
            storage.put(card.getCardNumber(), card);
        }
    }
}
