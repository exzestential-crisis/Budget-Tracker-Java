package ui;

import model.Transaction;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.ArrayList;

public class TransactionsPanel extends JPanel {

    private ArrayList<Transaction> transactions;
    private DefaultTableModel tableModel;
    private JTable transactionTable;

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

    public TransactionsPanel(ArrayList<Transaction> transactions) {
        this.transactions = transactions;

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
        transactionTable.setSelectionBackground(new Color(224, 231, 255));
        transactionTable.setSelectionForeground(TEXT_PRIMARY);

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
        scrollPane.setBorder(BorderFactory.createLineBorder(BORDER, 1, true));
        scrollPane.getViewport().setBackground(BACKGROUND);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        // Custom renderer
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) c.setBackground(CARD_BG);
                setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 15));

                if (column == 0) { setFont(new Font("Segoe UI", Font.BOLD, 12)); setForeground(TEXT_SECONDARY); }
                else if (column == 4) {
                    setFont(new Font("Segoe UI", Font.BOLD, 16)); setHorizontalAlignment(SwingConstants.RIGHT);
                    String amount = value.toString();
                    if (amount.startsWith("+")) setForeground(SUCCESS);
                    else if (amount.startsWith("-")) setForeground(DANGER);
                }
                else if (column == 2) { setFont(new Font("Segoe UI", Font.BOLD, 14)); }
                else if (column == 3) { setFont(new Font("Segoe UI", Font.ITALIC, 12)); setForeground(TEXT_SECONDARY); }
                else { setFont(new Font("Segoe UI", Font.PLAIN, 14)); }

                return c;
            }
        };

        for (int i = 0; i < transactionTable.getColumnCount(); i++)
            transactionTable.getColumnModel().getColumn(i).setCellRenderer(renderer);

        transactionTable.getColumnModel().getColumn(0).setPreferredWidth(120);
        transactionTable.getColumnModel().getColumn(1).setPreferredWidth(150);
        transactionTable.getColumnModel().getColumn(2).setPreferredWidth(150);
        transactionTable.getColumnModel().getColumn(3).setPreferredWidth(200);
        transactionTable.getColumnModel().getColumn(4).setPreferredWidth(130);

        tablePanel.add(scrollPane, BorderLayout.CENTER);
        tablePanel.add(createSummaryPanel(), BorderLayout.SOUTH);

        refreshTableData(); // populate table rows

        return tablePanel;
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
