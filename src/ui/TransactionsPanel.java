package ui;

import model.Transaction;
import model.Account;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class TransactionsPanel extends JPanel {

    private ArrayList<Transaction> transactions;
    private DefaultTableModel tableModel;
    private JTable transactionTable;
    private AccountsPanel accountsPanel; // Add this field


    // Colors
    private static final Color BACKGROUND = new Color(248, 249, 250);
    private static final Color CARD_BG = Color.WHITE;
    private static final Color PRIMARY = new Color(41, 128, 185);
    private static final Color SUCCESS = new Color(34, 197, 94);
    private static final Color DANGER = new Color(239, 68, 68);
    private static final Color TEXT_PRIMARY = new Color(31, 41, 55);
    private static final Color TEXT_SECONDARY = new Color(107, 114, 128);
    private static final Color BORDER = new Color(229, 231, 235);
    private static final Color HOVER = new Color(243, 244, 246);

    public TransactionsPanel(ArrayList<Transaction> transactions, AccountsPanel accountsPanel) {
    this.transactions = transactions;
    this.accountsPanel = accountsPanel; // Store reference

    // Always initialize table model and table to prevent null issues
    tableModel = new DefaultTableModel(
            new String[]{"Date", "Account", "Category", "Notes", "Amount"}, 0
    ) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    transactionTable = new JTable(tableModel);
    transactionTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
    transactionTable.setRowHeight(50);
    transactionTable.setShowGrid(false);
    transactionTable.setIntercellSpacing(new Dimension(0, 8));
    transactionTable.setBackground(BACKGROUND);
    transactionTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

    setLayout(new BorderLayout());
    setBackground(BACKGROUND);
    buildUI();
}

    private void buildUI() {
        removeAll();

        if (transactions.isEmpty()) {
            add(createEmptyState(), BorderLayout.CENTER);
        } else {
            add(createTableView(), BorderLayout.CENTER);
        }

        // Add button panel at bottom (only if there are transactions)
        if (!transactions.isEmpty()) {
            add(createButtonPanel(), BorderLayout.SOUTH);
        }

        revalidate();
        repaint();
    }

    private JPanel createEmptyState() {
        JPanel emptyPanel = new JPanel();
        emptyPanel.setLayout(new BoxLayout(emptyPanel, BoxLayout.Y_AXIS));
        emptyPanel.setBackground(BACKGROUND);

        emptyPanel.add(Box.createVerticalGlue());

        JLabel iconLabel = new JLabel("📊");
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 80));
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        emptyPanel.add(iconLabel);

        emptyPanel.add(Box.createVerticalStrut(20));

        JLabel messageLabel = new JLabel("No Transactions Available");
        messageLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        messageLabel.setForeground(TEXT_PRIMARY);
        messageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        emptyPanel.add(messageLabel);

        emptyPanel.add(Box.createVerticalStrut(10));

        JLabel subLabel = new JLabel("Start tracking your expenses by adding transactions");
        subLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subLabel.setForeground(TEXT_SECONDARY);
        subLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        emptyPanel.add(subLabel);

        emptyPanel.add(Box.createVerticalGlue());

        return emptyPanel;
    }

    private JPanel createTableView() {
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(BACKGROUND);
        tablePanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        JScrollPane scrollPane = new JScrollPane(transactionTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(BACKGROUND);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        // ---- HEADER STYLE ----
        JTableHeader header = transactionTable.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(PRIMARY);
        header.setForeground(Color.WHITE);
        header.setBorder(BorderFactory.createEmptyBorder());
        header.setReorderingAllowed(false);

        // ---- ROW RENDERER ----
        transactionTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                        boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                // Alternating row colors
                if (!isSelected) {
                    setBackground((row % 2 == 0) ? CARD_BG : new Color(235, 237, 240));
                    setForeground(TEXT_PRIMARY);
                } else {
                    setBackground(new Color(100, 149, 237)); // Cornflower blue
                    setForeground(Color.WHITE);
                }

                setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(isSelected ? PRIMARY.darker() : BORDER, isSelected ? 2 : 1, true),
                    BorderFactory.createEmptyBorder(8, 15, 8, 15)
                ));

                // Specific formatting
                if (column == 0) { // Date
                    setFont(new Font("Segoe UI", Font.BOLD, 12));
                    if (!isSelected) setForeground(TEXT_SECONDARY);
                    setHorizontalAlignment(SwingConstants.LEFT);
                } else if (column == 4) { // Amount
                    setFont(new Font("Segoe UI", Font.BOLD, 16));
                    setHorizontalAlignment(SwingConstants.RIGHT);
                    if (!isSelected) {
                        String amount = value.toString();
                        if (amount.startsWith("+")) setForeground(SUCCESS);
                        else if (amount.startsWith("-")) setForeground(DANGER);
                    }
                } else if (column == 2) { // Category
                    setFont(new Font("Segoe UI", Font.BOLD, 14));
                    setHorizontalAlignment(SwingConstants.LEFT);
                } else if (column == 3) { // Notes
                    setFont(new Font("Segoe UI", Font.ITALIC, 12));
                    if (!isSelected) setForeground(TEXT_SECONDARY);
                    setHorizontalAlignment(SwingConstants.LEFT);
                } else {
                    setFont(new Font("Segoe UI", Font.PLAIN, 14));
                    setHorizontalAlignment(SwingConstants.LEFT);
                }

                return this;
            }
        });

        // Clean deselection logic (same as AccountsPanel)
        transactionTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = transactionTable.rowAtPoint(e.getPoint());
                if (row != -1) {
                    if (transactionTable.isRowSelected(row)) {
                        // Deselect if clicking the already-selected row
                        transactionTable.clearSelection();
                    } else {
                        transactionTable.setRowSelectionInterval(row, row);
                    }
                }
            }
        });

        // Column widths
        transactionTable.getColumnModel().getColumn(0).setPreferredWidth(120);
        transactionTable.getColumnModel().getColumn(1).setPreferredWidth(150);
        transactionTable.getColumnModel().getColumn(2).setPreferredWidth(150);
        transactionTable.getColumnModel().getColumn(3).setPreferredWidth(200);
        transactionTable.getColumnModel().getColumn(4).setPreferredWidth(130);

        tablePanel.add(scrollPane, BorderLayout.CENTER);
        tablePanel.add(createSummaryPanel(), BorderLayout.SOUTH);

        refreshTableData();

        return tablePanel;
    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));

        JButton deleteBtn = createModernButton("Delete Selected", DANGER, true);
        deleteBtn.addActionListener(e -> deleteSelectedTransaction());
        buttonPanel.add(deleteBtn);

        return buttonPanel;
    }

    private JButton createModernButton(String text, Color color, boolean outlined) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setFocusPainted(false);
        btn.setBorderPainted(outlined);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        if (outlined) {
            btn.setBackground(Color.WHITE);
            btn.setForeground(color);
            btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(color, 2, true),
                BorderFactory.createEmptyBorder(8, 20, 8, 20)
            ));
        } else {
            btn.setBackground(color);
            btn.setForeground(Color.WHITE);
            btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(color, 1, true),
                BorderFactory.createEmptyBorder(8, 20, 8, 20)
            ));
        }
        
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (outlined) {
                    btn.setBackground(new Color(249, 250, 251));
                } else {
                    btn.setBackground(color.darker());
                }
            }
            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBackground(outlined ? Color.WHITE : color);
            }
        });
        
        return btn;
    }

private void deleteSelectedTransaction() {
        int selectedRow = transactionTable.getSelectedRow();
        if (selectedRow >= 0) {
            // Get the actual transaction (considering sorting)
            ArrayList<Transaction> sortedTransactions = new ArrayList<>(transactions);
            sortedTransactions.sort((t1, t2) -> t2.getDate().compareTo(t1.getDate()));
            Transaction transaction = sortedTransactions.get(selectedRow);
            
            int result = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this transaction?\n\n" +
                "Date: " + transaction.getDate() + "\n" +
                "Category: " + transaction.getCategory() + "\n" +
                "Amount: ₱" + String.format("%,.2f", transaction.getAmount()),
                "Confirm Deletion",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
            
            if (result == JOptionPane.YES_OPTION) {
                // Undo the transaction from the account balance
                Account account = transaction.getAccount();
                double amount = transaction.getAmount();
                String type = transaction.getType();
                
                if (type.equals("Income")) {
                    // Undo income: subtract from account
                    account.setBalance(account.getBalance() - amount);
                } else {
                    // Undo expense: add back to account
                    account.setBalance(account.getBalance() + amount);
                }
                
                // Remove the transaction
                transactions.remove(transaction);
                
                // Update displays
                updateDisplay();
                
                // Refresh the accounts panel to show updated balance
                if (accountsPanel != null) {
                    accountsPanel.refreshTable();
                }
                
                JOptionPane.showMessageDialog(this,
                    "Transaction deleted successfully!\n" +
                    "Account balance updated: ₱" + String.format("%,.2f", account.getBalance()),
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, 
                "Please select a transaction to delete!", 
                "No Selection", 
                JOptionPane.WARNING_MESSAGE);
        }
    }
    
    private JPanel createSummaryPanel() {
        JPanel summaryPanel = new JPanel(new GridLayout(1, 3, 20, 0));
        summaryPanel.setBackground(Color.WHITE);
        summaryPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER),
                BorderFactory.createEmptyBorder(20, 30, 20, 30)
        ));

        double totalIncome = 0, totalExpense = 0;
        for (Transaction t : transactions) {
            double amount = t.getAmount();
            if (t.getType().equals("Income")) totalIncome += amount;
            else totalExpense += amount;
        }
        double balance = totalIncome - totalExpense;

        summaryPanel.add(createSummaryCard("Total Income", totalIncome, SUCCESS));
        summaryPanel.add(createSummaryCard("Total Expenses", totalExpense, DANGER));
        summaryPanel.add(createSummaryCard("Net Balance", balance, balance >= 0 ? SUCCESS : DANGER));

        return summaryPanel;
    }

    private JPanel createSummaryCard(String label, double amount, Color color) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(new Color(color.getRed(), color.getGreen(), color.getBlue(), 20));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(color, 1, true),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        JLabel titleLabel = new JLabel(label);
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        titleLabel.setForeground(TEXT_SECONDARY);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel amountLabel = new JLabel(String.format("₱%,.2f", Math.abs(amount)));
        amountLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        amountLabel.setForeground(color);
        amountLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(titleLabel);
        card.add(Box.createVerticalStrut(5));
        card.add(amountLabel);

        return card;
    }

    // Refresh only table rows
    public void refreshTableData() {
        if (tableModel == null) return;

        tableModel.setRowCount(0);

        ArrayList<Transaction> sortedTransactions = new ArrayList<>(transactions);
        sortedTransactions.sort((t1, t2) -> t2.getDate().compareTo(t1.getDate()));

        for (Transaction t : sortedTransactions) {
            String notes = t.getNotes() == null || t.getNotes().isEmpty() ? "-" : t.getNotes();
            String type = t.getType();
            String amountPrefix = type.equals("Income") ? "+ " : "- ";

            tableModel.addRow(new Object[]{
                    t.getDate(),
                    t.getAccount().getName(),
                    t.getCategory(),
                    notes,
                    amountPrefix + String.format("₱%,.2f", t.getAmount())
            });
        }
    }

    // Safe refresh for external calls
    public void refreshTable() {
        if (tableModel != null) {
            refreshTableData();
            tableModel.fireTableDataChanged();
        }
    }

    // Call this when transactions are added/removed
    public void updateDisplay() {
        buildUI();
    }
}