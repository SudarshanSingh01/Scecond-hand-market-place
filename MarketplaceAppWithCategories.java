import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID; // For generating unique IDs for products

public class Marketappwithgui extends JFrame {

    // --- Data Models (In-memory for demonstration) ---
    // Represents a user in the system
    private static class User {
        String username;
        String password;
        String address; // User's address
        String phoneNumber; // New: User's phone number
        String email;       // New: User's email

        public User(String username, String password, String address, String phoneNumber, String email) {
            this.username = username;
            this.password = password;
            this.address = address;
            this.phoneNumber = phoneNumber;
            this.email = email;
        }

        public String getUsername() { return username; }
        public String getPassword() { return password; }
        public String getAddress() { return address; }
        public String getPhoneNumber() { return phoneNumber; }
        public String getEmail() { return email; }
    }

    // Represents a product listed for sale
    private static class Product {
        String id;
        String name;
        String description;
        double price;
        String sellerUsername;
        String imagePath; // Path to the image file, simulated
        String type; // New: Type of the product (e.g., "Electronics", "Books")

        public Product(String id, String name, String description, double price, String sellerUsername, String imagePath, String type) {
            this.id = id;
            this.name = name;
            this.description = description;
            this.price = price;
            this.sellerUsername = sellerUsername;
            this.imagePath = imagePath;
            this.type = type;
        }

        public String getId() { return id; }
        public String getName() { return name; }
        public String getDescription() { return description; }
        public double getPrice() { return price; }
        public String getSellerUsername() { return sellerUsername; }
        public String getImagePath() { return imagePath; }
        public String getType() { return type; } // Getter for the new type field

        @Override
        public String toString() {
            return name + " - Rs." + String.format("%.2f", price);
        }
    }

    // --- Application State Variables ---
    private List<User> registeredUsers = new ArrayList<>();
    private List<Product> availableProducts = new ArrayList<>();
    private List<Product> cartItems = new ArrayList<>();
    private User currentUser = null; // Currently logged-in user

    // --- GUI Components ---
    private JPanel mainPanel; // Panel using CardLayout for switching views
    private CardLayout cardLayout;

    // Login Panel Components
    private JPanel loginPanel;
    private JTextField loginUsernameField;
    private JPasswordField loginPasswordField;

    // Signup Panel Components
    private JPanel signupPanel;
    private JTextField signupUsernameField;
    private JPasswordField signupPasswordField;
    private JTextField signupAddressField; // New field for address
    private JTextField signupPhoneNumberField; // New: Phone number field
    private JTextField signupEmailField;       // New: Email field

    // Dashboard Panel Components
    private JPanel dashboardPanel;

    // Sell Product Panel Components
    private JPanel sellProductPanel;
    private JTextField productNameField;
    private JTextArea productDescriptionArea;
    private JTextField productPriceField;
    private JLabel productImageLabel; // To display selected image path or "No image selected"
    private String selectedImagePath = ""; // Stores the path of the selected image
    // Note: A real app would also need a way to input product type on selling.
    // For this demo, products created via 'Sell' will have a default type 'Miscellaneous'.

    // Buy Product Panel Components
    private JPanel buyProductPanel;
    private JList<Product> productJList; // List to display products
    private DefaultListModel<Product> productListModel; // Model for JList
    private JTextField priceFilterMinField;
    private JTextField priceFilterMaxField;
    private JComboBox<String> productTypeFilterComboBox; // Simple product type filter

    // Cart Panel Components
    private JPanel cartPanel;
    private JList<Product> cartJList;
    private DefaultListModel<Product> cartListModel;
    private JLabel cartTotalPriceLabel;

    // --- Constructor ---
    public Marketappwithgui() {
        setTitle("Second Hand Marketplace");
        setSize(800, 600); // Increased size for better layout
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center the window

        // Initialize dummy data (for demonstration)
        initializeDummyData();

        // Setup main panel with CardLayout
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        add(mainPanel);

        // Initialize all individual panels
        createLoginPanel();
        createSignupPanel();
        createDashboardPanel();
        createSellProductPanel();
        createBuyProductPanel();
        createCartPanel();

        // Add panels to the main panel with unique names for CardLayout
        mainPanel.add(loginPanel, "Login");
        mainPanel.add(signupPanel, "Signup");
        mainPanel.add(dashboardPanel, "Dashboard");
        mainPanel.add(sellProductPanel, "SellProduct");
        mainPanel.add(buyProductPanel, "BuyProduct");
        mainPanel.add(cartPanel, "Cart");

        // Show the login panel initially
        cardLayout.show(mainPanel, "Login");
        setVisible(true);
    }

    // --- Dummy Data Initialization ---
    private void initializeDummyData() {
        // Updated dummy users with phone and email
        registeredUsers.add(new User("user1", "pass1", "123 Main St", "9876543210", "user1@gmail.com"));
        registeredUsers.add(new User("sellerA", "sellpass", "456 Oak Ave", "1234567890", "sellerA@gmail.com"));

        // Updated dummy products with explicit types
        availableProducts.add(new Product(UUID.randomUUID().toString(), "Vintage Camera", "A classic film camera, fully functional.", 2500.00, "sellerA", "", "Electronics"));
        availableProducts.add(new Product(UUID.randomUUID().toString(), "Old Bicycle", "Needs new tires, but frame is good.", 1500.00, "user1", "", "Vehicles"));
        availableProducts.add(new Product(UUID.randomUUID().toString(), "Rare Book", "First edition of a popular novel.", 5000.00, "sellerA", "", "Books"));
        availableProducts.add(new Product(UUID.randomUUID().toString(), "Laptop (Used)", "8GB RAM, 256GB SSD, still good for daily use.", 18000.00, "user1", "", "Electronics"));
        availableProducts.add(new Product(UUID.randomUUID().toString(), "Antique Vase", "Beautiful decorative piece.", 3000.00, "sellerA", "", "Furniture"));
    }

    // --- Panel Creation Methods ---

    private void createLoginPanel() {
        loginPanel = new JPanel(new GridBagLayout());
        loginPanel.setBorder(new EmptyBorder(50, 50, 50, 50)); // Padding
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titleLabel = new JLabel("Welcome to the Marketplace!", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        loginPanel.add(titleLabel, gbc);

        gbc.gridwidth = 1; // Reset gridwidth

        gbc.gridx = 0;
        gbc.gridy = 1;
        loginPanel.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1;
        loginUsernameField = new JTextField(20);
        loginPanel.add(loginUsernameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        loginPanel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        loginPasswordField = new JPasswordField(20);
        loginPanel.add(loginPasswordField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        JButton loginButton = new JButton("Login");
        loginButton.setPreferredSize(new Dimension(100, 30));
        loginPanel.add(loginButton, gbc);

        gbc.gridy = 4;
        JButton signupPromptButton = new JButton("Don't have an account? Sign Up");
        signupPromptButton.setPreferredSize(new Dimension(200, 30));
        loginPanel.add(signupPromptButton, gbc);

        // Action Listeners
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                attemptLogin();
            }
        });

        signupPromptButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(mainPanel, "Signup");
            }
        });
    }

    private void createSignupPanel() {
        signupPanel = new JPanel(new GridBagLayout());
        signupPanel.setBorder(new EmptyBorder(50, 50, 50, 50));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titleLabel = new JLabel("Create New Account", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        signupPanel.add(titleLabel, gbc);

        gbc.gridwidth = 1;

        gbc.gridx = 0;
        gbc.gridy = 1;
        signupPanel.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1;
        signupUsernameField = new JTextField(20);
        signupPanel.add(signupUsernameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        signupPanel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        signupPasswordField = new JPasswordField(20);
        signupPanel.add(signupPasswordField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        signupPanel.add(new JLabel("Address:"), gbc);
        gbc.gridx = 1;
        signupAddressField = new JTextField(20);
        signupPanel.add(signupAddressField, gbc);

        // New fields for Phone Number and Email
        gbc.gridx = 0;
        gbc.gridy = 4;
        signupPanel.add(new JLabel("Phone Number:"), gbc);
        gbc.gridx = 1;
        signupPhoneNumberField = new JTextField(20);
        signupPanel.add(signupPhoneNumberField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 5;
        signupPanel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        signupEmailField = new JTextField(20);
        signupPanel.add(signupEmailField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 6; // Adjusted gridy for new fields
        gbc.gridwidth = 2;
        JButton signupButton = new JButton("Sign Up");
        signupButton.setPreferredSize(new Dimension(100, 30));
        signupPanel.add(signupButton, gbc);

        gbc.gridy = 7; // Adjusted gridy for new fields
        JButton backToLoginButton = new JButton("Back to Login");
        backToLoginButton.setPreferredSize(new Dimension(100, 30));
        signupPanel.add(backToLoginButton, gbc);

        // Action Listeners
        signupButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                attemptSignup();
            }
        });

        backToLoginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(mainPanel, "Login");
            }
        });
    }

    private void createDashboardPanel() {
        dashboardPanel = new JPanel(new GridBagLayout());
        dashboardPanel.setBorder(new EmptyBorder(50, 50, 50, 50));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20, 20, 20, 20);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel welcomeLabel = new JLabel("Hello, " + (currentUser != null ? currentUser.getUsername() : "Guest") + "!", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 28));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        dashboardPanel.add(welcomeLabel, gbc);

        gbc.gridwidth = 1; // Reset

        JButton buyButton = new JButton("Buy Products");
        buyButton.setPreferredSize(new Dimension(200, 80));
        buyButton.setFont(new Font("Arial", Font.PLAIN, 20));
        gbc.gridx = 0;
        gbc.gridy = 1;
        dashboardPanel.add(buyButton, gbc);

        JButton sellButton = new JButton("Sell Products");
        sellButton.setPreferredSize(new Dimension(200, 80));
        sellButton.setFont(new Font("Arial", Font.PLAIN, 20));
        gbc.gridx = 1;
        gbc.gridy = 1;
        dashboardPanel.add(sellButton, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        JButton logoutButton = new JButton("Logout");
        logoutButton.setPreferredSize(new Dimension(100, 40));
        dashboardPanel.add(logoutButton, gbc);

        // Action Listeners
        buyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                refreshBuyProductList(); // Refresh list before showing
                cardLayout.show(mainPanel, "BuyProduct");
            }
        });

        sellButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearSellProductForm(); // Clear form before showing
                cardLayout.show(mainPanel, "SellProduct");
            }
        });

        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                currentUser = null;
                cartItems.clear(); // Clear cart on logout
                JOptionPane.showMessageDialog(mainPanel, "Logged out successfully!");
                cardLayout.show(mainPanel, "Login");
            }
        });
    }

    private void createSellProductPanel() {
        sellProductPanel = new JPanel(new GridBagLayout());
        sellProductPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titleLabel = new JLabel("List a Product for Sale", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        sellProductPanel.add(titleLabel, gbc);

        gbc.gridwidth = 1;

        gbc.gridx = 0;
        gbc.gridy = 1;
        sellProductPanel.add(new JLabel("Product Name:"), gbc);
        gbc.gridx = 1;
        productNameField = new JTextField(25);
        sellProductPanel.add(productNameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        sellProductPanel.add(new JLabel("Description:"), gbc);
        gbc.gridx = 1;
        productDescriptionArea = new JTextArea(5, 25);
        productDescriptionArea.setLineWrap(true);
        productDescriptionArea.setWrapStyleWord(true);
        JScrollPane descriptionScrollPane = new JScrollPane(productDescriptionArea);
        sellProductPanel.add(descriptionScrollPane, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        sellProductPanel.add(new JLabel("Price (Rs.):"), gbc);
        gbc.gridx = 1;
        productPriceField = new JTextField(10);
        sellProductPanel.add(productPriceField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        sellProductPanel.add(new JLabel("Product Photo:"), gbc);
        gbc.gridx = 1;
        JPanel imagePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        productImageLabel = new JLabel("No image selected");
        JButton selectImageButton = new JButton("Select Image");
        imagePanel.add(productImageLabel);
        imagePanel.add(selectImageButton);
        sellProductPanel.add(imagePanel, gbc);

        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        JButton listProductButton = new JButton("List Product");
        listProductButton.setPreferredSize(new Dimension(150, 40));
        sellProductPanel.add(listProductButton, gbc);

        gbc.gridy = 6;
        JButton backToDashboardFromSell = new JButton("Back to Dashboard");
        backToDashboardFromSell.setPreferredSize(new Dimension(150, 40));
        sellProductPanel.add(backToDashboardFromSell, gbc);

        // Action Listeners
        selectImageButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setDialogTitle("Select Product Image");
                fileChooser.setFileFilter(new FileNameExtensionFilter("Image Files", "jpg", "jpeg", "png", "gif"));
                int userSelection = fileChooser.showOpenDialog(sellProductPanel);
                if (userSelection == JFileChooser.APPROVE_OPTION) {
                    File fileToLoad = fileChooser.getSelectedFile();
                    selectedImagePath = fileToLoad.getAbsolutePath();
                    productImageLabel.setText(fileToLoad.getName()); // Show just the file name
                }
            }
        });

        listProductButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                listProduct();
            }
        });

        backToDashboardFromSell.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(mainPanel, "Dashboard");
                promptForAppReview(); // Prompt for review after selling
            }
        });
    }

    private void createBuyProductPanel() {
        buyProductPanel = new JPanel(new BorderLayout(10, 10));
        buyProductPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Top Panel for Filters
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        filterPanel.setBorder(BorderFactory.createTitledBorder("Filter Products"));

        priceFilterMinField = new JTextField(5);
        priceFilterMaxField = new JTextField(5);
        // Ensure the types here match the types assigned in initializeDummyData()
        productTypeFilterComboBox = new JComboBox<>(new String[]{"All", "Electronics", "Books", "Furniture", "Vehicles", "Miscellaneous"}); // Example types

        JButton applyFilterButton = new JButton("Apply Filter");
        JButton resetFilterButton = new JButton("Reset Filter");

        filterPanel.add(new JLabel("Price Min:"));
        filterPanel.add(priceFilterMinField);
        filterPanel.add(new JLabel("Max:"));
        filterPanel.add(priceFilterMaxField);
        filterPanel.add(new JLabel("Type:"));
        filterPanel.add(productTypeFilterComboBox);
        filterPanel.add(applyFilterButton);
        filterPanel.add(resetFilterButton);

        buyProductPanel.add(filterPanel, BorderLayout.NORTH);

        // Center Panel for Product List
        productListModel = new DefaultListModel<>();
        productJList = new JList<>(productListModel);
        productJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        productJList.setCellRenderer(new ProductListCellRenderer()); // Custom renderer for better display
        JScrollPane productScrollPane = new JScrollPane(productJList);
        buyProductPanel.add(productScrollPane, BorderLayout.CENTER);

        // Bottom Panel for Actions
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        JButton viewDetailsButton = new JButton("View Details");
        JButton addToCartButton = new JButton("Add to Cart");
        JButton viewCartButton = new JButton("View Cart");
        JButton backToDashboardFromBuy = new JButton("Back to Dashboard");

        actionPanel.add(viewDetailsButton);
        actionPanel.add(addToCartButton);
        actionPanel.add(viewCartButton);
        actionPanel.add(backToDashboardFromBuy);
        buyProductPanel.add(actionPanel, BorderLayout.SOUTH);

        // Action Listeners
        applyFilterButton.addActionListener(e -> applyProductFilters());
        resetFilterButton.addActionListener(e -> resetProductFilters());

        viewDetailsButton.addActionListener(e -> {
            Product selectedProduct = productJList.getSelectedValue();
            if (selectedProduct != null) {
                showProductDetails(selectedProduct);
            } else {
                JOptionPane.showMessageDialog(buyProductPanel, "Please select a product to view details.");
            }
        });

        addToCartButton.addActionListener(e -> {
            Product selectedProduct = productJList.getSelectedValue();
            if (selectedProduct != null) {
                addToCart(selectedProduct);
            } else {
                JOptionPane.showMessageDialog(buyProductPanel, "Please select a product to add to cart.");
            }
        });

        viewCartButton.addActionListener(e -> {
            refreshCartList();
            cardLayout.show(mainPanel, "Cart");
        });

        backToDashboardFromBuy.addActionListener(e -> {
            cardLayout.show(mainPanel, "Dashboard");
        });

        refreshBuyProductList(); // Initial load of products
    }

    private void createCartPanel() {
        cartPanel = new JPanel(new BorderLayout(10, 10));
        cartPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = new JLabel("Your Shopping Cart", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        cartPanel.add(titleLabel, BorderLayout.NORTH);

        cartListModel = new DefaultListModel<>();
        cartJList = new JList<>(cartListModel);
        cartJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane cartScrollPane = new JScrollPane(cartJList);
        cartPanel.add(cartScrollPane, BorderLayout.CENTER);

        JPanel summaryPanel = new JPanel(new BorderLayout());
        cartTotalPriceLabel = new JLabel("Total: Rs. 0.00", SwingConstants.RIGHT);
        cartTotalPriceLabel.setFont(new Font("Arial", Font.BOLD, 16));
        summaryPanel.add(cartTotalPriceLabel, BorderLayout.NORTH);

        JPanel cartActionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        JButton removeItemButton = new JButton("Remove Item");
        JButton proceedToCheckoutButton = new JButton("Proceed to Checkout");
        JButton continueShoppingButton = new JButton("Continue Shopping");

        cartActionPanel.add(removeItemButton);
        cartActionPanel.add(proceedToCheckoutButton);
        cartActionPanel.add(continueShoppingButton);
        summaryPanel.add(cartActionPanel, BorderLayout.SOUTH);
        cartPanel.add(summaryPanel, BorderLayout.SOUTH);

        // Action Listeners
        removeItemButton.addActionListener(e -> {
            Product selectedProduct = cartJList.getSelectedValue();
            if (selectedProduct != null) {
                cartItems.remove(selectedProduct);
                refreshCartList();
            } else {
                JOptionPane.showMessageDialog(cartPanel, "Please select an item to remove.");
            }
        });

        proceedToCheckoutButton.addActionListener(e -> simulateCheckout());

        continueShoppingButton.addActionListener(e -> cardLayout.show(mainPanel, "BuyProduct"));
    }

    // --- Core Logic Methods ---

    private void attemptLogin() {
        String username = loginUsernameField.getText();
        String password = new String(loginPasswordField.getPassword());

        boolean loggedIn = false;
        for (User user : registeredUsers) {
            if (user.getUsername().equals(username) && user.getPassword().equals(password)) {
                currentUser = user;
                loggedIn = true;
                break;
            }
        }

        if (loggedIn) {
            JOptionPane.showMessageDialog(this, "Login Successful! Welcome, " + currentUser.getUsername() + ".");
            // Update dashboard welcome message
            Component[] components = dashboardPanel.getComponents();
            for (Component comp : components) {
                if (comp instanceof JLabel) {
                    JLabel label = (JLabel) comp;
                    if (label.getText().startsWith("Hello,")) {
                        label.setText("Hello, " + currentUser.getUsername() + "!");
                        break;
                    }
                }
            }
            cardLayout.show(mainPanel, "Dashboard");
            clearLoginFields();
        } else {
            JOptionPane.showMessageDialog(this, "Invalid Username or Password.", "Login Failed", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void attemptSignup() {
        String username = signupUsernameField.getText();
        String password = new String(signupPasswordField.getPassword());
        String address = signupAddressField.getText();
        String phoneNumber = signupPhoneNumberField.getText(); // Get phone number
        String email = signupEmailField.getText();             // Get email

        if (username.isEmpty() || password.isEmpty() || address.isEmpty() || phoneNumber.isEmpty() || email.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields are required for signup.", "Signup Failed", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Validate Phone Number
        if (!phoneNumber.matches("\\d{10}")) { // Checks for exactly 10 digits
            JOptionPane.showMessageDialog(this, "Phone number must be exactly 10 digits and contain only numbers.", "Signup Failed", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Validate Email
        if (!email.endsWith("@gmail.com")) {
            JOptionPane.showMessageDialog(this, "Email must end with @gmail.com.", "Signup Failed", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Check if username already exists
        for (User user : registeredUsers) {
            if (user.getUsername().equals(username)) {
                JOptionPane.showMessageDialog(this, "Username already exists. Please choose a different one.", "Signup Failed", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        User newUser = new User(username, password, address, phoneNumber, email); // Pass new fields to User constructor
        registeredUsers.add(newUser);
        JOptionPane.showMessageDialog(this, "Account created successfully for " + username + "! You can now login.");
        cardLayout.show(mainPanel, "Login");
        clearSignupFields();
    }

    private void listProduct() {
        if (currentUser == null) {
            JOptionPane.showMessageDialog(sellProductPanel, "You must be logged in to sell a product.");
            return;
        }

        String name = productNameField.getText();
        String description = productDescriptionArea.getText();
        String priceText = productPriceField.getText();

        if (name.isEmpty() || description.isEmpty() || priceText.isEmpty()) {
            JOptionPane.showMessageDialog(sellProductPanel, "Please fill in all product details.", "Missing Information", JOptionPane.WARNING_MESSAGE);
            return;
        }

        double price;
        try {
            price = Double.parseDouble(priceText);
            if (price <= 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(sellProductPanel, "Please enter a valid positive number for price.", "Invalid Price", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String productId = UUID.randomUUID().toString();
        // When selling, assign a generic "Miscellaneous" type for simplicity in this demo.
        // In a real application, you'd add a JComboBox or similar for the seller to choose the product type.
        Product newProduct = new Product(productId, name, description, price, currentUser.getUsername(), selectedImagePath, "Miscellaneous");
        availableProducts.add(newProduct);

        JOptionPane.showMessageDialog(sellProductPanel, "Product '" + name + "' listed successfully!");
        clearSellProductForm();
        promptForAppReview(); // Ask for review after selling
        // Optionally, go back to dashboard or stay on sell page
        cardLayout.show(mainPanel, "Dashboard");
    }

    private void refreshBuyProductList() {
        productListModel.clear();
        for (Product product : availableProducts) {
            // Do not show products listed by the current user
            if (currentUser == null || !product.getSellerUsername().equals(currentUser.getUsername())) {
                productListModel.addElement(product);
            }
        }
    }

    private void applyProductFilters() {
        productListModel.clear(); // Clear existing items

        double minPrice = -1;
        double maxPrice = Double.MAX_VALUE;

        // Error handling for price input fields
        try {
            if (!priceFilterMinField.getText().isEmpty()) {
                minPrice = Double.parseDouble(priceFilterMinField.getText());
            }
            if (!priceFilterMaxField.getText().isEmpty()) {
                maxPrice = Double.parseDouble(priceFilterMaxField.getText());
            }
            if (minPrice > maxPrice) {
                 JOptionPane.showMessageDialog(buyProductPanel, "Minimum price cannot be greater than maximum price.", "Filter Error", JOptionPane.ERROR_MESSAGE);
                 return; // Stop filtering if invalid range
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(buyProductPanel, "Invalid price input. Please enter numbers only for price filters.", "Filter Error", JOptionPane.ERROR_MESSAGE);
            return; // Stop filtering if invalid input
        }

        String selectedType = (String) productTypeFilterComboBox.getSelectedItem();

        for (Product product : availableProducts) {
            // Filter by seller (don't show own products)
            if (currentUser != null && product.getSellerUsername().equals(currentUser.getUsername())) {
                continue;
            }

            // Filter by price
            if (product.getPrice() < minPrice || product.getPrice() > maxPrice) {
                continue; // Skip this product if it's outside the price range
            }

            // Filter by type (now uses the explicit 'type' field)
            if (!selectedType.equals("All")) {
                if (!product.getType().equalsIgnoreCase(selectedType)) {
                    continue; // Skip this product if its type doesn't match the filter
                }
            }
            productListModel.addElement(product); // Add product if it passes all filters
        }
        if (productListModel.isEmpty() && !availableProducts.isEmpty()) {
             JOptionPane.showMessageDialog(buyProductPanel, "No products found matching your filter criteria.", "No Results", JOptionPane.INFORMATION_MESSAGE);
        } else if (availableProducts.isEmpty()) {
             JOptionPane.showMessageDialog(buyProductPanel, "No products are currently available in the marketplace.", "No Products", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void resetProductFilters() {
        priceFilterMinField.setText("");
        priceFilterMaxField.setText("");
        productTypeFilterComboBox.setSelectedItem("All");
        refreshBuyProductList(); // Reload all eligible products
    }

    private void showProductDetails(Product product) {
        String details = "Product Name: " + product.getName() + "\n" +
                         "Description: " + product.getDescription() + "\n" +
                         "Price: Rs." + String.format("%.2f", product.getPrice()) + "\n" +
                         "Seller: " + product.getSellerUsername() + "\n" +
                         "Category: " + product.getType() + "\n" + // Display product type
                         "Image Path: " + (product.getImagePath().isEmpty() ? "N/A" : product.getImagePath().substring(product.getImagePath().lastIndexOf(File.separator) + 1)); // Just filename

        JOptionPane.showMessageDialog(buyProductPanel, details, "Product Details", JOptionPane.INFORMATION_MESSAGE);

        // Simulate negotiation (optional)
        int negotiateOption = JOptionPane.showConfirmDialog(buyProductPanel, "Would you like to negotiate the price for " + product.getName() + "?", "Negotiate?", JOptionPane.YES_NO_OPTION);
        if (negotiateOption == JOptionPane.YES_OPTION) {
            simulateNegotiation(product);
        }
    }

    private void addToCart(Product product) {
        if (currentUser == null) {
            JOptionPane.showMessageDialog(buyProductPanel, "Please login to add items to cart.", "Login Required", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (cartItems.contains(product)) {
            JOptionPane.showMessageDialog(buyProductPanel, product.getName() + " is already in your cart.");
            return;
        }
        cartItems.add(product);
        JOptionPane.showMessageDialog(buyProductPanel, product.getName() + " added to cart!");
        refreshCartList(); // Update cart total
    }

    private void refreshCartList() {
        cartListModel.clear();
        double total = 0.0;
        for (Product item : cartItems) {
            cartListModel.addElement(item);
            total += item.getPrice();
        }
        cartTotalPriceLabel.setText("Total: Rs." + String.format("%.2f", total));
    }

    private void simulateCheckout() {
        if (cartItems.isEmpty()) {
            JOptionPane.showMessageDialog(cartPanel, "Your cart is empty. Please add items before checking out.", "Empty Cart", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Simulate notification to seller (would be real in a full app)
        StringBuilder sellerNotification = new StringBuilder("Items sold:\n");
        Map<String, List<Product>> salesBySeller = new HashMap<>();

        for (Product item : cartItems) {
            sellerNotification.append("- ").append(item.getName()).append(" (Seller: ").append(item.getSellerUsername()).append(")\n");
            salesBySeller.computeIfAbsent(item.getSellerUsername(), k -> new ArrayList<>()).add(item);
        }

        JOptionPane.showMessageDialog(cartPanel, "Proceeding to Payment & Transport for:\n" + sellerNotification.toString(), "Checkout Confirmation", JOptionPane.INFORMATION_MESSAGE);

        // Simulate payment/transport
        int paymentConfirm = JOptionPane.showConfirmDialog(cartPanel, "Payment and transport arranged for cart items. Confirm purchase?", "Confirm Purchase", JOptionPane.YES_NO_OPTION);

        if (paymentConfirm == JOptionPane.YES_OPTION) {
            // Remove sold items from available products
            availableProducts.removeAll(cartItems);
            cartItems.clear(); // Clear cart after successful checkout
            refreshCartList(); // Update cart display

            JOptionPane.showMessageDialog(cartPanel, "Purchase successful! Items will be delivered to " + currentUser.getAddress() + ".");

            // Prompt for reviews
            promptForAppReview();
            for (String seller : salesBySeller.keySet()) {
                promptForSellerReview(seller);
            }

            int continueShopping = JOptionPane.showConfirmDialog(cartPanel, "Would you like to buy anything else?", "Continue Shopping?", JOptionPane.YES_NO_OPTION);
            if (continueShopping == JOptionPane.YES_OPTION) {
                cardLayout.show(mainPanel, "BuyProduct");
                refreshBuyProductList(); // Refresh list after some items are "sold"
            } else {
                cardLayout.show(mainPanel, "Dashboard");
            }
        } else {
            JOptionPane.showMessageDialog(cartPanel, "Checkout cancelled.");
        }
    }

    private void simulateNegotiation(Product product) {
        String offer = JOptionPane.showInputDialog(buyProductPanel, "Enter your offer price for " + product.getName() + " (current: Rs." + String.format("%.2f", product.getPrice()) + "):");
        if (offer != null && !offer.trim().isEmpty()) {
            try {
                double offeredPrice = Double.parseDouble(offer);
                if (offeredPrice <= 0) {
                    JOptionPane.showMessageDialog(buyProductPanel, "Offer price must be a positive number.", "Negotiation Failed", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                // Simulate seller notification and response
                JOptionPane.showMessageDialog(buyProductPanel,
                        "Your offer of Rs." + String.format("%.2f", offeredPrice) + " for " + product.getName() + " has been sent to " + product.getSellerUsername() + ".\n" +
                        "Simulating negotiation: Seller accepts your offer for this demo!",
                        "Negotiation Sent", JOptionPane.INFORMATION_MESSAGE);

                // In a real app, this would involve server-side communication and seller approval.
                // For this demo, we'll assume negotiation is successful and update price in cart logic later.
                // For simplicity, we won't actually change the listed product price in `availableProducts`.
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(buyProductPanel, "Invalid offer price. Please enter a valid number.", "Negotiation Failed", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void promptForAppReview() {
        int reviewOption = JOptionPane.showConfirmDialog(mainPanel, "Would you like to give a review for the app?", "App Review", JOptionPane.YES_NO_OPTION);
        if (reviewOption == JOptionPane.YES_OPTION) {
            String review = JOptionPane.showInputDialog(mainPanel, "Please enter your app review (e.g., '5 stars, great app!'):");
            if (review != null && !review.trim().isEmpty()) {
                JOptionPane.showMessageDialog(mainPanel, "Thank you for your app review!");
                // In a real app, save this review to database
            } else {
                JOptionPane.showMessageDialog(mainPanel, "App review cancelled or empty.");
            }
        }
    }

    private void promptForSellerReview(String sellerUsername) {
        int reviewOption = JOptionPane.showConfirmDialog(mainPanel, "Would you like to give a review for seller " + sellerUsername + "?", "Seller Review", JOptionPane.YES_NO_OPTION);
        if (reviewOption == JOptionPane.YES_OPTION) {
            String review = JOptionPane.showInputDialog(mainPanel, "Please enter your review for " + sellerUsername + " (e.g., '4 stars, good communication'):");
            if (review != null && !review.trim().isEmpty()) {
                JOptionPane.showMessageDialog(mainPanel, "Thank you for your review for " + sellerUsername + "!");
                // In a real app, save this review to database
            } else {
                JOptionPane.showMessageDialog(mainPanel, "Seller review cancelled or empty.");
            }
        }
    }

    // --- Helper Methods ---

    private void clearLoginFields() {
        loginUsernameField.setText("");
        loginPasswordField.setText("");
    }

    private void clearSignupFields() {
        signupUsernameField.setText("");
        signupPasswordField.setText("");
        signupAddressField.setText("");
        signupPhoneNumberField.setText(""); // Clear new fields
        signupEmailField.setText("");       // Clear new fields
    }

    private void clearSellProductForm() {
        productNameField.setText("");
        productDescriptionArea.setText("");
        productPriceField.setText("");
        productImageLabel.setText("No image selected");
        selectedImagePath = "";
    }

    // Custom Cell Renderer for Product JList to display details better
    private static class ProductListCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value instanceof Product) {
                Product product = (Product) value;
                label.setText("<html><b>" + product.getName() + "</b> (Rs." + String.format("%.2f", product.getPrice()) + ")<br>" +
                              "<small>Category: " + product.getType() + " | Seller: " + product.getSellerUsername() + "</small><br>" + // Display type
                              "<small>" + product.getDescription() + "</small></html>");
                label.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10)); // Add some padding
            }
            return label;
        }
    }

    // --- Main Method ---
    public static void main(String[] args) {
        // Ensure GUI updates are done on the Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new Marketappwithgui();
            }
        });
    }
}
