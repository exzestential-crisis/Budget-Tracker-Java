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
        setSize(1200, 800);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(BACKGROUND);

        // Splash label (centered)
        JLabel splashLabel = new JLabel("ADV Expense Tracker", SwingConstants.CENTER);
        splashLabel.setFont(new Font("Segoe UI", Font.BOLD, 48));
        splashLabel.setForeground(new Color(41, 128, 185, 0)); // start fully transparent
        splashLabel.setBounds(0, getHeight() / 2 - 50, getWidth(), 100);
        splashLabel.setOpaque(false);
        getLayeredPane().add(splashLabel, JLayeredPane.PALETTE_LAYER);

        setVisible(true);

        // Fade-in
        final int frames = 30;  // fade duration (30 frames ~ 1 sec)
        final int delay = 30;   // ms per frame
        final int[] step = {0};

        Timer fadeInTimer = new Timer(delay, e -> {
            float progress = (float) step[0] / frames;
            splashLabel.setForeground(new Color(41, 128, 185, (int) (255 * progress)));
            step[0]++;
            if (step[0] > frames) {
                ((Timer) e.getSource()).stop();

                // Hold for 1 second before fading out
                new Timer(1000, hold -> {
                    ((Timer) hold.getSource()).stop();

                    // Fade-out
                    step[0] = 0;
                    Timer fadeOutTimer = new Timer(delay, fe -> {
                        float prog = (float) step[0] / frames;
                        splashLabel.setForeground(new Color(41, 128, 185, (int) (255 * (1 - prog))));
                        step[0]++;
                        if (step[0] > frames) {
                            ((Timer) fe.getSource()).stop();
                            getLayeredPane().remove(splashLabel);
                            initializeUI();
                            repaint();
                        }
                    });
                    fadeOutTimer.start();
                }).start();
            }
        });
        fadeInTimer.start();
    }

    // Build main UI after splash
    private void initializeUI() {

        // Header
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
        tabs.setBorder(BorderFactory.createEmptyBorder());

        UIManager.put("TabbedPane.contentBorderInsets", new Insets(0, 0, 0, 0));

        tabs.setUI(new javax.swing.plaf.basic.BasicTabbedPaneUI() {
            private final int TAB_HEIGHT = 50;

            @Override
            protected void installDefaults() {
                super.installDefaults();
                tabAreaInsets.left = 0;
                tabAreaInsets.right = 0;
                contentBorderInsets = new Insets(0, 0, 0, 0);
            }

            @Override
            protected int calculateTabWidth(int tabPlacement, int tabIndex, FontMetrics metrics) {
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
                Color bg = isSelected ? HEADER_BG : Color.WHITE;
                g2.setColor(bg);
                g2.fillRect(x, y, w, h);
            }

            @Override
            protected void paintContentBorder(Graphics g, int tabPlacement, int selectedIndex) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setColor(new Color(200, 200, 200));
                g2.fillRect(0, tabs.getHeight() - 2, tabs.getWidth(), 2);
            }

            @Override
            protected void paintTabBorder(Graphics g, int tabPlacement, int tabIndex,
                                          int x, int y, int w, int h, boolean isSelected) {
            }

            @Override
            protected void paintText(Graphics g, int tabPlacement, Font font, FontMetrics metrics,
                                     int tabIndex, String title, Rectangle textRect, boolean isSelected) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                        RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                g2.setColor(isSelected ? Color.WHITE : TAB_TEXT);
                g2.setFont(font);
                g2.drawString(title, textRect.x, textRect.y + metrics.getAscent());
            }
        });

        // Panels
        AccountsPanel accountsPanel = new AccountsPanel(accountsList);
        TransactionsPanel transactionsPanel = new TransactionsPanel(transactionsList);
        CategoriesPanel categoriesPanel = new CategoriesPanel(
                categoriesList, transactionsList, accountsList, transactionsPanel, accountsPanel
        );

        tabs.addTab("Categories", categoriesPanel);
        tabs.addTab("Accounts", accountsPanel);
        tabs.addTab("Transactions", transactionsPanel);

        JPanel tabWrapper = new JPanel(new BorderLayout());
        tabWrapper.setBackground(BACKGROUND);
        tabWrapper.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        tabWrapper.add(tabs, BorderLayout.CENTER);

        add(tabWrapper, BorderLayout.CENTER);

        revalidate();
        repaint();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainFrame::new);
    }
}
