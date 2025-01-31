import java.util.ArrayList;
import java.util.List;

public class Tariff {
    public List<Subscriber> subscribers;
    private String name;
    private double price;
    private Company company;
    private int company_id;
    private DataBase database;
    private int tariff_Id;
    private Subscriber subscriber;

    public Tariff(String name, double price, int company_id) {
        this.name = name;
        this.price = price;
        this.company_id = company_id;
        this.subscribers = new ArrayList<>();
        this.database = new DataBase();
        this.tariff_Id = tariff_Id;
    }

    public String getName() {
        return name;
    }
    public double getPrice() {
        return price;
    }
    public Company getCompany() {
        return company;
    }
    public List<Subscriber> getSubscribers() {
        return subscribers;
    }
    public int getCompany_id() {
        return company_id;
    }

    public void addSubscriber(Subscriber subscriber) {
        if (this.subscribers == null) {
            this.subscribers = new ArrayList<>();
        }
        this.subscribers.add(subscriber);
    }
    public void removeSubscriber(Subscriber subscriber) {
        subscribers.remove(subscriber);
    }
    public int getNumberOfSubscribers() {
        return subscribers.size();
    }

    public int getTariffId() {
        return tariff_Id;
    }

    public void setTariffId(int tariff_Id) {
        this.tariff_Id = tariff_Id;
    }
    public void setSubscribers(List<Subscriber> subscribers) {
        this.subscribers = subscribers;
    }
    public String toString() {
        return "Tariff: " + name + ", Price: " + price + ", Subscribers: " + subscribers.size();
    }
}
