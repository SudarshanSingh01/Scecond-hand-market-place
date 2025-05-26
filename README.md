 Second-Hand Marketplace (Console-Based Java Project)
This is a Java-based console application that simulates a second-hand marketplace for buying and selling items. Users can sign up, log in, and interact with categorized product listings.

 Features Implemented
1. JDK & IDE Setup
	•	Project created using JDK 17 and tested in IntelliJ IDEA / Eclipse.
2. Defined Project Structure
	•	Organized into a single Java class (MarketplaceAppWithCategories) for simplicity.
	•	Supports modular development and easy transition to layered architecture.
3. File Structure Maintenance
	•	users.txt is created and used for storing user credentials and data persistently.
4. Auto File Creation for CRUD
	•	Automatically creates users.txt if it does not exist.
	•	Appends new users and reads credentials for login.
5. I/O File Connectivity
	•	Uses BufferedReader, BufferedWriter, FileWriter, and FileReader for file input/output.
6. Planned Layered Architecture
	•	Future-ready structure that can evolve into:
	◦	Model (User, Product)
	◦	Service Layer (business logic)
	◦	Repository Layer (file handling)
	◦	UI Layer (console interface)
7. Input Validation
	•	Validates category and sub-category input.
	•	Checks if User ID is already taken during sign-up.
	•	Validates login with correct password and User ID.
8. Accuracy of Output
	•	Displays accurate messages for signup, login, and category navigation.
	•	Reflects user's selected action (buy/sell) and category.
9. Error Messages & Feedback
	•	Graceful error messages for:
	◦	Invalid menu options
	◦	Incorrect login
	◦	Invalid category/subcategory selection
	◦	IO exceptions during file operations

 User Roles
	•	Buyer
	•	Seller
	•	Both

 Categories
	•	Vehicles: Cars, Bikes, Scooters, Bicycles
	•	Furniture: Beds, Sofas, Chairs, etc.
	•	Electronics: Mobiles, Laptops, Tablets
	•	Books, Clothing, Fitness, Gaming, and more...

 How to Run
	1	Clone or download the repository.
	2	Open in your favorite IDE (e.g., IntelliJ IDEA).
	3	Compile and run the MarketplaceAppWithCategories.java.
	4	Follow on-screen instructions to sign up or log in.

 Files Used
	•	users.txt: stores user data in the format userID|firstName|lastName|email|address|phone|role|password

🚀 Future Improvements
	•	CRUD operations for product listings
	•	Search functionality for buyers
	•	Save product data in separate files
	•	Implement MVC or layered architecture
	•	Enhance input validation and formatting
