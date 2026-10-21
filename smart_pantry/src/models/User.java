package models;

import interfaces.Storable;
import java.io.Serializable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Pattern;

/**
 * Represents a system user with authentication and file storage capabilities
 */
public class User implements Storable, Serializable {
    // Static counter for auto-generating user IDs
    private static int idCounter = 1000;
    
    // Validation patterns
    private static final Pattern EMAIL_PATTERN = 
        Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
    private static final Pattern PHONE_PATTERN = 
        Pattern.compile("^[0-9]{10,15}$");
    
    // Instance variables
    private final String userId;
    private String name;
    private String email;
    private String phone;
    private String passwordHash;
    
    // Constructor for new users
    public User(String name, String email, String phone, String password) {
        this.userId = generateUserId();
        setName(name);
        setEmail(email);
        setPhone(phone);
        setPassword(password);
		// call shopping list constructor shoppingList(userId);
    }
    
    // Constructor for loading from file
    public User(String userId, String name, String email, String phone, String passwordHash) {
    	if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be null or empty");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        this.userId = userId;
        this.name = name;
        this.email = email; 
        this.phone = phone;
        this.passwordHash = passwordHash;        
        try {
            String numberPart = userId.replace("USER_", "");
            int idNumber = Integer.parseInt(numberPart);
            if (idNumber >= idCounter) {
                idCounter = idNumber + 1;
            }
        } catch (NumberFormatException e) {
            System.err.println("Warning: Could not parse user ID: " + userId);
        }
    }
    
    // Getters
    public String getUserId() { 
        return userId; 
    }
    
    public String getName() { 
        return name; 
    }
    
    public String getEmail() { 
        return email; 
    }
    
    public String getPhone() { 
        return phone; 
    }
    
    public String getPasswordHash() { 
        return passwordHash; 
    }
    
    // Setters with validation
    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be empty");
        }
        this.name = name.trim();
    }
    
    public void setEmail(String email) {
        if (email == null || !EMAIL_PATTERN.matcher(email).matches()) {
            throw new IllegalArgumentException("Invalid email format");
        }
        this.email = email.toLowerCase().trim();
    }
    
    public void setPhone(String phone) {
        String cleanedPhone = phone.replaceAll("[\\s-()]", "");
        if (phone == null || !PHONE_PATTERN.matcher(cleanedPhone).matches()) {
            throw new IllegalArgumentException("Phone number must contain 10-15 digits");
        }
        this.phone = cleanedPhone;
    }
    
    public void setPassword(String password) {
        if (password == null || password.length() < 6) {
            throw new IllegalArgumentException("Password must be at least 6 characters long");
        }
        this.passwordHash = hashPassword(password);
    }
    
    // Password hashing
    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hashedBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }
    
    // Password verification
    public boolean verifyPassword(String password) {
        return this.passwordHash.equals(hashPassword(password));
    }
    
    // File operations
    @Override
    public String toFileString() {
        return String.join("|", 
            userId,  // REMOVE String.valueOf()
            name,
            email,
            phone,
            passwordHash
        );
    }
    
    // Static method to create User from file string
    public static User fromFileString(String fileString) {
        String[] parts = fileString.split("\\|");
        if (parts.length != 5) {
            throw new IllegalArgumentException("Invalid user data format");
        }
        
        try {
        	String id = parts[0];           
        	String name = parts[1];
            String email = parts[2];
            String phone = parts[3];
            String passwordHash = parts[4];
            
            return new User(id, name, email, phone, passwordHash);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid user ID format", e);
        }
    }
    
    // Object methods
    private static synchronized String generateUserId() {
        String newId = "USER_" + idCounter;
        idCounter++;
        return newId;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        User user = (User) obj;
        return userId.equals(user.userId);
    }
    
    @Override
    public int hashCode() {
    	return userId.hashCode();
    }
    
    @Override
    public String toString() {
        return String.format("User{id=%d, name='%s', email='%s'}", userId, name, email);
    }
}