package ui;

import model.Transaction;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;

public class TransactionsPanel extends JPanel {

    private ArrayList<Transaction> transactions;
    private DefaultTableModel tableModel;
    private JTable transactionTable;

    public TransactionsPanel(ArrayList<Transaction> transactions) {
        this.transactions = transactions;
        setLayout(new BorderLayout());

        JLabel title = new JLabel("Transaction History", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        add(title, BorderLayout.NORTH);

        String[] columns = {"Date", "Account", "Type", "Category", "Amount"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        transactionTable = new JTable(tableModel);
        add(new JScrollPane(transactionTable), BorderLayout.CENTER);

        refreshTable();
    }

    public void refreshTable() {
        tableModel.setRowCount(0);
        for (Transaction t : transactions) {
            tableModel.addRow(new Object[]{
                    t.getDate(),
                    t.getAccount().getName(),
                    t.getType(),
                    t.getCategory(),
                    String.format("₱%.2f", t.getAmount())
            });
        }
    }
}
