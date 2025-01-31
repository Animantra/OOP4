import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;


public class MainFrame extends JFrame {

    private JButton submitButton;
    private JTextField inputField;
    private JFrame frame;
    private Company company1;
    private Company company2;
    private DataBase database;
    private JTextArea window;

    private JList<Company> companyList;
    private DefaultListModel<Company> companyListModel;

    private JList<Subscriber> subscriberList;
    private DefaultListModel<Subscriber> subscriberListModel;

    private JList<Tariff> tariffList;
    private DefaultListModel<Tariff> tariffListModel;

    public MainFrame() {
        database = new DataBase();


        // Настройка главного окна
        setTitle("Company Management System");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(20, 20));

        // Создание панелей для отображения информации о компании, подписчиках и тарифах
        JPanel companyPanel = new JPanel();
        JPanel subscriberPanel = new JPanel();
        JPanel tariffPanel=new JPanel();
        JPanel outputPanel = new JPanel(new BorderLayout());

        // Панель для пополнения счета(диалог окно)
        JPanel replacc=new JPanel();
        replacc.setLayout(new GridLayout(2,2));
        replacc.add(new JLabel("Replenish account"));
        JTextField balanceField = new JTextField();
        replacc.add(balanceField);


        //кнопка вывода компании
        JButton loadButton = new JButton("Load company");
        loadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadCompanies();
            }
        });

        //кнопка для вывода тариффов в панеле тариф
        JButton gettariffsButton=new JButton("Get Tariffs");
        gettariffsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
               loadTariffs();

            }
        });

        //кнопка для вывода сабов в панеле subs
        JButton getsubsButton= new JButton("Get Subscribers");
        getsubsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadSubscribers();
            }
        });

        //кнопка для поплнения баланса
        JButton replenish_accountButton = new JButton("Replenish account");
        replenish_accountButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int option = JOptionPane.showConfirmDialog(null, replacc, "Enter Replenish Amount", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

                if (option == JOptionPane.OK_OPTION) {
                    try {
                        double balance = Double.parseDouble(balanceField.getText());
                        if (balance > 0) {
                            replenish(balance);
                        } else {
                            JOptionPane.showMessageDialog(null, "Invalid amount! Please enter a positive number.");
                        }
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(null, "Invalid input! Please enter a valid number.");
                    }
                }
            }
        });
        //добавить новую компанию
        JButton addcompany=new JButton("Add new company");
        addcompany.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String name = JOptionPane.showInputDialog("Enter company name:");
                if (name != null && !name.trim().isEmpty()) {
                    DataBase db = new DataBase();
                    db.add_company(name);
                    loadCompanies();
                } else {
                    JOptionPane.showMessageDialog(null, "field are required!");
                }
            }
        });


        //добавить новый тарифф
        JButton addtariff=new JButton("Add new tariff");
        addtariff.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                Company selectedcompany = companyList.getSelectedValue();
                if (selectedcompany == null) {
                    JOptionPane.showMessageDialog(null, "Please select a company.");
                    return;
                }
                String name = JOptionPane.showInputDialog("Enter tariff name");
                String priceStr = JOptionPane.showInputDialog("Enter price");
                Double price = null;
                if (name != null && !name.trim().isEmpty() && priceStr != null && !priceStr.trim().isEmpty()) {
                    try {
                        price = Double.parseDouble(priceStr);
                        if (price <= 0) {
                            JOptionPane.showMessageDialog(null, "Price must be greater than zero.");
                            return;
                        }
                        int companyId = selectedcompany.getCompanyId();
                        database.add_tariff(name, price, companyId);
                        loadTariffs();
                        JOptionPane.showMessageDialog(null, "Tariff added successfully.");
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(null, "Invalid price input. Please enter a valid number.");
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Both fields are required.");
                }
            }
        });

        // добавить абонента
        JButton addsub = new JButton("Add new sub");
        addsub.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Tariff selectedtariff = tariffList.getSelectedValue();
                if (selectedtariff == null) {
                    JOptionPane.showMessageDialog(null, "Please select a tariff.");
                    return;
                }

                String name = JOptionPane.showInputDialog("Enter Subscriber name: ");

                String phonenum = JOptionPane.showInputDialog("Enter Subscriber number: ");
                if (database.isPhoneNumberExists(phonenum)) {
                    JOptionPane.showMessageDialog(null, "This phone number is already registered. Please enter a unique number.");
                    return;
                }

                String balanceStr = JOptionPane.showInputDialog("Enter Subscriber balance: ");

                Double balance = null;
                if (name != null && !name.trim().isEmpty() &&
                        phonenum != null && !phonenum.trim().isEmpty())
                    try {
                        balance = Double.parseDouble(balanceStr);

                        if (balance < 0) {
                            JOptionPane.showMessageDialog(null, "Balance must be greater than zero or equal.");
                            return;
                        }

                        int tariffId = selectedtariff.getTariffId();
                        database.add_subs(name, phonenum, balance, tariffId);
                        loadSubscribers();
                        loadTariffs();
                        JOptionPane.showMessageDialog(null, "Subscriber added successfully.");

                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(null, "Invalid balance input. Please enter a valid number.");
                    }
                else {
                    JOptionPane.showMessageDialog(null, "All fields are required.");
                }
            }
        });

        // удалить компанию
        JButton removecompany=new JButton("Remove Company");
        removecompany.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Company selectedCompany=companyList.getSelectedValue();
                if (selectedCompany == null) {
                    JOptionPane.showMessageDialog(null, "Please select a company.");
                    return;
                }
                int confirm = JOptionPane.showConfirmDialog(
                        null,
                        "Are you sure you want to delete " + selectedCompany.toString() + "?",
                        "Confirm Deletion",
                        JOptionPane.YES_NO_OPTION
                );


                if (confirm==JOptionPane.YES_OPTION) {
                    database.deleteCompany(selectedCompany.getCompanyId());
                    loadCompanies();
                }
            }
        });

        // удалять тарифф
        JButton removetariif=new JButton("Remove tariff");
        removetariif.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Tariff selectedTariff=tariffList.getSelectedValue();
                if (selectedTariff == null) {
                    JOptionPane.showMessageDialog(null, "Please select a tariff.");
                    return;
                }
                int confirm = JOptionPane.showConfirmDialog(
                        null,
                        "Are you sure you want to delete " + selectedTariff.toString() + "?",
                        "Confirm Deletion",
                        JOptionPane.YES_NO_OPTION
                );


                if (confirm==JOptionPane.YES_OPTION) {
                    database.deleteTariff(selectedTariff.getTariffId());
                    loadTariffs();
                }
            }
        });

        // удалить абонента
        JButton removesub=new JButton("Remove subscriber");
        removesub.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Subscriber selectedSub=subscriberList.getSelectedValue();
                if (selectedSub == null) {
                    JOptionPane.showMessageDialog(null, "Please select a subscriber.");
                    return;
                }
                int confirm = JOptionPane.showConfirmDialog(
                        null,
                        "Are you sure you want to delete " + selectedSub.toString() + "?",
                        "Confirm Deletion",
                        JOptionPane.YES_NO_OPTION
                );


                if (confirm==JOptionPane.YES_OPTION) {
                    database.deleteSub(selectedSub.getSubscriber_id());
                    loadTariffs();

                }
            }
        });

        // искать саба по номеру
        JButton search = new JButton("Search sub by number");
        search.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JTextField phoneNumberField = new JTextField(20);

                JPanel panel = new JPanel();
                panel.add(new JLabel("Enter phone number:"));
                panel.add(phoneNumberField);

                int option = JOptionPane.showConfirmDialog(
                        null,
                        panel,
                        "Enter Subscriber Phone Number",
                        JOptionPane.OK_CANCEL_OPTION,
                        JOptionPane.PLAIN_MESSAGE
                );

                if (option == JOptionPane.OK_OPTION) {
                    String phoneNumber = phoneNumberField.getText().trim();

                    if (phoneNumber.isEmpty()) {
                        JOptionPane.showMessageDialog(null, "Please enter a phone number.");
                        return;
                    }

                    Subscriber subscriber = database.getSubscriberByPhoneNumber(phoneNumber);

                    if (subscriber != null) {
                        JOptionPane.showMessageDialog(null, "Subscriber found: \n" + subscriber.toString());
                    } else {
                        JOptionPane.showMessageDialog(null, "No subscriber found with this phone number.");
                    }
                }
            }
        });



        // Настройка размеров и оформления панелей
        companyPanel.setPreferredSize(new Dimension(400, 600));
        tariffPanel.setPreferredSize(new Dimension(400,600));
        subscriberPanel.setPreferredSize(new Dimension(400, 600));

        companyPanel.setBorder(BorderFactory.createTitledBorder("Company"));
        tariffPanel.setBorder(BorderFactory.createTitledBorder("Tariffs"));
        subscriberPanel.setBorder(BorderFactory.createTitledBorder("Subscriber"));

        // Устанавливаем фоновый цвет для панелей
        companyPanel.setBackground(Color.white);
        tariffPanel.setBackground(Color.white);
        subscriberPanel.setBackground(Color.white);

        //Устанавливаем менеджер компоновки BoxLayout для панелей
        companyPanel.setLayout(new BoxLayout(companyPanel, BoxLayout.Y_AXIS));
        tariffPanel.setLayout(new BoxLayout(tariffPanel, BoxLayout.Y_AXIS));
        subscriberPanel.setLayout(new BoxLayout(subscriberPanel, BoxLayout.Y_AXIS));



        // Настройка списка компаний
        companyListModel = new DefaultListModel<>();
        companyList = new JList<>(companyListModel);
        companyList.setVisibleRowCount(10);
        companyList.setFixedCellWidth(200);
        JScrollPane listScrollPane = new JScrollPane(companyList);
        listScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

        // Добавляем элементы на панель компании
        companyPanel.add(listScrollPane);
        companyPanel.add(gettariffsButton);

        // Настройка списка подписчиков
        subscriberListModel = new DefaultListModel<>();
        subscriberList= new JList<>(subscriberListModel);
        subscriberList.setVisibleRowCount(10);
        subscriberList.setFixedCellWidth(200);
        JScrollPane subscriberScrollPane = new JScrollPane(subscriberList);
        subscriberScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);


        // Настройка списка тариффов
        tariffListModel=new DefaultListModel<>();
        tariffList= new JList<>(tariffListModel);
        tariffList.setVisibleRowCount(10);
        tariffList.setFixedCellWidth(200);
        JScrollPane tariffScrollPane=new JScrollPane(tariffList);
        tariffScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

        // прокрутка тарифов на панель тарифов
        tariffPanel.add(tariffScrollPane);
        tariffPanel.add(getsubsButton);
        tariffPanel.add(addsub);
        tariffPanel.add(removetariif);



        // Добавляем элементы на панель подписчиков
        subscriberPanel.add(subscriberScrollPane);
        subscriberPanel.add(replenish_accountButton);
        subscriberPanel.add(removesub);
        subscriberPanel.add(search);


        // Кнопка загрузки компании добавляется на панель компании
        companyPanel.add(loadButton);
        companyPanel.add(addcompany);
        companyPanel.add(addtariff);
        companyPanel.add(removecompany);


        // Добавление панелей
        add(companyPanel, BorderLayout.WEST);
        add(subscriberPanel, BorderLayout.EAST);
        add(tariffPanel, BorderLayout.CENTER);

    }

    // загружать компании
    public void loadCompanies() {
        List<Company> companies = database.getCompanies();

        if (!companies.isEmpty()) {
            companyListModel.clear();

            for (Company company : companies) {
                companyListModel.addElement(company);
            }
        } else {
            JOptionPane.showMessageDialog(null, "No subscribers found for the selected tariff.",
                    "Subscriber Info", JOptionPane.INFORMATION_MESSAGE);
        }
    }



    // загружать тариффы
    public void loadTariffs() {
        Company selectedCompany = companyList.getSelectedValue();
        if (selectedCompany != null) {
            int companyId = selectedCompany.getCompanyId();
            List<Tariff> tariffs = database.getTariffsByCompanyId(companyId);
            tariffListModel.clear();

            if (!tariffs.isEmpty()) {
                for (Tariff tariff : tariffs) {
                    tariffListModel.addElement(tariff);
                }
            } else {
                JOptionPane.showMessageDialog(null, "No tariffs found for the selected company.",
                        "Subscriber Info", JOptionPane.INFORMATION_MESSAGE);            }
        } else {
            JOptionPane.showMessageDialog(null, "Please select company.",
                    "Error", JOptionPane.INFORMATION_MESSAGE);        }
    }

    // загружать сабов
    public void loadSubscribers() {
        Tariff selectedTariff = tariffList.getSelectedValue();
        Company selectedCompany = companyList.getSelectedValue();
        Subscriber selectedSubscriber=subscriberList.getSelectedValue();

        if (selectedTariff != null && selectedCompany != null ||selectedSubscriber!=null ) {
            int tariffId = selectedTariff.getTariffId();
            int companyId = selectedCompany.getCompanyId();

            System.out.println("Selected Tariff ID: " + tariffId);
            System.out.println("Selected Company ID: " + companyId);

            List<Subscriber> subscribers = database.getSubscribersByTariffId(tariffId);

            subscriberListModel.clear();

            if (!subscribers.isEmpty()) {
                for (Subscriber subscriber : subscribers) {
                    subscriberListModel.addElement(subscriber);
                }
            } else {
                JOptionPane.showMessageDialog(null, "No subscribers for this tariff and company.", "Error", JOptionPane.INFORMATION_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(null, "Please select a company and a tariff.", "Error", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    // пополнять баланс
    public void replenish(double balance) {
        Subscriber subscriber = subscriberList.getSelectedValue();
        if (subscriber != null) {
            double newBalance = subscriber.getBalance() + balance;
            subscriber.setBalance(newBalance);

            database.updateSubscriberBalance(subscriber);

            int index = subscriberList.getSelectedIndex();
            if (index >= 0) {
                subscriberListModel.set(index, subscriber);
            }

            JOptionPane.showMessageDialog(null,"Balance replenished: " + subscriber.getNamesub() +
                    " by sum " + balance +
                    ". New balance: " + newBalance);
        } else {
            JOptionPane.showMessageDialog(null,"Select subscriber.\n");
        }
    }

    public static void main(String[] args) {
        MainFrame frame = new MainFrame();
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    }
}
