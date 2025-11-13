package ui;

import model.Account;
import model.Category;
import model.Transaction;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class MainFrame extends JFrame {

    private ArrayList<Category> categoriesList;
    private ArrayList<Account> accountsList;
    private ArrayList<Transaction> transactionsList;

    public MainFrame() {
        categoriesList = new ArrayList<>();
        accountsList = new ArrayList<>();
        transactionsList = new ArrayList<>();

        // Add predefined categories
        categoriesList.add(new Category("Food", "icons/category icons/food.png"));
        categoriesList.add(new Category("Transport", "icons/category icons/transport.png"));
        categoriesList.add(new Category("Entertainment", "icons/category icons/entertainment.png"));
        categoriesList.add(new Category("Bills", "icons/category icons/bills.png"));
        categoriesList.add(new Category("Health", "icons/category icons/health.png"));
        categoriesList.add(new Category("Education", "icons/category icons/education.png"));
        categoriesList.add(new Category("House", "icons/category icons/house.png"));
        categoriesList.add(new Category("Laundry", "icons/category icons/laundry-machine.png"));
        categoriesList.add(new Category("Pets", "icons/category icons/pets.png"));
        categoriesList.add(new Category("Salary", "icons/category icons/salary (1).png"));
        categoriesList.add(new Category("Savings", "icons/category icons/savings.png"));
        categoriesList.add(new Category("Shopping", "icons/category icons/shopping.png"));

        setTitle("ADV Expense Tracker");
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JLabel header = new JLabel("ADV Expense Tracker", SwingConstants.CENTER);
        header.setFont(new Font("Segoe UI", Font.BOLD, 26));
        header.setOpaque(true);
        header.setBackground(new Color(41,128,185));
        header.setForeground(Color.WHITE);
        header.setBorder(BorderFactory.createEmptyBorder(15,0,15,0));
        add(header, BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();

        TransactionsPanel transactionsPanel = new TransactionsPanel(transactionsList);

        // Tabs order: Categories → Accounts → Transactions
        CategoriesPanel categoriesPanel = new CategoriesPanel(categoriesList, transactionsList, accountsList, transactionsPanel);
        tabs.addTab("Categories", categoriesPanel);

        AccountsPanel accountsPanel = new AccountsPanel(accountsList);
        tabs.addTab("Accounts", accountsPanel);

        tabs.addTab("Transactions", transactionsPanel);

        add(tabs, BorderLayout.CENTER);
        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainFrame());
    }
}
