import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DataBase {
    static String connectionUrl = "jdbc:postgresql://localhost:5432/endtermdb";

    public static Connection getConnection() {
        try {
            Class.forName("org.postgresql.Driver");
            return DriverManager.getConnection(connectionUrl, "postgres", "FireFoxDB");
        } catch (ClassNotFoundException e) {
            System.out.println("PostgreSQL Driver not found: " + e.getMessage());
            throw new RuntimeException(e);
        } catch (SQLException e) {
            System.out.println("Connection to database failed: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }


    public List<Company> getCompanies() {
        List<Company> companies = new ArrayList<>();
        String sql = "SELECT * FROM companies";
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                Company company = new Company(rs.getInt("company_id"), rs.getString("company_name"));
                companies.add(company);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return companies;
    }


    public List<Tariff> getTariffsByCompanyId(int companyId) {
        List<Tariff> tariffs = new ArrayList<>();
        String sql = "SELECT * FROM tariffs WHERE company_id = ?";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, companyId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Tariff tariff = new Tariff(rs.getString("name"), rs.getDouble("price"), companyId);
                int tariffId = rs.getInt("tariff_id");
                tariff.setTariffId(tariffId);

                List<Subscriber> subscribers = getSubscribersByTariffId(tariffId);
                tariff.setSubscribers(subscribers);
                tariffs.add(tariff);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tariffs;
    }


    public List<Subscriber> getSubscribersByTariffId(int tariffId) {
        List<Subscriber> subscribers = new ArrayList<>();
        String sql = "SELECT * FROM subscribers WHERE tariff_id = ?";

        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, tariffId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Subscriber subscriber = new Subscriber(
                        rs.getInt("subscriber_id"),
                        rs.getString("name"),
                        rs.getDouble("balance"),
                        rs.getString("phone_number"),
                        rs.getInt("tariff_id")
                );
                subscribers.add(subscriber);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return subscribers;
    }



    public List<Subscriber> getSubscribersByCompanyId(int companyId) {
        List<Subscriber> subscribers = new ArrayList<>();
        String sql = "SELECT s.* FROM subscribers s " +
                "JOIN tariffs t ON s.tariff_id = t.tariff_id " +
                "WHERE t.company_id = ?";

        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, companyId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Subscriber subscriber = new Subscriber(
                        rs.getInt("subscriber_id"),
                        rs.getString("name"),
                        rs.getDouble("balance"),
                        rs.getString("phone_number"),
                        rs.getInt("tariff_id")
                );
                subscribers.add(subscriber);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return subscribers;
    }


    public void updateSubscriberBalance(Subscriber subscriber) {
        String query = "UPDATE subscribers SET balance = ? WHERE phone_number = ?";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(query)) {
            conn.setAutoCommit(false);

            pstmt.setDouble(1, subscriber.getBalance());
            pstmt.setString(2, subscriber.getPhonenum());

            System.out.println("SQL Запрос: UPDATE subscribers SET balance = " + subscriber.getBalance() +
                    " WHERE phone_number = '" + subscriber.getPhonenum() + "';");
            int rowsUpdated = pstmt.executeUpdate();
            if (rowsUpdated > 0) {
                conn.commit();
                System.out.println("Subscriber balance has been updared.");
            } else {
                System.out.println("Subscriber not find.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void add_company(String company_name) {
        String query = "INSERT INTO companies(company_name) VALUES (?)";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, company_name);
            pstmt.executeUpdate();
            System.out.println("Company has been created");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void add_tariff(String name, double price, int company_id) {
        String query = "INSERT INTO tariffs(name,price,company_id) VALUES (?,?,?)";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, name);
            pstmt.setDouble(2, price);
            pstmt.setInt(3, company_id);

            pstmt.executeUpdate();
            System.out.println("Tariff has been created for company id " + company_id);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void add_subs(String name, String phone_number, double balance, int tariff_id) {
        String query = "INSERT INTO subscribers(name,phone_number,balance,tariff_id) VALUES (?,?,?,?)";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, name);
            pstmt.setString(2, phone_number);
            pstmt.setDouble(3, balance);
            pstmt.setInt(4, tariff_id);

            pstmt.executeUpdate();
            System.out.println("Subscriber has been added for tariff " + tariff_id);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isPhoneNumberExists(String phoneNumber) {
        String query = "SELECT COUNT(*) FROM subscribers WHERE phone_number = ?";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, phoneNumber);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void deleteCompany(int company_id) {
        String query = "DELETE FROM companies WHERE company_id=?";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, company_id);
            pstmt.executeUpdate();
            System.out.println("Company has been deleted");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteTariff(int tariff_id) {
        String query = "DELETE FROM tariffs WHERE tariff_id=?";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, tariff_id);
            pstmt.executeUpdate();
            System.out.println("Tariff has been deleted");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteSub(int subscriber_id) {
        String query = "DELETE FROM subscribers WHERE subscriber_id=?";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, subscriber_id);
            pstmt.executeUpdate();
            System.out.println("Subscriber has been deleted");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Subscriber getSubscriberByPhoneNumber(String phoneNumber) {
        String sql = "SELECT * FROM subscribers WHERE phone_number = ?";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, phoneNumber);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {

                return new Subscriber(
                        rs.getInt("subscriber_id"),
                        rs.getString("name"),
                        rs.getDouble("balance"),
                        rs.getString("phone_number"),
                        rs.getInt("tariff_id")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

}
