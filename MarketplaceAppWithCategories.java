import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList; // Explicitly import ArrayList
import java.util.List;     // Explicitly import List
import java.util.UUID;

public class MarketplaceApp extends JFrame {

    // Data Models (In-memory)
    private static class User {
        String username, password, address, phoneNumber, email;
        public User(String u, String p, String a, String ph, String e) {
            username = u; password = p; address = a; phoneNumber = ph; email = e;
        }
        public String getUsername() { return username; }
        public String getPassword() { return password; }
        public String getAddress() { return address; } // Used for delivery address
    }

    private static class Product {
        String id, name, description, sellerUsername, type;
        double price;

        public Product(String id, String n, String d, double p, String s, String t) {
            this.id = id; name = n; description = d; price = p; sellerUsername = s; type = t;
        }
        public String getName() { return name; }
        public double getPrice() { return price; }
        public String getSellerUsername() { return sellerUsername; }
        public String getType() { return type; }
        @Override
        public String toString() { return name + " - Rs." + String.format("%.2f", price); }
    }

    // App State
    private final List<User> registeredUsers = new ArrayList<>();
    private final List<Product> availableProducts = new ArrayList<>();
    private final List<Product> cartItems = new ArrayList<>();
    private User currentUser = null;

    // GUI Components (references for dynamic updates)
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private JTextField loginUsernameField, signupUsernameField, signupAddressField, signupPhoneNumberField, signupEmailField, productNameField, productPriceField, priceFilterMinField, priceFilterMaxField;
    private JPasswordField loginPasswordField, signupPasswordField;
    private JTextArea productDescriptionArea;
    private JLabel welcomeLabelDashboard, cartTotalPriceLabel;
    private DefaultListModel<Product> productListModel, cartListModel;
    private JComboBox<String> productTypeFilterComboBox;

    public MarketplaceApp() {
        setTitle("Second Hand Marketplace");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initializeDummyData();
        initUI();
        setVisible(true);
    }

    private void initializeDummyData() {
        registeredUsers.add(new User("user1", "pass1", "123 Main St", "9876543210", "user1@gmail.com"));
        registeredUsers.add(new User("sellerA", "sellpass", "456 Oak Ave", "1234567890", "sellerA@gmail.com"));
        availableProducts.add(new Product(UUID.randomUUID().toString(), "Vintage Camera", "Classic", 2500.00, "sellerA", "Electronics"));
        availableProducts.add(new Product(UUID.randomUUID().toString(), "Old Bicycle", "Needs tires", 1500.00, "user1", "Vehicles"));
        availableProducts.add(new Product(UUID.randomUUID().toString(), "Rare Book", "First edition", 5000.00, "sellerA", "Books"));
    }

    private void initUI() {
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        add(mainPanel);

        // Login Panel
        JPanel loginPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        loginPanel.setBorder(new EmptyBorder(50, 50, 50, 50));
        loginPanel.add(new JLabel("Username:")); loginPanel.add(loginUsernameField = new JTextField(20));
        loginPanel.add(new JLabel("Password:")); loginPanel.add(loginPasswordField = new JPasswordField(20));
        JButton loginButton = new JButton("Login"); loginButton.addActionListener(e -> attemptLogin()); loginPanel.add(loginButton);
        JButton signupPromptButton = new JButton("Sign Up"); signupPromptButton.addActionListener(e -> cardLayout.show(mainPanel, "Signup")); loginPanel.add(signupPromptButton);
        mainPanel.add(loginPanel, "Login");

        // Signup Panel
        JPanel signupPanel = new JPanel(new GridLayout(8, 2, 10, 10));
        signupPanel.setBorder(new EmptyBorder(50, 50, 50, 50));
        signupPanel.add(new JLabel("Username:")); signupPanel.add(signupUsernameField = new JTextField(20));
        signupPanel.add(new JLabel("Password:")); signupPanel.add(signupPasswordField = new JPasswordField(20));
        signupPanel.add(new JLabel("Address:")); signupPanel.add(signupAddressField = new JTextField(20));
        signupPanel.add(new JLabel("Phone (10 digits):")); signupPanel.add(signupPhoneNumberField = new JTextField(20));
        signupPanel.add(new JLabel("Email (@gmail.com):")); signupPanel.add(signupEmailField = new JTextField(20));
        JButton signupButton = new JButton("Sign Up"); signupButton.addActionListener(e -> attemptSignup()); signupPanel.add(signupButton);
        JButton backToLoginButton = new JButton("Back to Login"); backToLoginButton.addActionListener(e -> cardLayout.show(mainPanel, "Login")); signupPanel.add(backToLoginButton);
        mainPanel.add(signupPanel, "Signup");

        // Dashboard Panel
        JPanel dashboardPanel = new JPanel(new BorderLayout(20, 20));
        dashboardPanel.setBorder(new EmptyBorder(50, 50, 50, 50));
        welcomeLabelDashboard = new JLabel("Hello, Guest!", SwingConstants.CENTER); welcomeLabelDashboard.setFont(new Font("Arial", Font.BOLD, 28));
        dashboardPanel.add(welcomeLabelDashboard, BorderLayout.NORTH);
        JPanel dashButtons = new JPanel(new GridLayout(1, 2, 20, 20));
        JButton buyButton = new JButton("Buy Products"); buyButton.addActionListener(e -> { refreshBuyProductList(); cardLayout.show(mainPanel, "BuyProduct"); }); dashButtons.add(buyButton);
        JButton sellButton = new JButton("Sell Products"); sellButton.addActionListener(e -> { clearSellProductForm(); cardLayout.show(mainPanel, "SellProduct"); }); dashButtons.add(sellButton);
        dashboardPanel.add(dashButtons, BorderLayout.CENTER);
        JButton logoutButton = new JButton("Logout"); logoutButton.addActionListener(e -> { currentUser = null; cartItems.clear(); JOptionPane.showMessageDialog(this, "Logged out!"); cardLayout.show(mainPanel, "Login"); });
        dashboardPanel.add(logoutButton, BorderLayout.SOUTH);
        mainPanel.add(dashboardPanel, "Dashboard");

        // Sell Product Panel
        JPanel sellProductPanel = new JPanel(new GridLayout(5, 2, 10, 10)); // Reduced rows
        sellProductPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        sellProductPanel.add(new JLabel("Product Name:")); sellProductPanel.add(productNameField = new JTextField(25));
        sellProductPanel.add(new JLabel("Description:")); productDescriptionArea = new JTextArea(3, 25); sellProductPanel.add(new JScrollPane(productDescriptionArea));
        sellProductPanel.add(new JLabel("Price (Rs.):")); sellProductPanel.add(productPriceField = new JTextField(10));
        JButton listProductButton = new JButton("List Product"); listProductButton.addActionListener(e -> listProduct()); sellProductPanel.add(listProductButton);
        JButton backToDashSell = new JButton("Back to Dashboard"); backToDashSell.addActionListener(e -> { cardLayout.show(mainPanel, "Dashboard"); promptForAppReview(); }); sellProductPanel.add(backToDashSell);
        mainPanel.add(sellProductPanel, "SellProduct");

        // Buy Product Panel
        JPanel buyProductPanel = new JPanel(new BorderLayout(10, 10));
        buyProductPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        filterPanel.add(new JLabel("Min:")); filterPanel.add(priceFilterMinField = new JTextField(5));
        filterPanel.add(new JLabel("Max:")); filterPanel.add(priceFilterMaxField = new JTextField(5));
        filterPanel.add(new JLabel("Type:")); filterPanel.add(productTypeFilterComboBox = new JComboBox<>(new String[]{"All", "Electronics", "Books", "Furniture", "Vehicles", "Miscellaneous"}));
        JButton applyFilterBtn = new JButton("Apply"); applyFilterBtn.addActionListener(e -> applyProductFilters()); filterPanel.add(applyFilterBtn);
        JButton resetFilterBtn = new JButton("Reset"); resetFilterBtn.addActionListener(e -> resetProductFilters()); filterPanel.add(resetFilterBtn);
        buyProductPanel.add(filterPanel, BorderLayout.NORTH);
        JList<Product> productJList = new JList<>(productListModel = new DefaultListModel<>());
        productJList.setCellRenderer(new ProductListCellRenderer());
        buyProductPanel.add(new JScrollPane(productJList), BorderLayout.CENTER);
        JPanel buyActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        JButton viewDetailsBtn = new JButton("Details"); viewDetailsBtn.addActionListener(e -> { Product p = productJList.getSelectedValue(); if(p!=null) showProductDetails(p); else JOptionPane.showMessageDialog(this, "Select a product."); }); buyActions.add(viewDetailsBtn);
        JButton addToCartBtn = new JButton("Add to Cart"); addToCartBtn.addActionListener(e -> { Product p = productJList.getSelectedValue(); if(p!=null) addToCart(p); else JOptionPane.showMessageDialog(this, "Select a product."); }); buyActions.add(addToCartBtn);
        JButton viewCartBtn = new JButton("View Cart"); viewCartBtn.addActionListener(e -> { refreshCartList(); cardLayout.show(mainPanel, "Cart"); }); buyActions.add(viewCartBtn);
        JButton backToDashBuy = new JButton("Back"); backToDashBuy.addActionListener(e -> cardLayout.show(mainPanel, "Dashboard")); buyActions.add(backToDashBuy);
        buyProductPanel.add(buyActions, BorderLayout.SOUTH);
        mainPanel.add(buyProductPanel, "BuyProduct");

        // Cart Panel
        JPanel cartPanel = new JPanel(new BorderLayout(10, 10));
        cartPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        cartPanel.add(new JLabel("Your Cart", SwingConstants.CENTER), BorderLayout.NORTH);
        JList<Product> cartJList = new JList<>(cartListModel = new DefaultListModel<>());
        cartPanel.add(new JScrollPane(cartJList), BorderLayout.CENTER);
        JPanel cartSummaryActions = new JPanel(new BorderLayout());
        cartTotalPriceLabel = new JLabel("Total: Rs. 0.00", SwingConstants.RIGHT); cartSummaryActions.add(cartTotalPriceLabel, BorderLayout.NORTH);
        JPanel cartBtns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        JButton removeItemBtn = new JButton("Remove"); removeItemBtn.addActionListener(e -> { Product p = cartJList.getSelectedValue(); if(p!=null) { cartItems.remove(p); refreshCartList(); } else JOptionPane.showMessageDialog(this, "Select item to remove."); }); cartBtns.add(removeItemBtn);
        JButton checkoutBtn = new JButton("Checkout"); checkoutBtn.addActionListener(e -> simulateCheckout()); cartBtns.add(checkoutBtn);
        JButton continueShoppingBtn = new JButton("Shop More"); continueShoppingBtn.addActionListener(e -> cardLayout.show(mainPanel, "BuyProduct")); cartBtns.add(continueShoppingBtn);
        cartSummaryActions.add(cartBtns, BorderLayout.SOUTH);
        cartPanel.add(cartSummaryActions, BorderLayout.SOUTH);
        mainPanel.add(cartPanel, "Cart");
    }

    // Core Logic Methods (simplified messages for brevity)
    private void attemptLogin() {
        String u = loginUsernameField.getText(), p = new String(loginPasswordField.getPassword());
        boolean loggedIn = registeredUsers.stream().anyMatch(user -> user.getUsername().equals(u) && user.getPassword().equals(p));
        if (loggedIn) {
            currentUser = registeredUsers.stream().filter(user -> user.getUsername().equals(u)).findFirst().orElse(null);
            JOptionPane.showMessageDialog(this, "Welcome, " + currentUser.getUsername() + "!");
            SwingUtilities.invokeLater(() -> welcomeLabelDashboard.setText("Hello, " + currentUser.getUsername() + "!"));
            cardLayout.show(mainPanel, "Dashboard"); clearLoginFields();
        } else { JOptionPane.showMessageDialog(this, "Invalid login.", "Login Failed", JOptionPane.ERROR_MESSAGE); }
    }

    private void attemptSignup() {
        String u = signupUsernameField.getText(), p = new String(signupPasswordField.getPassword()), a = signupAddressField.getText(), ph = signupPhoneNumberField.getText(), e = signupEmailField.getText();
        if (u.isEmpty() || p.isEmpty() || a.isEmpty() || ph.isEmpty() || e.isEmpty()) { JOptionPane.showMessageDialog(this, "All fields required.", "Signup Failed", JOptionPane.ERROR_MESSAGE); return; }
        if (!ph.matches("\\d{10}")) { JOptionPane.showMessageDialog(this, "Phone must be 10 digits.", "Signup Failed", JOptionPane.ERROR_MESSAGE); return; }
        if (!e.endsWith("@gmail.com")) { JOptionPane.showMessageDialog(this, "Email must be @gmail.com.", "Signup Failed", JOptionPane.ERROR_MESSAGE); return; }
        if (registeredUsers.stream().anyMatch(user -> user.getUsername().equals(u))) { JOptionPane.showMessageDialog(this, "User exists.", "Signup Failed", JOptionPane.ERROR_MESSAGE); return; }
        registeredUsers.add(new User(u, p, a, ph, e));
        JOptionPane.showMessageDialog(this, "Account created! Login now."); cardLayout.show(mainPanel, "Login"); clearSignupFields();
    }

    private void listProduct() {
        if (currentUser == null) { JOptionPane.showMessageDialog(this, "Login to sell."); return; }
        String n = productNameField.getText(), d = productDescriptionArea.getText(), pT = productPriceField.getText();
        if (n.isEmpty() || d.isEmpty() || pT.isEmpty()) { JOptionPane.showMessageDialog(this, "Fill product details.", "Missing Info", JOptionPane.WARNING_MESSAGE); return; }
        double price; try { price = Double.parseDouble(pT); if (price <= 0) throw new NumberFormatException(); }
        catch (NumberFormatException ex) { JOptionPane.showMessageDialog(this, "Valid price needed.", "Invalid Price", JOptionPane.ERROR_MESSAGE); return; }
        availableProducts.add(new Product(UUID.randomUUID().toString(), n, d, price, currentUser.getUsername(), "Miscellaneous"));
        JOptionPane.showMessageDialog(this, "'" + n + "' listed!"); clearSellProductForm(); promptForAppReview(); cardLayout.show(mainPanel, "Dashboard");
    }

    private void refreshBuyProductList() {
        productListModel.clear();
        availableProducts.stream()
            .forEach(productListModel::addElement);
    }

    private void applyProductFilters() {
        productListModel.clear();
        double minPrice = -1;
        double maxPrice = Double.MAX_VALUE;
        try {
            if (!priceFilterMinField.getText().isEmpty()) minPrice = Double.parseDouble(priceFilterMinField.getText());
            if (!priceFilterMaxField.getText().isEmpty()) maxPrice = Double.parseDouble(priceFilterMaxField.getText());
            if (minPrice > maxPrice) { JOptionPane.showMessageDialog(this, "Min price > Max.", "Filter Error", JOptionPane.ERROR_MESSAGE); return; }
        } catch (NumberFormatException e) { JOptionPane.showMessageDialog(this, "Invalid price input.", "Filter Error", JOptionPane.ERROR_MESSAGE); return; }

        final double finalMinPrice = minPrice;
        final double finalMaxPrice = maxPrice;
        String selectedT = (String) productTypeFilterComboBox.getSelectedItem();

        availableProducts.stream()
            .filter(product -> product.getPrice() >= finalMinPrice && product.getPrice() <= finalMaxPrice)
            .filter(product -> selectedT.equals("All") || product.getType().equalsIgnoreCase(selectedT))
            .forEach(productListModel::addElement);
        if (productListModel.isEmpty()) JOptionPane.showMessageDialog(this, "No products match filters.", "No Results", JOptionPane.INFORMATION_MESSAGE);
    }

    private void resetProductFilters() {
        priceFilterMinField.setText(""); priceFilterMaxField.setText(""); productTypeFilterComboBox.setSelectedItem("All");
        refreshBuyProductList();
    }

    private void showProductDetails(Product p) {
        JOptionPane.showMessageDialog(this, String.format("Name: %s\nDesc: %s\nPrice: Rs.%.2f\nSeller: %s\nCategory: %s", p.getName(), p.description, p.getPrice(), p.getSellerUsername(), p.getType()), "Details", JOptionPane.INFORMATION_MESSAGE);
        int negotiateOpt = JOptionPane.showConfirmDialog(this, "Negotiate for " + p.getName() + "?", "Negotiate?", JOptionPane.YES_NO_OPTION);
        if (negotiateOpt == JOptionPane.YES_OPTION) simulateNegotiation(p);
    }

    private void addToCart(Product p) {
        if (currentUser == null) { JOptionPane.showMessageDialog(this, "Login to add to cart."); return; }
        if (cartItems.contains(p)) { JOptionPane.showMessageDialog(this, p.getName() + " already in cart."); return; }
        cartItems.add(p); JOptionPane.showMessageDialog(this, p.getName() + " added!"); refreshCartList();
    }

    private void refreshCartList() {
        cartListModel.clear();
        double total = cartItems.stream().mapToDouble(Product::getPrice).sum();
        cartItems.forEach(cartListModel::addElement);
        cartTotalPriceLabel.setText("Total: Rs." + String.format("%.2f", total));
    }

    private void simulateCheckout() {
        if (cartItems.isEmpty()) { JOptionPane.showMessageDialog(this, "Cart is empty."); return; }
        JOptionPane.showMessageDialog(this, "Proceeding to checkout.", "Checkout", JOptionPane.INFORMATION_MESSAGE);
        int confirm = JOptionPane.showConfirmDialog(this, "Confirm purchase?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            availableProducts.removeAll(cartItems); cartItems.clear(); refreshCartList();
            JOptionPane.showMessageDialog(this, "Purchase successful! Delivered to " + currentUser.getAddress() + ".");
            promptForAppReview(); // Pass in cartItems so it knows which sellers were involved
            cartItems.stream().map(Product::getSellerUsername).distinct().forEach(this::promptForSellerReview);
            if (JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(this, "Buy anything else?", "Continue?", JOptionPane.YES_NO_OPTION)) {
                cardLayout.show(mainPanel, "BuyProduct"); refreshBuyProductList();
            } else { cardLayout.show(mainPanel, "Dashboard"); }
        } else { JOptionPane.showMessageDialog(this, "Checkout cancelled."); }
    }

    private void simulateNegotiation(Product p) {
        String offer = JOptionPane.showInputDialog(this, "Offer for " + p.getName() + " (current: Rs." + String.format("%.2f", p.getPrice()) + "):");
        if (offer != null && !offer.trim().isEmpty()) {
            try { double offeredP = Double.parseDouble(offer); if (offeredP <= 0) throw new NumberFormatException();
                JOptionPane.showMessageDialog(this, "Offer sent! Seller accepts for demo.", "Negotiation Sent", JOptionPane.INFORMATION_MESSAGE);
            } catch (NumberFormatException e) { JOptionPane.showMessageDialog(this, "Invalid offer.", "Negotiation Failed", JOptionPane.ERROR_MESSAGE); }
        }
    }

    private void promptForAppReview() {
        if (JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(this, "Review app?", "App Review", JOptionPane.YES_NO_OPTION)) {
            String review = JOptionPane.showInputDialog(this, "Enter app review:");
            if (review != null && !review.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Thanks for review!");
            } else { JOptionPane.showMessageDialog(this, "Review empty or cancelled."); }
        }
    }

    private void promptForSellerReview(String seller) {
        if (JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(this, "Review " + seller + "?", "Seller Review", JOptionPane.YES_NO_OPTION)) {
            String review = JOptionPane.showInputDialog(this, "Enter review for " + seller + ":");
            if (review != null && !review.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Thanks for review!");
            } else { JOptionPane.showMessageDialog(this, "Review empty or cancelled."); }
        }
    }

    // Helper Methods (clearing fields)
    private void clearLoginFields() { loginUsernameField.setText(""); loginPasswordField.setText(""); }
    private void clearSignupFields() { signupUsernameField.setText(""); signupPasswordField.setText(""); signupAddressField.setText(""); signupPhoneNumberField.setText(""); signupEmailField.setText(""); }
    private void clearSellProductForm() { productNameField.setText(""); productDescriptionArea.setText(""); productPriceField.setText(""); }

    // Custom Cell Renderer (simplified)
    private static class ProductListCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value instanceof Product) {
                Product product = (Product) value;
                label.setText("<html><b>" + product.getName() + "</b> (Rs." + String.format("%.2f", product.getPrice()) + ")<br><small>Cat: " + product.getType() + " | Seller: " + product.getSellerUsername() + "</small></html>");
                label.setBorder(new EmptyBorder(5, 10, 5, 10));
            }
            return label;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MarketplaceApp::new);
    }
}
