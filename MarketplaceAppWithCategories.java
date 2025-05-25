import java.io.*;
import java.util.*;

public class MarketplaceAppWithCategories {

    static final String USER_FILE = "users.txt";
    static Map<String, List<String>> categories = new LinkedHashMap<>();

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        initializeCategories();

        while (true) {
            System.out.println("\n=== Welcome to the Second-Hand Marketplace ===");
            System.out.println("1. Create a New Account");
            System.out.println("2. Log Into Your Account");
            System.out.println("3. Exit");
            System.out.print("Please choose an option (1-3): ");
            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    signUp(scanner);
                    break;
                case "2":
                    if (login(scanner)) {
                        categoryInteraction(scanner);
                    }
                    break;
                case "3":
                    System.out.println("Thanks for visiting! See you soon.");
                    scanner.close();
                    return;
                default:
                    System.out.println("Oops! That wasn't a valid option. Try again.");
            }
        }
    }

    public static void signUp(Scanner scanner) {
        try {
            System.out.print("Choose a unique User ID: ");
            String userId = scanner.nextLine().trim();

            if (isUserIdTaken(userId)) {
                System.out.println("Sorry, that User ID is already taken. Please pick another one.");
                return;
            }

            System.out.print("Your First Name: ");
            String name = scanner.nextLine().trim();

            System.out.print("Your Surname: ");
            String surname = scanner.nextLine().trim();

            System.out.print("Your Email Address: ");
            String email = scanner.nextLine().trim();

            System.out.print("Your Correspondence Address: ");
            String address = scanner.nextLine().trim();

            System.out.print("Your Phone Number: ");
            String phone = scanner.nextLine().trim();

            System.out.print("What will you use this app for? (buyer/seller/both): ");
            String role = scanner.nextLine().trim().toLowerCase();

            System.out.print("Create a Password: ");
            String password = scanner.nextLine().trim();

            String userData = userId + "|" + name + "|" + surname + "|" + email + "|" +
                    address + "|" + phone + "|" + role + "|" + password;

            BufferedWriter writer = new BufferedWriter(new FileWriter(USER_FILE, true));
            writer.write(userData);
            writer.newLine();
            writer.close();

            System.out.println("You're all set! Your account has been created successfully.");

        } catch (IOException e) {
            System.out.println("Something went wrong while signing up: " + e.getMessage());
        }
    }

    public static boolean login(Scanner scanner) {
        System.out.print("Enter Your User ID: ");
        String userId = scanner.nextLine().trim();

        System.out.print("Enter Your Password: ");
        String password = scanner.nextLine().trim();

        try {
            BufferedReader reader = new BufferedReader(new FileReader(USER_FILE));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length == 8 && parts[0].equals(userId) && parts[7].equals(password)) {
                    System.out.println("Login successful! Welcome back, " + parts[1] + "!");
                    reader.close();
                    return true;
                }
            }
            reader.close();
            System.out.println("Hmm... Incorrect User ID or Password. Please try again.");
        } catch (IOException e) {
            System.out.println("Something went wrong during login: " + e.getMessage());
        }

        return false;
    }

    public static boolean isUserIdTaken(String userId) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(USER_FILE));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length > 0 && parts[0].equals(userId)) {
                    reader.close();
                    return true;
                }
            }
            reader.close();
        } catch (IOException e) {
            // ignore
        }
        return false;
    }

    public static void categoryInteraction(Scanner scanner) {
        System.out.println("\nWhat would you like to do today?");
        System.out.println("1. Buy");
        System.out.println("2. Sell");
        System.out.print("Enter your choice: ");
        String action = scanner.nextLine().trim();

        System.out.println("\nSelect a category:");
        int i = 1;
        List<String> keys = new ArrayList<>(categories.keySet());
        for (String category : keys) {
            System.out.println(i + ". " + category);
            i++;
        }

        System.out.print("Enter category number: ");
        int catChoice = Integer.parseInt(scanner.nextLine());
        if (catChoice < 1 || catChoice > keys.size()) {
            System.out.println("Invalid category selection.");
            return;
        }

        String selectedCategory = keys.get(catChoice - 1);
        List<String> subcategories = categories.get(selectedCategory);

        System.out.println("\nChoose a sub-category from " + selectedCategory + ":");
        for (int j = 0; j < subcategories.size(); j++) {
            System.out.println((j + 1) + ". " + subcategories.get(j));
        }

        System.out.print("Enter sub-category number: ");
        int subChoice = Integer.parseInt(scanner.nextLine());
        if (subChoice < 1 || subChoice > subcategories.size()) {
            System.out.println("Invalid sub-category selection.");
            return;
        }

        String selectedSubCategory = subcategories.get(subChoice - 1);
        System.out.println("\nGreat! You chose to " + action.toLowerCase() + " an item in: " + selectedCategory + " → " + selectedSubCategory);
    }

    public static void initializeCategories() {
        categories.put("Vehicles", Arrays.asList("Cars", "Bikes", "Scooters", "Bicycles"));
        categories.put("Furniture", Arrays.asList("Beds", "Sofas", "Chairs", "Tables", "Wardrobes"));
        categories.put("Electronics", Arrays.asList("Mobile Phones", "Laptops", "Tablets", "TVs", "Smart Watches"));
        categories.put("Home Appliances", Arrays.asList("Refrigerators", "Washing Machines", "Air Conditioners", "Microwave Ovens", "Geysers"));
        categories.put("Books", Arrays.asList("School/College textbooks", "Novels", "Competitive exam books"));
        categories.put("Clothing & Accessories", Arrays.asList("Jackets", "Shoes", "Bags", "Watches"));
        categories.put("Sports & Fitness Equipment", Arrays.asList("Gym weights", "Treadmills", "Bicycles", "Cricket kits"));
        categories.put("Musical Instruments", Arrays.asList("Guitars", "Keyboards", "Drums", "Flutes"));
        categories.put("Gaming", Arrays.asList("Consoles (PS4, Xbox, etc.)", "Video games", "Controllers", "VR Headsets"));
        categories.put("Kids' Items", Arrays.asList("Toys", "Strollers", "Baby cots", "Study tables"));
    }
}