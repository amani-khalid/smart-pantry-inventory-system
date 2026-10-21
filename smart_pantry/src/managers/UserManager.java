package managers;

import models.User;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Manages user registration, authentication, and file operations
 */
public class UserManager {
	private static final String USERS_FILE = "data/users.txt";
	private List<User> users;
	private User currentUser;

	public UserManager() {
		this.users = new ArrayList<>();
		this.currentUser = null;
		createDataDirectory();
		loadUsersFromFile();
	}

	// Create data directory if it doesn't exist
	private void createDataDirectory() {
		File dataDir = new File("data");
		if (!dataDir.exists()) {
			dataDir.mkdirs();
		}
	}

	// User registration
	public boolean registerUser(String name, String email, String phone, String password) {
		// Check if email already exists
		if (users.stream().anyMatch(user -> user.getEmail() != null && user.getEmail().equalsIgnoreCase(email))) {
			return false; // Email already registered
		}

		try {
			User newUser = new User(name, email, phone, password);
			users.add(newUser);
			saveUsersToFile();
			return true;
		} catch (IllegalArgumentException e) {
			System.err.println("Registration failed: " + e.getMessage());
			return false;
		}
	}

	// User login
	public boolean login(String email, String password) {
		Optional<User> userOpt = users.stream().filter(user -> user.getEmail().equalsIgnoreCase(email)).findFirst();

		if (userOpt.isPresent() && userOpt.get().verifyPassword(password)) {
			this.currentUser = userOpt.get();
			return true;
		}
		return false;
	}

	// User logout
	public void logout() {
		this.currentUser = null;
	}

	// Get current user
	public User getCurrentUser() {
		return currentUser;
	}

	// Check if user is logged in
	public boolean isUserLoggedIn() {
		return currentUser != null;
	}

	// File operations
	private void loadUsersFromFile() {
		File file = new File(USERS_FILE);
		if (!file.exists()) {
			System.out.println("Users file not found. Starting with empty user list.");
			return;
		}

		try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
			String line;
			while ((line = reader.readLine()) != null) {
				if (!line.trim().isEmpty()) {
					try {
						User user = User.fromFileString(line);
						users.add(user);
					} catch (IllegalArgumentException e) {
						System.err.println("Error loading user: " + e.getMessage());
					}
				}
			}
			System.out.println("Loaded " + users.size() + " users from file.");
		} catch (IOException e) {
			System.err.println("Error reading users file: " + e.getMessage());
		}
	}

	private void saveUsersToFile() {
		try (PrintWriter writer = new PrintWriter(new FileWriter(USERS_FILE))) {
			for (User user : users) {
				writer.println(user.toFileString());
			}
			System.out.println("Saved " + users.size() + " users to file.");
		} catch (IOException e) {
			System.err.println("Error saving users file: " + e.getMessage());
		}
	}

	// Get all users (for testing)
	public List<User> getUsers() {
		return new ArrayList<>(users);
	}

	// Find user by email (for testing)
	public User findUserByEmail(String email) {
		return users.stream().filter(user -> user.getEmail().equalsIgnoreCase(email)).findFirst().orElse(null);
	}

	public User findUserById(String userId) {
		return users.stream().filter(user -> user.getUserId().equals(userId)).findFirst().orElse(null);
	}
}