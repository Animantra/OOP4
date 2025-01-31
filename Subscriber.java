public class Subscriber {
    private  int tariff_id;
    private int subscriber_id;
    private String namesub;
    private String phonenum;
    private Tariff tariff;
    private double balance;

    public Subscriber(int subscriber_id, String name, double balance, String phonenum, int tariff_id) {
        this.subscriber_id = subscriber_id;
        this.namesub = name;
        this.phonenum = phonenum;
        this.balance = balance;
        this.tariff_id = tariff_id;
    }
    public void setSubscriber_id(int subscriber_id) {
        this.subscriber_id = subscriber_id;
    }
    public String getNamesub() {
        return namesub;
    }
    public String getPhonenum() {
        return phonenum;
    }
    public double getBalance() {
        return balance;
    }
    public int getSubscriber_id() {
        return subscriber_id;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }
    public void setNamesub(String namesub){
        this.namesub=namesub;
    }
    public void setPhonenum(String phonenum){
        this.phonenum=phonenum;
    }

    public void setTariff_id(int tariffId){
        this.tariff_id=tariffId;
    }

    public Tariff getTariff() {
        return tariff;
    }

    public void setTariff(Tariff tariff) {
        this.tariff = tariff;
    }

    public int getTariff_id() {
        return tariff_id;
    }
    @Override
    public String toString() {
        return "Name: " + namesub + ", Phone: " + phonenum + ", Balance: " + balance;
    }



    public void replenishAccount(int amount) {
        if (amount > 0) {
            balance += amount;
            System.out.println("Successfully added " + amount + " to " + namesub + "'s account. New balance: " + balance);
        } else {
            System.out.println("Amount must be positive. Operation failed.");
        }
    }
}
