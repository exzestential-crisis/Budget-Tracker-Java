import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import javax.imageio.ImageIO;
import java.io.File;

public class BudgetTracker extends JFrame {
    private JTabbedPane tabbedPane;
    private JPanel accountsTab, categoriesTab, transactionsTab;
    private DefaultTableModel accountsModel;
    private JTable accountsTable;
    private boolean deletionMode = false;
    private boolean editMode = false;
    private Map<String, List<Category>> categories;
    private List<Transaction> transactions;
    private JDialog modal;
    private JPanel overlay;
    private String selectedAccountType;
    private String selectedIcon = "icons/placeholder.png";
    private JLabel iconLabel;
    private ImageIcon iconPhoto;
    private JButton deleteButton;
    private Map<String, Map<Integer, JCheckBox>> categoryCheckboxes;

    public BudgetTracker() {
        setTitle("Budget Tracker");
        setSize(600, 600);
        setMinimumSize(new Dimension(600, 400));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        categories = new HashMap<>();
        transactions = new ArrayList<>();
        categoryCheckboxes = new HashMap<>();

        initializeDefaultCategories();
        createTabs();
        createAccountsTab();
        createCategoriesTab();
        createTransactionsTab();

        // Add sample account
        accountsModel.addRow(new Object[]{"Regular", "Cash", "₱ 165.69", "Delete"});
    }

    private void initializeDefaultCategories() {
        List<Category> expenses = new ArrayList<>();
        expenses.add(new Category("icons/category icons/food.png", "Food"));
        expenses.add(new Category("icons/category icons/transport.png", "Transport"));
        expenses.add(new Category("icons/category icons/shopping.png", "Shopping"));
        expenses.add(new Category("icons/category icons/entertainment.png", "Entertainment"));
        expenses.add(new Category("icons/category icons/health.png", "Health"));
        expenses.add(new Category("icons/category icons/education.png", "Education"));
        expenses.add(new Category("icons/category icons/bills.png", "Bills"));
        expenses.add(new Category("icons/category icons/savings.png", "Savings"));

        List<Category> income = new ArrayList<>();
        income.add(new Category("icons/category icons/salary (1).png", "Salary"));
        income.add(new Category("icons/category icons/allowance.png", "Allowance"));

        categories.put("expenses", expenses);
        categories.put("income", income);
    }

    private void createTabs() {
        tabbedPane = new JTabbedPane();
        accountsTab = new JPanel(new BorderLayout());
        categoriesTab = new JPanel(new BorderLayout());
        transactionsTab = new JPanel(new BorderLayout());

        tabbedPane.addTab("Accounts", accountsTab);
        tabbedPane.addTab("Categories", categoriesTab);
        tabbedPane.addTab("Transactions", transactionsTab);

        add(tabbedPane);
    }

    // ACCOUNTS TAB
    private void createAccountsTab() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        String[] columns = {"Account Type", "Account Name", "Balance", "Delete"};
        accountsModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 3 && deletionMode;
            }
        };

        accountsTable = new JTable(accountsModel);
        accountsTable.setFont(new Font("Arial", Font.BOLD, 10));
        accountsTable.setRowHeight(25);
        
        // Hide delete column initially
        accountsTable.getColumnModel().getColumn(3).setMinWidth(0);
        accountsTable.getColumnModel().getColumn(3).setMaxWidth(0);
        accountsTable.getColumnModel().getColumn(3).setWidth(0);

        accountsTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (deletionMode) {
                    int col = accountsTable.columnAtPoint(e.getPoint());
                    int row = accountsTable.rowAtPoint(e.getPoint());
                    if (col == 3 && row >= 0) {
                        int result = JOptionPane.showConfirmDialog(
                            BudgetTracker.this,
                            "Are you sure you want to delete this account?",
                            "Confirm Deletion",
                            JOptionPane.YES_NO_OPTION
                        );
                        if (result == JOptionPane.YES_OPTION) {
                            accountsModel.removeRow(row);
                        }
                    }
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(accountsTable);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton deleteAccountsBtn = new JButton("Delete accounts");
        JButton addAccountsBtn = new JButton("Add accounts +");

        deleteAccountsBtn.addActionListener(e -> toggleDeletionMode());
        addAccountsBtn.addActionListener(e -> showAccountTypeModal());

        buttonPanel.add(deleteAccountsBtn);
        buttonPanel.add(addAccountsBtn);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        accountsTab.add(mainPanel);
    }

    private void toggleDeletionMode() {
        deletionMode = !deletionMode;
        TableColumn deleteCol = accountsTable.getColumnModel().getColumn(3);
        if (deletionMode) {
            deleteCol.setMinWidth(75);
            deleteCol.setMaxWidth(75);
            deleteCol.setWidth(75);
            for (int i = 0; i < accountsModel.getRowCount(); i++) {
                accountsModel.setValueAt("Delete", i, 3);
            }
        } else {
            deleteCol.setMinWidth(0);
            deleteCol.setMaxWidth(0);
            deleteCol.setWidth(0);
        }
    }

    private void showAccountTypeModal() {
        modal = new JDialog(this, "Select Account Type", true);
        modal.setSize(300, 250);
        modal.setLocationRelativeTo(this);
        modal.setLayout(new BorderLayout());

        JPanel contentPanel = new JPanel(new GridLayout(3, 1, 10, 10));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JButton regularBtn = createAccountTypeButton("Regular\nCash, Car...", "icons/wallet.png");
        JButton debtBtn = createAccountTypeButton("Debt\nCredit, Mortgage...", "icons/salary.png");
        JButton savingsBtn = createAccountTypeButton("Savings\nSavings, Goal...", "icons/money.png");

        regularBtn.addActionListener(e -> selectAccountType("Regular"));
        debtBtn.addActionListener(e -> selectAccountType("Debt"));
        savingsBtn.addActionListener(e -> selectAccountType("Savings"));

        contentPanel.add(regularBtn);
        contentPanel.add(debtBtn);
        contentPanel.add(savingsBtn);

        modal.add(contentPanel);
        modal.setVisible(true);
    }

    private JButton createAccountTypeButton(String text, String iconPath) {
        JButton btn = new JButton("<html>" + text.replace("\n", "<br>") + "</html>");
        try {
            ImageIcon icon = new ImageIcon(loadScaledImage(iconPath, 20, 20));
            btn.setIcon(icon);
        } catch (Exception e) {
            // Icon not found, continue without icon
        }
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        return btn;
    }

    private void selectAccountType(String type) {
        selectedAccountType = type;
        modal.dispose();
        showAddAccountModal();
    }

    private void showAddAccountModal() {
        modal = new JDialog(this, "Add Account", true);
        modal.setSize(300, 225);
        modal.setLocationRelativeTo(this);
        modal.setLayout(new BorderLayout());

        JPanel contentPanel = new JPanel(new GridLayout(5, 1, 5, 5));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        contentPanel.add(new JLabel("Account Type: " + selectedAccountType));
        contentPanel.add(new JLabel("Account Name:"));
        JTextField nameField = new JTextField();
        contentPanel.add(nameField);
        contentPanel.add(new JLabel("Balance:"));
        JTextField balanceField = new JTextField();
        contentPanel.add(balanceField);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton cancelBtn = new JButton("Cancel");
        JButton addBtn = new JButton("Add");

        cancelBtn.addActionListener(e -> modal.dispose());
        addBtn.addActionListener(e -> {
            String name = nameField.getText().trim();
            String balance = balanceField.getText().trim();
            if (name.isEmpty() || balance.isEmpty()) {
                JOptionPane.showMessageDialog(modal, "All fields must be filled.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
                return;
            }
            try {
                double balanceValue = Double.parseDouble(balance);
                String formatted = String.format("₱ %,.2f", balanceValue);
                accountsModel.addRow(new Object[]{selectedAccountType, name, formatted, "Delete"});
                modal.dispose();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(modal, "Balance must be a valid number.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
            }
        });

        buttonPanel.add(cancelBtn);
        buttonPanel.add(addBtn);

        modal.add(contentPanel, BorderLayout.CENTER);
        modal.add(buttonPanel, BorderLayout.SOUTH);
        modal.setVisible(true);
    }

    // CATEGORIES TAB
    private void createCategoriesTab() {
        updateCategoriesDisplay();
    }

    private void updateCategoriesDisplay() {
        categoriesTab.removeAll();
        JPanel mainPanel = new JPanel(new BorderLayout());
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));

        // Expenses Section
        JLabel expensesLabel = new JLabel("Expenses");
        expensesLabel.setFont(new Font("Arial", Font.BOLD, 12));
        expensesLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(expensesLabel);

        JPanel expensesPanel = createCategoryGrid(categories.get("expenses"), "expenses");
        expensesPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(expensesPanel);

        contentPanel.add(Box.createVerticalStrut(20));

        // Income Section
        JLabel incomeLabel = new JLabel("Income");
        incomeLabel.setFont(new Font("Arial", Font.BOLD, 12));
        incomeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(incomeLabel);

        JPanel incomePanel = createCategoryGrid(categories.get("income"), "income");
        incomePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(incomePanel);

        JScrollPane scrollPane = new JScrollPane(contentPanel);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton editBtn = new JButton(editMode ? "Cancel Edit" : "Edit Categories");
        editBtn.addActionListener(e -> toggleEditMode());
        buttonPanel.add(editBtn);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        categoriesTab.add(mainPanel);
        categoriesTab.revalidate();
        categoriesTab.repaint();
    }

    private JPanel createCategoryGrid(List<Category> categoryList, String type) {
        JPanel gridPanel = new JPanel(new GridLayout(0, 5, 10, 10));
        gridPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        if (!categoryCheckboxes.containsKey(type)) {
            categoryCheckboxes.put(type, new HashMap<>());
        }

        for (int i = 0; i < categoryList.size(); i++) {
            Category cat = categoryList.get(i);
            JPanel catPanel = new JPanel(new BorderLayout());
            catPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

            JLabel catLabel = new JLabel();
            catLabel.setHorizontalAlignment(SwingConstants.CENTER);
            catLabel.setVerticalAlignment(SwingConstants.CENTER);
            
            try {
                ImageIcon icon = new ImageIcon(loadScaledImage(cat.icon, 70, 70));
                catLabel.setIcon(icon);
            } catch (Exception e) {
                catLabel.setText("[Icon]");
            }
            catLabel.setText("<html><center>" + cat.name + "</center></html>");
            catLabel.setVerticalTextPosition(SwingConstants.BOTTOM);
            catLabel.setHorizontalTextPosition(SwingConstants.CENTER);

            if (!editMode) {
                final Category category = cat;
                catLabel.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        showAddTransactionModal(category);
                    }
                    @Override
                    public void mouseEntered(MouseEvent e) {
                        catPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
                    }
                    @Override
                    public void mouseExited(MouseEvent e) {
                        catPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
                    }
                });
            } else {
                JCheckBox cb = new JCheckBox();
                cb.setHorizontalAlignment(SwingConstants.CENTER);
                categoryCheckboxes.get(type).put(i, cb);
                cb.addActionListener(e -> updateDeleteButtonVisibility());
                catPanel.add(cb, BorderLayout.NORTH);
            }

            catPanel.add(catLabel, BorderLayout.CENTER);
            gridPanel.add(catPanel);
        }

        // Add "Add Category" button in edit mode
        if (editMode) {
            JPanel addPanel = new JPanel(new BorderLayout());
            addPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
            JButton addBtn = new JButton("+");
            addBtn.setFont(new Font("Arial", Font.BOLD, 24));
            addBtn.addActionListener(e -> showAddCategoryModal());
            addPanel.add(addBtn, BorderLayout.CENTER);
            JLabel addLabel = new JLabel("add category");
            addLabel.setHorizontalAlignment(SwingConstants.CENTER);
            addPanel.add(addLabel, BorderLayout.SOUTH);
            gridPanel.add(addPanel);
        }

        return gridPanel;
    }

    private void toggleEditMode() {
        editMode = !editMode;
        if (deleteButton != null) {
            deleteButton.setVisible(false);
        }
        updateCategoriesDisplay();
    }

    private void updateDeleteButtonVisibility() {
        boolean hasSelection = false;
        for (Map<Integer, JCheckBox> typeMap : categoryCheckboxes.values()) {
            for (JCheckBox cb : typeMap.values()) {
                if (cb.isSelected()) {
                    hasSelection = true;
                    break;
                }
            }
            if (hasSelection) break;
        }

        if (hasSelection && deleteButton == null) {
            // Add delete button to the categories tab button panel
            JPanel buttonPanel = (JPanel) categoriesTab.getComponent(0);
            if (buttonPanel instanceof JPanel) {
                Component[] comps = buttonPanel.getComponents();
                for (Component c : comps) {
                    if (c instanceof JPanel) {
                        deleteButton = new JButton("Delete Selected");
                        deleteButton.addActionListener(e -> deleteSelectedCategories());
                        ((JPanel) c).add(deleteButton, 0);
                        ((JPanel) c).revalidate();
                        ((JPanel) c).repaint();
                        break;
                    }
                }
            }
        } else if (!hasSelection && deleteButton != null) {
            Container parent = deleteButton.getParent();
            parent.remove(deleteButton);
            deleteButton = null;
            parent.revalidate();
            parent.repaint();
        }
    }

    private void deleteSelectedCategories() {
        for (String type : categoryCheckboxes.keySet()) {
            List<Category> catList = categories.get(type);
            List<Category> newList = new ArrayList<>();
            Map<Integer, JCheckBox> checkboxes = categoryCheckboxes.get(type);
            
            for (int i = 0; i < catList.size(); i++) {
                if (!checkboxes.containsKey(i) || !checkboxes.get(i).isSelected()) {
                    newList.add(catList.get(i));
                }
            }
            categories.put(type, newList);
        }
        categoryCheckboxes.clear();
        updateCategoriesDisplay();
    }

    private void showAddCategoryModal() {
        modal = new JDialog(this, "Add Category", true);
        modal.setSize(300, 300);
        modal.setLocationRelativeTo(this);
        modal.setLayout(new BorderLayout());

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        contentPanel.add(new JLabel("Choose an icon:"));
        
        iconLabel = new JLabel();
        iconLabel.setHorizontalAlignment(SwingConstants.CENTER);
        try {
            iconPhoto = new ImageIcon(loadScaledImage(selectedIcon, 50, 50));
            iconLabel.setIcon(iconPhoto);
        } catch (Exception e) {
            iconLabel.setText("[Icon]");
        }
        contentPanel.add(iconLabel);

        JButton chooseIconBtn = new JButton("Choose Icon");
        chooseIconBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        chooseIconBtn.addActionListener(e -> showIconSelectionModal());
        contentPanel.add(chooseIconBtn);

        contentPanel.add(new JLabel("Category Name:"));
        JTextField nameField = new JTextField();
        contentPanel.add(nameField);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton cancelBtn = new JButton("Cancel");
        JButton addBtn = new JButton("Add");

        cancelBtn.addActionListener(e -> modal.dispose());
        addBtn.addActionListener(e -> {
            String name = nameField.getText().trim();
            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(modal, "Category name must be filled.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
                return;
            }
            categories.get("expenses").add(new Category(selectedIcon, name));
            modal.dispose();
            updateCategoriesDisplay();
        });

        buttonPanel.add(cancelBtn);
        buttonPanel.add(addBtn);

        modal.add(contentPanel, BorderLayout.CENTER);
        modal.add(buttonPanel, BorderLayout.SOUTH);
        modal.setVisible(true);
    }

    private void showIconSelectionModal() {
        JDialog iconDialog = new JDialog(modal, "Choose Icon", true);
        iconDialog.setSize(400, 400);
        iconDialog.setLocationRelativeTo(modal);

        JPanel gridPanel = new JPanel(new GridLayout(0, 5, 5, 5));
        gridPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        File iconFolder = new File("icons/category icons");
        if (iconFolder.exists() && iconFolder.isDirectory()) {
            File[] iconFiles = iconFolder.listFiles((dir, name) -> name.endsWith(".png"));
            if (iconFiles != null) {
                for (File iconFile : iconFiles) {
                    try {
                        ImageIcon icon = new ImageIcon(loadScaledImage(iconFile.getPath(), 50, 50));
                        JButton iconBtn = new JButton(icon);
                        iconBtn.addActionListener(e -> {
                            selectedIcon = iconFile.getPath();
                            try {
                                iconPhoto = new ImageIcon(loadScaledImage(selectedIcon, 50, 50));
                                iconLabel.setIcon(iconPhoto);
                            } catch (Exception ex) {
                                // Handle error
                            }
                            iconDialog.dispose();
                        });
                        gridPanel.add(iconBtn);
                    } catch (Exception e) {
                        // Skip this icon
                    }
                }
            }
        }

        JScrollPane scrollPane = new JScrollPane(gridPanel);
        iconDialog.add(scrollPane);
        iconDialog.setVisible(true);
    }

    private void showAddTransactionModal(Category category) {
        // Check if this is an income category
        boolean isIncome = categories.get("income").contains(category);

        JDialog transDialog = new JDialog(this, "Add Transaction", true);
        transDialog.setSize(300, 500);
        transDialog.setLocationRelativeTo(this);
        transDialog.setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Account/Category selection
        JPanel topPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        JLabel accountLabel = new JLabel("Cash");
        accountLabel.setFont(new Font("Arial", Font.BOLD, 12));
        
        if (isIncome) {
            topPanel.add(new JLabel("From: " + category.name));
            topPanel.add(new JLabel("To: " + accountLabel.getText()));
        } else {
            topPanel.add(new JLabel("From: " + accountLabel.getText()));
            topPanel.add(new JLabel("To: " + category.name));
        }
        mainPanel.add(topPanel);

        // Amount input
        JTextField amountField = new JTextField("₱");
        amountField.setFont(new Font("Arial", Font.PLAIN, 18));
        amountField.setHorizontalAlignment(JTextField.CENTER);
        mainPanel.add(amountField);

        // Notes
        JTextField notesField = new JTextField("Notes...");
        notesField.setForeground(Color.GRAY);
        notesField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (notesField.getText().equals("Notes...")) {
                    notesField.setText("");
                    notesField.setForeground(Color.BLACK);
                }
            }
            @Override
            public void focusLost(FocusEvent e) {
                if (notesField.getText().isEmpty()) {
                    notesField.setText("Notes...");
                    notesField.setForeground(Color.GRAY);
                }
            }
        });
        mainPanel.add(notesField);

        // Calculator buttons
        JPanel calcPanel = new JPanel(new GridLayout(4, 5, 5, 5));
        String[][] buttons = {
            {"/", "7", "8", "9", "C"},
            {"*", "4", "5", "6", "CA"},
            {"-", "1", "2", "3", "✔"},
            {"+", "₱", "0", ".", ""}
        };

        for (String[] row : buttons) {
            for (String btn : row) {
                if (!btn.isEmpty()) {
                    JButton calcBtn = new JButton(btn);
                    calcBtn.addActionListener(e -> handleCalculatorButton(btn, amountField));
                    calcPanel.add(calcBtn);
                } else {
                    calcPanel.add(new JLabel());
                }
            }
        }
        mainPanel.add(calcPanel);

        // Action buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton cancelBtn = new JButton("Cancel");
        JButton addBtn = new JButton("Add");

        cancelBtn.addActionListener(e -> transDialog.dispose());
        addBtn.addActionListener(e -> {
            String amount = amountField.getText().replace("₱", "").trim();
            String notes = notesField.getText().equals("Notes...") ? "" : notesField.getText();
            String account = accountLabel.getText();
            
            try {
                double amountValue = Double.parseDouble(amount);
                Transaction trans = new Transaction(
                    account,
                    (isIncome ? "+ " : "- ") + String.format("₱%,.2f", Math.abs(amountValue)),
                    new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()),
                    notes,
                    category.name
                );
                transactions.add(trans);
                
                // Update account balance
                updateAccountBalance(account, amountValue, isIncome);
                
                transDialog.dispose();
                createTransactionsTab();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(transDialog, "Invalid amount", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        buttonPanel.add(cancelBtn);
        buttonPanel.add(addBtn);

        transDialog.add(mainPanel, BorderLayout.CENTER);
        transDialog.add(buttonPanel, BorderLayout.SOUTH);
        transDialog.setVisible(true);
    }

    private void handleCalculatorButton(String btn, JTextField field) {
        String current = field.getText();
        
        if (btn.equals("C")) {
            if (current.length() > 1) {
                field.setText(current.substring(0, current.length() - 1));
            } else {
                field.setText("₱");
            }
        } else if (btn.equals("CA")) {
            field.setText("₱");
        } else if (btn.equals("✔")) {
            try {
                String expr = current.substring(1).replace("x", "*");
                double result = evaluateExpression(expr);
                field.setText(String.format("₱%.2f", result));
            } catch (Exception e) {
                field.setText("₱0");
            }
        } else {
            if (current.equals("₱0")) {
                field.setText("₱" + btn);
            } else {
                field.setText(current + btn);
            }
        }
    }

    private double evaluateExpression(String expr) {
        // Simple expression evaluator (you may want to use a library for complex expressions)
        try {
            return new javax.script.ScriptEngineManager()
                .getEngineByName("JavaScript")
                .eval(expr) instanceof Number 
                ? ((Number) new javax.script.ScriptEngineManager()
                    .getEngineByName("JavaScript")
                    .eval(expr)).doubleValue() 
                : 0.0;
        } catch (Exception e) {
            return 0.0;
        }
    }

    private void updateAccountBalance(String accountName, double amount, boolean add) {
        for (int i = 0; i < accountsModel.getRowCount(); i++) {
            if (accountsModel.getValueAt(i, 1).equals(accountName)) {
                String balanceStr = accountsModel.getValueAt(i, 2).toString()
                    .replace("₱", "").replace(",", "").trim();
                try {
                    double currentBalance = Double.parseDouble(balanceStr);
                    double newBalance = add ? currentBalance + amount : currentBalance - amount;
                    accountsModel.setValueAt(String.format("₱ %,.2f", newBalance), i, 2);
                } catch (NumberFormatException e) {
                    // Handle error
                }
                break;
            }
        }
    }

    // TRANSACTIONS TAB
    private void createTransactionsTab() {
        transactionsTab.removeAll();

        if (transactions.isEmpty()) {
            JLabel emptyLabel = new JLabel("No transactions available");
            emptyLabel.setFont(new Font("Arial", Font.PLAIN, 12));
            emptyLabel.setHorizontalAlignment(SwingConstants.CENTER);
            transactionsTab.add(emptyLabel, BorderLayout.CENTER);
        } else {
            JPanel contentPanel = new JPanel();
            contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
            contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            // Group transactions by date
            Map<String, List<Transaction>> byDate = new TreeMap<>(Collections.reverseOrder());
            for (Transaction t : transactions) {
                String date = t.datetime.split(" ")[0];
                byDate.computeIfAbsent(date, k -> new ArrayList<>()).add(t);
            }

            for (Map.Entry<String, List<Transaction>> entry : byDate.entrySet()) {
                String date = entry.getKey();
                List<Transaction> dayTransactions = entry.getValue();

                // Calculate total for the day
                double total = 0;
                for (Transaction t : dayTransactions) {
                    String amtStr = t.amount.replace("₱", "").replace(",", "").trim();
                    try {
                        total += Double.parseDouble(amtStr);
                    } catch (NumberFormatException e) {
                        // Skip
                    }
                }

                // Date header
                JLabel dateLabel = new JLabel(String.format("%s | Total: ₱%.2f", date, total));
                dateLabel.setFont(new Font("Arial", Font.BOLD, 10));
                dateLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
                contentPanel.add(dateLabel);
                contentPanel.add(new JSeparator());

                // Transaction items
                for (Transaction t : dayTransactions) {
                    JPanel transPanel = new JPanel(new BorderLayout(10, 0));
                    transPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));

                    JLabel detailsLabel = new JLabel("<html>" + t.category + "<br>" + 
                        t.account + (t.notes.isEmpty() ? "" : "<br>" + t.notes) + "</html>");
                    JLabel amountLabel = new JLabel(t.amount);
                    amountLabel.setFont(new Font("Arial", Font.BOLD, 10));

                    transPanel.add(detailsLabel, BorderLayout.CENTER);
                    transPanel.add(amountLabel, BorderLayout.EAST);

                    contentPanel.add(transPanel);
                    contentPanel.add(new JSeparator());
                }

                contentPanel.add(Box.createVerticalStrut(10));
            }

            JScrollPane scrollPane = new JScrollPane(contentPanel);
            transactionsTab.add(scrollPane, BorderLayout.CENTER);
        }

        transactionsTab.revalidate();
        transactionsTab.repaint();
    }

    // Helper method to load and scale images
    private Image loadScaledImage(String path, int width, int height) throws Exception {
        return ImageIO.read(new File(path)).getScaledInstance(width, height, Image.SCALE_SMOOTH);
    }

    // Inner classes for data structures
    static class Category {
        String icon;
        String name;

        Category(String icon, String name) {
            this.icon = icon;
            this.name = name;
        }
    }

    static class Transaction {
        String account;
        String amount;
        String datetime;
        String notes;
        String category;

        Transaction(String account, String amount, String datetime, String notes, String category) {
            this.account = account;
            this.amount = amount;
            this.datetime = datetime;
            this.notes = notes;
            this.category = category;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new BudgetTracker().setVisible(true);
        });
    }
}