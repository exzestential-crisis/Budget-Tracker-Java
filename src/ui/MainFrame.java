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

    // Modern palette
    private static final Color BACKGROUND = new Color(248, 249, 250);
    private static final Color HEADER_BG = new Color(41, 128, 185);
    private static final Color HEADER_TEXT = Color.WHITE;
    private static final Color TAB_BG = Color.WHITE;
    private static final Color TAB_SELECTED = new Color(99, 102, 241); // Indigo
    private static final Color TAB_TEXT = new Color(31, 41, 55);

    public MainFrame() {
        categoriesList = new ArrayList<>();
        accountsList = new ArrayList<>();
        transactionsList = new ArrayList<>();

        // Predefined categories
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
        setSize(1200, 800);  // Bigger window
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(BACKGROUND);

        // Modern Header
        JLabel header = new JLabel("ADV Expense Tracker", SwingConstants.CENTER);
        header.setFont(new Font("Segoe UI", Font.BOLD, 28));
        header.setOpaque(true);
        header.setBackground(HEADER_BG);
        header.setForeground(HEADER_TEXT);
        header.setBorder(BorderFactory.createEmptyBorder(25, 0, 25, 0));
        add(header, BorderLayout.NORTH);

        // Tabs
        JTabbedPane tabs = new JTabbedPane(JTabbedPane.TOP);
        tabs.setFont(new Font("Segoe UI", Font.BOLD, 16));
        tabs.setBackground(TAB_BG);
        tabs.setForeground(TAB_TEXT);
        tabs.setBorder(BorderFactory.createEmptyBorder());  // remove default border

        // Remove extra border around tab content
        UIManager.put("TabbedPane.contentBorderInsets", new Insets(0, 0, 0, 0));

        // Bigger tab height
       tabs.setUI(new javax.swing.plaf.basic.BasicTabbedPaneUI() {
        private final int TAB_HEIGHT = 50;

        @Override
        protected void installDefaults() {
            super.installDefaults();
            tabAreaInsets.left = 0;
            tabAreaInsets.right = 0;
            contentBorderInsets = new Insets(0, 0, 0, 0); // no border around content
        }

        @Override
        protected int calculateTabWidth(int tabPlacement, int tabIndex, FontMetrics metrics) {
            // Stretch tabs evenly across the entire width
            return tabs.getWidth() / tabs.getTabCount();
        }

        @Override
        protected int calculateTabHeight(int tabPlacement, int tabIndex, int fontHeight) {
            return TAB_HEIGHT;
        }

        @Override
        protected void paintTabBackground(Graphics g, int tabPlacement, int tabIndex,
                                        int x, int y, int w, int h, boolean isSelected) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            Color bg = isSelected ? new Color(41, 128, 185) : Color.WHITE;
            g2.setColor(bg);
            g2.fillRect(x, y, w, h);
        }

        @Override
        protected void paintContentBorder(Graphics g, int tabPlacement, int selectedIndex) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setColor(new Color(200, 200, 200)); // light gray bottom border
            g2.fillRect(0, tabs.getHeight() - 2, tabs.getWidth(), 2);
        }

        @Override
        protected void paintTabBorder(Graphics g, int tabPlacement, int tabIndex,
                                    int x, int y, int w, int h, boolean isSelected) {
            // no extra border
        }

        @Override
        protected void paintText(Graphics g, int tabPlacement, Font font, FontMetrics metrics,
                                int tabIndex, String title, Rectangle textRect, boolean isSelected) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, 
                                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            // Set white text if selected, otherwise dark
            g2.setColor(isSelected ? Color.WHITE : new Color(31, 41, 55)); // TEXT_PRIMARY
            g2.setFont(font);
            g2.drawString(title, textRect.x, textRect.y + metrics.getAscent());
        }

    });

        // FIX: Create panels in correct order - AccountsPanel and TransactionsPanel FIRST
        AccountsPanel accountsPanel = new AccountsPanel(accountsList);
        TransactionsPanel transactionsPanel = new TransactionsPanel(transactionsList);
        
        // Then create CategoriesPanel with references to both panels
        CategoriesPanel categoriesPanel = new CategoriesPanel(
            categoriesList, transactionsList, accountsList, transactionsPanel, accountsPanel
        );

        tabs.addTab("Categories", categoriesPanel);
        tabs.addTab("Accounts", accountsPanel);
        tabs.addTab("Transactions", transactionsPanel);

        // Wrap tabs in a shadowed panel to mimic "folder" feel
        JPanel tabWrapper = new JPanel(new BorderLayout());
        tabWrapper.setBackground(BACKGROUND);
        tabWrapper.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // padding
        tabWrapper.add(tabs, BorderLayout.CENTER);

        add(tabWrapper, BorderLayout.CENTER);

        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainFrame());
    }
}