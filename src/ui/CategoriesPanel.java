package ui;

import model.Category;
import model.Transaction;
import model.Account;

import javax.swing.*;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.*;

public class CategoriesPanel extends JPanel {

    private ArrayList<Category> categories;
    private ArrayList<Transaction> transactions;
    private ArrayList<Account> accounts;
    private TransactionsPanel transactionsPanel;
    private AccountsPanel accountsPanel;
    
    private boolean editMode = false;
    private JPanel contentPanel;
    private String selectedIcon = "icons/placeholder.png";
    private JLabel iconLabel;
    private ImageIcon iconPhoto;
    private JButton deleteButton;
    private Map<String, Map<Integer, JCheckBox>> categoryCheckboxes;
    
    private ArrayList<Category> expenseCategories;
    private ArrayList<Category> incomeCategories;
    
    // Modern color palette
    private static final Color BACKGROUND = new Color(248, 249, 250);
    private static final Color CARD_BG = Color.WHITE;
    private static final Color PRIMARY = new Color(41, 128, 185); // Blue
    private static final Color SUCCESS = new Color(34, 197, 94); // Green
    private static final Color DANGER = new Color(239, 68, 68); // Red
    private static final Color TEXT_PRIMARY = new Color(31, 41, 55);
    private static final Color TEXT_SECONDARY = new Color(107, 114, 128);
    private static final Color BORDER = new Color(229, 231, 235);
    private static final Color HOVER = new Color(243, 244, 246);

public CategoriesPanel(ArrayList<Category> categories, 
                       ArrayList<Transaction> transactions, 
                       ArrayList<Account> accounts, 
                       TransactionsPanel transactionsPanel,
                       AccountsPanel accountsPanel) {
    this.categories = categories;
    this.transactions = transactions;
    this.accounts = accounts;
        this.accountsPanel = accountsPanel;
        this.transactionsPanel = transactionsPanel;

    // FIX: Initialize the categoryCheckboxes map
    this.categoryCheckboxes = new HashMap<>();

    setLayout(new BorderLayout());
    updateCategoriesDisplay();
}


    
    private void separateCategories() {
        expenseCategories = new ArrayList<>();
        incomeCategories = new ArrayList<>();
        
        Set<String> incomeNames = new HashSet<>(Arrays.asList("Salary", "Allowance"));
        
        for (Category cat : categories) {
            if (incomeNames.contains(cat.getName())) {
                incomeCategories.add(cat);
            } else {
                expenseCategories.add(cat);
            }
        }
    }

private void updateCategoriesDisplay() {
    // FIX: separate categories before creating grids
    separateCategories();

    removeAll();
    contentPanel = new JPanel();
    contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
    contentPanel.setBackground(BACKGROUND);
    contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

    // Expenses Section
    contentPanel.add(createSectionHeader(" Expenses", DANGER));
    contentPanel.add(Box.createVerticalStrut(15));
    
    JPanel expensesPanel = createCategoryGrid(expenseCategories, "expenses");
    expensesPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
    contentPanel.add(expensesPanel);
    contentPanel.add(Box.createVerticalStrut(30));

    // Income Section
    contentPanel.add(createSectionHeader(" Income", SUCCESS));
    contentPanel.add(Box.createVerticalStrut(15));
    
    JPanel incomePanel = createCategoryGrid(incomeCategories, "income");
    incomePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
    contentPanel.add(incomePanel);
    contentPanel.add(Box.createVerticalGlue());

        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);

        // Modern floating action bar
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        
        JButton editBtn = createModernButton(
            editMode ? "Cancel" : "Edit", 
            editMode ? TEXT_SECONDARY : PRIMARY,
            editMode
        );
        editBtn.addActionListener(e -> toggleEditMode());
        buttonPanel.add(editBtn);

        add(buttonPanel, BorderLayout.SOUTH);
        
        revalidate();
        repaint();
    }
    
    private JPanel createSectionHeader(String title, Color accentColor) {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(BACKGROUND);
        header.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(TEXT_PRIMARY);
        
        // Accent line
        JPanel accentLine = new JPanel();
        accentLine.setBackground(accentColor);
        accentLine.setPreferredSize(new Dimension(4, 25));
        
        header.add(accentLine, BorderLayout.WEST);
        header.add(Box.createHorizontalStrut(12), BorderLayout.CENTER);
        header.add(titleLabel, BorderLayout.CENTER);
        
        return header;
    }

    private JPanel createCategoryGrid(ArrayList<Category> categoryList, String type) {
        JPanel gridPanel = new JPanel(new GridLayout(0, 4, 15, 15));
        gridPanel.setBackground(BACKGROUND);

        if (!categoryCheckboxes.containsKey(type)) {
            categoryCheckboxes.put(type, new HashMap<>());
        }

        for (int i = 0; i < categoryList.size(); i++) {
            Category cat = categoryList.get(i);
            JPanel catPanel = createModernCategoryCard(cat, type, i);
            gridPanel.add(catPanel);
        }

        // Add category button in edit mode
        if (editMode) {
            JPanel addPanel = createAddCategoryCard(type);
            gridPanel.add(addPanel);
        }

        return gridPanel;
    }
    
    private JPanel createModernCategoryCard(Category cat, String type, int index) {
        JPanel card = new JPanel(new BorderLayout(0, 10));
        card.setBackground(CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER, 1, true),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        card.setPreferredSize(new Dimension(140, 140));

        // Checkbox overlay for edit mode
        if (editMode) {
            JCheckBox cb = new JCheckBox();
            cb.setBackground(CARD_BG);
            cb.setFocusPainted(false);
            categoryCheckboxes.get(type).put(index, cb);
            cb.addActionListener(e -> updateDeleteButtonVisibility());
            
            JPanel checkPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
            checkPanel.setBackground(CARD_BG);
            checkPanel.add(cb);
            card.add(checkPanel, BorderLayout.NORTH);
        }

        // Icon and label container
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(CARD_BG);

        // Top padding
        contentPanel.add(Box.createVerticalStrut(15));
        
        JLabel iconLabel = new JLabel();
        iconLabel.setHorizontalAlignment(SwingConstants.CENTER);
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        try {
            ImageIcon icon = new ImageIcon(loadScaledImage(cat.getIconPath(), 55, 55));
            iconLabel.setIcon(icon);
        } catch (Exception e) {
            iconLabel.setText("📦");
            iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 40));
        }
        
        contentPanel.add(iconLabel);
        contentPanel.add(Box.createVerticalStrut(8));
        
        JLabel nameLabel = new JLabel(cat.getName());
        nameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        nameLabel.setForeground(TEXT_PRIMARY);
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        nameLabel.setHorizontalAlignment(SwingConstants.CENTER);
        contentPanel.add(nameLabel);
        
        card.add(contentPanel, BorderLayout.CENTER);

        if (!editMode) {
            card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            card.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    showAddTransactionModal(cat);
                }
                @Override
                public void mouseEntered(MouseEvent e) {
                    card.setBackground(HOVER);
                    contentPanel.setBackground(HOVER);
                    if (editMode) {
                        JPanel checkPanel = (JPanel) card.getComponent(0);
                        checkPanel.setBackground(HOVER);
                    }
                    card.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(PRIMARY, 2, true),
                        BorderFactory.createEmptyBorder(15, 15, 15, 15)
                    ));
                }
                @Override
                public void mouseExited(MouseEvent e) {
                    card.setBackground(CARD_BG);
                    contentPanel.setBackground(CARD_BG);
                    if (editMode) {
                        JPanel checkPanel = (JPanel) card.getComponent(0);
                        checkPanel.setBackground(CARD_BG);
                    }
                    card.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(BORDER, 1, true),
                        BorderFactory.createEmptyBorder(15, 15, 15, 15)
                    ));
                }
            });
        }

        return card;
    }
    
    private JPanel createAddCategoryCard(String type) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(BACKGROUND);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createDashedBorder(PRIMARY, 2, 5, 3, true),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        card.setPreferredSize(new Dimension(140, 140));
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(BACKGROUND);
        
        JLabel plusLabel = new JLabel("+");
        plusLabel.setFont(new Font("Segoe UI", Font.BOLD, 48));
        plusLabel.setForeground(PRIMARY);
        plusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel textLabel = new JLabel("Add Category");
        textLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        textLabel.setForeground(TEXT_SECONDARY);
        textLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        contentPanel.add(Box.createVerticalGlue());
        contentPanel.add(plusLabel);
        contentPanel.add(Box.createVerticalStrut(5));
        contentPanel.add(textLabel);
        contentPanel.add(Box.createVerticalGlue());
        
        card.add(contentPanel, BorderLayout.CENTER);
        
        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                showAddCategoryModal(type);
            }
            @Override
            public void mouseEntered(MouseEvent e) {
                card.setBackground(new Color(245, 243, 255));
                contentPanel.setBackground(new Color(245, 243, 255));
            }
            @Override
            public void mouseExited(MouseEvent e) {
                card.setBackground(BACKGROUND);
                contentPanel.setBackground(BACKGROUND);
            }
        });
        
        return card;
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

    private void toggleEditMode() {
        editMode = !editMode;
        if (deleteButton != null) {
            deleteButton.setVisible(false);
        }
        categoryCheckboxes.clear();
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
            JPanel buttonPanel = (JPanel) getComponent(1);
            deleteButton = createModernButton("🗑️ Delete Selected", DANGER, false);
            deleteButton.addActionListener(e -> deleteSelectedCategories());
            buttonPanel.add(deleteButton, 0);
            buttonPanel.revalidate();
            buttonPanel.repaint();
        } else if (!hasSelection && deleteButton != null) {
            Container parent = deleteButton.getParent();
            parent.remove(deleteButton);
            deleteButton = null;
            parent.revalidate();
            parent.repaint();
        }
    }

    private void deleteSelectedCategories() {
        int result = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to delete the selected categories?",
            "Confirm Deletion",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );
        
        if (result != JOptionPane.YES_OPTION) return;
        
        if (categoryCheckboxes.containsKey("expenses")) {
            Map<Integer, JCheckBox> checkboxes = categoryCheckboxes.get("expenses");
            ArrayList<Category> newList = new ArrayList<>();
            for (int i = 0; i < expenseCategories.size(); i++) {
                if (!checkboxes.containsKey(i) || !checkboxes.get(i).isSelected()) {
                    newList.add(expenseCategories.get(i));
                }
            }
            expenseCategories = newList;
        }
        
        if (categoryCheckboxes.containsKey("income")) {
            Map<Integer, JCheckBox> checkboxes = categoryCheckboxes.get("income");
            ArrayList<Category> newList = new ArrayList<>();
            for (int i = 0; i < incomeCategories.size(); i++) {
                if (!checkboxes.containsKey(i) || !checkboxes.get(i).isSelected()) {
                    newList.add(incomeCategories.get(i));
                }
            }
            incomeCategories = newList;
        }
        
        categories.clear();
        categories.addAll(expenseCategories);
        categories.addAll(incomeCategories);
        
        categoryCheckboxes.clear();
        updateCategoriesDisplay();
    }

    private void showAddCategoryModal(String type) {
        JDialog modal = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Add Category", true);
        modal.setSize(420, 480);
        modal.setLocationRelativeTo(this);
        modal.setLayout(new BorderLayout());
        modal.setBackground(Color.WHITE);

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(25, 30, 25, 30));

        // Title
        JLabel titleLabel = new JLabel("Create New Category");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(TEXT_PRIMARY);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(titleLabel);
        contentPanel.add(Box.createVerticalStrut(8));
        
        JLabel subtitleLabel = new JLabel("Choose an icon and give it a name");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitleLabel.setForeground(TEXT_SECONDARY);
        subtitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(subtitleLabel);
        contentPanel.add(Box.createVerticalStrut(25));

        // Icon selection area
        JPanel iconArea = new JPanel(new BorderLayout());
        iconArea.setBackground(HOVER);
        iconArea.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER, 1, true),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        iconArea.setMaximumSize(new Dimension(360, 140));
        
        iconLabel = new JLabel();
        iconLabel.setHorizontalAlignment(SwingConstants.CENTER);
        try {
            iconPhoto = new ImageIcon(loadScaledImage(selectedIcon, 80, 80));
            iconLabel.setIcon(iconPhoto);
        } catch (Exception e) {
            iconLabel.setText("📦");
            iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 60));
        }
        
        JButton chooseIconBtn = createModernButton("Choose Icon", PRIMARY, true);
        chooseIconBtn.addActionListener(e -> showIconSelectionModal(modal));
        
        JPanel iconContent = new JPanel();
        iconContent.setLayout(new BoxLayout(iconContent, BoxLayout.Y_AXIS));
        iconContent.setBackground(HOVER);
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        chooseIconBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        iconContent.add(iconLabel);
        iconContent.add(Box.createVerticalStrut(12));
        iconContent.add(chooseIconBtn);
        
        iconArea.add(iconContent, BorderLayout.CENTER);
        contentPanel.add(iconArea);
        contentPanel.add(Box.createVerticalStrut(20));

        // Name input
        JLabel nameLabel = new JLabel("Category Name");
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        nameLabel.setForeground(TEXT_PRIMARY);
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
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

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 20));
        buttonPanel.setBackground(Color.WHITE);
        
        JButton cancelBtn = createModernButton("Cancel", TEXT_SECONDARY, true);
        JButton addBtn = createModernButton("Add Category", SUCCESS, false);

        cancelBtn.addActionListener(e -> modal.dispose());
        addBtn.addActionListener(e -> {
            String name = nameField.getText().trim();
            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(modal, 
                    "Please enter a category name.", 
                    "Invalid Input", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            Category newCat = new Category(name, selectedIcon);
            
            if (type.equals("expenses")) {
                expenseCategories.add(newCat);
            } else {
                incomeCategories.add(newCat);
            }
            
            categories.add(newCat);
            modal.dispose();
            updateCategoriesDisplay();
        });

        buttonPanel.add(cancelBtn);
        buttonPanel.add(addBtn);

        modal.add(contentPanel, BorderLayout.CENTER);
        modal.add(buttonPanel, BorderLayout.SOUTH);
        modal.setVisible(true);
    }

    private void showIconSelectionModal(JDialog parentModal) {
        JDialog iconDialog = new JDialog(parentModal, "Choose Icon", true);
        iconDialog.setSize(520, 520);
        iconDialog.setLocationRelativeTo(parentModal);
        iconDialog.setBackground(Color.WHITE);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel title = new JLabel("Select an Icon");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(TEXT_PRIMARY);
        mainPanel.add(title, BorderLayout.NORTH);

        JPanel gridPanel = new JPanel(new GridLayout(0, 5, 12, 12));
        gridPanel.setBackground(Color.WHITE);
        gridPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        File iconFolder = new File("icons/category icons");
        if (iconFolder.exists() && iconFolder.isDirectory()) {
            File[] iconFiles = iconFolder.listFiles((dir, name) -> name.endsWith(".png"));
            if (iconFiles != null) {
                for (File iconFile : iconFiles) {
                    try {
                        ImageIcon icon = new ImageIcon(loadScaledImage(iconFile.getPath(), 55, 55));
                        JButton iconBtn = new JButton(icon);
                        iconBtn.setPreferredSize(new Dimension(80, 80));
                        iconBtn.setBackground(Color.WHITE);
                        iconBtn.setFocusPainted(false);
                        iconBtn.setBorder(BorderFactory.createLineBorder(BORDER, 1, true));
                        iconBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                        
                        iconBtn.addMouseListener(new MouseAdapter() {
                            @Override
                            public void mouseEntered(MouseEvent e) {
                                iconBtn.setBackground(HOVER);
                                iconBtn.setBorder(BorderFactory.createLineBorder(PRIMARY, 2, true));
                            }
                            @Override
                            public void mouseExited(MouseEvent e) {
                                iconBtn.setBackground(Color.WHITE);
                                iconBtn.setBorder(BorderFactory.createLineBorder(BORDER, 1, true));
                            }
                        });
                        
                        iconBtn.addActionListener(e -> {
                            selectedIcon = iconFile.getPath();
                            try {
                                iconPhoto = new ImageIcon(loadScaledImage(selectedIcon, 80, 80));
                                iconLabel.setIcon(iconPhoto);
                                iconLabel.setText(null);
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
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        
        iconDialog.add(mainPanel);
        iconDialog.setVisible(true);
    }

private void showAddTransactionModal(Category category) {
    if (accounts.isEmpty()) {
        JOptionPane.showMessageDialog(this,
                "Please add an account first in the Accounts tab.",
                "No Accounts",
                JOptionPane.WARNING_MESSAGE);
        return;
    }

    boolean isIncome = incomeCategories.contains(category);

    JDialog transDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "New Transaction", true);
    transDialog.setSize(460, 580);
    transDialog.setLocationRelativeTo(this);
    transDialog.setLayout(new BorderLayout());
    transDialog.setBackground(Color.WHITE);

    // Main vertical panel
    JPanel mainPanel = new JPanel();
    mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
    mainPanel.setBackground(Color.WHITE);
    mainPanel.setBorder(BorderFactory.createEmptyBorder(25, 15, 25, 15));

    // Center wrapper
    JPanel wrapper = new JPanel(new GridBagLayout());
    wrapper.setBackground(Color.WHITE);
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.weightx = 1;
    gbc.weighty = 1;
    gbc.anchor = GridBagConstraints.CENTER;
    gbc.fill = GridBagConstraints.NONE;
    wrapper.add(mainPanel, gbc);

    // Header
    JLabel titleLabel = new JLabel(isIncome ? "Add Income" : "Add Expense");
    titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
    titleLabel.setForeground(TEXT_PRIMARY);
    titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
    mainPanel.add(titleLabel);
    mainPanel.add(Box.createVerticalStrut(20));

    // Category badge
    JPanel categoryBadge = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 8));
    categoryBadge.setBackground(isIncome ? new Color(220, 252, 231) : new Color(254, 226, 226));
    categoryBadge.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(isIncome ? SUCCESS : DANGER, 1, true),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
    ));
    JLabel categoryLabel = new JLabel((isIncome ? "💰 " : "💸 ") + category.getName());
    categoryLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
    categoryLabel.setForeground(isIncome ? new Color(22, 163, 74) : new Color(220, 38, 38));
    categoryBadge.add(categoryLabel);
    mainPanel.add(categoryBadge);
    mainPanel.add(Box.createVerticalStrut(20));

    // Account
    JLabel accountLabel = new JLabel("Account");
    accountLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
    accountLabel.setForeground(TEXT_PRIMARY);
    accountLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
    mainPanel.add(accountLabel);
    mainPanel.add(Box.createVerticalStrut(8));

    JComboBox<Account> accountCombo = new JComboBox<>(accounts.toArray(new Account[0]));
    accountCombo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
    accountCombo.setMaximumSize(new Dimension(400, 40));
    accountCombo.setBackground(Color.WHITE);
    accountCombo.setBorder(BorderFactory.createLineBorder(BORDER, 1, true));
    accountCombo.setAlignmentX(Component.CENTER_ALIGNMENT);
    mainPanel.add(accountCombo);
    mainPanel.add(Box.createVerticalStrut(20));

    // Amount
    JLabel amountLabel = new JLabel("Amount");
    amountLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
    amountLabel.setForeground(TEXT_PRIMARY);
    amountLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
    mainPanel.add(amountLabel);
    mainPanel.add(Box.createVerticalStrut(8));

    JTextField amountField = new JTextField("0.00");
    amountField.setFont(new Font("Segoe UI", Font.BOLD, 24));
    amountField.setHorizontalAlignment(JTextField.CENTER);
    amountField.setForeground(isIncome ? SUCCESS : DANGER);
    amountField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER, 2, true),
            BorderFactory.createEmptyBorder(15, 12, 15, 12)
    ));
    amountField.setMaximumSize(new Dimension(400, 70));
    amountField.setAlignmentX(Component.CENTER_ALIGNMENT);
    mainPanel.add(amountField);
    mainPanel.add(Box.createVerticalStrut(20));

    // Notes
    JLabel notesLabel = new JLabel("Notes (optional)");
    notesLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
    notesLabel.setForeground(TEXT_PRIMARY);
    notesLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
    mainPanel.add(notesLabel);
    mainPanel.add(Box.createVerticalStrut(8));

    JTextField notesField = new JTextField();
    notesField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
    notesField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER, 1, true),
            BorderFactory.createEmptyBorder(10, 12, 10, 12)
    ));
    notesField.setMaximumSize(new Dimension(400, 42));
    notesField.setAlignmentX(Component.CENTER_ALIGNMENT);
    mainPanel.add(notesField);

    // Buttons
    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 20));
    buttonPanel.setBackground(Color.WHITE);

    JButton cancelBtn = createModernButton("Cancel", TEXT_SECONDARY, true);
    JButton addBtn = createModernButton(isIncome ? "Add Income" : "Add Expense",
            isIncome ? SUCCESS : PRIMARY, false);

    cancelBtn.addActionListener(e -> transDialog.dispose());

addBtn.addActionListener(e -> {
    String amountStr = amountField.getText().replace("₱", "").replace(",", "").trim();
    String notes = notesField.getText().trim();
    Account selectedAccount = (Account) accountCombo.getSelectedItem();

    try {
        double amount = Double.parseDouble(amountStr);
        if (amount <= 0) {
            JOptionPane.showMessageDialog(transDialog,
                    "Amount must be greater than 0",
                    "Invalid Amount",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!isIncome && amount > selectedAccount.getBalance()) {
            JOptionPane.showMessageDialog(transDialog,
                    "Insufficient balance in " + selectedAccount.getName() + "!\nCurrent balance: ₱" +
                            String.format("%,.2f", selectedAccount.getBalance()),
                    "Insufficient Funds",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // DEBUG: Print before balance update
        System.out.println("=== BEFORE TRANSACTION ===");
        System.out.println("Account: " + selectedAccount.getName());
        System.out.println("Old Balance: " + selectedAccount.getBalance());
        System.out.println("Transaction Amount: " + amount);
        System.out.println("Transaction Type: " + (isIncome ? "Income" : "Expense"));

        Transaction trans = new Transaction(
                selectedAccount,
                category.getName(),
                isIncome ? "Income" : "Expense",
                amount,
                new Date()
        );
        if (!notes.isEmpty()) {
            trans.setNotes(notes);
        }

        transactions.add(trans);

        // Update account balance
        double oldBalance = selectedAccount.getBalance();
        if (isIncome) {
            selectedAccount.setBalance(selectedAccount.getBalance() + amount);
        } else {
            selectedAccount.setBalance(selectedAccount.getBalance() - amount);
        }

        // DEBUG: Print after balance update
        System.out.println("=== AFTER TRANSACTION ===");
        System.out.println("New Balance: " + selectedAccount.getBalance());
        System.out.println("Balance Changed: " + (oldBalance != selectedAccount.getBalance()));
        
        // DEBUG: Check if it's the same object in the accounts list
        System.out.println("=== CHECKING ACCOUNTS LIST ===");
        for (int i = 0; i < accounts.size(); i++) {
            Account acc = accounts.get(i);
            System.out.println("Account " + i + ": " + acc.getName() + " - Balance: " + acc.getBalance());
            System.out.println("Is same object? " + (acc == selectedAccount));
        }

        // FIX: Refresh both panels properly
        System.out.println("=== REFRESHING PANELS ===");
        System.out.println("TransactionsPanel is null? " + (transactionsPanel == null));
        System.out.println("AccountsPanel is null? " + (accountsPanel == null));
        
        if (transactionsPanel != null) {
            transactionsPanel.updateDisplay();
            System.out.println("TransactionsPanel refreshed");
        }
        if (accountsPanel != null) {
            accountsPanel.refresh();
            System.out.println("AccountsPanel refreshed");
        }

        transDialog.dispose();

        JOptionPane.showMessageDialog(this,
                "Transaction added successfully!\nNew balance: ₱" +
                        String.format("%,.2f", selectedAccount.getBalance()),
                "Success",
                JOptionPane.INFORMATION_MESSAGE);

    } catch (NumberFormatException ex) {
        JOptionPane.showMessageDialog(transDialog,
                "Please enter a valid number",
                "Invalid Amount",
                JOptionPane.ERROR_MESSAGE);
    }
});

    buttonPanel.add(cancelBtn);
    buttonPanel.add(addBtn);

    transDialog.add(wrapper, BorderLayout.CENTER);
    transDialog.add(buttonPanel, BorderLayout.SOUTH);
    transDialog.setVisible(true);
}
    private Image loadScaledImage(String path, int width, int height) throws Exception {
        return ImageIO.read(new File(path)).getScaledInstance(width, height, Image.SCALE_SMOOTH);
    }
}