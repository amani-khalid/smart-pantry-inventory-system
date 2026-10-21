package models;

public class PantryCharacter {
    private String name;
    private String role;
    private int level;
    private String avatarPath;
    private String[] tips;
    
    public PantryCharacter(String name, String role, String avatarPath) {
        this.name = name;
        this.role = role;
        this.level = 1;
        this.avatarPath = avatarPath;
        this.tips = new String[]{
            "Keep your pantry organized!",
            "Check expiration dates regularly!",
            "Restock before you run out!"
        };
    }
    
    public String getRandomTip() {
        return tips[(int)(Math.random() * tips.length)];
    }
    
    // Getters and setters...
}