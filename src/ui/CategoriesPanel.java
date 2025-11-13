package ui;

import model.Category;
import model.Transaction;
import model.Account;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Date;

public class CategoriesPanel extends JPanel {

    private ArrayList<Category> categories;
    private ArrayList<Transaction> transactions;
    private ArrayList<Account> accounts;
    private TransactionsPanel transactionsPanel;
    private JPanel grid;

    public CategoriesPanel(ArrayList<Category> categories, ArrayList<Transaction> transactions,
                           ArrayList<Account> accounts, TransactionsPanel transactionsPanel) {
        this.categories = categories;
        this.transactions = transactions;
        this.accounts = accounts;
        this.transactionsPanel = transactionsPanel;

        setLayout(new BorderLayout());

        // Title
        JLabel title = new JLabel("Categories", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(title, BorderLayout.NORTH);

        // Add/Delete buttons
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton addCategoryBtn = new JButton("Add Category");
        addCategoryBtn.addActionListener(e -> addCategory());
        topPanel.add(addCategoryBtn);
        add(topPanel, BorderLayout.SOUTH);

        // Grid
        grid = new JPanel(new GridLayout(0, 3, 10, 10));
        grid.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(new JScrollPane(grid), BorderLayout.CENTER);

        refreshGrid();
    }

    private void refreshGrid() {
        grid.removeAll();

        for (Category cat : categories) {
            JPanel catPanel = new JPanel(new BorderLayout());
            catPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

            // Icon
            JLabel iconLabel = new JLabel();
            iconLabel.setHorizontalAlignment(SwingConstants.CENTER);
            try {
                ImageIcon icon = new ImageIcon(cat.getIconPath());
                Image img = icon.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH);
                iconLabel.setIcon(new ImageIcon(img));
            } catch (Exception e) {
                iconLabel.setText("[Icon]");
            }

            // Name
            JLabel nameLabel = new JLabel(cat.getName(), SwingConstants.CENTER);
            nameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));

            // Add amount button
            JButton addBtn = new JButton("Add Amount");
            addBtn.addActionListener(e -> addTransaction(cat));

            // Delete category button
            JButton deleteBtn = new JButton("Delete");
            deleteBtn.addActionListener(e -> {
                categories.remove(cat);
                refreshGrid();
            });

            catPanel.add(iconLabel, BorderLayout.CENTER);
            catPanel.add(nameLabel, BorderLayout.NORTH);
            JPanel btnPanel = new JPanel(new GridLayout(1, 2));
            btnPanel.add(addBtn);
            btnPanel.add(deleteBtn);
            catPanel.add(btnPanel, BorderLayout.SOUTH);

            grid.add(catPanel);
        }

        grid.revalidate();
        grid.repaint();
    }

    private void addTransaction(Category cat) {
        if (accounts.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please add an account first.");
            return;
        }

        // Let user choose account
        Account selectedAccount = (Account) JOptionPane.showInputDialog(
                this,
                "Select account:",
                "Choose Account",
                JOptionPane.PLAIN_MESSAGE,
                null,
                accounts.toArray(),
                accounts.get(0)
        );

        if (selectedAccount == null) return;

        // Ask for amount
        String amountStr = JOptionPane.showInputDialog(this, "Enter amount for " + cat.getName());
        if (amountStr == null || amountStr.isEmpty()) return;

        double amount;
        try {
            amount = Double.parseDouble(amountStr);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid amount!");
            return;
        }

        // Check if account has enough balance
        if (amount > selectedAccount.getBalance()) {
            JOptionPane.showMessageDialog(this, "Insufficient balance in " + selectedAccount.getName());
            return;
        }

        // Create transaction
        Transaction t = new Transaction(selectedAccount, cat.getName(), "Expense", amount, new Date());
        transactions.add(t);

        // Deduct balance
        selectedAccount.setBalance(selectedAccount.getBalance() - amount);

        // Refresh transactions table
        transactionsPanel.refreshTable();

        JOptionPane.showMessageDialog(this, "Transaction added successfully!");
    }

    private void addCategory() {
        String name = JOptionPane.showInputDialog(this, "Enter category name:");
        if (name == null || name.trim().isEmpty()) return;

        String[] predefinedIcons = {
                "icons/category icons/food.png",
                "icons/category icons/transport.png",
                "icons/category icons/entertainment.png",
                "icons/category icons/bills.png",
                "icons/category icons/health.png",
                "icons/category icons/education.png",
                "icons/category icons/house.png",
                "icons/category icons/laundry-machine.png",
                "icons/category icons/pets.png",
                "icons/category icons/salary (1).png",
                "icons/category icons/savings.png",
                "icons/category icons/shopping.png"
        };

        String icon = (String) JOptionPane.showInputDialog(
                this,
                "Select icon:",
                "Icon Selection",
                JOptionPane.PLAIN_MESSAGE,
                null,
                predefinedIcons,
                predefinedIcons[0]
        );

        if (icon != null && !icon.isEmpty()) {
            categories.add(new Category(name, icon));
            refreshGrid();
        }
    }
}
