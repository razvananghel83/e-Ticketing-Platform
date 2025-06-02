package persistance;

import model.Payment;
import persistance.util.DatabaseConnectionUtil;

import java.sql.*;
import java.util.*;

public class PaymentRepository implements GenericRepository<Payment> {

    private final Map<Integer, Payment> storage = new HashMap<>();
    private final Connection connection;

    private static final String INSERT_SQL = "INSERT INTO payments(card_number, amount, payment_date) VALUES (?, ?, ?)";
    private static final String FIND_BY_ID_SQL = "SELECT * FROM payments WHERE payment_id=?";
    private static final String FIND_ALL_SQL = "SELECT * FROM payments";
    private static final String FIND_BY_CARD_SQL = "SELECT * FROM payments WHERE card_number=?";

    private static volatile PaymentRepository instance;

    private PaymentRepository() {
        this.connection = DatabaseConnectionUtil.getDatabaseConnection();
    }

    public static PaymentRepository getInstance() {
        if (instance == null) {
            synchronized (PaymentRepository.class) {
                if (instance == null) {
                    instance = new PaymentRepository();
                }
            }
        }
        return instance;
    }

    @Override
    public Payment save(Payment payment) {
        try (PreparedStatement stmt = connection.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, payment.getCardNumber());
            stmt.setBigDecimal(2, payment.getAmount());
            stmt.setTimestamp(3, Timestamp.valueOf(payment.getPaymentDate()));
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    payment.setPaymentId(rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        storage.put(payment.getPaymentId(), payment);
        return payment;
    }

    public List<Payment> findByCardNumber(String cardNumber) {
        try (PreparedStatement stmt = connection.prepareStatement(FIND_BY_CARD_SQL)) {
            stmt.setString(1, cardNumber);
            ResultSet rs = stmt.executeQuery();
            extractResultSet(rs);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return new ArrayList<>(storage.values());
    }

    private void extractResultSet(ResultSet rs) throws SQLException {
        while (rs.next()) {
            Payment payment = new Payment(
                rs.getTimestamp("payment_date").toLocalDateTime(),
                rs.getBigDecimal("amount"),
                rs.getString("card_number")
            );
            payment.setPaymentId(rs.getInt("payment_id"));
            storage.put(payment.getPaymentId(), payment);
        }
    }

    @Override
    public List<Payment> findAll() {
        try (PreparedStatement stmt = connection.prepareStatement(FIND_ALL_SQL);
             ResultSet rs = stmt.executeQuery()) {
            extractResultSet(rs);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return new ArrayList<>(storage.values());
    }

    @Override
    public Optional<Payment> findById(String id) {
        int paymentId = Integer.parseInt(id);
        try (PreparedStatement stmt = connection.prepareStatement(FIND_BY_ID_SQL)) {
            stmt.setInt(1, paymentId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Payment payment = new Payment(
                        rs.getTimestamp("payment_date").toLocalDateTime(),
                        rs.getBigDecimal("amount"),
                        rs.getString("card_number")
                    );
                    payment.setPaymentId(paymentId);
                    storage.put(paymentId, payment);
                    return Optional.of(payment);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }

    @Override
    public void update(Payment payment) {
        String updateSql = "UPDATE payments SET card_number=?, amount=?, payment_date=? WHERE payment_id=?";
        try (PreparedStatement stmt = connection.prepareStatement(updateSql)) {
            stmt.setString(1, payment.getCardNumber());
            stmt.setBigDecimal(2, payment.getAmount());
            stmt.setTimestamp(3, Timestamp.valueOf(payment.getPaymentDate()));
            stmt.setInt(4, payment.getPaymentId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        storage.put(payment.getPaymentId(), payment);
    }

    @Override
    public void delete(Payment payment) {
        String deleteSql = "DELETE FROM payments WHERE payment_id=?";
        try (PreparedStatement stmt = connection.prepareStatement(deleteSql)) {
            stmt.setInt(1, payment.getPaymentId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        storage.remove(payment.getPaymentId());
    }
}
