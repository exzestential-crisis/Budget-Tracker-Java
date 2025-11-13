package ui;

import model.Account;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;

public class AccountsPanel extends JPanel {

    private ArrayList<Account> accounts;
    private DefaultTableModel tableModel;
    private JTable accountsTable;

    public AccountsPanel(ArrayList<Account> accounts) {
        this.accounts = accounts;
        setLayout(new BorderLayout());

        // Title
        JLabel title = new JLabel("Accounts", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(title, BorderLayout.NORTH);

        // Table setup
        String[] columns = {"Name", "Balance"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        accountsTable = new JTable(tableModel);
        add(new JScrollPane(accountsTable), BorderLayout.CENTER);

        refreshTable();

        // Buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton addBtn = new JButton("Add Account");
        addBtn.addActionListener(e -> showAddDialog());
        btnPanel.add(addBtn);

        JButton deleteBtn = new JButton("Delete Selected");
        deleteBtn.addActionListener(e -> deleteSelectedAccount());
        btnPanel.add(deleteBtn);

        add(btnPanel, BorderLayout.SOUTH);
    }

    private void showAddDialog() {
        JTextField nameField = new JTextField(10);
        JTextField balanceField = new JTextField(10);

        JPanel panel = new JPanel(new GridLayout(0, 2));
        panel.add(new JLabel("Account Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Initial Balance:"));
        panel.add(balanceField);

        int result = JOptionPane.showConfirmDialog(this, panel,
                "Add New Account", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            try {
                String name = nameField.getText().trim();
                double balance = Double.parseDouble(balanceField.getText().trim());

                Account newAcc = new Account(name, balance);
                accounts.add(newAcc);
                refreshTable();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid balance amount!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void deleteSelectedAccount() {
        int selectedRow = accountsTable.getSelectedRow();
        if (selectedRow >= 0) {
            accounts.remove(selectedRow);
            refreshTable();
        } else {
            JOptionPane.showMessageDialog(this, "Please select an account to delete!", "Warning", JOptionPane.WARNING_MESSAGE);
        }
    }

    public void refreshTable() {
        tableModel.setRowCount(0);
        for (Account acc : accounts) {
            tableModel.addRow(new Object[]{acc.getName(), String.format("₱%.2f", acc.getBalance())});
        }
    }
}
