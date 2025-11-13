package ui;

import model.Account;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.DefaultTableCellRenderer;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.ArrayList;

public class AccountsPanel extends JPanel {

    private ArrayList<Account> accounts;
    private DefaultTableModel tableModel;
    private JTable accountsTable;
    private JPanel contentPanel;
    private String selectedAccountType;

    // Modern color palette (matching CategoriesPanel)
    private static final Color BACKGROUND = new Color(248, 249, 250);
    private static final Color CARD_BG = Color.WHITE;
    private static final Color PRIMARY = new Color(41, 128, 185); // Blue
    private static final Color SUCCESS = new Color(34, 197, 94); // Green
    private static final Color DANGER = new Color(239, 68, 68); // Red
    private static final Color WARNING = new Color(251, 146, 60); // Orange
    private static final Color TEXT_PRIMARY = new Color(31, 41, 55);
    private static final Color TEXT_SECONDARY = new Color(107, 114, 128);
    private static final Color BORDER = new Color(229, 231, 235);
    private static final Color HOVER = new Color(243, 244, 246);

    public AccountsPanel(ArrayList<Account> accounts) {
        this.accounts = accounts;
        setLayout(new BorderLayout());
        setBackground(BACKGROUND);
        
        updateDisplay();
    }

    private void updateDisplay() {
        removeAll();
        
        if (accounts.isEmpty()) {
            // Show empty state
            add(createEmptyState(), BorderLayout.CENTER);
        } else {
            // Show table with accounts
            add(createTableView(), BorderLayout.CENTER);
        }
        
        // Add button panel at bottom
        add(createButtonPanel(), BorderLayout.SOUTH);
        
        revalidate();
        repaint();
    }

    private JPanel createEmptyState() {
        JPanel emptyPanel = new JPanel();
        emptyPanel.setLayout(new BoxLayout(emptyPanel, BoxLayout.Y_AXIS));
        emptyPanel.setBackground(BACKGROUND);
        
        // Add vertical glue to center content
        emptyPanel.add(Box.createVerticalGlue());
        
        // Icon
        JLabel iconLabel = new JLabel("💳");
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 80));
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        emptyPanel.add(iconLabel);
        
        emptyPanel.add(Box.createVerticalStrut(20));
        
        // Message
        JLabel messageLabel = new JLabel("No Accounts Available");
        messageLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        messageLabel.setForeground(TEXT_PRIMARY);
        messageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        emptyPanel.add(messageLabel);
        
        emptyPanel.add(Box.createVerticalStrut(10));
        
        JLabel subLabel = new JLabel("Add your first account to start tracking expenses");
        subLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subLabel.setForeground(TEXT_SECONDARY);
        subLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        emptyPanel.add(subLabel);
        
        emptyPanel.add(Box.createVerticalStrut(30));
        
        // Add account button
        JButton addBtn = createModernButton("+ Add Account", PRIMARY, false);
        addBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        addBtn.addActionListener(e -> showAccountTypeModal());
        emptyPanel.add(addBtn);
        
        emptyPanel.add(Box.createVerticalGlue());
        
        return emptyPanel;
    }

private JPanel createTableView() {
    JPanel tablePanel = new JPanel(new BorderLayout());
    tablePanel.setBackground(BACKGROUND);
    tablePanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

    String[] columns = {"Type", "Name", "Balance"};
    tableModel = new DefaultTableModel(columns, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    accountsTable = new JTable(tableModel);
    accountsTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
    accountsTable.setRowHeight(60);
    accountsTable.setShowGrid(false);
    accountsTable.setIntercellSpacing(new Dimension(0, 0));
    accountsTable.setFillsViewportHeight(true);
    accountsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

    // Modern header style
    JTableHeader header = accountsTable.getTableHeader();
    header.setFont(new Font("Segoe UI", Font.BOLD, 14));
    header.setBackground(PRIMARY);
    header.setForeground(Color.WHITE);
    header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, PRIMARY));
    header.setReorderingAllowed(false);

    // Simplified custom renderer
    accountsTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
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
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
            ));
            
            // Column-specific styling
            if (column == 0) { // Type badge
                String type = value.toString();
                Color typeColor = type.equals("Regular") ? PRIMARY :
                                  type.equals("Savings") ? SUCCESS : DANGER;
                setForeground(isSelected ? Color.WHITE : typeColor);
                setFont(new Font("Segoe UI", Font.BOLD, 13));
                setHorizontalAlignment(SwingConstants.LEFT);
            } else if (column == 2) { // Balance
                setHorizontalAlignment(SwingConstants.RIGHT);
                setFont(new Font("Segoe UI", Font.BOLD, 14));
            } else {
                setFont(new Font("Segoe UI", Font.PLAIN, 14));
                setHorizontalAlignment(SwingConstants.LEFT);
            }
            
            return this;
        }
    });

    // Clean deselection logic
    accountsTable.addMouseListener(new MouseAdapter() {
        @Override
        public void mouseClicked(MouseEvent e) {
            int row = accountsTable.rowAtPoint(e.getPoint());
            if (row != -1) {
                if (accountsTable.isRowSelected(row)) {
                    // Deselect if clicking the already-selected row
                    accountsTable.clearSelection();
                } else {
                    accountsTable.setRowSelectionInterval(row, row);
                }
            }
        }
    });

    // Set column widths
    accountsTable.getColumnModel().getColumn(0).setPreferredWidth(120);
    accountsTable.getColumnModel().getColumn(1).setPreferredWidth(220);
    accountsTable.getColumnModel().getColumn(2).setPreferredWidth(140);

    refreshTable();

    JScrollPane scrollPane = new JScrollPane(accountsTable);
    scrollPane.setBorder(BorderFactory.createEmptyBorder());
    scrollPane.getViewport().setBackground(BACKGROUND);
    scrollPane.getVerticalScrollBar().setUnitIncrement(16);

    tablePanel.add(scrollPane, BorderLayout.CENTER);

    return tablePanel;
}

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));

        if (!accounts.isEmpty()) {
            JButton deleteBtn = createModernButton("Delete Selected", DANGER, true);
            deleteBtn.addActionListener(e -> deleteSelectedAccount());
            buttonPanel.add(deleteBtn);
        }

        JButton addBtn = createModernButton("+ Add Account", PRIMARY, false);
        addBtn.addActionListener(e -> showAccountTypeModal());
        buttonPanel.add(addBtn);

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

    private void showAccountTypeModal() {
        JDialog modal = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), 
            "Select Account Type", true);
        modal.setSize(520, 500); // increased size to fit icons better
        modal.setLocationRelativeTo(this);
        modal.setLayout(new BorderLayout());
        modal.setBackground(Color.WHITE);

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        // Title
        JLabel titleLabel = new JLabel("Choose Account Type", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(TEXT_PRIMARY);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT); // center horizontally
        contentPanel.add(titleLabel);
        contentPanel.add(Box.createVerticalStrut(10));
        
        JLabel subtitleLabel = new JLabel("Select the type that best describes your account", SwingConstants.CENTER);
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitleLabel.setForeground(TEXT_SECONDARY);
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT); // center horizontally
        contentPanel.add(subtitleLabel);
        contentPanel.add(Box.createVerticalStrut(30));

        // Account type cards
        JPanel regularCard = createAccountTypeCard(
            "Regular", 
            "Cash, Wallet, Checking Account...",
            "icons/wallet.png",
            PRIMARY
        );
        regularCard.setMaximumSize(new Dimension(450, 100)); // ensure card doesn't overflow
        regularCard.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                selectAccountType("Regular", modal);
            }
        });
        contentPanel.add(regularCard);
        contentPanel.add(Box.createVerticalStrut(15));

        JPanel savingsCard = createAccountTypeCard(
            "Savings",
            "Savings Account, Investment, Goals...",
            "icons/money.png",
            SUCCESS
        );
        savingsCard.setMaximumSize(new Dimension(450, 100));
        savingsCard.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                selectAccountType("Savings", modal);
            }
        });
        contentPanel.add(savingsCard);
        contentPanel.add(Box.createVerticalStrut(15));

        JPanel debtCard = createAccountTypeCard(
            "Debt",
            "Credit Card, Loan, Mortgage...",
            "icons/salary.png",
            DANGER
        );
        debtCard.setMaximumSize(new Dimension(450, 100));
        debtCard.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                selectAccountType("Debt", modal);
            }
        });
        contentPanel.add(debtCard);

        modal.add(contentPanel, BorderLayout.CENTER);
        modal.setVisible(true);
    }


    private JPanel createAccountTypeCard(String title, String description, 
                                         String iconPath, Color accentColor) {
        JPanel card = new JPanel(new BorderLayout(15, 0));
        card.setBackground(CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER, 1, true),
            BorderFactory.createEmptyBorder(19, 19, 19, 19)
        ));
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        card.setMaximumSize(new Dimension(400, 90));

        // Icon
        JLabel iconLabel = new JLabel();
        iconLabel.setHorizontalAlignment(SwingConstants.CENTER);
        iconLabel.setVerticalAlignment(SwingConstants.CENTER);
        try {
            ImageIcon icon = new ImageIcon(loadScaledImage(iconPath, 40, 40));
            iconLabel.setIcon(icon);
        } catch (Exception e) {
            iconLabel.setText("💳");
            iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 32));
        }
        
        JPanel iconPanel = new JPanel(new GridBagLayout());
        iconPanel.setBackground(new Color(accentColor.getRed(), 
                                          accentColor.getGreen(), 
                                          accentColor.getBlue(), 30));
        iconPanel.setPreferredSize(new Dimension(60, 60));
        iconPanel.setBorder(BorderFactory.createLineBorder(accentColor, 1, true));
        iconPanel.add(iconLabel);
        
        card.add(iconPanel, BorderLayout.WEST);

        // Text content
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setBackground(CARD_BG);
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setForeground(TEXT_PRIMARY);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel descLabel = new JLabel(description);
        descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        descLabel.setForeground(TEXT_SECONDARY);
        descLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        textPanel.add(titleLabel);
        textPanel.add(Box.createVerticalStrut(5));
        textPanel.add(descLabel);
        
        card.add(textPanel, BorderLayout.CENTER);

        // Hover effect
        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                card.setBackground(HOVER);
                textPanel.setBackground(HOVER);
                iconPanel.setBackground(new Color(accentColor.getRed(), 
                                                  accentColor.getGreen(), 
                                                  accentColor.getBlue(), 50));
                card.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(accentColor, 2, true),
                    BorderFactory.createEmptyBorder(18, 18, 18, 18)
                ));
            }
            @Override
            public void mouseExited(MouseEvent e) {
                card.setBackground(CARD_BG);
                textPanel.setBackground(CARD_BG);
                iconPanel.setBackground(new Color(accentColor.getRed(), 
                                                  accentColor.getGreen(), 
                                                  accentColor.getBlue(), 30));
                card.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(BORDER, 1, true),
                    BorderFactory.createEmptyBorder(19, 19, 19, 19)
                ));
            }
        });

        return card;
    }

    private void selectAccountType(String type, JDialog modal) {
        selectedAccountType = type;
        modal.dispose();
        showAddAccountModal();
    }

    private void showAddAccountModal() {
        JDialog modal = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), 
            "Add Account", true);
        modal.setSize(420, 500);
        modal.setResizable(false);
        modal.setLayout(new BorderLayout());
        modal.setBackground(Color.WHITE);

        // Main content panel (vertical stacking)
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(25, 30, 25, 30));

        // Title
        JLabel titleLabel = new JLabel("Create " + selectedAccountType + " Account");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(TEXT_PRIMARY);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(titleLabel);
        contentPanel.add(Box.createVerticalStrut(8));

        JLabel subtitleLabel = new JLabel("Enter account details below");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitleLabel.setForeground(TEXT_SECONDARY);
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(subtitleLabel);
        contentPanel.add(Box.createVerticalStrut(25));

        // Account type badge
        JPanel typeBadge = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        Color badgeColor = selectedAccountType.equals("Regular") ? PRIMARY :
                        selectedAccountType.equals("Savings") ? SUCCESS : DANGER;
        typeBadge.setBackground(new Color(badgeColor.getRed(), 
                                        badgeColor.getGreen(), 
                                        badgeColor.getBlue(), 30));
        typeBadge.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(badgeColor, 1, true),
            BorderFactory.createEmptyBorder(0, 10, 5, 10)
        ));
        typeBadge.setMaximumSize(new Dimension(360, 40));

        JLabel typeLabel = new JLabel(selectedAccountType + " Account");
        typeLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        typeLabel.setForeground(badgeColor);
        typeBadge.add(typeLabel);
        contentPanel.add(typeBadge);
        contentPanel.add(Box.createVerticalStrut(20));

        // Account name input
        JLabel nameLabel = new JLabel("Account Name");
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        nameLabel.setForeground(TEXT_PRIMARY);
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(nameLabel);
        contentPanel.add(Box.createVerticalStrut(8));

        JTextField nameField = new JTextField();
        nameField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        nameField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER, 1, true),
            BorderFactory.createEmptyBorder(10, 12, 10, 12)
        ));
        nameField.setMaximumSize(new Dimension(360, 42));
        contentPanel.add(nameField);
        contentPanel.add(Box.createVerticalStrut(20));

        // Initial balance input
        JLabel balanceLabel = new JLabel("Initial Balance");
        balanceLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        balanceLabel.setForeground(TEXT_PRIMARY);
        balanceLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(balanceLabel);
        contentPanel.add(Box.createVerticalStrut(8));

        JTextField balanceField = new JTextField("0.00");
        balanceField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        balanceField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER, 1, true),
            BorderFactory.createEmptyBorder(10, 12, 10, 12)
        ));
        balanceField.setMaximumSize(new Dimension(360, 42));
        contentPanel.add(balanceField);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 20));
        buttonPanel.setBackground(Color.WHITE);

        JButton cancelBtn = createModernButton("Cancel", TEXT_SECONDARY, true);
        JButton addBtn = createModernButton("Add Account", SUCCESS, false);

        cancelBtn.addActionListener(e -> modal.dispose());
        addBtn.addActionListener(e -> {
            String name = nameField.getText().trim();
            String balanceStr = balanceField.getText().replace("₱", "").replace(",", "").trim();

            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(modal, 
                    "Please enter an account name.", 
                    "Invalid Input", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                double balance = Double.parseDouble(balanceStr);

                Account newAccount = new Account(name, balance);
                newAccount.setType(selectedAccountType);
                accounts.add(newAccount);

                modal.dispose();
                updateDisplay();

                JOptionPane.showMessageDialog(this, 
                    "Account created successfully!\n" +
                    name + ": ₱" + String.format("%,.2f", balance),
                    "Success", 
                    JOptionPane.INFORMATION_MESSAGE);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(modal, 
                    "Please enter a valid number for balance.", 
                    "Invalid Input", 
                    JOptionPane.ERROR_MESSAGE);
            }
        });

        buttonPanel.add(cancelBtn);
        buttonPanel.add(addBtn);

        // --- WRAPPER PANEL TO CENTER CONTENT ---
        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setBackground(Color.WHITE);
        wrapper.add(contentPanel); // contentPanel stays centered

        modal.add(wrapper, BorderLayout.CENTER);
        modal.add(buttonPanel, BorderLayout.SOUTH);

        modal.setLocationRelativeTo(SwingUtilities.getWindowAncestor(this)); // true center
        modal.setVisible(true);
    }


    private void deleteSelectedAccount() {
        int selectedRow = accountsTable.getSelectedRow();
        if (selectedRow >= 0) {
            Account account = accounts.get(selectedRow);
            int result = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete '" + account.getName() + "'?",
                "Confirm Deletion",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
            
            if (result == JOptionPane.YES_OPTION) {
                accounts.remove(selectedRow);
                updateDisplay();
            }
        } else {
            JOptionPane.showMessageDialog(this, 
                "Please select an account to delete!", 
                "No Selection", 
                JOptionPane.WARNING_MESSAGE);
        }
    }

public void refreshTable() {
    System.out.println("=== AccountsPanel.refreshTable() called ===");
    System.out.println("tableModel is null? " + (tableModel == null));
    System.out.println("accountsTable is null? " + (accountsTable == null));
    System.out.println("Number of accounts: " + accounts.size());
    
    if (tableModel != null && accountsTable != null) {
        tableModel.setRowCount(0);
        for (Account acc : accounts) {
            String type = acc.getType() != null ? (String) acc.getType() : "Regular";
            System.out.println("Adding to table: " + acc.getName() + " - " + acc.getBalance());
            tableModel.addRow(new Object[]{
                type,
                acc.getName(), 
                String.format("₱%,.2f", acc.getBalance())
            });
        }
        tableModel.fireTableDataChanged();
        accountsTable.revalidate();
        accountsTable.repaint();
        System.out.println("Table updated with " + tableModel.getRowCount() + " rows");
    } else {
        System.out.println("Calling updateDisplay() instead");
        updateDisplay();
    }
}

public void refresh() {
    System.out.println("=== AccountsPanel.refresh() called ===");
    updateDisplay();
}
    private Image loadScaledImage(String path, int width, int height) throws Exception {
        return ImageIO.read(new File(path)).getScaledInstance(width, height, Image.SCALE_SMOOTH);
    }
}