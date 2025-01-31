import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Company {
    private String companyname;
    private List<Tariff> tariffs;
    private DataBase database;
    private int company_id;


    public Company(int id, String companyname) {
        this.companyname = companyname;
        this.tariffs = new ArrayList<>();
        this.database = new DataBase();
        this.company_id = id;

    }

    public int getCompanyId() {
        return company_id;
    }


    public String toString() {
        return companyname;
    }

    public void setCompanyName(String companyName) {

        this.companyname = companyName;
    }

    public List<Tariff> getTariffs() {

        return tariffs;
    }

    public void addTariff(Tariff tariff) {

        tariffs.add(tariff);
    }


    public static Company fromDatabase(DataBase db, int id) {
        String sql = "SELECT * FROM companies WHERE company_id = ?";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                Company company = new Company(id, rs.getString("company_name"));
                company.tariffs = db.getTariffsByCompanyId(id);

                for (Tariff tariff : company.tariffs) {
                    List<Subscriber> subscribers = db.getSubscribersByTariffId(company.getCompanyId());

                    for (Subscriber subscriber : subscribers) {
                        tariff.addSubscriber(subscriber);
                    }
                }

                return company;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static Connection getConnection() throws SQLException {
        return DataBase.getConnection();
    }


    public void setCompany_id(int companyId) {
        this.company_id = companyId;
    }
}
